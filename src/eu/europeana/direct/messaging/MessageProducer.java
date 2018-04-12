package eu.europeana.direct.messaging;

import javax.naming.InitialContext;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.cdi.ContextName;

public class MessageProducer {
		
	@ContextName("camel-context")	
	private static CamelContext camelContext;
	
    public static void sendMsg(Object msg, MessageType msgType) throws Exception {
    	    	
    	try{
    		initContext();
    		ProducerTemplate template = camelContext.createProducerTemplate();    		    		
    		template.sendBodyAndHeader("jms:queue:CamelQueue",msg, "header", msgType.toString());    		    	
    	} catch (Exception e){
    		e.printStackTrace();
    	}
    }		
    
    private static void initContext() throws Exception{    	    	    	
    	if(camelContext == null){    		
			camelContext = new InitialContext().doLookup("java:jboss/camel/context/camel-context");
    	}
    }
	
}
