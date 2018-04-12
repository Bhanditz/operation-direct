package eu.europeana.direct.messaging.add;

import java.text.SimpleDateFormat;
import java.util.Date;

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
import eu.europeana.direct.messaging.delete.DeleteProducer;

public class AddProducer extends Producer{	

	private static final String QUEUE = "java:/jms/queue/ExpiryQueue";		
	private static final String CONNECTION_FACTORY = "java:/ConnectionFactory"; 
	// JHN
//	private static final String QUEUE = "java:/jms/queue/JHNExpiryQueue";		
//	private static final String CONNECTION_FACTORY = "java:/JHNConnectionFactory";
	
	private final static Logger logger = Logger.getLogger(AddProducer.class);	
	
	public AddProducer(String queue, String conFactory) {
		super(queue, conFactory);
	}

	public AddProducer(){
		super(QUEUE, CONNECTION_FACTORY);		
	}
	
	public void enqueue(String objectId, String luceneDocType, boolean onlyRelated) {		
		try{
			createConnection();
			TextMessage msg = getQueueSession().createTextMessage();									
			
			if(msg != null){				
				msg.setStringProperty("objectId", objectId);												
				msg.setBooleanProperty("onlyRelated", onlyRelated);
				msg.setStringProperty("type", luceneDocType);
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
