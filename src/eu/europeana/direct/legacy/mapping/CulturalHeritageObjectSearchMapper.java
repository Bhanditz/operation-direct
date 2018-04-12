package eu.europeana.direct.legacy.mapping;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoublePoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;
import org.apache.lucene.search.SortField;
import org.apache.lucene.util.BytesRef;

import com.sun.javafx.css.StringStore;

import org.apache.lucene.document.Field;

import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.repositories.CulturalHeritageObjectRepository;
import eu.europeana.direct.legacy.helpers.DateParser;
import eu.europeana.direct.legacy.logic.helpers.MappingHelper;
import eu.europeana.direct.legacy.model.search.ProfileType;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageNonAware.TypeEnum;
import java.util.*;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.PlaceLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLangaugeAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLanguageNonAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

public class CulturalHeritageObjectSearchMapper implements MainIndexingMapper<CulturalHeritageObject> {

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private DateParser dateParser = new DateParser();
	
	public Document mapForRemovedRecords(long choId){
		
		Document doc = new Document();
		
		doc.add(new TextField("choidString",String.valueOf(choId), Field.Store.YES));
		Date deleted = new Date();		
		doc.add(new TextField("deletedString", simpleDateFormat2.format(deleted), Field.Store.YES));
		doc.add(new LongPoint("deleted", deleted.getTime()));
		
		return doc;
	}
	
	@Override
	public Document mapFromSource(CulturalHeritageObject cho, Date created) {	

		Document doc = new Document();
		
		boolean setAlreadyAdded = false;
		
		doc.add(new TextField("choidString", "" + cho.getId(), Field.Store.YES));
		doc.add(new SortedDocValuesField("choid", new BytesRef(cho.getId().toString())));

		doc.add(new TextField("object_type", "CulturalHeritageObject", Field.Store.YES));
//		if(cho.getLanguageNonAwareFields().getRelation() != null){
//			if (!cho.getLanguageNonAwareFields().getRelation().isEmpty()) {
//				for (int i = 0; i < cho.getLanguageNonAwareFields().getRelation().size(); i++) {
//					if (cho.getLanguageNonAwareFields().getRelation().get(i).length() > 0) {
//						doc.add(new TextField("relation", cho.getLanguageNonAwareFields().getRelation().get(i),
//								Field.Store.YES));						
//					}
//				}
//			}	
//		}		

//		if (cho.getLanguageNonAwareFields().getIdentifier() != null
//				&& !cho.getLanguageNonAwareFields().getIdentifier().isEmpty()) {
//			for (int i = 0; i < cho.getLanguageNonAwareFields().getIdentifier().size(); i++) {
//				if (cho.getLanguageNonAwareFields().getIdentifier().get(i).length() > 0) {
//					doc.add(new TextField("identifier", cho.getLanguageNonAwareFields().getIdentifier().get(i),
//							Field.Store.YES));
//					doc.add(new FacetField("identifier", cho.getLanguageNonAwareFields().getIdentifier().get(i)));
//				}
//			}
//		}	

		if (cho.getLanguageNonAwareFields().getType() != null) {
			String mediatype = cho.getLanguageNonAwareFields().getType().toString();
			doc.add(new TextField("type", mediatype, Field.Store.YES));
			doc.add(new FacetField("type", mediatype));
		}

		doc.add(new TextField("modifiedString", simpleDateFormat.format(new Date()), Field.Store.YES));
		doc.add(new TextField("modifiedEpoch", "" + new Date().getTime(), Field.Store.YES));
		doc.add(new LongPoint("modified", new Date().getTime()));		

		if (created == null) {
			// if created is null, means its first time indexing this record
			created = new Date();
		}

		doc.add(new TextField("createdString", simpleDateFormat.format(created), Field.Store.YES));
		doc.add(new TextField("createdEpoch", "" + created.getTime(), Field.Store.YES));
		doc.add(new LongPoint("created", created.getTime()));	

		for (CulturalHeritageObjectLanguageAware choLangAware : cho.getLanguageAwareFields()) {

			String lang = choLangAware.getLanguage();			
			if (choLangAware.getTitle() != null && choLangAware.getTitle().length() > 0) {				
				doc.add(new TextField("title_" + lang, choLangAware.getTitle(), Field.Store.YES));
				doc.add(new TextField("title_index", choLangAware.getTitle(), Field.Store.YES));				
			}
			if (choLangAware.getAlternative() != null && !choLangAware.getAlternative().isEmpty()) {
				for (String alt : choLangAware.getAlternative()) {
					if (alt.length() > 0) {
						doc.add(new TextField("alternative_" + lang, alt, Field.Store.YES));
						doc.add(new TextField("alternative_index", alt, Field.Store.YES));						
					}
				}
			}

			if (choLangAware.getDescription() != null && choLangAware.getDescription().length() > 0) {
				doc.add(new TextField("description_" + lang, choLangAware.getDescription(), Field.Store.YES));
				doc.add(new TextField("description_index", choLangAware.getDescription(), Field.Store.YES));				
			}

			
//			if (choLangAware.getSource() != null && !choLangAware.getSource().isEmpty()) {
//				for (String source : choLangAware.getSource()) {
//					if (source.length() > 0) {
//						doc.add(new TextField("source_" + lang, source, Field.Store.YES));
//						doc.add(new TextField("source_index", source, Field.Store.YES));
//					}
//				}
//			}
			if (choLangAware.getCreated() != null && choLangAware.getCreated().length() > 0) {
				doc.add(new TextField("createdChoString_" + lang, choLangAware.getCreated(), Field.Store.YES));
				doc.add(new TextField("createdChoString_index", choLangAware.getCreated(), Field.Store.YES));

				Date d = dateParser.tryParse(choLangAware.getCreated());
				if (d != null) {					
					doc.add(new LongPoint("createdCho_index", d.getTime()));
				}

			}
			
			if(choLangAware.getPublisher() != null && choLangAware.getPublisher().size() > 0){				
				if(doc.get("publisher") == null){	
					for(String publisher : choLangAware.getPublisher()){
						if(publisher.length() > 0){
							doc.add(new TextField("publisher_"+lang, publisher, Field.Store.YES));
							doc.add(new TextField("publisher_index", publisher, Field.Store.YES));
							doc.add(new FacetField("publisher_index", publisher));
							// for data.museums.eu OAI purpose - SET is publisher in slovenian language
							if(choLangAware.getLanguage().equals("sl")){									
								// original name of set
								doc.add(new TextField("setName", publisher, Field.Store.YES));
								doc.add(new FacetField("setName", publisher));
								// modified name of original set (presledki => _, čšž => csz)
								String modifiedSetName = MappingHelper.modifySetName(publisher);
								doc.add(new TextField("setSpec", modifiedSetName, Field.Store.YES));								
								setAlreadyAdded = true;
							}
						}						
					}					
				}				
			}
			
//			if (choLangAware.getIssued() != null && choLangAware.getIssued().length() > 0) {
//				doc.add(new TextField("issuedString_" + lang, choLangAware.getIssued(), Field.Store.YES));
//				doc.add(new TextField("issuedString_index", choLangAware.getIssued(), Field.Store.YES));
//
//				Date d = dateParser.tryParse(choLangAware.getIssued());
//				if (d != null) {
//					doc.add(new LongPoint("issued_index", d.getTime()));
//				}
//			}
//			if (choLangAware.getFormat() != null && !choLangAware.getFormat().isEmpty()) {
//				for (String format : choLangAware.getFormat()) {
//					if (format.length() > 0) {
//						doc.add(new TextField("format_" + lang, format, Field.Store.YES));
//						doc.add(new TextField("format_index", format, Field.Store.YES));
//					}
//				}
//			}
//			if (choLangAware.getExtent() != null && !choLangAware.getExtent().isEmpty()) {
//				for (String extent : choLangAware.getExtent()) {
//					if (extent.length() > 0) {
//						doc.add(new TextField("extent_" + lang, extent, Field.Store.YES));
//						doc.add(new TextField("extent_index", extent, Field.Store.YES));
//					}
//				}
//			}
//			if (choLangAware.getMedium() != null && !choLangAware.getMedium().isEmpty()) {
//				for (String medium : choLangAware.getMedium()) {
//					if (medium.length() > 0) {
//						doc.add(new TextField("medium_" + lang, medium, Field.Store.YES));
//						doc.add(new TextField("medium_index", medium, Field.Store.YES));
//					}
//				}
//			}
//			if (choLangAware.getProvenance() != null && !choLangAware.getProvenance().isEmpty()) {
//				for (String prov : choLangAware.getProvenance()) {
//					if (prov.length() > 0) {
//						doc.add(new TextField("provenance_" + lang, prov, Field.Store.YES));
//						doc.add(new TextField("provenance_index", prov, Field.Store.YES));
//					}
//				}
//			}

			if (choLangAware.getLanguage() != null && choLangAware.getLanguage().length() > 0) {
				doc.add(new FacetField("language", choLangAware.getLanguage()));
				doc.add(new FacetField("country", choLangAware.getLanguage()));
				doc.add(new TextField("language", choLangAware.getLanguage(), Field.Store.YES));
				doc.add(new TextField("country", choLangAware.getLanguage(), Field.Store.YES));
			}
		}

		if (cho.getLanguageNonAwareFields().getDataOwner() != null
				&& cho.getLanguageNonAwareFields().getDataOwner().length() > 0) {			
			doc.add(new TextField("dataOwner", cho.getLanguageNonAwareFields().getDataOwner(), Field.Store.YES));
			doc.add(new FacetField("dataOwner", cho.getLanguageNonAwareFields().getDataOwner()));
			// add only if set field we're not added already (some sets might be added in cho.langAware.publisher - sl value for data.museums.eu)
			if(!setAlreadyAdded){
				// original name of set								
				doc.add(new TextField("setName", cho.getLanguageNonAwareFields().getDataOwner(), Field.Store.YES));
				doc.add(new FacetField("setName", cho.getLanguageNonAwareFields().getDataOwner()));
				// moodified name of set
				String modifiedSetName = MappingHelper.modifySetName(cho.getLanguageNonAwareFields().getDataOwner());
				doc.add(new TextField("setSpec", modifiedSetName, Field.Store.YES));				
			}
		}
		
		
		// mapping cho agent fields to lucene document
		for (Agent a : cho.getAgents()) {

			doc.add(new TextField("agentId", "" + a.getId(), Field.Store.YES));

			//System.out.println(a.getLanguageNonAwareFields().getRole());
			
			if(a.getLanguageNonAwareFields() != null){
				if (a.getLanguageNonAwareFields().getDateOfBirth() != null
						&& a.getLanguageNonAwareFields().getDateOfBirth().length() > 0) {
					doc.add(new TextField("dateOfBirthString", a.getLanguageNonAwareFields().getDateOfBirth(),
							Field.Store.YES));
					Date d = dateParser.tryParse(a.getLanguageNonAwareFields().getDateOfBirth());
					if (d != null) {
						doc.add(new LongPoint("dateOfBirth", d.getTime()));
					}
				}
				if (a.getLanguageNonAwareFields().getDateOfDeath() != null
						&& a.getLanguageNonAwareFields().getDateOfDeath().length() > 0) {
					doc.add(new TextField("dateOfDeathString", a.getLanguageNonAwareFields().getDateOfDeath(),
							Field.Store.YES));

					Date d = dateParser.tryParse(a.getLanguageNonAwareFields().getDateOfDeath());
					if (d != null) {
						doc.add(new LongPoint("dateOfDeath", d.getTime()));
					}
				}
				if (a.getLanguageNonAwareFields().getDateOfEstablishment() != null
						&& a.getLanguageNonAwareFields().getDateOfEstablishment().length() > 0) {
					doc.add(new TextField("dateOfEstablishmentString",
							a.getLanguageNonAwareFields().getDateOfEstablishment(), Field.Store.YES));

					Date d = dateParser.tryParse(a.getLanguageNonAwareFields().getDateOfEstablishment());
					if (d != null) {
						doc.add(new LongPoint("dateOfEstablishment", d.getTime()));
					}
				}
				if (a.getLanguageNonAwareFields().getDateOfTermination() != null
						&& a.getLanguageNonAwareFields().getDateOfTermination().length() > 0) {
					doc.add(new TextField("dateOfTerminationString", a.getLanguageNonAwareFields().getDateOfTermination(),
							Field.Store.YES));

					Date d = dateParser.tryParse(a.getLanguageNonAwareFields().getDateOfTermination());

					if (d != null) {
						doc.add(new LongPoint("dateOfTermination", d.getTime()));
					}
				}	
			}			
			// mapping cho agent language aware fields to lucene document
			for (AgentLanguageAware agentLangAware : a.getLanguageAwareFields()) {

				String lang = agentLangAware.getLanguage();
				doc.add(new TextField("agentLanguage", agentLangAware.getLanguage(), Field.Store.YES));

				if (agentLangAware.getGender() != null && agentLangAware.getGender().length() > 0) {
					doc.add(new TextField("gender_" + lang, agentLangAware.getGender(), Field.Store.YES));
				}
				if (agentLangAware.getPlaceOfBirth() != null && agentLangAware.getPlaceOfBirth().length() > 0) {
					doc.add(new TextField("placeOfBirth_" + lang, agentLangAware.getPlaceOfBirth(), Field.Store.YES));
				}
				if (agentLangAware.getPlaceOfDeath() != null && agentLangAware.getPlaceOfDeath().length() > 0) {					
					doc.add(new TextField("placeOfDeath_" + lang, agentLangAware.getPlaceOfDeath(), Field.Store.YES));
				}
				if (agentLangAware.getPreferredLabel() != null && agentLangAware.getPreferredLabel().length() > 0) {
					doc.add(new TextField("agentPreferredLabel_" + lang, agentLangAware.getPreferredLabel(),
							Field.Store.YES));
					doc.add(new TextField("agentPreferredLabel_index", agentLangAware.getPreferredLabel(),
							Field.Store.YES));
				}
				if (agentLangAware.getAlternativeLabel() != null && !agentLangAware.getAlternativeLabel().isEmpty()) {
					for (String altLabel : agentLangAware.getAlternativeLabel()) {
						if (altLabel.length() > 0) {							
							doc.add(new TextField("agentAlternativeLabel_" + lang, altLabel, Field.Store.YES));
							doc.add(new TextField("agentAlternativeLabel_index", altLabel, Field.Store.YES));
						}
					}
				}
				if (agentLangAware.getIdentifier() != null && !agentLangAware.getIdentifier().isEmpty()) {
					for (String identifier : agentLangAware.getAlternativeLabel()) {
						if (identifier.length() > 0) {							
							doc.add(new TextField("agentIdentifier_" + lang, identifier, Field.Store.YES));
							doc.add(new TextField("agentIdentifier_index", identifier, Field.Store.YES));
						}
					}
				}
				if (agentLangAware.getBiographicalInformation() != null
						&& agentLangAware.getBiographicalInformation().length() > 0) {					
					doc.add(new TextField("biographicalInformation_" + lang,
							agentLangAware.getBiographicalInformation(), Field.Store.YES));
					doc.add(new TextField("biographicalInformation_index", agentLangAware.getBiographicalInformation(),
							Field.Store.YES));
				}
				if (agentLangAware.getProfessionOrOccupation() != null
						&& agentLangAware.getProfessionOrOccupation().length() > 0) {					
					doc.add(new TextField("professionOrOccupation_" + lang, agentLangAware.getProfessionOrOccupation(),
							Field.Store.YES));
					doc.add(new TextField("professionOrOccupation_index", agentLangAware.getProfessionOrOccupation(),
							Field.Store.YES));
				}
				if (agentLangAware.getSameAs() != null && !agentLangAware.getSameAs().isEmpty()) {
					for (String sameAs : agentLangAware.getSameAs()) {						
						doc.add(new TextField("sameAs_" + lang, sameAs, Field.Store.YES));
					}
				}
			}
		}

		// mapping cho concept fields to lucene document
		for (Concept c : cho.getConcepts()) {

			doc.add(new TextField("conceptId", "" + c.getId(), Field.Store.YES));

			// mapping cho concept language aware fields to lucene document
			for (ConceptLanguageAware conceptLangAware : c.getLanguageAwareFields()) {

				String lang = conceptLangAware.getLanguage();
				doc.add(new TextField("conceptLanguage", lang, Field.Store.YES));

				if (conceptLangAware.getPreferredLabel() != null && conceptLangAware.getPreferredLabel().length() > 0) {					
					doc.add(new TextField("conceptPreferredLabel_" + lang, conceptLangAware.getPreferredLabel(),
							Field.Store.YES));
					doc.add(new TextField("conceptPreferredLabel_index", conceptLangAware.getPreferredLabel(),
							Field.Store.YES));
				}
				if (conceptLangAware.getAlternativeLabel() != null
						&& !conceptLangAware.getAlternativeLabel().isEmpty()) {
					for (String altLabel : conceptLangAware.getAlternativeLabel()) {
						if (altLabel.length() > 0) {				
							doc.add(new TextField("conceptAlternativeLabel_" + lang, altLabel, Field.Store.YES));
							doc.add(new TextField("conceptAlternativeLabel_index", altLabel, Field.Store.YES));
						}
					}
				}
			}
		}

		// mapping cho place fields to lucene document
		for (Place p : cho.getSpatial()) {

			doc.add(new TextField("placeId", "" + p.getId(), Field.Store.YES));

			if(p.getLanguageNonAwareFields() != null){
				if (p.getLanguageNonAwareFields().getLatitude() != null) {				
					doc.add(new StoredField("latitudeString", p.getLanguageNonAwareFields().getLatitude()));
					doc.add(new DoublePoint("latitude", p.getLanguageNonAwareFields().getLatitude()));
				}
				if (p.getLanguageNonAwareFields().getLongitude() != null) {				
					doc.add(new StoredField("longitudeString", p.getLanguageNonAwareFields().getLongitude()));
					doc.add(new DoublePoint("longitude", p.getLanguageNonAwareFields().getLongitude()));
				}
				if (p.getLanguageNonAwareFields().getAltitude() != null) {				
					doc.add(new StoredField("altitudeString", p.getLanguageNonAwareFields().getAltitude()));
					doc.add(new DoublePoint("altitude", p.getLanguageNonAwareFields().getAltitude()));
				}	
			}			

			// mapping cho place language aware fields to lucene document
			for (PlaceLanguageAware placeLangAware : p.getLanguageAwareFields()) {
				String lang = placeLangAware.getLanguage();

				doc.add(new TextField("placeLanguage", lang, Field.Store.YES));
				if (placeLangAware.getPreferredLabel() != null && placeLangAware.getPreferredLabel().length() > 0) {
					doc.add(new TextField("placePreferredLabel_" + lang, placeLangAware.getPreferredLabel(),
							Field.Store.YES));
					doc.add(new TextField("placePreferredLabel_index", placeLangAware.getPreferredLabel(),
							Field.Store.YES));
				}
				if (placeLangAware.getCustomFields() != null && placeLangAware.getCustomFields().size() > 0) {
					for (KeyValuePair kv : placeLangAware.getCustomFields()) {
						if (kv.getKey().toLowerCase().contains("note")) {
							if (kv.getValue().length() > 0) {								
								doc.add(new TextField("note", kv.getValue(), Field.Store.YES));
							}
						}
					}
				}
				if (placeLangAware.getAlternativeLabel() != null && !placeLangAware.getAlternativeLabel().isEmpty()) {
					for (String altLabel : placeLangAware.getAlternativeLabel()) {
						if (altLabel.length() > 0) {
							doc.add(new TextField("placeAlternativeLabel_" + lang, altLabel, Field.Store.YES));
							doc.add(new TextField("placeAlternativeLabel_index", altLabel, Field.Store.YES));
						}
					}
				}
			}
		}

		// mapping cho timespan fields to lucene document
		for (TimeSpan s : cho.getTemporal()) {
			doc.add(new TextField("timespanId", "" + s.getId(), Field.Store.YES));
			
			// mapping cho timespan fields to lucene document
			for (TimeSpanLangaugeAware timespanLangAware : s.getLanguageAwareFields()) {
				String lang = timespanLangAware.getLanguage();
				doc.add(new TextField("timespanLanguage", lang, Field.Store.YES));
				if (timespanLangAware.getPreferredLabel() != null
						&& timespanLangAware.getPreferredLabel().length() > 0) {					
					doc.add(new TextField("timespanPreferredLabel_" + lang, timespanLangAware.getPreferredLabel(),
							Field.Store.YES));
					doc.add(new TextField("timespanPreferredLabel_index", timespanLangAware.getPreferredLabel(),
							Field.Store.YES));
				}
				if (timespanLangAware.getAlternativeLabel() != null
						&& !timespanLangAware.getAlternativeLabel().isEmpty()) {
					for (String altLabel : timespanLangAware.getAlternativeLabel()) {
						if (altLabel.length() > 0) {
							doc.add(new TextField("timespanAlternativeLabel_" + lang, altLabel, Field.Store.YES));							
							doc.add(new TextField("timespanAlternativeLabel_index", altLabel, Field.Store.YES));
						}
					}
				}
			}
			if(s.getLanguageNonAwareFields() != null){
				if (s.getLanguageNonAwareFields().getBegin() != null
						&& s.getLanguageNonAwareFields().getBegin().length() > 0) {
					doc.add(new TextField("timespanBegin", s.getLanguageNonAwareFields().getBegin(), Field.Store.YES));
				}
				if (s.getLanguageNonAwareFields().getEnd() != null && s.getLanguageNonAwareFields().getEnd().length() > 0) {
					doc.add(new TextField("timespanEnd", s.getLanguageNonAwareFields().getEnd(), Field.Store.YES));
				}	
			}			
		}

		Map<Integer, String> previewImages = new HashMap<Integer, String>();
		if (cho.getWebLinks().size() < 1) {
			doc.add(new TextField("hasLandingPage", "false", Field.Store.YES));
			doc.add(new TextField("thumbnail", "false", Field.Store.YES));

		}

		boolean thumbnail = false;
		boolean hasLandingPage = false;

		// sorts weblinks by id ascending
		/*List<WebLink> sortedWeblinks = cho.getWebLinks().parallelStream()
				.sorted(Comparator.comparingLong(w -> w.getId().longValueExact())).collect(Collectors.toList());*/

		// mapping cho weblink fields to lucene document
		for (WebLink wl : cho.getWebLinks()) {
			doc.add(new TextField("weblinkId", "" + wl.getId(), Field.Store.YES));

			if (wl.getOwner() != null && wl.getOwner().length() > 0) {				
				doc.add(new TextField("owner", wl.getOwner(), Field.Store.YES));
			}
			if (wl.getRights() != null && wl.getRights().length() > 0) {
				doc.add(new FacetField("rights", wl.getRights()));
				doc.add(new TextField("rights", wl.getRights(), Field.Store.YES));
			}

			if (wl.getType() != null) {

				String type = null;
				type = wl.getType().toString();

				switch (wl.getType()) {
				case DIRECT_MEDIA:
					if (!previewImages.containsKey(0)) {
						previewImages.put(0, wl.getLink());
					}
					break;
				case LANDING_PAGE:
					doc.add(new TextField("landingPage", wl.getLink(), Field.Store.YES));
					hasLandingPage = true;
					doc.add(new TextField("isShownAt", wl.getLink(), Field.Store.YES));
					break;
				case PREVIEW_SOURCE:
					if (!previewImages.containsKey(2)) {
						previewImages.put(2, wl.getLink());
					}
					break;
				case OTHER:
					break;
				}
				if (type != null) {
					doc.add(new TextField("weblinkType", type, Field.Store.YES));					
				}
			}

			if (previewImages.containsKey(0)) {
				doc.add(new TextField("previewImage", previewImages.get(0), Field.Store.YES));
				thumbnail = true;
			} else if (previewImages.containsKey(2)) {
				doc.add(new TextField("previewImage", previewImages.get(2), Field.Store.YES));
				thumbnail = true;
			}
		}
		doc.add(new TextField("hasLandingPage", Boolean.toString(hasLandingPage), Field.Store.YES));
		doc.add(new TextField("thumbnail", Boolean.toString(thumbnail), Field.Store.YES));

		return doc;
	}

	@Override
	public CulturalHeritageObject mapToSource(Document luceneDocument, ProfileType profile) {
	
		CulturalHeritageObject source = new CulturalHeritageObject();
		
		// adding additional fields to cho for Standard profile
		if(profile.equals(ProfileType.Standard) || profile.equals(ProfileType.Portal) || profile.equals(ProfileType.Rich) || profile.equals(ProfileType.OAI)){
			
			List<Concept> concepts = new ArrayList<Concept>();
			Concept c = new Concept();	
			ConceptLanguageNonAware conceptNonAware = new ConceptLanguageNonAware();
			List<ConceptLanguageAware> conceptLangAwareList = new ArrayList<ConceptLanguageAware>();	
			
			String conceptLangBefore = "";
			// maps concept fields from CHO Lucene document to Operation Direct Concept model
			
			Set<String> conceptLangs = new HashSet<String>(Arrays.asList(luceneDocument.getValues("conceptLanguage")));				
		    for (Iterator<String> it = conceptLangs.iterator(); it.hasNext(); ) {
				String lang = it.next();
				
				if(conceptLangBefore.equals(lang)){
					continue;
				}
				
				if(luceneDocument.get("conceptPreferredLabel_"+lang) != null){
					if(luceneDocument.getValues("conceptPreferredLabel_"+lang).length > 1){
						for(int z=0; z < luceneDocument.getValues("conceptPreferredLabel_"+lang).length; z++){
							ConceptLanguageAware conceptLangAware = new ConceptLanguageAware();
							conceptLangAware.setPreferredLabel(luceneDocument.getValues("conceptPreferredLabel_"+lang)[z]);						
							conceptLangAwareList.add(conceptLangAware);									
						}					
					}else{
						ConceptLanguageAware conceptLangAware = new ConceptLanguageAware();
						conceptLangAware.setPreferredLabel(luceneDocument.get("conceptPreferredLabel_"+lang));
						conceptLangAwareList.add(conceptLangAware);
					}								
					
				}						
				conceptLangBefore = lang;

			}
			c.setLanguageAwareFields(conceptLangAwareList);
			c.setLanguageNonAwareFields(conceptNonAware);
			concepts.add(c);
			source.setConcepts(concepts);

			List<TimeSpan> timespans = new ArrayList<TimeSpan>();
			
			// maps timespan fields from CHO Lucene document to Operation Direct Timespan model
			for(int j=0; j < luceneDocument.getValues("timespanId").length;j++){
				TimeSpan t = new TimeSpan();	
				TimeSpanLanguageNonAware timespanNonAware = new TimeSpanLanguageNonAware();
				List<TimeSpanLangaugeAware> timespanLangAwareList = new ArrayList<TimeSpanLangaugeAware>();				
				
				if(luceneDocument.getValues("timespanBegin").length > 0){
					for(int k=0; k<luceneDocument.getValues("timespanBegin").length;k++){
						boolean found = false;
						String begin = luceneDocument.getValues("timespanBegin")[k];
						for(TimeSpan ts : timespans){							
							if(ts.getLanguageNonAwareFields() != null){
								if(ts.getLanguageNonAwareFields().getBegin() != null){
									if(ts.getLanguageNonAwareFields().getBegin().equals(begin)){
										found = true;
									}
								}
							}						
						}
						if(!found){
							timespanNonAware.setBegin(begin);
							break;
						}
					}
				}	
				
				if(luceneDocument.getValues("timespanEnd").length > 0){
					for(int k=0; k<luceneDocument.getValues("timespanEnd").length;k++){
						boolean found = false;
						String end = luceneDocument.getValues("timespanEnd")[k];
						for(TimeSpan ts : timespans){				
							if(ts.getLanguageNonAwareFields() != null){
								if(ts.getLanguageNonAwareFields().getEnd() != null){
									if(ts.getLanguageNonAwareFields().getEnd().equals(end)){
										found = true;
									}
								}
							}						
						}
						if(!found){
							timespanNonAware.setEnd(end);
							break;
						}
					}
				}	
				
				String timespanLangBefore = "";
				Set<String> timespanLangs = new HashSet<String>(Arrays.asList(luceneDocument.getValues("timespanLanguage")));		

			    for (Iterator<String> it = timespanLangs.iterator(); it.hasNext(); ) {
					String lang = it.next();
							
					if(timespanLangBefore.equals(lang)){
						continue;
					}
					
					if(luceneDocument.get("timespanPreferredLabel_"+lang) != null){
						if(luceneDocument.getValues("timespanPreferredLabel_"+lang).length > 1){
							for(int z=0; z < luceneDocument.getValues("timespanPreferredLabel_"+lang).length; z++){
								TimeSpanLangaugeAware timespanLangAware = new TimeSpanLangaugeAware();
								timespanLangAware.setPreferredLabel(luceneDocument.getValues("timespanPreferredLabel_"+lang)[z]);						
								timespanLangAwareList.add(timespanLangAware);							
							}					
						}else{
							TimeSpanLangaugeAware timespanLangAware = new TimeSpanLangaugeAware();
							timespanLangAware.setPreferredLabel(luceneDocument.get("timespanPreferredLabel_"+lang));
							timespanLangAwareList.add(timespanLangAware);
						}								
						
					}						
					timespanLangBefore = lang;	
				}
				t.setLanguageAwareFields(timespanLangAwareList);
				t.setLanguageNonAwareFields(timespanNonAware);
				timespans.add(t);
			}
			source.setTemporal(timespans);

		}
		
		
		if(luceneDocument.get("choidString") != null){		
			source.setId(new BigDecimal(luceneDocument.get("choidString")));				
		}		
		
		CulturalHeritageObjectLanguageNonAware sourceLangNonAware = new CulturalHeritageObjectLanguageNonAware();
//		if(luceneDocument.getValues("relation").length > 0){			
//			sourceLangNonAware.setRelation(Arrays.asList(luceneDocument.getValues("relation")));	
//		}
//		if(luceneDocument.getValues("identifier").length > 0){			
//			sourceLangNonAware.setIdentifier(Arrays.asList(luceneDocument.getValues("identifier")));	
//		}
		
		if(profile.equals(ProfileType.OAI)){
			if(luceneDocument.get("setSpec") != null){
				sourceLangNonAware.getCustomFields().add(new KeyValuePair("setSpec",luceneDocument.get("setSpec")));	
			}			
		}
		
		if(luceneDocument.get("dataOwner") != null){
			sourceLangNonAware.setDataOwner(luceneDocument.get("dataOwner"));
		}
		
		// only if profile rich
		if(profile.equals(ProfileType.Rich)){
			if(luceneDocument.getValues("landingPage").length > 0){
				for(int i=0; i < luceneDocument.getValues("landingPage").length;i++){
					sourceLangNonAware.getCustomFields().add(new KeyValuePair("landingPage "+i,luceneDocument.getValues("landingPage")[i]));
				}
			}	
		}
		
		if(luceneDocument.getValues("isShownAt").length > 0){
			for(int i=0; i < luceneDocument.getValues("isShownAt").length;i++){
				sourceLangNonAware.getCustomFields().add(new KeyValuePair("isShownAt "+i,luceneDocument.getValues("isShownAt")[i]));
			}
		}	
		if(luceneDocument.getValues("previewImage").length > 0){
			for(int i=0; i < luceneDocument.getValues("previewImage").length; i++){
				sourceLangNonAware.getCustomFields().add(new KeyValuePair("edmPreview "+i,luceneDocument.getValues("previewImage")[i]));
			}			
		}								
		
		// only if profile standard or higher
		if(profile.equals(ProfileType.Standard) || profile.equals(ProfileType.Portal) || profile.equals(ProfileType.Rich) || profile.equals(ProfileType.OAI)){
			if(luceneDocument.get("createdString") != null){
				sourceLangNonAware.getCustomFields().add(new KeyValuePair("createdString",luceneDocument.get("createdString")));
			}
			if(luceneDocument.get("createdEpoch") != null){
				sourceLangNonAware.getCustomFields().add(new KeyValuePair("createdEpoch",luceneDocument.get("createdEpoch")));
			}
			if(luceneDocument.get("modifiedString") != null){
				sourceLangNonAware.getCustomFields().add(new KeyValuePair("modifiedString",luceneDocument.get("modifiedString")));
			}
			if(luceneDocument.get("modifiedEpoch") != null){
				sourceLangNonAware.getCustomFields().add(new KeyValuePair("modifiedEpoch",luceneDocument.get("modifiedEpoch")));
			}
			if(luceneDocument.get("modifiedEpoch") != null){
				sourceLangNonAware.getCustomFields().add(new KeyValuePair("modifiedEpoch",luceneDocument.get("modifiedEpoch")));
			}
			
			if(luceneDocument.getValues("country").length > 0){
				for(int i=0; i < luceneDocument.getValues("country").length;i++){
					sourceLangNonAware.getCustomFields().add(new KeyValuePair("country "+i,luceneDocument.getValues("country")[i]));
				}
			}	
		}		
		
		if(luceneDocument.get("type") != null){
			switch(luceneDocument.get("type")){
				case "IMAGE":
					sourceLangNonAware.setType(TypeEnum.IMAGE);
					break;
				case "VIDEO":
					sourceLangNonAware.setType(TypeEnum.VIDEO);
					break;
				case "AUDIO":
					sourceLangNonAware.setType(TypeEnum.AUDIO);
					break;
				case "_3D":
					sourceLangNonAware.setType(TypeEnum._3D);
					break;
				case "TEXT":
					sourceLangNonAware.setType(TypeEnum.TEXT);
					break;
			}	
		}		
		
		List<CulturalHeritageObjectLanguageAware> listLangAware = new ArrayList<CulturalHeritageObjectLanguageAware>();
		
		for(int i=0; i < luceneDocument.getValues("language").length; i++){
			CulturalHeritageObjectLanguageAware sourceLangAware = new CulturalHeritageObjectLanguageAware();

			String lang = luceneDocument.getValues("language")[i];
			
			// only if profile standard or higher
			if(profile.equals(ProfileType.Standard) || profile.equals(ProfileType.Portal) || profile.equals(ProfileType.Rich) || profile.equals(ProfileType.OAI)){				
				sourceLangAware.setLanguage(lang);	
			}			
			
			if(luceneDocument.get("title_"+lang) != null){
				sourceLangAware.setTitle(luceneDocument.get("title_"+lang));
			}		
			
			// only if profile rich
			if(profile.equals(ProfileType.Rich)){
				if(luceneDocument.get("description_"+lang) != null){
					sourceLangAware.setDescription(luceneDocument.get("description_"+lang));
				}				
			}
					
//			if(luceneDocument.getValues("publisher_"+lang).length > 0){
//				sourceLangAware.setPublisher(Arrays.asList(luceneDocument.getValues("publisher_"+lang)));
//			}			
//			if(luceneDocument.getValues("source_"+lang).length > 0){
//				sourceLangAware.setPublisher(Arrays.asList(luceneDocument.getValues("source_"+lang)));
//			}			
//			if(luceneDocument.get("createdString_"+lang) != null){
//				sourceLangAware.setCreated(luceneDocument.get("createdString_"+lang));
//			}			
//			if(luceneDocument.get("issuedString_"+lang) != null){
//				sourceLangAware.setIssued(luceneDocument.get("issuedString_"+lang));
//			}
//			if(luceneDocument.getValues("format_"+lang).length > 0){
//				sourceLangAware.setFormat(Arrays.asList(luceneDocument.getValues("format_"+lang)));
//			}
//			if(luceneDocument.getValues("extent_"+lang).length > 0){
//				sourceLangAware.setFormat(Arrays.asList(luceneDocument.getValues("extent_"+lang)));
//			}
//			if(luceneDocument.getValues("medium_"+lang).length > 0){
//				sourceLangAware.setFormat(Arrays.asList(luceneDocument.getValues("medium_"+lang)));
//			}
//			if(luceneDocument.getValues("provenance_"+lang).length > 0){
//				sourceLangAware.setFormat(Arrays.asList(luceneDocument.getValues("provenance_"+lang)));
//			}
//			if(luceneDocument.getValues("alternative_"+lang).length > 0){
//				sourceLangAware.setAlternative(Arrays.asList(luceneDocument.getValues("alternative_"+lang)));
//			}			
			if(luceneDocument.getValues("createdChoString_"+lang).length > 0){
				for(int z=0; z < luceneDocument.getValues("createdChoString_"+lang).length;z++){
					sourceLangNonAware.getCustomFields().add(new KeyValuePair("createdChoString "+z,luceneDocument.getValues("createdChoString_"+lang)[z]));
				}
			}
			listLangAware.add(sourceLangAware);
		}
		
		// only if profile portal or higher
		if(profile.equals(ProfileType.Portal) || profile.equals(ProfileType.Rich)){
			List<Agent> agents = new ArrayList<Agent>();
			Agent a = new Agent();	
			AgentLanguageNonAware agentNonAware = new AgentLanguageNonAware();
			List<AgentLanguageAware> agentLangAwareList = new ArrayList<AgentLanguageAware>();						
			
			String langBefore = "";		
			// maps agent fields from CHO Lucene document to Operation Direct Agent model
			Set<String> agentLangs = new HashSet<String>(Arrays.asList(luceneDocument.getValues("agentLanguage")));				
		    for (Iterator<String> it = agentLangs.iterator(); it.hasNext(); ) {
				String lang = it.next();
						
				if(langBefore.equals(lang)){
					continue;
				}
				if(luceneDocument.get("agentPreferredLabel_"+lang) != null){
					if(luceneDocument.getValues("agentPreferredLabel_"+lang).length > 1){
						for(int z=0; z < luceneDocument.getValues("agentPreferredLabel_"+lang).length; z++){
							AgentLanguageAware agentLangAware = new AgentLanguageAware();
							agentLangAware.setPreferredLabel(luceneDocument.getValues("agentPreferredLabel_"+lang)[z]);						
							agentLangAwareList.add(agentLangAware);								
						}					
					}else{
						AgentLanguageAware agentLangAware = new AgentLanguageAware();
						agentLangAware.setPreferredLabel(luceneDocument.get("agentPreferredLabel_"+lang));						
						agentLangAwareList.add(agentLangAware);
					}								
				}			
				langBefore = lang;
			}
			a.setLanguageAwareFields(agentLangAwareList);
			a.setLanguageNonAwareFields(agentNonAware);
			agents.add(a);	
			
			source.setAgents(agents);
		}			
				
		
		List<Place> places = new ArrayList<Place>();
		
		// maps place fields from CHO Lucene document to Operation Direct Place model
		for(int j=0; j < luceneDocument.getValues("placeId").length;j++){
			Place p = new Place();	
			PlaceLanguageNonAware placeNonAware = new PlaceLanguageNonAware();
			List<PlaceLanguageAware> placeLangAwareList = new ArrayList<PlaceLanguageAware>();				
			
			if(luceneDocument.getValues("longitudeString").length > 0){
				for(int k=0; k<luceneDocument.getValues("longitudeString").length;k++){
					boolean found = false;
					double longitude = Double.parseDouble(luceneDocument.getValues("longitudeString")[k]);
					for(Place place : places){	
						
						if(place != null && place.getLanguageNonAwareFields() != null && place.getLanguageNonAwareFields().getLongitude() != null && place.getLanguageNonAwareFields().getLongitude().equals(longitude)){
							found = true;
						}
					}
					if(!found){
						placeNonAware.setLongitude(longitude);
						break;
					}
				}
			}
			
			if(luceneDocument.getValues("altitudeString").length > 0){
				for(int k=0; k<luceneDocument.getValues("altitudeString").length;k++){
					boolean found = false;
					double altitude = Double.parseDouble(luceneDocument.getValues("altitudeString")[k]);
					for(Place place : places){					
						if(place != null && place.getLanguageNonAwareFields() != null &&  place.getLanguageNonAwareFields().getAltitude() != null && place.getLanguageNonAwareFields().getAltitude().equals(altitude)){
							found = true;
						}
					}
					if(!found){
						placeNonAware.setAltitude(altitude);
						break;
					}
				}
			}
			
			if(luceneDocument.getValues("latitudeString").length > 0){
				for(int k=0; k<luceneDocument.getValues("latitudeString").length;k++){
					boolean found = false;
					double latitude = Double.parseDouble(luceneDocument.getValues("latitudeString")[k]);
					for(Place place : places){					
						if(place != null && place.getLanguageNonAwareFields() != null &&  place.getLanguageNonAwareFields().getLatitude() != null && place.getLanguageNonAwareFields().getLatitude().equals(latitude)){
							found = true;
						}
					}
					if(!found){
						placeNonAware.setLatitude(latitude);
						break;
					}
				}
			}
			String placeLangBefore = "";

			//map place pref label only if profile standard or higher
			if(profile.equals(ProfileType.Standard) || profile.equals(ProfileType.Portal) || profile.equals(ProfileType.Rich) || profile.equals(ProfileType.OAI)){
				Set<String> placeLangs = new HashSet<String>(Arrays.asList(luceneDocument.getValues("placeLanguage")));					

			    for (Iterator<String> it = placeLangs.iterator(); it.hasNext(); ) {
					String lang = it.next();
											
					if(placeLangBefore.equals(lang)){
						continue;
					}
					
					if(luceneDocument.get("placePreferredLabel_"+lang) != null){
						if(luceneDocument.getValues("placePreferredLabel_"+lang).length > 1){
							for(int z=0; z < luceneDocument.getValues("placePreferredLabel_"+lang).length; z++){
								PlaceLanguageAware placeLangAware = new PlaceLanguageAware();
								placeLangAware.setPreferredLabel(luceneDocument.getValues("placePreferredLabel_"+lang)[z]);						
								placeLangAwareList.add(placeLangAware);	
							}					
						}else{
							PlaceLanguageAware placeLangAware = new PlaceLanguageAware();
							placeLangAware.setPreferredLabel(luceneDocument.get("placePreferredLabel_"+lang));
							placeLangAwareList.add(placeLangAware);
						}								
						
					}						
					placeLangBefore = lang;
				}
			}					    
		    
			p.setLanguageAwareFields(placeLangAwareList);
			p.setLanguageNonAwareFields(placeNonAware);
			places.add(p);
		}		
	
		
		List<WebLink> weblinkList = new ArrayList<WebLink>();
		
		// maps weblink fields from CHO Lucene document to Operation Direct WebLink model
		for(int i=0;i<luceneDocument.getValues("weblinkId").length;i++){
			WebLink wl = new WebLink();
			
			if(luceneDocument.getValues("rights").length > 0){
				for(int k=0; k<luceneDocument.getValues("rights").length;k++){
					boolean found = false;
					String rights = luceneDocument.getValues("rights")[k];
					for(WebLink w : weblinkList){	
						if(w.getRights() != null){
							if(w.getRights().equals(rights)){
								found = true;
							}	
						}						
					}
					if(!found){
						wl.setRights(rights);
						weblinkList.add(wl);
						break;
					}
				}
			}					
		}
		
		source.setWebLinks(weblinkList);
		source.setSpatial(places);
		source.setLanguageAwareFields(listLangAware);
		source.setLanguageNonAwareFields(sourceLangNonAware);
		
		return source;
	}
	
	@Override
	public void configureFacets(FacetsConfig config) {		
		config.setMultiValued("relation", true);
		config.setMultiValued("modified", true);
		config.setMultiValued("created", true);
		config.setMultiValued("title", true);
		config.setMultiValued("description", true);
		config.setMultiValued("publisher", true);
		config.setMultiValued("publisher_index", true);
		config.setMultiValued("setName", true);
		config.setMultiValued("setSpec", true);
		config.setMultiValued("set", true);
		config.setMultiValued("source", true);
		config.setMultiValued("created", true);
		config.setMultiValued("issued", true);
		config.setMultiValued("format", true);					
		config.setMultiValued("extent", true);
		config.setMultiValued("medium", true);
		config.setMultiValued("provenance", true);
		config.setMultiValued("country", true);
		config.setMultiValued("language", true);
		config.setMultiValued("dateOfBirth", true);
		config.setMultiValued("dateOfDeath", true);
		config.setMultiValued("dateOfEstablishment", true);
		config.setMultiValued("dateOfTermination", true);
		config.setMultiValued("gender", true);
		config.setMultiValued("placeOfBirth", true);
		config.setMultiValued("placeOfDeath", true);
		config.setMultiValued("preferredLabel", true);
		config.setMultiValued("agentAlternativeLabel", true);
		config.setMultiValued("identifier", true);
		config.setMultiValued("biographicalInformation", true);
		config.setMultiValued("professionOrOccupation", true);
		config.setMultiValued("conceptPreferredLabel", true);
		config.setMultiValued("conceptAlternativeLabel", true);
		config.setMultiValued("latitude", true);
		config.setMultiValued("longitude", true);
		config.setMultiValued("altitude", true);
		config.setMultiValued("placePreferredLabel", true);
		config.setMultiValued("note", true);
		config.setMultiValued("placeAlternativeLabel", true);
		config.setMultiValued("timespanPreferredLabel", true);
		config.setMultiValued("timespanAlternativeLabel", true);						
		config.setMultiValued("dataOwner", true);
		config.setMultiValued("owner", true);
		config.setMultiValued("rights", true);
		config.setMultiValued("agentPreferredLabel", true);
		config.setMultiValued("type", true);
		config.setMultiValued("weblinkType", true);
		config.setMultiValued("agentIdentifier", true);
		config.setMultiValued("sameAs", true);	
		config.setMultiValued("createdCho", true);	
		config.setMultiValued("previewImage", true);	
		config.setMultiValued("landingPage", true);	
		config.setMultiValued("isShownAt", true);	
	}
}
