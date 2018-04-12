package eu.europeana.direct.backend.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.backend.model.Timespan;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;

/**
 * Data access class for Operation Direct Timespan entity
 *
 */
public class TimespanRepository extends BaseRepository {
	
	final static Logger logger = Logger.getLogger(TimespanRepository.class);
	
	public TimespanRepository() {
		super();
	}

	public TimespanRepository(EntityManager manager) {
		super(manager);
	}

	public Timespan saveObject(Timespan cho){
		manager.persist(cho);
		return manager.find(Timespan.class, cho.getId());
	}
	
	/**
	 * Saves Timespan to database, if Timespan has id then updates Timespan.
	 * @param timespan Timespan database model
	 * @return long ID of saved Timespan
	 */
	public long saveTimespan(Timespan timespan) {
		boolean update = false;
		boolean localTrans = false;
		try {

			if (!manager.getTransaction().isActive()) {												
				manager.getTransaction().begin();
				localTrans = true;
			}
			if (timespan.getId() > 0) {
				timespan = manager.merge(timespan);
				manager.merge(timespan);
				update = true;
			} else {
				manager.persist(timespan);
			}
			
			if(localTrans){
				manager.getTransaction().commit();
			}			
			
			return (long) timespan.getId();
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
	 * 
	 * @param id Unique id for Timespan
	 * @return Timespan object
	 */
	public Timespan getTimespan(long id) {
		try {
			Timespan ts = manager.find(Timespan.class,id);
			
			return ts;
		} catch (NoResultException nex) {
			logger.error(nex.getMessage(),nex);
			
			return null;
		}
	}
	
	
	/**
	 * 
	 * @param id Unique id for Timespan
	 * @return True if deleted, false if not
	 */
	public boolean deleteTimespan(long id){
		boolean localTrans = false;
		try{		
			// deletes EuropeanaDataObjectEuropeanaDataObject with timespan id
			final Query deleteQuery = manager.createNamedQuery("EuropeanaDataObjectEuropeanaDataObject.findByLinkedObjectId");
			deleteQuery.setParameter("objectId",id);
			List<EuropeanaDataObjectEuropeanaDataObject> edo_edoList = (List<EuropeanaDataObjectEuropeanaDataObject>) deleteQuery.getResultList();
			for(EuropeanaDataObjectEuropeanaDataObject edo_edo : edo_edoList){
				manager.remove(edo_edo);
			}	
			if(!manager.getTransaction().isActive()){
				manager.getTransaction().begin();
				localTrans = true;
			}
			
			
			Timespan timespan = manager.find(Timespan.class, id);
			if(timespan != null){							
				// deletes EuropeanaDataObject with timespan id
				manager.remove(timespan);									
				try{
					LuceneIndexing.getInstance().deleteFromIndex(timespan.getId(),LuceneDocumentType.Timespan,true,true);
				}catch(Exception e){
					logger.error(e.getMessage(),e);		
				}
			}
			
			EuropeanaDataObject edo = manager.find(EuropeanaDataObject.class, id);
			if(edo != null){
				manager.remove(edo);
			}
			if(localTrans){
				manager.getTransaction().commit();
			}			
			return true;
		}catch(Exception e){		
			logger.error("Error deleting Timespan: "+e.getMessage(),e);
			manager.getTransaction().rollback();
			
			return false;
		}
	}
	
}
