package eu.europeana.direct.backend.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.backend.model.Place;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;

/**
 * Data access class for Operation Direct Place entity
 *
 */
public class PlaceRepository extends BaseRepository {

	final static Logger logger = Logger.getLogger(PlaceRepository.class);

	public PlaceRepository() {
		super();
	}

	public PlaceRepository(EntityManager manager) {
		super(manager);
	}

	public Place saveObject(Place cho){
		manager.persist(cho);
		return manager.find(Place.class, cho.getId());
	}
	
	/**
	 * Saves place to database, if place has id then updates place.
	 * @param place Place database model
	 * @return long ID of saved Place
	 */
	public long savePlace(Place place) {
		boolean update = false;
		boolean localTrans = false;
		try {
			if (!manager.getTransaction().isActive()) {												
				manager.getTransaction().begin();
				localTrans = true;
			}			
			if (place.getId() > 0) {
				place = manager.merge(place);
				manager.merge(place);
				update = true;
			} else {
				manager.persist(place);
			}
			if(localTrans){
				manager.getTransaction().commit();
			}					
			
			return (long) place.getId();
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
	 * @param id Unique id for Place
	 * @return Place
	 */
	public Place getPlace(long id) {
		try {
			Place p = manager.find(Place.class, id);
			
			return p;
		} catch (NoResultException nex) {
			logger.error(nex.getMessage(),nex);
			
			return null;
		}
	}	
	
	/**
	 * 
	 * @param id Unique id for Place
	 * @return True if deleted, false if not
	 */
	public boolean deletePlace(long id){
		boolean localTrans = false;
		try{		
			// deletes EuropeanaDataObjectEuropeanaDataObject with place id
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
			Place place = manager.find(Place.class, id);
			if(place != null){							
				manager.remove(place);									
				try{
					LuceneIndexing.getInstance().deleteFromIndex(place.getId(),LuceneDocumentType.Place,true,true);
				}catch(Exception e){
					logger.error(e.getMessage(),e);		
				}
			}
			// deletes EuropeanaDataObject with place id
			EuropeanaDataObject edo = manager.find(EuropeanaDataObject.class, id);
			if(edo != null){
				manager.remove(edo);
			}
			if(localTrans){
				manager.getTransaction().commit();
			}			
			return true;
		}catch(Exception e){		
			logger.error("Error deleting Place: "+e.getMessage(),e);
			manager.getTransaction().rollback();
			
			return false;
		}
	}
	

}
