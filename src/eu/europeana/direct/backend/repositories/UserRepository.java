package eu.europeana.direct.backend.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.Agent;
import eu.europeana.direct.backend.model.DirectUser;
import eu.europeana.direct.backend.model.Provider;

public class UserRepository extends BaseRepository {
	final static Logger logger = Logger.getLogger(UserRepository.class);

	public UserRepository() {
		super();
	}

	public UserRepository(EntityManager manager) {
		super(manager);
	}
	
	public int saveUser(DirectUser user) {		
		boolean localTrans = false;
		
		try {
			if (!manager.getTransaction().isActive()) {												
				manager.getTransaction().begin();
				localTrans = true;
			}
			
			manager.persist(user);
			
			if(localTrans){
				manager.getTransaction().commit();	
			}
					
			return user.getId();
		} catch (Exception ex) {
			try {
				manager.getTransaction().rollback();
			} catch (Exception ex2) {
				// We are out of options - ignore
			}
			throw ex;
		}
	}
	
	public boolean isValidAPIKey(String apikey) {
		final Query query = manager.createNamedQuery("DirectUser.findByAPIKey");
		List<Provider> list = null;
		try{
			query.setParameter("apikey",apikey);
			list = query.getResultList();			
		} catch (NoResultException nex) {
			return false;
		}		
		if(list != null && list.size() > 0){
			return true;
		}
		return false;
	}

	public DirectUser findByUsername(String username) {
		final Query query = manager.createNamedQuery("DirectUser.findByUsername");
		
		try{
			query.setParameter("username",username);			
			List<DirectUser> users = (List<DirectUser>) query.getResultList();
			if(users.size() > 0){
				return users.get(0);	
			}			
		} catch (NoResultException nex) {
			return null;
		}		
				
		return null;
	}

	public DirectUser findByMail(String email) {
		final Query query = manager.createNamedQuery("DirectUser.findByMail");
		
		try{
			query.setParameter("mail",email);			
			List<DirectUser> users = (List<DirectUser>) query.getResultList();
			if(users.size() > 0){
				return users.get(0);	
			}			
		} catch (NoResultException nex) {
			return null;
		}		
				
		return null;
	}

	public List<DirectUser> findUnapproved() {
		final Query query = manager.createNamedQuery("DirectUser.findUnapproved");
		
		try{			
			return (List<DirectUser>) query.getResultList();			
		} catch (NoResultException nex) {
			return null;
		}								
	}

	public DirectUser findByApikey(String apikey) {
	final Query query = manager.createNamedQuery("DirectUser.findByApikey");
		
		try{
			query.setParameter("apikey",apikey);			
			List<DirectUser> users = (List<DirectUser>) query.getResultList();
			if(users.size() > 0){
				return users.get(0);	
			}			
		} catch (NoResultException nex) {
			return null;
		}		
				
		return null;
	}
}
