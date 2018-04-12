package eu.europeana.direct.messaging.delete;

import java.io.IOException;
import java.util.Date;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.direct.backend.model.DeletedEntity;
import eu.europeana.direct.helpers.LogicHelper;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;

@MessageDriven(name = "DeleteConsumer", activationConfig = {
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "java:/jms/queue/DeleteIndex"),
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Dups-ok-acknowledge")})
public class DeleteConsumer implements MessageListener {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	//consumes message received from producer
	@Override
	public void onMessage(Message msg) {			
		if (msg instanceof TextMessage) {					
			try {						
				DeleteIndexMessage dim = OBJECT_MAPPER.readValue(((TextMessage)msg).getText(), DeleteIndexMessage.class);								
				// deleting related + CHO
				if(!dim.isRelatedEntity()){
					// deletes CHO object document from main index					
					LuceneIndexing.getInstance().deleteFromIndex(dim.getObjectId(), LuceneDocumentType.CulturalHeritageObject,true,true);					
					for(DeletedEntity de : dim.getDeletedEntities()){
						// deletes contextual entity document related to CHO from entity-index 
						LuceneIndexing.getInstance().deleteFromIndex(de.getId(), LogicHelper.edoRoleType2LuceneType(de.getEntityType()),true,true);
					}
					// at last also add new document to removed-records index
					LuceneIndexing.getInstance().addRemovedRecordDocument(dim.getObjectId());					
				}else{
					// deleting only contextual entity (not whole CHO)
					LuceneIndexing.getInstance().deleteFromIndex(dim.getObjectId(), dim.getDocType(),true,true);					
				}
				
			} catch (JMSException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
		}	
	}
}