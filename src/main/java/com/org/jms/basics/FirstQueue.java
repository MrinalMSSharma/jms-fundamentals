package com.org.jms.basics;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class FirstQueue {

	public static void main(String[] args) throws NamingException, JMSException {

		InitialContext initialContext = null;
		Connection connection = null;
		try {
			initialContext = new InitialContext();
			ConnectionFactory connectionFactory = (ConnectionFactory) initialContext.lookup("ConnectionFactory");
			connection = connectionFactory.createConnection();
			Session session = connection.createSession();
			Queue queue = (Queue) initialContext.lookup("queue/myQueue");
			MessageProducer createProducer = session.createProducer(queue);
			TextMessage message = session.createTextMessage("I am creator of my destiny");
			createProducer.send(message);
			System.out.println("Message sent : " + message.getText());
			
			MessageConsumer createConsumer = session.createConsumer(queue);
			connection.start();
			TextMessage receive = (TextMessage) createConsumer.receive(5000);
			System.out.println("Message received is :" + receive.getText());
		}
		finally {
			if(null != initialContext) {
				initialContext.close();
			}
			if(null != connection) {
				connection.close();
			}
		}
	}
}