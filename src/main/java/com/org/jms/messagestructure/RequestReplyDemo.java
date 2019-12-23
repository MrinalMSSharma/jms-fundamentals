package com.org.jms.messagestructure;

import java.util.HashMap;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class RequestReplyDemo {

	public static void main(String[] args) throws NamingException, JMSException {

		InitialContext context = new InitialContext();
		Queue requestQueue = (Queue) context.lookup("queue/requestQueue");
		Queue replyQueue = (Queue) context.lookup("queue/replyQueue");
		
		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory(); JMSContext jmsContext =  cf.createContext();){
			JMSProducer requestProducer = jmsContext.createProducer();
			TextMessage message = jmsContext.createTextMessage("Arise, Awake and stop not till the goal is reached.");
//			TemporaryQueue replyQueue = jmsContext.createTemporaryQueue();
			message.setJMSReplyTo(replyQueue);
			requestProducer.send(requestQueue, message);
			System.out.println(message.getJMSMessageID());
			
			HashMap<String,TextMessage> hashMap = new HashMap<String, TextMessage>();
			hashMap.put(message.getJMSMessageID(), message);
			
			// This is Reply Queue Code
			JMSConsumer requestConsumer = jmsContext.createConsumer(requestQueue);
			TextMessage receive = (TextMessage) requestConsumer.receive();
			System.out.println(receive.getJMSMessageID());
			System.out.println("Message received from request is: " + receive.getText());
			
			JMSProducer replyProducer = jmsContext.createProducer();
			TextMessage replyMessage = jmsContext.createTextMessage("You are awesome.");
			replyMessage.setJMSCorrelationID(receive.getJMSMessageID());
			replyProducer.send(receive.getJMSReplyTo(), replyMessage);
			
			// This is consumed by Request Queue
			JMSConsumer replyConsumer = jmsContext.createConsumer(replyQueue);
			TextMessage replyReceived = (TextMessage) replyConsumer.receive();
			System.out.println("Message received from reply is: " + replyReceived.getText());
			System.out.println(hashMap.get(replyMessage.getJMSCorrelationID()));
		}
	}
}