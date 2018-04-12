package eu.europeana.direct.legacy.mapping;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;

import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.legacy.helpers.DateParser;
import eu.europeana.direct.legacy.model.search.ProfileType;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageNonAware;

public class AgentMapper implements EntityIndexingMapper<Agent> {

	private DateParser dateParser = new DateParser();
	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");


	@Override
	public Document mapFromSource(Agent agent, Date created,Set<EuropeanaDataObjectEuropeanaDataObject> linkedObjects) {
		Document doc = new Document();

		if (agent.getId() != null) {
			doc.add(new TextField("agentIdString", "" + agent.getId(), Field.Store.YES));
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
		
		doc.add(new TextField("object_type", "Agent", Field.Store.YES));
		if (agent.getLanguageNonAwareFields().getDateOfBirth() != null && agent.getLanguageNonAwareFields().getDateOfBirth().length() > 0) {
			doc.add(new TextField("dateOfBirthString", "" + agent.getLanguageNonAwareFields().getDateOfBirth(),
					Field.Store.YES));

			Date d = dateParser.tryParse(agent.getLanguageNonAwareFields().getDateOfBirth());
			if (d != null) {
				doc.add(new LongPoint("dateOfBirth", d.getTime()));
			}
		}

		if (agent.getLanguageNonAwareFields().getDateOfDeath() != null && agent.getLanguageNonAwareFields().getDateOfDeath().length() > 0) {
			doc.add(new TextField("dateOfDeathString", "" + agent.getLanguageNonAwareFields().getDateOfDeath(),
					Field.Store.YES));
			Date d = dateParser.tryParse(agent.getLanguageNonAwareFields().getDateOfDeath());
			if (d != null) {
				doc.add(new LongPoint("dateOfDeath", d.getTime()));
			}
		}
		if (agent.getLanguageNonAwareFields().getDateOfTermination() != null && agent.getLanguageNonAwareFields().getDateOfTermination().length() > 0) {
			doc.add(new TextField("dateOfTerminationString",
					"" + agent.getLanguageNonAwareFields().getDateOfTermination(), Field.Store.YES));
			Date d = dateParser.tryParse(agent.getLanguageNonAwareFields().getDateOfTermination());
			if (d != null) {
				doc.add(new LongPoint("dateOfTermination", d.getTime()));

			}
		}
		if (agent.getLanguageNonAwareFields().getDateOfEstablishment() != null && agent.getLanguageNonAwareFields().getDateOfEstablishment().length() > 0) {
			doc.add(new TextField("dateOfEstablishmentString",
					"" + agent.getLanguageNonAwareFields().getDateOfEstablishment(), Field.Store.YES));
			Date d = dateParser.tryParse(agent.getLanguageNonAwareFields().getDateOfEstablishment());
			if (d != null) {
				doc.add(new LongPoint("dateOfEstablishment", d.getTime()));
			}
		}

		for (AgentLanguageAware agentLangAware : agent.getLanguageAwareFields()) {
			String lang = agentLangAware.getLanguage();
			doc.add(new TextField("agentLanguage", lang, Field.Store.YES));
			doc.add(new FacetField("language", lang));

			if (agentLangAware.getPreferredLabel() != null && agentLangAware.getPreferredLabel().length() > 0) {
				doc.add(new TextField("agentPreferredLabel_" + lang, agentLangAware.getPreferredLabel(),
						Field.Store.YES));				
				doc.add(new TextField("agentPreferredLabel_index", agentLangAware.getPreferredLabel(),
						Field.Store.YES));
			}

			if (agentLangAware.getAlternativeLabel() != null && agentLangAware.getAlternativeLabel().size() > 0) {
				for (String a : agentLangAware.getAlternativeLabel()) {
					doc.add(new TextField("agentAlternativeLabel_" + lang, a, Field.Store.YES));
					doc.add(new TextField("agentAlternativeLabel_index", a, Field.Store.YES));				
				}
			}

			if (agentLangAware.getIdentifier() != null && agentLangAware.getIdentifier().size() > 0) {
				for (String a : agentLangAware.getIdentifier()) {
					doc.add(new TextField("identifier_" + lang, a, Field.Store.YES));
					doc.add(new TextField("identifier_index", a, Field.Store.YES));					
				}
			}

			if (agentLangAware.getBiographicalInformation() != null && agentLangAware.getBiographicalInformation().length() > 0) {
				doc.add(new TextField("biographicalInformation_" + lang, agentLangAware.getBiographicalInformation(),
						Field.Store.YES));
				doc.add(new TextField("biographicalInformation_index", agentLangAware.getBiographicalInformation(),
						Field.Store.YES));				
			}
			if (agentLangAware.getGender() != null && agentLangAware.getGender().length() > 0) {
				doc.add(new TextField("gender_" + lang, agentLangAware.getGender(), Field.Store.YES));				
				doc.add(new TextField("gender_index", agentLangAware.getGender(), Field.Store.YES));
			}
			if (agentLangAware.getProfessionOrOccupation() != null && agentLangAware.getProfessionOrOccupation().length() > 0) {
				doc.add(new TextField("professionOrOccupation_" + lang, agentLangAware.getProfessionOrOccupation(),
						Field.Store.YES));
				doc.add(new TextField("professionOrOccupation_index", agentLangAware.getProfessionOrOccupation(),
						Field.Store.YES));				
			}
			if (agentLangAware.getPlaceOfBirth() != null && agentLangAware.getPlaceOfBirth().length() > 0) {
				doc.add(new TextField("placeOfBirth_" + lang, agentLangAware.getPlaceOfBirth(), Field.Store.YES));
				doc.add(new TextField("placeOfBirth_index", agentLangAware.getPlaceOfBirth(), Field.Store.YES));
			}
			if (agentLangAware.getPlaceOfDeath() != null && agentLangAware.getPlaceOfDeath().length() > 0) {
				doc.add(new TextField("placeOfDeath_" + lang, agentLangAware.getPlaceOfDeath(), Field.Store.YES));				
				doc.add(new TextField("placeOfDeath_index", agentLangAware.getPlaceOfDeath(), Field.Store.YES));
			}

			if (agentLangAware.getSameAs() != null && agentLangAware.getSameAs().size() > 0) {
				for (String a : agentLangAware.getSameAs()) {
					doc.add(new TextField("sameAs_" + lang, a, Field.Store.YES));
					doc.add(new TextField("sameAs_index", a, Field.Store.YES));					
				}
			}
		}
		return doc;
	}

	@Override
	public Agent mapToSource(Document luceneDocument, ProfileType profile) {
		Agent agent = new Agent();
		AgentLanguageNonAware aNonAware = new AgentLanguageNonAware();
		
		
		
		if (luceneDocument.get("agentIdString") != null) {
			agent.setId(new BigDecimal(luceneDocument.get("agentIdString")));
		}
		
		if (luceneDocument.get("dateOfBirthString") != null) {
			aNonAware.setDateOfBirth((luceneDocument.get("dateOfBirthString")));
		}
		
		if (luceneDocument.get("dateOfDeathString") != null) {
			aNonAware.setDateOfDeath((luceneDocument.get("dateOfDeathString")));
		}
		
		if (luceneDocument.get("dateOfTerminationString") != null) {
			aNonAware.setDateOfTermination((luceneDocument.get("dateOfTerminationString")));
		}
		
		if (luceneDocument.get("dateOfEstablishmentString") != null) {
			aNonAware.setDateOfEstablishment((luceneDocument.get("dateOfEstablishmentString")));
		}						
		
		for (int i = 0; i < luceneDocument.getValues("agentLanguage").length; i++) {
			AgentLanguageAware aLangAware = new AgentLanguageAware();
			String lang = luceneDocument.getValues("agentLanguage")[i];
			aLangAware.setLanguage(lang);

			if (luceneDocument.get("agentPreferredLabel_" + lang) != null) {
				aLangAware.setPreferredLabel(luceneDocument.get("agentPreferredLabel_" + lang));
			}

			if (luceneDocument.get("agentAlternativeLabel_" + lang) != null) {
				aLangAware
						.setAlternativeLabel(Arrays.asList(luceneDocument.getValues("agentAlternativeLabel_" + lang)));
			}
			if (luceneDocument.get("identifier_" + lang) != null) {
				List<String> list = new ArrayList<>();
				for (int k = 0; k < luceneDocument.getValues("identifier_" + lang).length; k++) {
					list.add(luceneDocument.getValues("identifier_" + lang)[k]);
				}
				aLangAware.setIdentifier(list);
			}

			if (luceneDocument.get("biographicalInformation_" + lang) != null) {
				aLangAware.setBiographicalInformation(luceneDocument.get("biographicalInformation_" + lang));
			}
			if (luceneDocument.get("gender_" + lang) != null) {
				aLangAware.setGender(luceneDocument.get("gender_" + lang));
			}
			if (luceneDocument.get("professionOrOccupation_" + lang) != null) {
				aLangAware.setProfessionOrOccupation(luceneDocument.get("professionOrOccupation_" + lang));
			}
			if (luceneDocument.get("placeOfDeath_" + lang) != null) {
				aLangAware.setPlaceOfDeath(luceneDocument.get("placeOfDeath_" + lang));
			}
			if (luceneDocument.get("placeOfBirth_" + lang) != null) {
				aLangAware.setPlaceOfBirth(luceneDocument.get("placeOfBirth_" + lang));
			}

			if (luceneDocument.getValues("sameAs_" + lang).length > 0) {
				List<String> list = new ArrayList<>();
				for (int j = 0; j < luceneDocument.getValues("sameAs_" + lang).length; j++) {
					list.add(luceneDocument.getValues("sameAs_" + lang)[j]);
				}
				aLangAware.setSameAs(list);
			}

			agent.getLanguageAwareFields().add(aLangAware);
		}

		agent.setLanguageNonAwareFields(aNonAware);

		return agent;
	}

	@Override
	public void configureFacets(FacetsConfig config) {
		config.setMultiValued("gender", true);
		config.setMultiValued("language", true);
		config.setMultiValued("dateOfBirth", true);
		config.setMultiValued("dateOfDeath", true);
		config.setMultiValued("dateOfEstablishment", true);
		config.setMultiValued("dateOfTermination", true);
		config.setMultiValued("gender", true);
		config.setMultiValued("placeOfBirth", true);
		config.setMultiValued("placeOfDeath", true);
		config.setMultiValued("agentAlternativeLabel", true);
		config.setMultiValued("identifier", true);
		config.setMultiValued("biographicalInformation", true);
		config.setMultiValued("professionOrOccupation", true);
		config.setMultiValued("agentPreferredLabel", true);
		config.setMultiValued("sameAs", true);
		config.setMultiValued("dataOwner", true);
		config.setMultiValued("choIdString", true);
	}

}
