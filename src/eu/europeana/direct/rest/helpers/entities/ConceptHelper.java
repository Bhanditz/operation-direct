package eu.europeana.direct.rest.helpers.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public class ConceptHelper {

	public void sortByLanguage(CulturalHeritageObject cho, List<Concept> concepts, String language) {
		
		// go through concepts and check if any of them has the same language value as specified in request parameter
		List<ConceptLanguageAware> conceptAwareList = new ArrayList<ConceptLanguageAware>();							
		for(int i=0; i < cho.getConcepts().size(); i++){															
			if(cho.getConcepts().get(i).getLanguageAwareFields().stream().anyMatch(t -> t.getLanguage().equals(language))){
				// find all lang aware object that match the language parameter
				conceptAwareList.addAll(cho.getConcepts().get(i).getLanguageAwareFields().stream().filter(co -> co.getLanguage().equals(language)).collect(Collectors.toList()));									
				//clear concept lang aware object								
				cho.getConcepts().get(i).getLanguageAwareFields().clear();								
				// add only concept lang aware objects that match the language parameter
				cho.getConcepts().get(i).getLanguageAwareFields().addAll(conceptAwareList);
			}else{
				// remove concept from cho response
				cho.getConcepts().remove(i);
			}																								
		}
		
	}

}
