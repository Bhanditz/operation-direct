package eu.europeana.direct.logic;

import java.util.List;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.DirectUser;
import eu.europeana.direct.backend.model.Provider;
import eu.europeana.direct.backend.repositories.CulturalHeritageObjectRepository;
import eu.europeana.direct.backend.repositories.ProviderRepository;
import eu.europeana.direct.backend.repositories.UserRepository;
import eu.europeana.direct.logic.entities.AgentLogic;

public class UserLogic {

	private final UserRepository repository = new UserRepository();
	private final static Logger logger = Logger.getLogger(UserLogic.class);	
	
	public void close() {
		repository.close();
	}
	
	public int saveUser(DirectUser user, String institution){				
		user.setInstitution(findProviderByName(institution));
		return repository.saveUser(user);		
	}
	
	public int saveUser(DirectUser user){						
		return repository.saveUser(user);		
	}
	
	public Provider findProviderByName(String name){

		ProviderLogic providerLogic = new ProviderLogic(repository.getManager());
		
		Provider provider = providerLogic.getByName(name);
		// if provider doesn't exists yet, save it first
		if(provider == null){
			provider = new Provider(name);
			provider = providerLogic.saveProvider(provider);
		}
		
		return provider;		
	}
	
	public boolean isValidAPIKey(String apikey){
		return repository.isValidAPIKey(apikey);
	}

	public DirectUser findByUsername(String username) {
		return repository.findByUsername(username);
	}

	public DirectUser findByMail(String email) {
		return repository.findByMail(email);
	}
		
	public List<DirectUser> findUnapproved() {
		return repository.findUnapproved();
	}

	public DirectUser findByApikey(String apikey) {
		return repository.findByApikey(apikey);
	}
	
	
}
