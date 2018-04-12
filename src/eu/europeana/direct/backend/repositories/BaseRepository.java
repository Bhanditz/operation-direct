package eu.europeana.direct.backend.repositories;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.Agent;
import eu.europeana.direct.backend.model.Country;
import eu.europeana.direct.backend.model.CulturalHeritageObject;
import eu.europeana.direct.backend.model.EuropeanaDataObject;
import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;

/**
 * 
 * Base DAO class
 *
 */
public class BaseRepository {
	protected EntityManager manager;
	final static Logger logger = Logger.getLogger(BaseRepository.class);	
	
	/**
	 * Closes entity manager is manager is not null and is open.
	 */
	public void close() {
		if (manager != null && manager.isOpen()) {			
			manager.close();				
		}
	}	
	
	public void clear(){
		manager.clear();
	}
	
	
	public long saveCountry(Country agent) {
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
	
	public void flush(){
		manager.flush();
	}
	
	public BaseRepository() {
		manager = JpaHelper.getManager();
	}

	public BaseRepository(EntityManager manager) {
		this.manager = manager;
	}

	public EntityManager getManager() {
		return manager;
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		// Ensure we close the manager, if it was not closed by the
		// instantiating object
	/*	try {
			if (manager != null && manager.isOpen()) {
				if(manager.getTransaction().isActive()){
					
					manager.getTransaction().commit();
					logger.error("FINALIZE CLOSE - FIX ME");
				}
				manager.close();
								
			}
		} catch (Exception ex) {
			// Ignore exception
		}*/
	}
}
