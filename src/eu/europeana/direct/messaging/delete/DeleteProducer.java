package eu.europeana.direct.messaging.delete;

import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.log4j.Logger;

import eu.europeana.direct.messaging.Producer;
import eu.europeana.direct.messaging.add.IndexMessage;

public class DeleteProducer extends Producer{
	
	private static final String QUEUE = "java:/jms/queue/DLQ";			
	private static final String CONNECTION_FACTORY = "java:/DeleteIndexCF";
	// JHN
//	private static final String QUEUE = "java:/jms/queue/JHNDeletionQueue";
//	private static final String CONNECTION_FACTORY = "java:/JHNDeletionFactory";
	
	public DeleteProducer(String queue, String conFactory) {
		super(queue, conFactory);		
	}

	public DeleteProducer(){
		super(QUEUE, CONNECTION_FACTORY);		
	}
	
	private final static Logger logger = Logger.getLogger(DeleteProducer.class);	
	
	public void enqueue(DeleteIndexMessage dim) {		
		try{
			createConnection();
			ObjectMessage msg = getQueueSession().createObjectMessage();
						
			if(msg != null){			
				msg.setObject(dim);
				getQueueSender().setDisableMessageID(true);
				getQueueSender().setDisableMessageTimestamp(true);						
				getQueueSender().send(msg);				
			}
			
		} catch (JMSException e) {
			logger.error("JMS Exception "+e.getMessage(),e);
			e.printStackTrace();
		} catch (Exception e){
			e.printStackTrace();
			logger.error("Exception "+e.getMessage(),e);
		} finally {
			closeConnection();	
		}  	
	}
}
