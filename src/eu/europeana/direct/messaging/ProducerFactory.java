package eu.europeana.direct.messaging;

import eu.europeana.direct.messaging.add.AddProducer;
import eu.europeana.direct.messaging.delete.DeleteProducer;

public class ProducerFactory {
	
	// Producer for sending message through JMS queue to index CHO object 
	private static final AddProducer producer = new AddProducer();
	// Producer for sending message through JMS queue to delete CHO(and related entities) from index
	private static final DeleteProducer deleteProducer = new DeleteProducer();
	
	public static DeleteProducer getDeleteProducer(){
		return deleteProducer;
	}
	
	public static AddProducer getAddProducer(){
		return producer;
	}
}
