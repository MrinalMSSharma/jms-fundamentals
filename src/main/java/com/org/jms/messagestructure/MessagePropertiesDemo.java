package com.org.jms.messagestructure;

import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class MessagePropertiesDemo {

	public static void main(String[] args) throws NamingException, InterruptedException, JMSException {

		InitialContext context = new InitialContext();
		Queue queue = (Queue) context.lookup("queue/myQueue");
		
		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext =  cf.createContext()){
			JMSProducer producer = jmsContext.createProducer();
			TextMessage message = jmsContext.createTextMessage("Arise, Awake and stop not till the goal is reached.");
			message.setBooleanProperty("LoggedIn", true);
			message.setStringProperty("User", "Mrinal");
			producer.send(queue, message);
			
			Message message2 = jmsContext.createConsumer(queue).receive(5000);
			System.out.println("Message received is: " + message2);
			System.out.println(message2.getBooleanProperty("LoggedIn"));
			System.out.println(message2.getStringProperty("User"));
		}
	}
}