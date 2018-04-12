package eu.europeana.direct.helpers;

import eu.europeana.direct.backend.model.EuropeanaDataObjectRoleType;
import eu.europeana.direct.legacy.index.LuceneDocumentType;

public class LogicHelper {

	/**
	 * Maps EuropeanaDataObjectRoleType to LuceneDocumentType
	 * @param edoRoleType EuropeanaDataObjectRoleType of entity
	 * @return LuceneDocumentType type
	 */
	public static LuceneDocumentType edoRoleType2LuceneType(EuropeanaDataObjectRoleType edoRoleType){
		
		if(edoRoleType != null){
			switch(edoRoleType){
				case Agent:
					return LuceneDocumentType.Agent;					
				case Concept:
					return LuceneDocumentType.Concept;					
				case Location:
					return LuceneDocumentType.Place;
				case Timespan:
					return LuceneDocumentType.Timespan;
			}
		}	
		return null;
	}	
	
	public static LuceneDocumentType stringType2LuceneType(String type){
		
		if(type != null && type.length() > 0){
			switch (type.trim().toLowerCase()) {
			case "agent":
				return LuceneDocumentType.Agent;
			case "concept":
				return LuceneDocumentType.Concept;
			case "timespan":
				return LuceneDocumentType.Timespan;
			case "place":
				return LuceneDocumentType.Place;
			case "weblink":			
				return LuceneDocumentType.Weblink;				
			}
		}
		return null;
	}
}
