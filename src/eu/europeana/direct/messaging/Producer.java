package eu.europeana.direct.messaging;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public abstract class Producer {

	private static String QUEUE = "";
	private static String CONNECTION_FACTORY = "";    
	private InitialContext context;
	private Queue queue;
	private QueueConnectionFactory conFactory;
	private QueueConnection queueConnection;
	private QueueSession queueSession;
	private QueueSender queueSender;
	
	
	public Producer(String queue, String conFactory){
		QUEUE = queue;
		CONNECTION_FACTORY = conFactory;
		init();
	}		
	
	private void init(){
		try {			
			// get the initial context
			context = new InitialContext();			
			// lookup the queue object
			queue = (Queue) context.lookup(QUEUE);

			// lookup the queue connection factory
			conFactory = (QueueConnectionFactory) context.lookup(CONNECTION_FACTORY);

		} catch (NamingException ne) {
			ne.printStackTrace();
		}
	 }
	 
	 public void createConnection(){
		try {
			// create a queue connection
			queueConnection = conFactory.createQueueConnection();

			// create a queue session
			queueSession = queueConnection.createQueueSession(false, Session.DUPS_OK_ACKNOWLEDGE);

			// create a queue sender
			queueSender = queueSession.createSender(queue);
			queueSender.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}                                                                                       	
	 }
	 
	 public void closeConnection(){	    	
		try {
			if (queueConnection != null) {
				queueConnection.close();
			}
			if (queueSession != null) {
				queueSession.close();
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	    	
	 }

	public QueueSession getQueueSession() {
		return queueSession;
	}
	
	public QueueSender getQueueSender() {
		return queueSender;
	}
	 
}
