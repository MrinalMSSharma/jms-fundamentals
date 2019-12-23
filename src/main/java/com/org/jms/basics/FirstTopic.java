package com.org.jms.basics;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FirstTopic {
	
	public static void main(String[] args) throws NamingException, JMSException {

		InitialContext initialContext = null;
		Connection connection = null;
		initialContext = new InitialContext();
		Topic topic = (Topic) initialContext.lookup("topic/myTopic");
		ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
		connection = connectionFactory.createConnection();
		Session session = connection.createSession();
		MessageProducer createProducer = session.createProducer(topic);
		
		MessageConsumer consumer1 = session.createConsumer(topic);
		MessageConsumer consumer2 = session.createConsumer(topic);
		
		TextMessage textMessage = session.createTextMessage("I am sending something");
		createProducer.send(textMessage);
		
		connection.start();
		TextMessage receive1 = (TextMessage) consumer1.receive();
		TextMessage receive2 = (TextMessage) consumer2.receive();
		
		System.out.println("Received message by 1 is : " + receive1.getText());
		System.out.println("Received message by 2 is : " + receive2.getText());
		
		connection.close();
		initialContext.close();
	}
}