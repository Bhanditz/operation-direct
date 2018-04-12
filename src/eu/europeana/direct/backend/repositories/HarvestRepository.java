package eu.europeana.direct.backend.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import eu.europeana.direct.backend.model.HarvestSource;
import eu.europeana.direct.harvesting.logging.model.ImportLog;

/**
 * Data access class for Operation Direct harvesters
 *
 */
public class HarvestRepository extends BaseRepository{

	public HarvestRepository() {
		super();
	}

	public HarvestRepository(EntityManager manager) {
		super(manager);
	}
	
	
	/**
	 * 
	 * @return List of all harvesters
	 */
	public List<HarvestSource> getAll() {
		try {
			final Query choQuery = manager.createNamedQuery("HarvestSource.loadAll");
			List<HarvestSource> list = choQuery.getResultList();			
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}		
		return null;
	}
}
