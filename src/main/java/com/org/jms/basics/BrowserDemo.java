package com.org.jms.basics;

import java.util.Enumeration;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class BrowserDemo {

	@SuppressWarnings("rawtypes")
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
			TextMessage message1 = session.createTextMessage("I am creator of my destiny 1");
			TextMessage message2 = session.createTextMessage("I am creator of my destiny 2");
			createProducer.send(message1);
			createProducer.send(message2);
			
			QueueBrowser browser = session.createBrowser(queue);
			Enumeration enumeration = browser.getEnumeration();
			while(enumeration.hasMoreElements()) {
				TextMessage nextElement = (TextMessage) enumeration.nextElement();
				System.out.println("Browsing " + nextElement.getText());
			}
			
			MessageConsumer createConsumer = session.createConsumer(queue);
			connection.start();
			TextMessage receive1 = (TextMessage) createConsumer.receive(5000);
			System.out.println("Message received is :" + receive1.getText());
			
			TextMessage receive2 = (TextMessage) createConsumer.receive(5000);
			System.out.println("Message received is :" + receive2.getText());
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