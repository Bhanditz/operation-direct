package eu.europeana.direct.legacy.logic.helpers;

import org.apache.lucene.document.Document;

import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.logic.search.SearchCulturalHeritageObjectLogic;
import eu.europeana.direct.legacy.mapping.AgentMapper;
import eu.europeana.direct.legacy.mapping.ConceptMapper;
import eu.europeana.direct.legacy.mapping.CulturalHeritageObjectSearchMapper;
import eu.europeana.direct.legacy.mapping.PlaceMapper;
import eu.europeana.direct.legacy.mapping.TimespanMapper;
import eu.europeana.direct.legacy.mapping.WebLinkMapper;
import eu.europeana.direct.legacy.model.search.Item;
import eu.europeana.direct.legacy.model.search.ProfileType;

public class MappingHelper {

	private SearchCulturalHeritageObjectLogic searchChoLogic = new SearchCulturalHeritageObjectLogic();
	//entity mappers
	private CulturalHeritageObjectSearchMapper choSearchMapper = new CulturalHeritageObjectSearchMapper();
	private AgentMapper agentSearchMapper = new AgentMapper();
	private ConceptMapper conceptSearchMapper = new ConceptMapper();
	private PlaceMapper placeSearchMapper = new PlaceMapper();
	private TimespanMapper timespanSearchMapper = new TimespanMapper();
	private WebLinkMapper weblinkMapper = new WebLinkMapper();
	
	
	public Item documentToItem(Document doc, LuceneDocumentType documentType, ProfileType profile) {
		switch (documentType) {

		case CulturalHeritageObject:
			// First we map found document to CHO Operation Direct
			// model. Then from CHO Operation Direct model to CHO search
			// response model														
			return searchChoLogic.ApiModelToSearchModel(choSearchMapper.mapToSource(doc,profile));			
		case Agent:
			// First we map found document to Agent Operation Direct
			// model. Then from Agent Operation Direct model to Agent
			// search response model
			return searchChoLogic.AgentToSearchModel(agentSearchMapper.mapToSource(doc,profile));			
		case Concept:
			// First we map found document to Concept Operation Direct
			// model. Then from Concept Operation Direct model to
			// Concept search response model
			return searchChoLogic.ConceptToSearchModel(conceptSearchMapper.mapToSource(doc,profile));			
		case Place:
			// First we map found document to Place Operation Direct
			// model. Then from Place Operation Direct model to Place
			// search response model
			return searchChoLogic.PlaceToSearchModel(placeSearchMapper.mapToSource(doc,profile));			
		case Timespan:
			// First we map found document to Timespan Operation Direct
			// model. Then from Timespan Operation Direct model to
			// Timespan search response model
			return searchChoLogic.TimespanToSearchModel(timespanSearchMapper.mapToSource(doc,profile));			
		case Weblink:
			// First we map found document to Weblink Operation Direct
			// model. Then from Weblink Operation Direct model to
			// Weblink search response model
			return searchChoLogic.WeblinkToSearchModel(weblinkMapper.mapToSource(doc,profile));					
		}
		return null;
	}
		
	public static String modifySetName(String originalSetName){		
		// first replace multiple whitespaces with only one
		String modifiedName = originalSetName.trim().replaceAll(" +", " ");
		// replace every whitespace with underscore
		modifiedName = modifiedName.replace(" ", "_");
		// replace special characters
		modifiedName = modifiedName.replace("š", "s");
		modifiedName = modifiedName.replace("č", "c");
		modifiedName = modifiedName.replace("ž", "z");
		modifiedName = modifiedName.replace("Š", "S");
		modifiedName = modifiedName.replace("Č", "C");
		modifiedName = modifiedName.replace("Ž", "Z");
		
		return modifiedName.toLowerCase();
	}	
		
}
