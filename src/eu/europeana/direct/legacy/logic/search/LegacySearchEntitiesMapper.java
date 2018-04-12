package eu.europeana.direct.legacy.logic.search;

import eu.europeana.direct.legacy.model.search.Item;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

public interface LegacySearchEntitiesMapper {

	/**
	 * Method maps Operation Direct API WebLink entity to search api model
	 * @param weblink Operation Direct API WebLink
	 * 
	 */
	Item WeblinkToSearchModel(WebLink weblink);
	/**
	 * Method maps Operation Direct API Place entity to search api model
	 * @param place Operation Direct API Place	 
	 */
	Item PlaceToSearchModel(Place place);
	
	/**
	 * Method maps Operation Direct API TimeSpan entity to search api model
	 * @param ts Operation Direct API TimeSpan
	 */
	Item TimespanToSearchModel(TimeSpan ts);
	
	/**
	 * Method maps Operation Direct API Concept entity to search api model
	 * @param c Operation Direct API Concept
	 */
	Item ConceptToSearchModel(Concept c);
	
	/**
	 * Method maps Operation Direct API Agent entity to search api model
	 * @param a Operation Direct API Agent	 
	 */
	Item AgentToSearchModel(Agent a);			
}
