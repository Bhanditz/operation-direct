package eu.europeana.direct.legacy.mapping;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.TextField;
import org.apache.lucene.facet.FacetField;
import org.apache.lucene.facet.FacetsConfig;

import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.legacy.model.search.ProfileType;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum;

public class WebLinkMapper implements WebLinkIndexMapper<WebLink> {

	private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public Document mapFromSource(WebLink weblink, Date created, String choId, String dataOwner) {
		Document doc = new Document();
		
		if(weblink.getId() != null){
			doc.add(new TextField("weblinkIdString", "" + weblink.getId(), Field.Store.YES));
		}		
		
		if(created == null){
			created = new Date();
		}
		
		doc.add(new TextField("createdString", simpleDateFormat.format(created), Field.Store.YES));		
		doc.add(new LongPoint("created", created.getTime()));	
		doc.add(new TextField("modifiedString", simpleDateFormat.format(new Date()), Field.Store.YES));		
		doc.add(new LongPoint("modified", new Date().getTime()));
								
		if(dataOwner != null){
			doc.add(new TextField("dataOwner",dataOwner, Field.Store.YES));
		}
		
		if(choId != null){
			doc.add(new TextField("choIdString",String.valueOf(choId), Field.Store.YES));
		}
				
		doc.add(new TextField("object_type","Weblink",Field.Store.YES));

		if(weblink.getLink() != null && weblink.getLink().length() > 0){
			doc.add(new TextField("link", weblink.getLink(), Field.Store.YES));			
		}		
		if(weblink.getOwner() != null && weblink.getOwner().length() > 0){
			doc.add(new TextField("owner", weblink.getOwner(), Field.Store.YES));			
		}		
		if(weblink.getRights() != null && weblink.getRights().length() > 0){
			doc.add(new TextField("rights", weblink.getRights(), Field.Store.YES));			
		}
		if(weblink.getType() != null){
			doc.add(new TextField("weblinkType", weblink.getType().toString(), Field.Store.YES));						
		}		
		return doc;
	}

	@Override
	public WebLink mapToSource(Document luceneDocument, ProfileType profile) {
		WebLink weblink = new WebLink();
		
		if(luceneDocument.get("weblinkIdString") != null){
			weblink.setId(new BigDecimal(luceneDocument.get("weblinkIdString")));
		}
		if(luceneDocument.get("link") != null){
			weblink.setLink(luceneDocument.get("link"));
		}
		if(luceneDocument.get("owner") != null){
			weblink.setOwner(luceneDocument.get("owner"));
		}
		if(luceneDocument.get("rights") != null){
			weblink.setRights(luceneDocument.get("rights"));
		}
		if(luceneDocument.get("weblinkType") != null){
			switch(luceneDocument.get("weblinkType")){
			case "DIRECT_MEDIA":
				weblink.setType(TypeEnum.DIRECT_MEDIA);
				break;
			case "LANDING_PAGE":
				weblink.setType(TypeEnum.LANDING_PAGE);
				break;
			case "PREVIEW_SOURCE":
				weblink.setType(TypeEnum.PREVIEW_SOURCE);
				break;
			case "OTHER":
				weblink.setType(TypeEnum.OTHER);
				break;			
			}
			
		}
		return weblink;
	}

	@Override
	public void configureFacets(FacetsConfig config) {
		config.setMultiValued("owner", true);
		config.setMultiValued("rights", true);
		config.setMultiValued("type", true);
		config.setMultiValued("weblinkType", true);
		config.setMultiValued("dataOwner", true);
		config.setMultiValued("choIdString", true);
	}

}
