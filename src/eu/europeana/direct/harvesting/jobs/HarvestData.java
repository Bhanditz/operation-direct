package eu.europeana.direct.harvesting.jobs;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.InterruptableJob;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;

import eu.europeana.direct.harvesting.mapper.EdmMapper;
import eu.europeana.direct.harvesting.mapper.IMapper;
import eu.europeana.direct.harvesting.mapper.OaiDcMapper;
import eu.europeana.direct.harvesting.source.edm.ISourceReader;
import eu.europeana.direct.harvesting.source.edm.SourceReader;
import eu.europeana.direct.harvesting.source.edm.SourceReaderHelper;
import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.helpers.FileUtil;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObjectLanguageAware;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;
import inescid.opaf.europeanadirect.IiifEndpointHarvester;
import inescid.opaf.europeanadirect.RecordHandler;

/**
 * 
 * Quartz job for harvesting metadata from sources
 *
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class HarvestData implements Job, InterruptableJob {

	ISourceReader<EdmOaiSource> sourceReader = new SourceReader();
	IMapper<EdmOaiSource> edmMapper = new EdmMapper();
	IMapper<EdmOaiSource> oaiDcMapper = new OaiDcMapper();
	JobExecutionContext ctx = null;	
	final static Logger logger = Logger.getLogger(HarvestData.class);
	FileUtil fileUtil = new FileUtil();

	/**
	 * Executes harvesting job. Harvester will start harvesting from source.
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {	
	
		List<EdmOaiSource> edmOaiSourceList = new ArrayList<EdmOaiSource>();
		List<CulturalHeritageObject> culturalHeritageObjects = new ArrayList<CulturalHeritageObject>();
		
		// source for harvesting
		String source = context.getJobDetail().getJobDataMap().getString("source");
		String jobName = context.getJobDetail().getKey().getName();
		
		ctx = context;
		
		try {

			if (jobName.equals("NL Macedonia harvester") || jobName.equals("NL Austria harvester")
					|| jobName.equals("NL Romania harvester") || jobName.equals("bnf-fr harvester")) {
				
				edmOaiSourceList = sourceReader.readSourceDataFromOaiPmh(source, "oai_dc",jobName);

				/*if (jobName.equals("NL Romania harvester")) {
					for (EdmOaiSource e : edmOaiSourceList) {												
						//adding language value for record if it doesn't exists
						if (e.getProvidedCHO().getOaiDc().getLanguage().size() < 1) {
							e.getProvidedCHO().getOaiDc().getLanguage().add("ro");
						}
					}
				} else if (jobName.equals("NL Austria harvester")) {
					for (EdmOaiSource e : edmOaiSourceList) {					
						//adding language value for record if it doesn't exists
						if (e.getProvidedCHO().getOaiDc().getLanguage().size() < 1) {
							e.getProvidedCHO().getOaiDc().getLanguage().add("de");
						}
					}
				} else if (jobName.equals("NL Macedonia harvester")) {
					for (EdmOaiSource e : edmOaiSourceList) {
						//clearing language list because it contains values in Cyrillic language and adding proper ISO-639 two value language code
						e.getProvidedCHO().getOaiDc().getLanguage().clear();
						e.getProvidedCHO().getOaiDc().getLanguage().add("mk");
					}
				}
				for(EdmOaiSource edm : edmOaiSourceList){	        		
	        		// if no edm:type element, set default value to IMAGE
	        		if(edm.getEdmType() == null){
	        			edm.setEdmType("IMAGE");
	        		}	        		        		        		
	        	}*/
								

				oaiDcMapper.setInterrupt(false);
				
				//number of errors appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errors", oaiDcMapper.getErrors());
				//message for each error appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errorMessages", oaiDcMapper.getErrorMessages());
				//number of warnings appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warnings", oaiDcMapper.getWarnings());
				//message for each warning appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warningMessages", oaiDcMapper.getWarningMessages());
			
			} else if (jobName.equals("DDB harvester")){
				String apiKey = context.getJobDetail().getJobDataMap().getString("apiKey");
				CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();
				try{
					edmOaiSourceList = sourceReader.readSourceDataFromDDB(source, "edm", apiKey,choLogic);
				}finally{
					choLogic.close();
				}
				edmMapper.setInterrupt(false);
				
				//number of errors appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errors", edmMapper.getErrors());
				//message for each error appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errorMessages", edmMapper.getErrorMessages());				
				//number of warnings appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warnings", edmMapper.getWarnings());
				//message for each warning appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warningMessages", edmMapper.getWarningMessages());
				
			}else if(jobName.equals("APE harvester")){				
				String apiKey = context.getJobDetail().getJobDataMap().getString("apiKey");							
				CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();
				try{
					edmOaiSourceList = new SourceReaderHelper().getAPESourceList(apiKey,choLogic);
				}finally{
					choLogic.close();
				}
				edmMapper.setInterrupt(false);				

				//number of errors appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errors", edmMapper.getErrors());
				//message for each error appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errorMessages", edmMapper.getErrorMessages());				
				//number of warnings appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warnings", edmMapper.getWarnings());
				//message for each warning appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warningMessages", edmMapper.getWarningMessages());
			
				// TODO: interrupt method for IIIF harvester
			}else if(jobName.equals("IIIF harvester")){					
				try{
					final int maxRecords=3;
					IiifEndpointHarvester iiifHarvester=new IiifEndpointHarvester(
							"National Library of Wales", new File("target/iiif_harvester_workdir"));
					iiifHarvester.setCollectionStartUrl(new URL(source));
					
					
					iiifHarvester.runHarvest(new RecordHandler() {			
						int recordCount=0;
						
						@Override
						public boolean handleRecord(String jsonRecord) {
							recordCount++;							
							//return false when maxRecords is reached to stop the harvest in the middle
							return recordCount<maxRecords;
						}
					});
					CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();

					List<CulturalHeritageObject> list = new ArrayList<>();
					try {
						for (CulturalHeritageObject cho : iiifHarvester.getChoList()) {
							if (cho.getLanguageNonAwareFields().getDataOwner() == null) {
								cho.getLanguageNonAwareFields().setDataOwner("nlw");
							}
							for (Iterator<CulturalHeritageObjectLanguageAware> it = cho.getLanguageAwareFields()
									.iterator(); it.hasNext();) {
								CulturalHeritageObjectLanguageAware choLang = it.next();
								if (choLang.getLanguage() == null || choLang.getLanguage().length() < 1) {
									it.remove();
								}
								if (choLang.getTitle() == null || choLang.getTitle().length() < 1) {
									it.remove();
								}
							}

							for (WebLink wl : cho.getWebLinks()) {
								if (wl.getRights() == null || wl.getRights().length() < 1) {
									wl.setRights("https://creativecommons.org/licenses/by-nc-nd/4.0/");
								}
							}

							choLogic.mapAndSaveCHO(cho, true,false);

						}
					} finally {
						choLogic.close();
					}
					
					
					
					//number of errors appeared while saving CHO object in database
					context.getJobDetail().getJobDataMap().put("errors", 0);
					//message for each error appeared while saving CHO object in database
					context.getJobDetail().getJobDataMap().put("errorMessages", null);
					//number of warnings appeared while saving CHO object in database
					context.getJobDetail().getJobDataMap().put("warnings", 0);
					//message for each warning appeared while saving CHO object in database
					context.getJobDetail().getJobDataMap().put("warningMessages", null);					
				}catch (Exception e) {
					logger.error(e.getMessage(),e);					
				}
				
			}else if(jobName.equals("NM Warsaw harvester")){					
				
				edmOaiSourceList = sourceReader.readSourceDataFromOaiPmh(source, "mets",jobName);												
				edmMapper.setInterrupt(false);
				
				//number of errors appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errors", edmMapper.getErrors());
				//message for each error appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errorMessages", edmMapper.getErrorMessages());				
				//number of warnings appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warnings", edmMapper.getWarnings());
				//message for each warning appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warningMessages", edmMapper.getWarningMessages());				
			
			}else if(jobName.equals("MKG Hamburg harvester") || jobName.equals("MKGHamburg harvester")){
				
				/*String content = "";
				CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();
				try{
					for(int i = 2; i <= 8465; i++){
						try{
							content = fileUtil.readFile("/var/direct/wildfly/Edm"+i+".xml");
							EdmOaiSource edm = sourceReader.readSourceDataEdm(content, "Edm"+i);
							CulturalHeritageObject cho = edmMapper.mapFromEdm(edm);
							if(cho != null){
								if(cho.getLanguageNonAwareFields() != null){
									if(cho.getLanguageNonAwareFields().getDataOwner() != null){
										cho.getLanguageNonAwareFields().setDataOwner("MKG Hamburg");
									}
								}
							}
							choLogic.mapAndSaveCHO(cho, true);
						}catch(Exception e){
							logger.error("ERROR MKGHamburg harvester "+e.getMessage());
						}
					}	
				}finally{
					choLogic.close();
				}*/
				
				String content = "";				
				CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();
				try{
					for(int i = 2; i <= 190; i++){
						try{
							content = fileUtil.readFile("C:/Users/semantika/Desktop/resultZenaISvet/Edm"+i+".xml");													
							content = content.replace("<ore:aggregates>","");
							content = content.replace("</ore:aggregates>","");
							EdmOaiSource edm = sourceReader.readSourceDataEdm(content, "Edm"+i);							
							CulturalHeritageObject cho = edmMapper.mapFromEdm(edm);
							/*if(cho != null){
								if(cho.getLanguageNonAwareFields() != null){
									if(cho.getLanguageNonAwareFields().getDataOwner() != null){
										cho.getLanguageNonAwareFields().setDataOwner("MKG Hamburg");
									}
								}
							}*/
							choLogic.mapAndSaveCHO(cho, true, false);									
						}catch(Exception e){
							logger.error("ERROR MKGHamburg harvester "+e.getMessage());
						}
					}
					for(int i = 1; i <= 9; i++){
						
						try{
							content = fileUtil.readFile("C:/Users/semantika/Desktop/resultUBSM/Edm"+i+".xml");
							EdmOaiSource edm = sourceReader.readSourceDataEdm(content, "Edm"+i);
							CulturalHeritageObject cho = edmMapper.mapFromEdm(edm);
							/*if(cho != null){
								if(cho.getLanguageNonAwareFields() != null){
									if(cho.getLanguageNonAwareFields().getDataOwner() != null){
										cho.getLanguageNonAwareFields().setDataOwner("MKG Hamburg");
									}
								}
							}*/
							choLogic.mapAndSaveCHO(cho, true, false);														
						}catch(Exception e){
							logger.error("ERROR MKGHamburg harvester "+e.getMessage());
						}
					}
				}finally{
					choLogic.close();
				}
																
				
				edmMapper.setInterrupt(false);				
				
				//number of errors appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errors", edmMapper.getErrors());
				//message for each error appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errorMessages", edmMapper.getErrorMessages());
				//number of warnings appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warnings", edmMapper.getWarnings());
				//message for each warning appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warningMessages", edmMapper.getWarningMessages());
			
			}else if(jobName.equals("Europeana harvester")){
				
				edmOaiSourceList = sourceReader.readSourceDataFromOaiPmh("http://oai.europeana.eu/oaicat/OAIHandler", "edm","europeana");												
				edmMapper.setInterrupt(false);
				

				//number of errors appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errors", edmMapper.getErrors());
				//message for each error appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errorMessages", edmMapper.getErrorMessages());
				//number of warnings appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warnings", edmMapper.getWarnings());
				//message for each warning appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warningMessages", edmMapper.getWarningMessages());
			}else{
				edmOaiSourceList = sourceReader.readSourceDataFromOaiPmh(source, "edm",jobName);												
				edmMapper.setInterrupt(false);
				
				//number of errors appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errors", edmMapper.getErrors());
				//message for each error appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("errorMessages", edmMapper.getErrorMessages());
				//number of warnings appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warnings", edmMapper.getWarnings());
				//message for each warning appeared while saving CHO object in database
				context.getJobDetail().getJobDataMap().put("warningMessages", edmMapper.getWarningMessages());
			}
			
			//number of successfully saved CHO objects
			context.getJobDetail().getJobDataMap().put("successes", culturalHeritageObjects.size());	
						
			sourceReader.setInterrupt(false);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
	}

	/**
	 * Interrupts job(of type HarvestData) that is currently executing
	 */
	@Override
	public void interrupt() throws UnableToInterruptJobException {
		// interrupts source reader in case job is reading source at the time of interrupting
		sourceReader.setInterrupt(true);
		// interrupts both mappers in case job is mapping source to CHO at the time of interrupting
		edmMapper.setInterrupt(true);		
		oaiDcMapper.setInterrupt(true);
		
		try {
			QuartzJobListener listener = (QuartzJobListener) ctx.getScheduler().getListenerManager()
					.getJobListener("Quartz job listener");
			// tells listener that job was interrupted (for log purposes)
			listener.setInterrupted(true);
		} catch (SchedulerException e) {
			logger.error(e.getMessage(),e);
		}
	}
}
