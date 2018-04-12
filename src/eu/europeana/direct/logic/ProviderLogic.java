package eu.europeana.direct.logic;

import javax.persistence.EntityManager;

import eu.europeana.direct.backend.model.Provider;
import eu.europeana.direct.backend.repositories.ProviderRepository;

public class ProviderLogic {
	
	private final ProviderRepository repository;

	public ProviderLogic() {
		repository = new ProviderRepository();
	}

	public ProviderLogic(EntityManager manager) {
		repository = new ProviderRepository(manager);
	}
	
	public Provider saveProvider(Provider provider) {
		return repository.saveProvider(provider);		
	}
	
	public Provider getByName(String name){
		return repository.getProviderByName(name);
	}

	public void close(){
		repository.clear();
	}

	public Provider getProvider(int id){
		return repository.getProvider(id);
	}
}
