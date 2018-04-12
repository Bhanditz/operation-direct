package eu.europeana.direct.harvesting.jobs;

import java.util.Date;
import java.util.List;

import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

public class IndexChoWorker implements Runnable {

	private List<CulturalHeritageObject> listCho;
	
	public IndexChoWorker(List<CulturalHeritageObject> listCho) {
		this.listCho = listCho;
	}
	
	@Override
	public void run() {	
		try{
			if(listCho != null){
				for(CulturalHeritageObject cho : listCho){
					try{
						LuceneIndexing.getInstance().updateLuceneIndex(cho, true, false, new Date(),true);
					}catch(Exception e){
						e.printStackTrace();
					}			
				}		
			}	
		} finally {
			LuceneIndexing.getInstance().commitIndex(true);	
		}					
	}
}
