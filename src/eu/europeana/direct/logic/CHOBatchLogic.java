package eu.europeana.direct.logic;

import java.util.List;

import javax.ws.rs.core.Response;

import eu.europeana.direct.backend.model.DeletedEntity;
import eu.europeana.direct.helpers.LogicHelper;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.legacy.index.LuceneSearchModel;
import eu.europeana.direct.legacy.model.search.Item;
import eu.europeana.direct.rest.gen.java.io.swagger.api.ApiResponseMessage;

public class CHOBatchLogic {

	
	public void deleteRecordsFromIndex(List<Item> items){
		
		
		for (Item item : items) {
			long id = Long.parseLong(item.getId().replace("/direct/", ""));					
			CulturalHeritageObjectLogic logic = new CulturalHeritageObjectLogic();			
			try {	
				logic.startTransaction();
				boolean deleted = logic.deleteCHO(id,false);					
				logic.commitTransaction();			

				if(deleted){
					// delete cho from index
					LuceneIndexing.getInstance().deleteFromIndex(id, LuceneDocumentType.CulturalHeritageObject, false, false);
					// deleted related entities
					for(DeletedEntity de : logic.getDeletedEntityIds()){
						LuceneIndexing.getInstance().deleteFromIndex(de.getId(), LogicHelper.edoRoleType2LuceneType(de.getEntityType()), false, false);
					}
	
				}				
			} catch (Exception ex) {
				logic.rollbackTransaction();
				ex.printStackTrace();						
			}finally {
				logic.close();
			}			
		}
		
	}
	
}
