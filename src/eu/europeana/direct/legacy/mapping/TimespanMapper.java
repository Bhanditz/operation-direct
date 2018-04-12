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
import eu.europeana.direct.legacy.model.search.ProfileType;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.ConceptLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpanLangaugeAware;

public class TimespanMapper implements EntityIndexingMapper<TimeSpan>{

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Document mapFromSource(TimeSpan source, Date created, Set<EuropeanaDataObjectEuropeanaDataObject> linkedObjects) {
		Document doc = new Document();

		if(source.getId() != null){
			doc.add(new TextField("timespanIdString", "" + source.getId(), Field.Store.YES));		
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
		
		doc.add(new TextField("object_type","Timespan",Field.Store.YES));

		for(TimeSpanLangaugeAware tsLangAware : source.getLanguageAwareFields()){
			String lang = tsLangAware.getLanguage();
			doc.add(new TextField("timespanLanguage",lang, Field.Store.YES));
			doc.add(new FacetField("language",lang));

			if(tsLangAware.getPreferredLabel() != null && tsLangAware.getPreferredLabel().length() > 0){
				doc.add(new TextField("timespanPreferredLabel_"+lang,tsLangAware.getPreferredLabel(), Field.Store.YES));				
				doc.add(new TextField("timespanPreferredLabel_index",tsLangAware.getPreferredLabel(), Field.Store.YES));
			}
			
			if(tsLangAware.getAlternativeLabel() != null && tsLangAware.getAlternativeLabel().size() > 0){
				for(String a : tsLangAware.getAlternativeLabel()){
					doc.add(new TextField("timespanAlternativeLabel_"+lang, a, Field.Store.YES));
					doc.add(new TextField("timespanAlternativeLabel_index", a, Field.Store.YES));
				}
			}
			
		}
		
		return doc;
	}

	@Override
	public TimeSpan mapToSource(Document luceneDocument, ProfileType profile) {
		TimeSpan timespan = new TimeSpan();
		
		if(luceneDocument.get("timespanIdString") != null){
			timespan.setId(new BigDecimal(luceneDocument.get("timespanIdString")));
		}
				
		for(int i=0; i < luceneDocument.getValues("timespanLanguage").length;i++){
			TimeSpanLangaugeAware tsLangAware = new TimeSpanLangaugeAware();
			String lang = luceneDocument.getValues("timespanLanguage")[i];
			tsLangAware.setLanguage(lang);
			
			
			if(luceneDocument.get("timespanPreferredLabel_"+lang) != null){
				tsLangAware.setPreferredLabel(luceneDocument.get("timespanPreferredLabel_"+lang));
			}
			
			List<String> list = new ArrayList<>();
			if(luceneDocument.getValues("timespanAlternativeLabel_"+lang).length > 0){
				for(int j=0; j < luceneDocument.getValues("timespanAlternativeLabel_"+lang).length; j++){
					list.add(luceneDocument.getValues("timespanAlternativeLabel_"+lang)[j]);
				}		
				tsLangAware.setAlternativeLabel(list);
			}
		
			timespan.getLanguageAwareFields().add(tsLangAware);
		}
				
		return timespan;
	}

	@Override
	public void configureFacets(FacetsConfig config) {
		config.setMultiValued("language", true);
		config.setMultiValued("timespanPreferredLabel", true);
		config.setMultiValued("timespanAlternativeLabel", true);	
		config.setMultiValued("dataOwner", true);
		config.setMultiValued("choIdString", true);
	}

}
