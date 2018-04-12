package eu.europeana.direct.legacy.logic.search;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.codec.binary.Base64;

import eu.europeana.direct.legacy.helpers.DateParser;
import eu.europeana.direct.legacy.model.search.AgentItemLanguageAware;
import eu.europeana.direct.legacy.model.search.ConceptItemLanguageAware;
import eu.europeana.direct.legacy.model.search.Item;
import eu.europeana.direct.legacy.model.search.PlaceItemLanguageAware;
import eu.europeana.direct.legacy.model.search.TimespanItemLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLangaugeAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

public class SearchCulturalHeritageObjectLogic implements LegacySearchChoMapper,LegacySearchEntitiesMapper{
	private DateParser dateParser = new DateParser();		
	

	@Override
	public Item ApiModelToSearchModel(CulturalHeritageObject cho) {

		Item i = new Item();
		if(cho.getId() != null){
			i.setId("/direct/"+cho.getId());
			//i.setLink("https://europeana-direct.semantika.eu/EuropeanaDirect/api/v2/record/direct/"+cho.getId());		
			i.setLink("http://data.jewisheritage.net/rest/api/v2/record/direct/"+cho.getId());
		}
		
		if(cho.getLanguageNonAwareFields().getDataOwner() != null){
			String[] array = new String[] {cho.getLanguageNonAwareFields().getDataOwner()};
			i.setDataProvider(array);
		}
		
		List<String> landingPageList = new ArrayList<String>();
		List<String> shownAtList = new ArrayList<String>();
		List<String> countryList = new ArrayList<>();
		for(KeyValuePair kv : cho.getLanguageNonAwareFields().getCustomFields()){
			
			if(kv.getKey().contains("setSpec")){
				i.setSetSpec(kv.getValue());
			}			
			if(kv.getKey().contains("landingPage")){
				landingPageList.add(kv.getValue());
			}
			if(kv.getKey().contains("isShownAt")){
				shownAtList.add(kv.getValue());
			}
			if(kv.getKey().contains("edmPreview")){
				//String edmPreview = "http://europeanastatic.eu/api/image?uri=" + kv.getValue() + "&size=LARGE&type=IMAGE";
				//byte[] encodedImageUri = Base64.encodeBase64(kv.getValue().getBytes());					
				//String edmPreview = "http://data.jewisheritage.net/rest/api/v2/thumbnail-by-url.json?uri=" + new String(encodedImageUri) + "&size=LARGE&type=IMAGE";				
				if(i.getEdmPreview() == null){					
					i.setEdmPreview(kv.getValue());	
				}				
			}
			if(kv.getKey().equals("createdString")){
				i.setTimestamp_created(kv.getValue());
			}
			if(kv.getKey().equals("modifiedString")){
				i.setTimestamp_update(kv.getValue());
			}
			if(kv.getKey().equals("createdEpoch")){
				i.setTimestamp_created_epoch(Double.parseDouble(kv.getValue()));
			}
			if(kv.getKey().equals("modifiedEpoch")){
				i.setTimestamp_update_epoch(Double.parseDouble(kv.getValue()));
			}
			if(kv.getKey().contains("country")){
				countryList.add(kv.getValue());
			}
		}	
		if(landingPageList.size() > 0){
			i.setGuid(landingPageList.get(0));
		}
		i.setCountry(countryList.toArray(new String[countryList.size()]));
		i.setEdmLandingPage(landingPageList.toArray(new String[landingPageList.size()]));
		i.setEdmIsShownAt(shownAtList.toArray(new String[shownAtList.size()]));
		
		List<String> listTitle = new ArrayList<String>();
		List<String> listLanguage = new ArrayList<String>();
		List<String> listDescription = new ArrayList<String>();
		List<String> yearsList = new ArrayList<String>();

		for(CulturalHeritageObjectLanguageAware langAware : cho.getLanguageAwareFields()){
			if(langAware.getTitle() != null)
				listTitle.add(langAware.getTitle());			
			if(!langAware.getAlternative().isEmpty())
				listTitle.addAll(langAware.getAlternative());
			if(langAware.getLanguage() != null)
				listLanguage.add(langAware.getLanguage());
			if(langAware.getDescription() != null)
				listDescription.add(langAware.getDescription());
			
			for(KeyValuePair kv : langAware.getCustomFields()){
				if(kv.getKey().contains("createdChoString")){
					yearsList.add(kv.getValue());
				}
			}			
		}						
		
		List<String> actualYears = new ArrayList<String>();
		for(String year : yearsList){
			Date d = dateParser.tryParse(year); 
			
			if(d == null){
				actualYears.add(year);	
			}	
		}
		i.setYear(actualYears.toArray(new String[actualYears.size()]));
		
		List<String> rightsList = new ArrayList<String>();
		for(WebLink wl : cho.getWebLinks()){
			if(wl.getRights() != null)
				rightsList.add(wl.getRights());		
		}
		
		List<String> longitudeList = new ArrayList<String>();
		List<String> latitudeList = new ArrayList<String>();
		List<String> placeLabelList = new ArrayList<String>();
		
		for(Place p : cho.getSpatial()){
			if(p.getLanguageNonAwareFields().getLatitude() != null){
				latitudeList.add(""+p.getLanguageNonAwareFields().getLatitude());
			}
			if(p.getLanguageNonAwareFields().getLongitude() != null){
				longitudeList.add(""+p.getLanguageNonAwareFields().getLongitude());
			}
						
			for(PlaceLanguageAware plangAware : p.getLanguageAwareFields()){				
				if(plangAware.getPreferredLabel() != null){
					if(!placeLabelList.contains(plangAware.getPreferredLabel())){
						placeLabelList.add(plangAware.getPreferredLabel());	
					}					
				}					
			}			
		}
		
		List<String> timespanlabelList = new ArrayList<>();
		List<String> timespanBeginList = new ArrayList<>();
		List<String> timespanEndList = new ArrayList<>();
		for(TimeSpan s : cho.getTemporal()){
			for(TimeSpanLangaugeAware sAware : s.getLanguageAwareFields()){
				if(sAware.getPreferredLabel() != null){
					timespanlabelList.add(sAware.getPreferredLabel());	
				}				
			}
			if(s.getLanguageNonAwareFields().getBegin() != null){
				timespanBeginList.add(s.getLanguageNonAwareFields().getBegin());	
			}				
			if(s.getLanguageNonAwareFields().getEnd() != null){
				timespanEndList.add(s.getLanguageNonAwareFields().getEnd());	
			}				
			break;
		}
		
		if(!timespanlabelList.isEmpty()){
			i.setEdmTimespanLabel(timespanlabelList.get(0));
		}
		
		List<String> agentLabelList = new ArrayList<>();
		for(Agent a : cho.getAgents()){
			for(AgentLanguageAware agentLangAware : a.getLanguageAwareFields()){
				if(agentLangAware.getPreferredLabel() != null){
					if(!agentLabelList.contains(agentLangAware)){
						if(agentLangAware.getPreferredLabel() != null){
							agentLabelList.add(agentLangAware.getPreferredLabel());	
						}						
					}					
				}
			}			
			break;
		}
		
		List<String> conceptlabelList = new ArrayList<>();
		for(Concept c : cho.getConcepts()){
			for(ConceptLanguageAware conceptLangAware : c.getLanguageAwareFields()){
				if(conceptLangAware.getPreferredLabel() != null){
					if(!conceptlabelList.contains(conceptLangAware.getPreferredLabel())){
						if(conceptLangAware.getPreferredLabel() != null){
							conceptlabelList.add(conceptLangAware.getPreferredLabel());	
						}						
					}					
				}
			}
			break;
		}
		
		if(cho.getLanguageNonAwareFields().getType() != null){
			i.setType(cho.getLanguageNonAwareFields().getType().toString());	
		}
		
		i.setEdmConceptPrefLabel(conceptlabelList.toArray(new String[conceptlabelList.size()]));
		i.setEdmAgentLabel(agentLabelList.toArray(new String[agentLabelList.size()]));
		i.setEdmTimespanBegin(timespanBeginList.toArray(new String[timespanBeginList.size()]));
		i.setEdmTimespanEnd(timespanEndList.toArray(new String[timespanEndList.size()]));
		i.setEdmPlaceAltLabel(placeLabelList.toArray(new String[placeLabelList.size()]));
		i.setEdmPlaceLatitude(latitudeList.toArray(new String[latitudeList.size()]));
		i.setEdmPlaceLongitude(longitudeList.toArray(new String[longitudeList.size()]));
		i.setRights(rightsList.toArray(new String[rightsList.size()]));
		i.setDcDescription(listDescription.toArray(new String[listDescription.size()]));
		i.setTitle(listTitle.toArray(new String[listTitle.size()]));		
		i.setLanguage(listLanguage.toArray(new String[listLanguage.size()]));		
		
		return i;
	}
	@Override
	public Item WeblinkToSearchModel(WebLink weblink) {
	Item i = new Item();
		
		if(weblink.getId() != null){
			i.setId(weblink.getId().toString());
		}
		
		if(weblink.getLink() != null){
			i.setLink(weblink.getLink());
		}
		
		if(weblink.getOwner() != null){
			i.setOwner(weblink.getOwner());
		}
		
		if(weblink.getRights() != null){
			String[] rightsArr = new String[]{weblink.getRights()};
			i.setRights(rightsArr);
		}
		
		if(weblink.getType() != null){
			i.setType(weblink.getType().toString());
		}
		
		return i;
	}

	@Override
	public Item PlaceToSearchModel(Place place) {
		Item i = new Item();		
		
		if(place.getId() != null){
			i.setId(place.getId().toString());
		}
		
		if(place.getLanguageNonAwareFields().getLatitude() != null){
			String[] latitude = new String[]{""+place.getLanguageNonAwareFields().getLatitude()};			
			i.setEdmPlaceLatitude(latitude);
		}
		if(place.getLanguageNonAwareFields().getLongitude() != null){
			String[] longitude = new String[]{""+place.getLanguageNonAwareFields().getLongitude()};			
			i.setEdmPlaceLongitude(longitude);
		}
		if(place.getLanguageNonAwareFields().getAltitude() != null){
			String[] altitude = new String[]{""+place.getLanguageNonAwareFields().getAltitude()};			
			i.setAltitude(altitude);
		}
		
		List<PlaceItemLanguageAware> placeItemLangAwareList = new ArrayList<>();
		for(PlaceLanguageAware placeLangAware : place.getLanguageAwareFields()){
			PlaceItemLanguageAware placeItemLang = new PlaceItemLanguageAware();
			
			if(placeLangAware.getLanguage() != null)
				placeItemLang.setLanguage(placeLangAware.getLanguage());
			
			if(placeLangAware.getPreferredLabel() != null)
				placeItemLang.setPreferredLabel(placeLangAware.getPreferredLabel());
			
			if(placeLangAware.getAlternativeLabel().size() > 0)
				placeItemLang.setAlternativeLabel(placeLangAware.getAlternativeLabel().toArray(new String[placeLangAware.getAlternativeLabel().size()]));
						
			List<String> list = new ArrayList<>();
			for(KeyValuePair kv : placeLangAware.getCustomFields()){
				if(kv.getKey().contains("note")){
					list.add(kv.getValue());
				}
			}
			placeItemLang.setNote(list.toArray(new String[list.size()]));
			placeItemLangAwareList.add(placeItemLang);
		}
		i.setPlaceLanguageAwareFields(placeItemLangAwareList.toArray(new PlaceItemLanguageAware[placeItemLangAwareList.size()]));
		
		return i;
	}

	@Override
	public Item TimespanToSearchModel(TimeSpan ts) {
		Item i = new Item();
		
		if(ts.getId() != null){
			i.setId(ts.getId().toString());		
		}	
		
		List<TimespanItemLanguageAware> timespanItemLangAware = new ArrayList<>();
		for(TimeSpanLangaugeAware timespanLang : ts.getLanguageAwareFields()){
			TimespanItemLanguageAware timespanItemLang = new TimespanItemLanguageAware();
			
			if(timespanLang.getLanguage() != null)
				timespanItemLang.setLanguage(timespanLang.getLanguage());
			
			if(timespanLang.getPreferredLabel() != null)
				timespanItemLang.setPreferredLabel(timespanLang.getPreferredLabel());
			
			if(timespanLang.getAlternativeLabel().size() > 0)
				timespanItemLang.setAlternativeLabel(timespanLang.getAlternativeLabel().toArray(new String[timespanLang.getAlternativeLabel().size()]));
						
			timespanItemLangAware.add(timespanItemLang);
		}
		i.setTimespanLanguageAwareFields(timespanItemLangAware.toArray(new TimespanItemLanguageAware[timespanItemLangAware.size()]));		
		return i;
	}


	@Override
	public Item ConceptToSearchModel(Concept c) {
	Item i = new Item();
		
		if(c.getId() != null){
			i.setId(c.getId().toString());		
		}	
		
		List<ConceptItemLanguageAware> conceptItemLangAwareList = new ArrayList<>();
		for(ConceptLanguageAware conceptLangAware : c.getLanguageAwareFields()){
			ConceptItemLanguageAware conceptItemLangAware = new ConceptItemLanguageAware();
			
			if(conceptLangAware.getLanguage() != null)
				conceptItemLangAware.setLanguage(conceptLangAware.getLanguage());
			
			if(conceptLangAware.getPreferredLabel() != null)
				conceptItemLangAware.setPreferredLabel(conceptLangAware.getPreferredLabel());
			
			if(conceptLangAware.getAlternativeLabel().size() > 0)
				conceptItemLangAware.setAlternativeLabel(conceptLangAware.getAlternativeLabel().toArray(new String[conceptLangAware.getAlternativeLabel().size()]));
						
			conceptItemLangAwareList.add(conceptItemLangAware);
		}
		i.setConceptLanguageAwareFields(conceptItemLangAwareList.toArray(new ConceptItemLanguageAware[conceptItemLangAwareList.size()]));		
		return i;
	}
	@Override
	public Item AgentToSearchModel(Agent a) {
	Item i = new Item();
		
		if(a.getId() != null){
			i.setId(a.getId().toString());		
		}	
		if(a.getLanguageNonAwareFields().getDateOfBirth() != null){
			i.setDateOfBirth(a.getLanguageNonAwareFields().getDateOfBirth());
		}
		if(a.getLanguageNonAwareFields().getDateOfDeath() != null){
			i.setDateOfDeath(a.getLanguageNonAwareFields().getDateOfDeath());
		}
		if(a.getLanguageNonAwareFields().getDateOfEstablishment() != null){
			i.setDateOfEstablishment(a.getLanguageNonAwareFields().getDateOfEstablishment());
		}
		if(a.getLanguageNonAwareFields().getDateOfTermination() != null){
			i.setDateOfTermination(a.getLanguageNonAwareFields().getDateOfTermination());
		}
		
		List<AgentItemLanguageAware> agentItemLangAwareList = new ArrayList<>();
		for(AgentLanguageAware agentLangAware : a.getLanguageAwareFields()){
			AgentItemLanguageAware agentItemLangAware = new AgentItemLanguageAware();
			
			if(agentLangAware.getLanguage() != null)
				agentItemLangAware.setLanguage(agentLangAware.getLanguage());
			
			if(agentLangAware.getPreferredLabel() != null)
				agentItemLangAware.setPreferredLabel(agentLangAware.getPreferredLabel());
			
			if(agentLangAware.getAlternativeLabel().size() > 0)
				agentItemLangAware.setAlternativeLabel(agentLangAware.getAlternativeLabel().toArray(new String[agentLangAware.getAlternativeLabel().size()]));
			
			if(agentLangAware.getGender() != null)
				agentItemLangAware.setGender(agentLangAware.getGender());
			
			if(agentLangAware.getBiographicalInformation() != null)
				agentItemLangAware.setBiographicalInformation(agentLangAware.getBiographicalInformation());
			
			if(agentLangAware.getProfessionOrOccupation() != null)
				agentItemLangAware.setProfessionOrOccupation(agentLangAware.getProfessionOrOccupation());
				
			if(agentLangAware.getPlaceOfBirth() != null)
				agentItemLangAware.setPlaceOfBirth(agentLangAware.getPlaceOfBirth());
			
			if(agentLangAware.getPlaceOfDeath() != null)
				agentItemLangAware.setPlaceOfDeath(agentLangAware.getPlaceOfDeath());
			
			if(agentLangAware.getIdentifier().size() > 0)
				agentItemLangAware.setIdentifier(agentLangAware.getIdentifier().toArray(new String[agentLangAware.getIdentifier().size()]));
			
			if(agentLangAware.getSameAs().size() > 0)
				agentItemLangAware.setSameAs(agentLangAware.getIdentifier().toArray(new String[agentLangAware.getIdentifier().size()]));			
			
			agentItemLangAwareList.add(agentItemLangAware);
		}
		i.setAgentLanguageAwareFields(agentItemLangAwareList.toArray(new AgentItemLanguageAware[agentItemLangAwareList.size()]));		
		return i;
	}
}
