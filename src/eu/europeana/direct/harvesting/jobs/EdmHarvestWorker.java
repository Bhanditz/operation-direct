package eu.europeana.direct.harvesting.jobs;

import java.math.BigDecimal;
import java.util.List;

import eu.europeana.direct.harvesting.mapper.IMapper;
import eu.europeana.direct.harvesting.source.edm.ISourceReader;
import eu.europeana.direct.harvesting.source.edm.model.EdmOaiSource;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import se.kb.oai.pmh.Header;
import se.kb.oai.pmh.Record;

public class EdmHarvestWorker implements Runnable {

	private List<Record> recordsList;
	private String metadataFormat;
	private String harvester;
	private ISourceReader<EdmOaiSource> sourceReader;
	private IMapper<EdmOaiSource> edmMapper;
	private IMapper<EdmOaiSource> oaiDcMapper;		
	
	public EdmHarvestWorker(List<Record> recordsList, String metadataFormat, String harvester, ISourceReader<EdmOaiSource> sourceReader, IMapper<EdmOaiSource> edmMapper, IMapper<EdmOaiSource> oaiDcMapper) {		
		this.recordsList = recordsList;		
		this.metadataFormat = metadataFormat;
		this.harvester = harvester;
		this.edmMapper = edmMapper;
		this.sourceReader = sourceReader;
		this.oaiDcMapper = oaiDcMapper;
	}
	
	@Override
	public void run() {
		
		//CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();	
			
		try{
		
			//choLogic.startTransaction();
			int j=0;			
			for (int i = 0; i < recordsList.size(); i++) {				
				try {
					// Record metadata								
					String recordXmlMetadata;
					try{
						recordXmlMetadata = recordsList.get(i).getMetadataAsString();	
						j++;
						
						Header header = recordsList.get(i).getHeader();
						String identifier = header.getIdentifier();

						// map metadata to edmOai model
						if (metadataFormat.equals("oai_dc") || metadataFormat.equals("mets")) {
							EdmOaiSource source = sourceReader.readSourceDataOaiDc(recordXmlMetadata, identifier);
							CulturalHeritageObject cho = oaiDcMapper.mapFromEdm(source);
							//choLogic.mapAndSaveCHO(cho, false);										
						} else {
							String dataOwner = null;
							if(harvester.equals("europeana")){	
								dataOwner = "EuropeanaTest";
							}														
																											
							EdmOaiSource source = sourceReader.readSourceDataEdm(recordXmlMetadata, identifier);
							CulturalHeritageObject cho = edmMapper.mapFromEdm(source);
							if(cho != null && dataOwner != null){
								cho.getLanguageNonAwareFields().setDataOwner(dataOwner);
							}			
							
							
							CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();
							try {
								choLogic.startTransaction();
								for (int k = 0; k < 50; k++) {
									cho.setId(new BigDecimal(0));
									if (cho.getAgents() != null && cho.getAgents().size() > 0) {
										cho.getAgents().stream().forEach(u -> u.setId(new BigDecimal(0)));
									}
									if (cho.getConcepts() != null && cho.getConcepts().size() > 0) {
										cho.getConcepts().stream().forEach(u -> u.setId(new BigDecimal(0)));
									}
									if (cho.getTemporal() != null && cho.getTemporal().size() > 0) {
										cho.getTemporal().stream().forEach(u -> u.setId(new BigDecimal(0)));
									}
									if (cho.getSpatial() != null && cho.getSpatial().size() > 0) {
										cho.getSpatial().stream().forEach(u -> u.setId(new BigDecimal(0)));
									}
									if (cho.getWebLinks() != null && cho.getWebLinks().size() > 0) {
										cho.getWebLinks().stream().forEach(u -> u.setId(new BigDecimal(0)));
									}
									choLogic.mapAndSaveCHO(cho, false, false);
								}
								choLogic.commitTransaction();								
							}finally {
								choLogic.close();
							}																		
						}	
					
					}catch(NullPointerException e){
						//no metadata for record, continue with loop					
					}							

				} catch (Exception e1) {
					e1.printStackTrace();
				}				
			}
			
			// commit saved records
			//choLogic.commitTransaction();					
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			//choLogic.close();
			//commit index 
			LuceneIndexing.getInstance().commitIndex(true);
			System.out.println("commit index after worker done");
		}					
	}
}
