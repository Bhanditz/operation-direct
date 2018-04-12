package eu.europeana.direct.backend.repositories;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.WebResource;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;


/**
 * Data access class for Operation Direct WebResource entity
 *
 */
public class WebLinkRepository extends BaseRepository {
	final static Logger logger = Logger.getLogger(WebLinkRepository.class);

	public WebLinkRepository() {
		super();
	}

	public WebLinkRepository(EntityManager manager) {
		super(manager);
	}

	/**
	 * 
	 * @param id Unique id for WebResource
	 * @return WebResource object
	 */
	public WebResource getWebLink(long id) {
		try {
			final WebResource cho = manager.find(WebResource.class, id);			
			return cho;
		}catch(Exception e){			
			logger.error(e.getMessage());			
			return null;
		}
	}	
	
	/**
	 * If WebResource exists, returns ID of WebResource
	 * @param id Unique id for WebResource
	 * @return long ID of WebResource
	 */
	public long checkExists(long id) {
		try {
			final Query agentQuery = manager
					.createNamedQuery("WebResource.checkExists");
			agentQuery.setParameter("id", id);
			final long wrId = (long) agentQuery.getSingleResult();			
			return wrId;
		} catch (NoResultException nex) {
			logger.error(nex.getMessage());			
			return 0;
		}
	}

	/**
	 * Saves WebResource to database, if WebResource has id then updates WebResource.
	 * @param weblink WebResource
	 * @return long ID of saved WebResource
	 */
	public long saveWeblink(WebResource weblink) {
		boolean update = false;
		boolean localTrans = false;
		try {
			if (!manager.getTransaction().isActive()) {												
				manager.getTransaction().begin();
				localTrans = true;
			}

			if (weblink.getId() > 0) {				
				weblink = manager.merge(weblink);
				manager.merge(weblink);
				update = true;
			} else {								
				weblink = manager.merge(weblink);				
				manager.persist(weblink);
			}
			if(localTrans){
				manager.getTransaction().commit();
			}			
			
			return weblink.getId();
		} catch (Exception ex) {
			try {
				manager.getTransaction().rollback();
			} catch (Exception ex2) {
				// We are out of options - ignore
			}
			throw ex;
		}
	}
	
	/**
	 * Method cheks if webresource is related to any CHO othan than that specified in parameter
	 * @param choId Unique ID of CHO
	 * @param wrId Unique ID of WebResource
	 * @return True if webresource is also in use with another CHO, false if not
	 */
	 public boolean WebResourceInUse(long choId, long wrId){
		WebResource wr = manager.find(WebResource.class, wrId);		
		if(wr.getEuropeanaDataObject().getId() == choId){			
			return true;
		}				
		return false;
	}			
	
	/**
	 * Deletes WebResource based on WebResoure ID
	 * @param id Unique id for WebResource
	 * @return True if deleted, false if not
	 */
	public boolean deleteWebLink(long id){
		boolean localTrans = false;
		try{		
			if(!manager.getTransaction().isActive()){
				manager.getTransaction().begin();
				localTrans = true;
			}			
			
			WebResource wl = manager.find(WebResource.class, id);
			if(wl != null){							
				manager.remove(wl);									
				try{
					LuceneIndexing.getInstance().deleteFromIndex(wl.getId(),LuceneDocumentType.Weblink,true,true);
				}catch(Exception e){
					logger.error(e.getMessage(),e);		
				}
			}		
			if(localTrans){
				manager.getTransaction().commit();
			}
			return true;
		}catch(Exception e){		
			logger.error("Error deleting Weblink: "+e.getMessage(),e);
			manager.getTransaction().rollback();			
			return false;
		}
	}
}
