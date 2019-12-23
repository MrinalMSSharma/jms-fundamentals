package com.org.jms.messagestructure;

import javax.jms.BytesMessage;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.StreamMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class MessageTypesDemo {

	public static void main(String[] args) throws NamingException, JMSException {

		InitialContext context = new InitialContext();
		Queue queue = (Queue) context.lookup("queue/myQueue");
		
		try(ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory();
				JMSContext jmsContext =  cf.createContext()){
			JMSProducer producer = jmsContext.createProducer();
			BytesMessage bytesMessage = jmsContext.createBytesMessage();
			bytesMessage.writeUTF("Mrinal");
			bytesMessage.writeLong(123);
			
			StreamMessage streamMessage = jmsContext.createStreamMessage();
			streamMessage.writeBoolean(true);
			streamMessage.writeFloat(2.5f);
			
			MapMessage mapMessage = jmsContext.createMapMessage();
			mapMessage.setBoolean("Boolean", true);
			mapMessage.setString("String", "Mrinal");
			
			ObjectMessage objectMessage = jmsContext.createObjectMessage();
			Patient patient = new Patient();
			patient.setId(10);
			patient.setName("Mrinal");
			objectMessage.setObject(patient);
			
			producer.send(queue, patient);
//			producer.send(queue, streamMessage);
//			producer.send(queue, bytesMessage);
			
//			BytesMessage message2 = (BytesMessage) jmsContext.createConsumer(queue).receive(5000);
//			System.out.println(message2.readUTF());
//			System.out.println(message2.readLong());
//			StreamMessage receive = (StreamMessage) jmsContext.createConsumer(queue).receive(5000);
//			System.out.println("Message received is: " + receive);
//			System.out.println(receive.readBoolean());
//			System.out.println(receive.readFloat());
//			MapMessage message = (MapMessage) jmsContext.createConsumer(queue).receive();
//			System.out.println(message.getBoolean("Boolean"));
//			System.out.println(message.getString("String"));
			ObjectMessage message = (ObjectMessage) jmsContext.createConsumer(queue).receive();
			Patient object = (Patient) message.getObject();
			System.out.println(object.getId());
			System.out.println(object.getName());
		}
	}
}