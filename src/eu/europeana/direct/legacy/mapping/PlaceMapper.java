package eu.europeana.direct.legacy.mapping;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;

import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.legacy.model.search.ProfileType;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageNonAware;

public class PlaceMapper implements EntityIndexingMapper<Place> {

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Document mapFromSource(Place place, Date created, Set<EuropeanaDataObjectEuropeanaDataObject> linkedObjects) {
		Document doc = new Document();

		if(place.getId() != null){
			doc.add(new TextField("placeIdString", "" + place.getId(), Field.Store.YES));
		}	
		
		if(created == null){
			created = new Date();
		}
		
		doc.add(new TextField("createdString", simpleDateFormat.format(created), Field.Store.YES));		
		doc.add(new LongPoint("created", created.getTime()));	
		doc.add(new TextField("modifiedString", simpleDateFormat.format(new Date()), Field.Store.YES));		
		doc.add(new LongPoint("modified", new Date().getTime()));
		
		for(EuropeanaDataObjectEuropeanaDataObject edoedo : linkedObjects){		
			if(edoedo.getEuropeanaDataObjectByLinkingObjectId().getCulturalHeritageObject().getLanguageNonAwareFields().getDataowner() != null){
				doc.add(new TextField("dataOwner",edoedo.getEuropeanaDataObjectByLinkingObjectId().getCulturalHeritageObject().getLanguageNonAwareFields().getDataowner(), Field.Store.YES));	
			}
			doc.add(new TextField("choIdString",""+edoedo.getEuropeanaDataObjectByLinkingObjectId().getCulturalHeritageObject().getId(), Field.Store.YES));			
		}
		
		doc.add(new TextField("object_type","Place",Field.Store.YES));

		if(place.getLanguageNonAwareFields().getLatitude() != null){
			doc.add(new TextField("latitudeString", "" + place.getLanguageNonAwareFields().getLatitude(), Field.Store.YES));
			doc.add(new DoublePoint("latitude", place.getLanguageNonAwareFields().getLatitude()));			
		}
		
		if(place.getLanguageNonAwareFields().getAltitude() != null){
			doc.add(new TextField("altitudeString", "" + place.getLanguageNonAwareFields().getAltitude(), Field.Store.YES));			
			doc.add(new DoublePoint("altitude", place.getLanguageNonAwareFields().getAltitude()));
		}
		if(place.getLanguageNonAwareFields().getLongitude() != null){
			doc.add(new TextField("longitudeString", "" + place.getLanguageNonAwareFields().getLongitude(), Field.Store.YES));
			doc.add(new DoublePoint("longitude", place.getLanguageNonAwareFields().getLongitude()));			
		}
		
		for(PlaceLanguageAware pLangAware : place.getLanguageAwareFields()){
			
			String lang = pLangAware.getLanguage();
			doc.add(new TextField("placeLanguage",lang, Field.Store.YES));
			doc.add(new FacetField("language",lang));
			doc.add(new FacetField("language", lang));
			
			if(pLangAware.getPreferredLabel() != null && pLangAware.getPreferredLabel().length() > 0){
				doc.add(new TextField("placePreferredLabel_"+lang,pLangAware.getPreferredLabel(), Field.Store.YES));				
				doc.add(new TextField("placePreferredLabel_index",pLangAware.getPreferredLabel(), Field.Store.YES));
			}
			
			if(pLangAware.getAlternativeLabel() != null && pLangAware.getAlternativeLabel().size() > 0){
				for(String a : pLangAware.getAlternativeLabel()){
					doc.add(new TextField("placeAlternativeLabel_"+lang, a, Field.Store.YES));
					doc.add(new TextField("placeAlternativeLabel_index", a, Field.Store.YES));					
				}
			}
			
			if(pLangAware.getCustomFields() != null && pLangAware.getCustomFields().size() > 0){
				for(KeyValuePair kv : pLangAware.getCustomFields()){					
					if(kv.getKey().contains("note")){
						doc.add(new TextField("note_"+lang, kv.getValue(), Field.Store.YES));
						doc.add(new TextField("note_index", kv.getValue(), Field.Store.YES));						
					}					
				}
			}			
		}		
		return doc;
	}

	@Override
	public Place mapToSource(Document luceneDocument, ProfileType profile) {
		Place place = new Place();
		PlaceLanguageNonAware pNonAware = new PlaceLanguageNonAware();
		
		if(luceneDocument.get("placeIdString") != null){
			place.setId(new BigDecimal(luceneDocument.get("placeIdString")));
		}		
		if(luceneDocument.get("latitudeString") != null){
			double lat = Double.parseDouble(luceneDocument.get("latitudeString"));
			pNonAware.setLatitude(lat);
		}

		if(luceneDocument.get("longitudeString") != null){
			double longi = Double.parseDouble(luceneDocument.get("longitudeString"));
			pNonAware.setLongitude(longi);
		}

		if(luceneDocument.get("altitudeString") != null){
			double alt = Double.parseDouble(luceneDocument.get("altitudeString"));
			pNonAware.setAltitude(alt);
		}
		
		for(int i=0; i < luceneDocument.getValues("placeLanguage").length;i++){
			PlaceLanguageAware pLangAware = new PlaceLanguageAware();
			String lang = luceneDocument.getValues("placeLanguage")[i];
			pLangAware.setLanguage(lang);
			
			if(luceneDocument.get("placePreferredLabel_"+lang) != null){
				pLangAware.setPreferredLabel(luceneDocument.get("placePreferredLabel_"+lang));
			}
			
			if(luceneDocument.get("placeAlternativeLabel_"+lang) != null){
				pLangAware.setAlternativeLabel(Arrays.asList(luceneDocument.getValues("placeAlternativeLabel_"+lang)));
			}
			
			if(luceneDocument.get("note_"+lang) != null){				
				pLangAware.getCustomFields().add(new KeyValuePair("note "+(++i),luceneDocument.get("note_"+lang)));								
			}
		
			place.getLanguageAwareFields().add(pLangAware);
		}
		place.setLanguageNonAwareFields(pNonAware);
		return place;
	}

	@Override
	public void configureFacets(FacetsConfig config) {
		config.setMultiValued("language", true);
		config.setMultiValued("latitude", true);
		config.setMultiValued("longitude", true);
		config.setMultiValued("altitude", true);		
		config.setMultiValued("placePreferredLabel", true);
		config.setMultiValued("note", true);		
		config.setMultiValued("placeAlternativeLabel", true);	
		config.setMultiValued("dataOwner", true);
		config.setMultiValued("choIdString", true);

	}
}
