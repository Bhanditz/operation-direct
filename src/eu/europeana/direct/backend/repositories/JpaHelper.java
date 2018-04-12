package eu.europeana.direct.backend.repositories;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaHelper {
	private static EntityManagerFactory emf;

	public static final EntityManager getManager() {
		if (emf == null || emf.isOpen() == false)
			emf = Persistence.createEntityManagerFactory("EuropeanaDirect");

		EntityManager em = emf.createEntityManager();

		return em;
	}
}
