package com.org.jms.grouping;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class MessageGroupingDemo {

	public static void main(String[] args) throws NamingException, JMSException, InterruptedException {

		InitialContext context = new InitialContext();
		Queue queue = (Queue) context.lookup("queue/myQueue");
		Map<String, String> receivedMessages = new ConcurrentHashMap<>();
		
		try(ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
				JMSContext jmsContext1 = connectionFactory.createContext();
				JMSContext jmsContext2 = connectionFactory.createContext()){
			JMSProducer producer = jmsContext1.createProducer();
			
			JMSConsumer consumer1 = jmsContext2.createConsumer(queue);
			consumer1.setMessageListener(new MyListener("Consumer-1", receivedMessages));
			
			JMSConsumer consumer2 = jmsContext2.createConsumer(queue);
			consumer2.setMessageListener(new MyListener("Consumer-2", receivedMessages));
			
			int count = 10;
			TextMessage[] messages = new TextMessage[count]; 
			for (int i = 0; i < count; i++) {
				messages[i] = jmsContext1.createTextMessage("Group-0 message" + i);
				messages[i].setStringProperty("JMSXGroupID", "Group-0");
				producer.send(queue, messages[i]);
			}
			Thread.sleep(2000);
			for(TextMessage message : messages){
				if(!receivedMessages.get(message.getText()).equals("Consimer-1")) {
					throw new IllegalStateException("Group Message" + message.getText() + "gone to wrong receiver");
				}
			}
		}
	}
}

class MyListener implements MessageListener{

	private String name;
	private Map<String, String> receivedMessages;
	
	public MyListener(String name, Map<String, String> receivedMessages) {
		
		this.name = name;
		this.receivedMessages = receivedMessages;
	}
	
	@Override
	public void onMessage(Message message) {

		TextMessage textMessage = (TextMessage) message;
		try {
			System.out.println("Message received is: " + textMessage.getText());
			System.out.println("Received by: " + name);

			receivedMessages.put(textMessage.getText(), name);
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}