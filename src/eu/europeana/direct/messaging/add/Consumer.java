package eu.europeana.direct.messaging.add;

import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.direct.helpers.LogicHelper;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;

@MessageDriven(name = "ConsumerMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/UpdateIndex"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Dups-ok-acknowledge")})
public class Consumer implements MessageListener {
    	
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	
	//consumes message received from producer
	@Override
	public void onMessage(Message msg) {			
		if (msg instanceof TextMessage) {
			try {														
				IndexMessage indexMsg = OBJECT_MAPPER.readValue(((TextMessage)msg).getText(), IndexMessage.class);				
				long objectId = indexMsg.getObjectId();
			
				if(indexMsg.isOnlyRelated()){
					String entityType = indexMsg.getDocumentType();
					LuceneIndexing.getInstance().updateEntityDocument(objectId, true, null, LogicHelper.stringType2LuceneType(entityType), 0, null,true);	
				}else{
					// update document for new/updated CHO						
					LuceneIndexing.getInstance().updateLuceneIndex(objectId, true, true, null,true);	
				}				
			} catch (final JMSException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		}		
	}
}