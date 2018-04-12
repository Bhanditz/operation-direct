package eu.europeana.direct.backend.repositories;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.CulturalHeritageObject;
import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.backend.model.WebResource;
import eu.europeana.direct.backend.model.DeletedEntity;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;

/**
 * Data access class for Operation Direct Cultural Heritage Object
 */
public class CulturalHeritageObjectRepository extends BaseRepository {

	final static Logger logger = Logger.getLogger(CulturalHeritageObjectRepository.class);	
	
	// ID's of entities that we're not deleted because of existing relation to other CHO's (other than currently deleting)
	private List<String> notDeletedEntities = new ArrayList<String>();
	// ID's of entities that we're deleted with CHO (because of the relation)
	private List<DeletedEntity> deletedEntities = new ArrayList<DeletedEntity>();
	
	public CulturalHeritageObjectRepository() {		
		super();
	}

	public CulturalHeritageObjectRepository(EntityManager manager) {
		super(manager);
	}
	
	public CulturalHeritageObject persistObject(CulturalHeritageObject cho){
		try{
			manager.persist(cho);		
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			throw e;
		}	
		return manager.find(CulturalHeritageObject.class, cho.getId());
	}
	
	public Object persistRelated(Object obj){
		manager.persist(obj);		
		return obj;
	}			

	/**
	 * Saves CulturalHeritageObject to database, if CulturalHeritageObject has
	 * id then updates CulturalHeritageObject.
	 * 
	 * @param cho
	 *            CulturalHeritageObject
	 * @return long ID of saved CulturalHeritageObject
	 */
	public long saveCHO(CulturalHeritageObject cho, boolean commitIndex) {
		boolean localTrans = false;
		try {
			if (!manager.getTransaction().isActive()) {				
				manager.getTransaction().begin();
				localTrans = true;
			}
			
			if (cho.getId() > 0){
				
			}else{				
				manager.persist(cho);				
			}
		
			if(localTrans){				
				manager.getTransaction().commit();	
			}												
			return (long) cho.getId();
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
	 * @param id Uniqiue ID of Cultural Heritage Object
	 * @param onlyRelatedEntities
	 *            If true, deletes only related entities of Cultural Heritage
	 *            Object
	 * @param onlyCHO
	 *            If true, deletes only CHO but not entities related to CHO
	 * @param listIds
	 *            ID's of related entities that won't be deleted
	 * @return id ID of deleted Cultural Heritage Object
	 */
	public long deleteCHO(long id, boolean onlyRelatedEntities, boolean onlyCHO, List<Long> listIds) {
		CulturalHeritageObject cho = manager.find(CulturalHeritageObject.class, id);

		boolean localTransaction = false;

		if (cho != null) {			
			try {
				if (!manager.getTransaction().isActive()) {
					manager.getTransaction().begin();
					localTransaction = true;
				}								
				
				if (!onlyCHO) {
					Set<EuropeanaDataObjectEuropeanaDataObject> linkedObjectIdList = cho.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject();					
					// deleting only related entities that are not specified in listIds
					if (listIds != null) {					
						for (Iterator<EuropeanaDataObjectEuropeanaDataObject> iter = linkedObjectIdList
								.iterator(); iter.hasNext();) {
							EuropeanaDataObjectEuropeanaDataObject edoedo = iter.next();
							
							if (!listIds.contains(edoedo.getEuropeanaDataObjectByLinkedObjectId().getId())) {																																																																
								
								/* If size is not higher than 1, that means linkedObject is only related to CHO that we are currently deleting.
								 * In this case we delete linkedObject and relation beetwen CHO and linkedObject
								 */
								if(!(edoedo.getEuropeanaDataObjectByLinkedObjectId().getEuropeanaDataObjectForLinkedObject().size() > 1)){									
									// add entity to list of deleted entities
									deletedEntities.add(new DeletedEntity(edoedo.getEuropeanaDataObjectByLinkedObjectId().getId(),edoedo.getRoleType()));
									// removes relation between contextual entity and cho
									manager.remove(edoedo);
									
									switch(edoedo.getRoleType()){
										
									case Agent:										
										// removes agent entity										
										manager.remove(edoedo.getEuropeanaDataObjectByLinkedObjectId().getAgent());											
										break;
									case Concept:										
										// removes concept entity
										manager.remove(edoedo.getEuropeanaDataObjectByLinkedObjectId().getConcept());
										break;
									case Location:
										// removes location (place) entity
										manager.remove(edoedo.getEuropeanaDataObjectByLinkedObjectId().getPlace());
										break;
									case Timespan:
										// removes timespan entity
										manager.remove(edoedo.getEuropeanaDataObjectByLinkedObjectId().getTimespan());
										break;
									}																																			
								} else {
									// add entities that we're not deleted (because of relation to others CHO's) to list
									notDeletedEntities.add(String.valueOf(edoedo.getEuropeanaDataObjectByLinkedObjectId().getId()));
								}
							}
						}
					}				
				}

				// also set deletion date for EDO and set lang-aware and lang-non-aware values of CHO to null
				if (!onlyRelatedEntities) {										
					if(onlyCHO){
						// remove all relations to contextual entities
						for(EuropeanaDataObjectEuropeanaDataObject edoedo : cho.getEuropeanaDataObject().getEuropeanaDataObjectForLinkingObject()){
							manager.remove(edoedo);
						}	
					}																	
					cho.getEuropeanaDataObject().setEuropeanaDataObjectForLinkingObject(null);					
					cho.getEuropeanaDataObject().setEuropeanaDataObjectForLinkedObject(null);
					cho.getEuropeanaDataObject().setDeletionDate(new Date());
					cho.setLanguageAwareFields(null);
					cho.setLanguageNonAwareFields(null);
				}
								
				if (localTransaction) {						
					manager.getTransaction().commit();
				}					
				
				return cho.getId();
			} catch (Exception ex) {
				try {
					ex.printStackTrace();
					manager.getTransaction().rollback();
					return 0;
				} catch (Exception ex2) {
					// We are out of options - ignore
				}
				throw ex;
			}
		} else {
			return 0;
		}
	}

	/**
	 * Method check if CHO's related entity is also in use with another CHO
	 * 
	 * @param choId
	 *            Unique ID of Cultural Heritage Object
	 * @param relatedEntityId
	 *            Unique ID of related entity
	 * @return True if entity is in use, false if not
	 */
	public boolean EntityInUse(long choId, long relatedEntityId) {
		try {
			final Query choQuery = manager.createNamedQuery("EuropeanaDataObjectEuropeanaDataObject.isRelated");
			choQuery.setParameter("choId", choId);
			choQuery.setParameter("linkedObject", relatedEntityId);
			List<EuropeanaDataObjectEuropeanaDataObject> list = choQuery.getResultList();
			if (list.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (NoResultException nex) {
			return false;
		}
	}

	/**
	 * 
	 * @param id
	 *            Unique id for CulturalHeritageObject
	 * @return CulturalHeritageObject object
	 */
	public CulturalHeritageObject getCHO(long id) {
		CulturalHeritageObject cho = null;
		try {
			cho = manager.find(CulturalHeritageObject.class, id);						
			if(cho != null && cho.getLanguageNonAwareFields() == null && cho.getLanguageAwareFields() == null && cho.getEuropeanaDataObject() != null && cho.getEuropeanaDataObject().getDeletionDate() != null){
				return null;
			}			
		} catch (NoResultException nex) {
			return null;
		}
		
		return cho;
	}
	
	public List<CulturalHeritageObject> loadByLimit(int startFromNow, int recordsNum) {
		try {
			final Query choQuery = manager.createQuery("SELECT cho FROM CulturalHeritageObject cho ORDER BY cho.id ASC");
			choQuery.setFirstResult(startFromNow);
			choQuery.setMaxResults(recordsNum);
			return (List<CulturalHeritageObject>) choQuery.getResultList();		
		} catch (NoResultException nex) {
			return null;
		}
	}
	
	public List<CulturalHeritageObject> loadByDataOwner(String dataOwner) {
		try {
			final Query choQuery = manager.createNamedQuery("CulturalHeritageObject.loadByDataOwner");
			choQuery.setParameter("dt", dataOwner);			
			return (List<CulturalHeritageObject>) choQuery.getResultList();			
		} catch (NoResultException nex) {
			return null;
		}
	}
		
	public List<String> getNotDeletedEntities() {
		return notDeletedEntities;
	}
	
	public List<DeletedEntity> getDeletedEntities() {
		return deletedEntities;
	}

	public List<CulturalHeritageObject> fromTo(long id,long id2) {
		try {
			final Query choQuery = manager.createNamedQuery("CulturalHeritageObject.sortedFindAll");
			choQuery.setParameter("id",id);
			choQuery.setParameter("idd",id2);
			return (List<CulturalHeritageObject>) choQuery.getResultList();			
		} catch (NoResultException nex) {
			return null;
		}
	}
}
