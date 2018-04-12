package eu.europeana.direct.backend.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.Agent;
import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;

/**
 * Data access class for Operation Direct Agent entity
 *
 */
public class AgentRepository extends BaseRepository {
	final static Logger logger = Logger.getLogger(AgentRepository.class);

	
	
	public AgentRepository() {
		super();
	}

	public AgentRepository(EntityManager manager) {
		super(manager);
	}

	/**
	 * Saves agent to database, if agent has id then updates agent.
	 * @param agent Agent database model
	 * @return long ID of saved Agent
	 */
	public long saveAgent(Agent agent) {
		boolean update = false;
		boolean localTrans = false;
		try {
			if (!manager.getTransaction().isActive()) {												
				manager.getTransaction().begin();
				localTrans = true;
			}
			if (agent.getId() > 0) {
				agent = manager.merge(agent);
				manager.merge(agent);
				update = true;
			} else {
				manager.persist(agent);
			}
			if(localTrans){
				manager.getTransaction().commit();	
			}
					
			return (long) agent.getId();
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
	 * @param id Unique id for Agent
	 * @return Agent
	 */
	public Agent getAgent(long id){		
		try {
			Agent a = manager.find(Agent.class, id);				
			return a;
		} catch (NoResultException nex) {
			logger.error(nex.getMessage(),nex);
			return null;
		}
	}
	
	public Agent saveObject(Agent cho){
		manager.persist(cho);
		return manager.find(Agent.class, cho.getId());
	}
	
	/**
	 * 
	 * @param id Unique id for Agent
	 * @return True if deleted, false if not
	 */
	public boolean deleteAgent(long id){
		boolean localTrans = false;
		try{		
			
			if(!manager.getTransaction().isActive()){
				manager.getTransaction().begin();
				localTrans = true;
			}
			
			// deletes EuropeanaDataObjectEuropeanaDataObject with agent id 
			final Query deleteQuery = manager.createNamedQuery("EuropeanaDataObjectEuropeanaDataObject.findByLinkedObjectId");
			
			deleteQuery.setParameter("objectId",id);
			List<EuropeanaDataObjectEuropeanaDataObject> edo_edoList = (List<EuropeanaDataObjectEuropeanaDataObject>) deleteQuery.getResultList();
			
			for(EuropeanaDataObjectEuropeanaDataObject edo_edo : edo_edoList){				
				manager.remove(edo_edo);
			}								
						
			Agent agent = manager.find(Agent.class, id);
			if(agent != null){							
				manager.remove(agent);													
			}
			
			//deletes EuropeanaDataObject with agent id
			EuropeanaDataObject edo = manager.find(EuropeanaDataObject.class, id);
			if(edo != null){
				manager.remove(edo);
			}
			if(localTrans){
				manager.getTransaction().commit();	
			}
								
			return true;
		}catch(Exception e){		
			logger.error("Error deleting Agent: "+e.getMessage(),e);
			manager.getTransaction().rollback();
			return false;
		}
	}	
}
