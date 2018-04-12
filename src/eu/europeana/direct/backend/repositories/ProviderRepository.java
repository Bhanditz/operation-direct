package eu.europeana.direct.backend.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.Provider;

public class ProviderRepository extends BaseRepository{

	final static Logger logger = Logger.getLogger(ProviderRepository.class);
	
	public ProviderRepository() {
		super();
	}

	public ProviderRepository(EntityManager manager) {
		super(manager);
	}
	
	@SuppressWarnings("unchecked")
	public Provider getProviderByName(String name){
		final Query query = manager.createNamedQuery("Provider.findByName");
		List<Provider> list = null;
		try{
			query.setParameter("name", name);
			list = query.getResultList();			
		} catch (NoResultException nex) {
			return null;
		}		
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

	public Provider saveProvider(Provider provider) {
		boolean localTrans = false;
		
		try {
			if (!manager.getTransaction().isActive()) {												
				manager.getTransaction().begin();
				localTrans = true;
			}			
			
			manager.persist(provider);
			
			if(localTrans){
				manager.getTransaction().commit();	
			}
					
			return provider;
		} catch (Exception ex) {
			try {
				manager.getTransaction().rollback();
			} catch (Exception ex2) {
				// We are out of options - ignore
			}
			throw ex;
		}
	}

	public Provider getProvider(int id) {
		Provider p = null;
		try{
			p = manager.find(Provider.class, id);
		} catch (NoResultException nex) {
			return null;
		}
		return p;
	}
	
}
