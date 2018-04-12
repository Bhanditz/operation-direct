package eu.europeana.direct.logic.entities;

import java.io.IOException;
import java.math.BigDecimal;

import javax.persistence.EntityManager;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.WebResource;
import eu.europeana.direct.backend.model.WebResourceType;
import eu.europeana.direct.backend.repositories.WebLinkRepository;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.messaging.MessageProducer;
import eu.europeana.direct.messaging.MessageType;
import eu.europeana.direct.messaging.add.IndexMessage;
import eu.europeana.direct.messaging.delete.DeleteIndexMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.model.KeyValuePair;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink.TypeEnum;

public class WebLinkLogic {
	private final WebLinkRepository repository;
	private final static Logger logger = Logger.getLogger(WebLinkLogic.class);	

	public WebLinkLogic() {
		repository = new WebLinkRepository();		
	}

	public WebLinkLogic(EntityManager manager) {
		repository = new WebLinkRepository(manager);	
	}	

	/**
	 * Maps database WebResource model to Operation Direct WebLink model
	 * 
	 * @param databaseCho
	 *            Database WebResource model
	 * @return Operation Direct WebLink model
	 */
	public WebLink mapFromDatabase(eu.europeana.direct.backend.model.WebResource databaseCho) {		
		WebLink choMapped = new WebLink();

		choMapped.setId(new BigDecimal(databaseCho.getId()));
		if(databaseCho.getLink() != null){
			choMapped.setLink(databaseCho.getLink());	
		}		
		if(databaseCho.getOwner() != null){
			choMapped.setOwner(databaseCho.getOwner());	
		}
		if(databaseCho.getRights() != null){
			choMapped.setRights(databaseCho.getRights());	
		}
		
		if(databaseCho.getResourceType() != null){
			choMapped.setType(TypeEnum.values()[databaseCho.getResourceType().ordinal()]);	
		}		
		
		return choMapped;
	}

	/**
	 * Maps Operation Direct WebLink to database WebResource model
	 * 
	 * @param weblink
	 *            Operation Direct WebLink model
	 * @return Database WebResource model
	 */
	public eu.europeana.direct.backend.model.WebResource mapToDatabase(WebLink weblink) throws Exception {
		
		eu.europeana.direct.backend.model.WebResource newResource = new WebResource();
		boolean update = false;
		if (weblink.getId() != null && weblink.getId().longValue() > 0) {
			update = true;
			newResource = repository.getWebLink(weblink.getId().longValueExact());
			if (repository.getWebLink(weblink.getId().longValueExact()) == null) {
				throw new Exception("Weblink " + weblink.getId().longValueExact() + " does not exists!");
			}
		}

		String link = "";

		// link is required, if null throw exception
		if (weblink.getLink() != null && weblink.getLink().trim().length() > 0) {
			link = weblink.getLink();
		} else {
			throw new Exception("Required field link is missing for WebLink entity");
		}

		// type is required, if null throw exception
		if (weblink.getType() != null) {
			newResource.setResourceType(WebResourceType.values()[weblink.getType().ordinal()]);
			// if updating, we set new values that are specified in request for
			// weblink and update weblinks with that id

//			if (WebResourceType.values()[weblink.getType().ordinal()].equals(WebResourceType.DIRECT_MEDIA)) {
//				if (weblink.getLink().contains("museums.blob.core.windows.net")) {
//					link = weblink.getLink().replace("museums.blob.core.windows.net",
//							"testaccountsemantika.blob.core.windows.net");
//				}
//			}
		} else {
			throw new Exception("Required field type is missing for WebLink entity");	
		}

		newResource.setLink(link);

		// rights are required, if null throw exception
		if (weblink.getRights() != null && weblink.getRights().trim().length() > 0) {
			newResource.setRights(weblink.getRights());
		} else {
			throw new Exception("Required field rights is missing for WebLink entity");
		}
		if (weblink.getOwner() != null && weblink.getOwner().length() > 0) {
			newResource.setOwner(weblink.getOwner());
		}

		// saving custom field in weblink					
		if (weblink.getCustomFields() != null && !weblink.getCustomFields().isEmpty()) {
			for (KeyValuePair kv : weblink.getCustomFields()) {	
				if(kv.getKey() != null){
					newResource.getCustomFields().put(kv.getKey(),kv.getValue());	
				} else {
					throw new Exception("Key of one of the custom field for WebLink is null");
				}
				
			}
		}
		
		if (update) {
			//newResource.setId(weblink.getId().longValueExact());
		}

		return newResource;
	}

	public WebLink getWebLink(long id) {
		eu.europeana.direct.backend.model.WebResource cho = getWeblinkDb(id);
		if (cho == null) {
			return null;
		} else {
			WebLink result = mapFromDatabase(cho);
			return result;
		}	
	}
	
	
	public WebResource getWeblinkDb(long id){
		eu.europeana.direct.backend.model.WebResource w = repository.getWebLink(id);
		if(w != null){
			return w;
		}
		return null;
	}

	public long mapAndSaveWebLink(WebLink wl) throws Exception {
		long result = repository.saveWeblink(mapToDatabase(wl));
		wl.setId(new BigDecimal(result));
		return result;
	}

	public void sendIndexing(long weblinkId) throws IOException{
		try {
			MessageProducer.sendMsg(new IndexMessage(weblinkId, true, "weblink"), MessageType.UPDATE_INDEX);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int deleteWebLink(long id) {
		if (id < 1) {
			return 0;
		} else {
			WebResource wr = repository.getWebLink(id);
			if (wr != null) {
				// if webresource is related to cho we cannot delete
				// webresource
				if (repository.deleteWebLink(id)) {
					return 1;
				} else {
					return 0;
				}
			} else {
				return 0;
			}
		}
	}
	
	public void close(){
		repository.close();
	}
	
	public void deleteIndex(long id) {
		try {
			MessageProducer.sendMsg(new DeleteIndexMessage(id,LuceneDocumentType.Weblink,true),MessageType.DELETE_FROM_INDEX);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	public void startTransaction() {
		repository.getManager().getTransaction().begin();				
	}

	public void commitTransaction() {
		try {
			if(repository.getManager().getTransaction().isActive()){
				repository.getManager().getTransaction().commit();	
			}			
		} catch (Exception e) {
			repository.getManager().getTransaction().rollback();
			logger.error(e.getMessage(), e);
		}
		
	}

	public void rollbackTransaction() {
		try {
			if(repository.getManager().getTransaction().isActive()){
				repository.getManager().getTransaction().rollback();				
			}			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}	
	}
	
}
