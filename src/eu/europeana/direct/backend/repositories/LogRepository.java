package eu.europeana.direct.backend.repositories;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import eu.europeana.direct.harvesting.logging.model.ImportLog;
import eu.europeana.direct.harvesting.logging.model.ImportLogDetail;

public class LogRepository extends BaseRepository{

	final static Logger logger = Logger.getLogger(LogRepository.class);
	
	public LogRepository() {
		super();
	}

	public LogRepository(EntityManager manager) {
		super(manager);
	}
	
	public long saveImportLog(ImportLog importLog) {
		boolean localTrans = false;
		try {
			if (!manager.getTransaction().isActive()) {				
				manager.getTransaction().begin();
				localTrans = true;
			}
			manager.persist(importLog);
			if(localTrans){
				manager.getTransaction().commit();
			}
			return (long) importLog.getId();
		} catch (Exception ex) {
			try {
				manager.getTransaction().rollback();
			} catch (Exception ex2) {
				// We are out of options - ignore
			}
			throw ex;
		}
	}
	
	public long saveImportLogDetail(ImportLogDetail importLogDetail) {
		boolean localTrans = false;
		try {
			if (!manager.getTransaction().isActive()) {				
				manager.getTransaction().begin();
				localTrans = true;
			}
			manager.persist(importLogDetail);			
			if(localTrans){
				manager.getTransaction().commit();
			}			
			return (long) importLogDetail.getId();
		} catch (Exception ex) {
			try {
				manager.getTransaction().rollback();
			} catch (Exception ex2) {
				// We are out of options - ignore
			}
			throw ex;
		}
	}
	

	public List<ImportLog> getAllImportLog() {
		try {
					
			final Query choQuery = manager.createNamedQuery("ImportLog.loadAll");
			List<ImportLog> list = choQuery.getResultList();
			
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		close();
		return null;
	}
	public List<ImportLogDetail> getAllImportLogDetail() {
		try {
						
			final Query choQuery = manager.createNamedQuery("ImportLogDetail.loadAll");
			List<ImportLogDetail> list = choQuery.getResultList();
			
			return list;
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return null;
	}
	
}
