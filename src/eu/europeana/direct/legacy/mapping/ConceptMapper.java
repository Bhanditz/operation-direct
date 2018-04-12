package eu.europeana.direct.legacy.mapping;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;

import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.legacy.model.search.ProfileType;
import eu.europeana.direct.rest.gen.java.io.swagger.model.AgentLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;

public class ConceptMapper implements EntityIndexingMapper<Concept> {

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
	
	@Override
	public Document mapFromSource(Concept concept, Date created, Set<EuropeanaDataObjectEuropeanaDataObject> linkedObjects) {
		Document doc = new Document();

		if(concept.getId() != null){
			doc.add(new TextField("conceptIdString", "" + concept.getId(), Field.Store.YES));
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
		
		doc.add(new TextField("object_type","Concept",Field.Store.YES));

		for(ConceptLanguageAware cLangAware : concept.getLanguageAwareFields()){
			String lang = cLangAware.getLanguage();
			doc.add(new TextField("conceptLanguage",lang, Field.Store.YES));
			doc.add(new FacetField("language",lang));

			if(cLangAware.getPreferredLabel() != null && cLangAware.getPreferredLabel().length() > 0){
				doc.add(new TextField("conceptPreferredLabel_"+lang,cLangAware.getPreferredLabel(), Field.Store.YES));				
				doc.add(new TextField("conceptPreferredLabel_index",cLangAware.getPreferredLabel(), Field.Store.YES));
			}
			
			if(cLangAware.getAlternativeLabel() != null && cLangAware.getAlternativeLabel().size() > 0){
				for(String a : cLangAware.getAlternativeLabel()){
					doc.add(new TextField("conceptAlternativeLabel_"+lang, a, Field.Store.YES));
					doc.add(new TextField("conceptAlternativeLabel_index", a, Field.Store.YES));					
				}
			}			
		}		
		return doc;
	}

	@Override
	public Concept mapToSource(Document luceneDocument, ProfileType profile) {
		Concept concept = new Concept();
		
		if(luceneDocument.get("conceptIdString") != null){
			concept.setId(new BigDecimal(luceneDocument.get("conceptIdString")));
		}
				
		for(int i=0; i < luceneDocument.getValues("conceptLanguage").length;i++){
			ConceptLanguageAware cLangAware = new ConceptLanguageAware();
			String lang = luceneDocument.getValues("conceptLanguage")[i];
			cLangAware.setLanguage(lang);
			
			if(luceneDocument.get("conceptPreferredLabel_"+lang) != null){
				cLangAware.setPreferredLabel(luceneDocument.get("conceptPreferredLabel_"+lang));
			}
			
			if(luceneDocument.get("conceptAlternativeLabel_"+lang) != null){
				cLangAware.setAlternativeLabel(Arrays.asList(luceneDocument.getValues("conceptAlternativeLabel_"+lang)));
			}	
			concept.getLanguageAwareFields().add(cLangAware);
		}			
		return concept;
	}

	@Override
	public void configureFacets(FacetsConfig config) {
		config.setMultiValued("language", true);
		config.setMultiValued("conceptPreferredLabel", true);
		config.setMultiValued("conceptAlternativeLabel", true);
		config.setMultiValued("dataOwner", true);
		config.setMultiValued("choIdString", true);

	}

}
