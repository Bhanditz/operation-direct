package eu.europeana.direct.messaging;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;
import javax.jms.ConnectionFactory;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.cdi.ContextName;
import org.apache.camel.component.jms.JmsComponent;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.ExplicitCamelContextNameStrategy;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.omg.CORBA.MARSHAL;

import eu.europeana.direct.legacy.index.LuceneDocumentType;

@Singleton
@Startup
public class CamelESB {

	@Resource(mappedName = "java:jboss/DefaultJMSConnectionFactory")
	private ConnectionFactory connectionFactory;
			
	private CamelContext camelContext = null;
	
	@PostConstruct
	public void start() throws Exception{
		camelContext = new DefaultCamelContext();		
		camelContext.setNameStrategy(new ExplicitCamelContextNameStrategy("camel-context"));		
		
		JmsComponent component = new JmsComponent();
		component.setConnectionFactory(connectionFactory);
		camelContext.addComponent("jms", component);
		
		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				
				from("jms:queue:CamelQueue")				
					.choice()
					// route to JMS consumer for adding/updating index 
					.when(header("header").isEqualTo(MessageType.UPDATE_INDEX.toString()))
						.marshal().json(JsonLibrary.Jackson).to("jms:queue:UpdateIndex?jmsMessageType=Text")						
					// route to JMS consumer for deleting from index
					.when(header("header").isEqualTo(MessageType.DELETE_FROM_INDEX.toString()))
						.marshal().json(JsonLibrary.Jackson).to("jms:queue:DeleteIndex?jmsMessageType=Text");										
			}
		});
				
		camelContext.start();
	}
	
	@PreDestroy
	public void close() throws Exception{
		if(camelContext != null){
			camelContext.stop();	
		}		
	}
	
}
