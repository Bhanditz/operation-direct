package eu.europeana.direct.harvesting.jobs;

import java.io.IOException;
import java.util.List;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.UnableToInterruptJobException;

import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;

@DisallowConcurrentExecution
public class IndexingQuartzJob implements Job,InterruptableJob{
	
	private boolean interrupt = false;
	
	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {		
		CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();

		try{
			int start = 0;
			int recordsNum = 100;
			int i = 0;
			List<CulturalHeritageObject> list = null;
			HarvestThread workerThread = new HarvestThread(3);

			while(i == 0 || list.size() > 0){					
				if(interrupt){
					break;
				}
				
				list = choLogic.getByLimit(start,recordsNum);
				start += list.size();
				workerThread.execute(new IndexChoWorker(list));
				if(i%10 == 0){					
					workerThread.execute(new IndexThread());					
				}				
				if(i%5 == 0){
					choLogic.close();
					choLogic = new CulturalHeritageObjectLogic();
				}
				i++;
			}
		}catch(Exception e){
			e.printStackTrace();		
		}finally{			
			choLogic.close();
			LuceneIndexing.getInstance().commitIndex(true);			
		}
	}

	@Override
	public void interrupt() throws UnableToInterruptJobException {
		interrupt = true;
	}

	
}
