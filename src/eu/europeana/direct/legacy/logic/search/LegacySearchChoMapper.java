package eu.europeana.direct.legacy.logic.search;

import eu.europeana.direct.legacy.model.search.Item;


public interface LegacySearchChoMapper {

	/**
	 * Method maps Operation Direct API CHO to search api model
	 * @param cho Operation Direct API Cultural heritage object
	 * 
	 */
	Item ApiModelToSearchModel(eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject cho);
}
