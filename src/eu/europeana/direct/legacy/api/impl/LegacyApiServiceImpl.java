package eu.europeana.direct.legacy.api.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import eu.europeana.direct.authentication.ApiAuthenticator;
import eu.europeana.direct.backend.model.CulturalHeritageObject;
import eu.europeana.direct.harvesting.jobs.HarvestThread;
import eu.europeana.direct.harvesting.jobs.IndexChoWorker;
import eu.europeana.direct.legacy.api.LegacyApiService;
import eu.europeana.direct.legacy.helpers.ImageUtil;
import eu.europeana.direct.legacy.helpers.QueryHelper;
import eu.europeana.direct.legacy.helpers.SearchHelper;
import eu.europeana.direct.legacy.helpers.SearchQueryMapper;
import eu.europeana.direct.legacy.index.LuceneDocumentType;
import eu.europeana.direct.legacy.index.LuceneIndexing;
import eu.europeana.direct.legacy.index.LuceneSearchModel;
import eu.europeana.direct.legacy.logic.record.RecordCulturalHeritageObjectLogic;
import eu.europeana.direct.legacy.logic.search.SearchEngine;
import eu.europeana.direct.legacy.model.record.ResponseObject;
import eu.europeana.direct.legacy.model.search.Parameters;
import eu.europeana.direct.legacy.model.search.SearchViewModel;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.api.ApiResponseMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.api.NotFoundException;

public class LegacyApiServiceImpl extends LegacyApiService{

	final static Logger logger = Logger.getLogger(LegacyApiServiceImpl.class);
	private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
			.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Unauthorized")).build();

	// engine responsible for searching records in main index based on query we provide
	private SearchEngine searchEngine = new SearchEngine();
	
	private SearchHelper searchHelper = new SearchHelper();
	private ImageUtil imageUtil = new ImageUtil();
	private QueryHelper queryHelper = new QueryHelper();	
	private SearchQueryMapper searchQueryMapper;
	
	
	/**
	 * Returns image based on uri parameter
	 */
	@Override
  	public Response getFullImage(UriInfo info) throws NotFoundException {
		
		String url = null;
		
		if(info.getQueryParameters().getFirst("uri") != null){						
			url = info.getQueryParameters().getFirst("uri");							
			if(url != null && url.length() > 1){
				try {													
					ByteArrayInputStream bais = imageUtil.generateImage(url);										
					if(bais != null){
						return Response.ok(bais).build();
					}				
				} catch (Exception e){
					e.printStackTrace();
				}			
			}			
		}		
		return Response.noContent().build();
	}

	/**
	 * Retrieves records from index based on search query parameters 
	 */
	@Override
	public Response getRecords(UriInfo info) throws NotFoundException {									
		String apiKey = info.getQueryParameters().getFirst("wskey");
		if(apiKey != null && ApiAuthenticator.validApiKey(apiKey)){
			// default number of records returned
			int rows = 12;
			SearchViewModel searchViewModel = new SearchViewModel();
			LuceneSearchModel luceneSearchModel = null;
			// type of lucene document (cho,agent,...)
			LuceneDocumentType documentType = null;		
			Parameters searchParameters = new Parameters();		
			
			boolean hasLandingAttr = false;
			boolean hasThumbnailAttr = false;
							
			String object = info.getQueryParameters().getFirst("object");			
						
			if (info.getQueryParameters().getFirst("query") != null) {
				
				if (info.getQueryParameters().getFirst("query") != null) {
					List<String> queries = new ArrayList<String>();
					if(info.getQueryParameters().getFirst("query").length() > 0){
						queries.add(info.getQueryParameters().getFirst("query"));	
					}				
					
					if (queries.size() > 0) {
						// mapping url search query fields to fields that we have in index					
						queries = new SearchQueryMapper().mapToDatabaseField(queries);					
						if(queries.size() > 0){
							searchParameters.setQuery(queries.get(0));
							//checking if every search word has also field defined for (example: query=title:france -> true, query=war -> false)										
							searchParameters.setSearchTerm(queries.get(0));	
						}												
					}
				}
										
				// aditional search query parameters						
				if (info.getQueryParameters().getFirst("qf") != null && info.getQueryParameters().getFirst("qf").length() > 0) {												
					List<String> qfList = new ArrayList<String>();
					qfList.add(info.getQueryParameters().getFirst("qf"));
					qfList = new SearchQueryMapper().mapToDatabaseField(qfList);
					searchParameters.setQf(qfList);			
				}

				
				if (info.getQueryParameters().get("facet") != null) {				
					// mapping facet fields to fields that we have in index
					searchQueryMapper = new SearchQueryMapper();
												
					// list of facet titles specified in query parameter: facet={facetTitle(s)}
					List<String> queryFacets = searchQueryMapper.mapToDatabaseField(queryHelper.parseFacetsQuery(info.getQueryParameters().get("facet")));								
					// limits (number of top childrens) defined for facets (only for those that are specified in request parameter f.[facet name].facet.limit)
					Map<String,Integer> facetsLimit = queryHelper.getFacetsLimits(info.getQueryParameters());						
					searchParameters.setFacetWithLimits(searchHelper.providerFacetsWithLimits(queryFacets, facetsLimit));				
				}
							
				searchParameters.setProfile(queryHelper.setProfileType(info.getQueryParameters().getFirst("profile")));
				
				if (info.getQueryParameters().getFirst("reusability") != null) {
					searchParameters.setReusability(info.getQueryParameters().getFirst("reusability"));
				}

				if (info.getQueryParameters().getFirst("media") != null) {
					searchParameters.setMedia(searchHelper.setMediaValue(info.getQueryParameters().getFirst("media")));											
				}
				
				if (info.getQueryParameters().getFirst("thumbnail") != null) {
					searchParameters.setThumbnail(searchHelper.setThumbnailValue(info.getQueryParameters().getFirst("thumbnail")));											
					hasThumbnailAttr = true;								
				}

				if (info.getQueryParameters().getFirst("landingpage") != null) {
					//set to true, so we can later add landing page attribute to search query syntax
					hasLandingAttr = true;
					searchParameters.setLandingpage(searchHelper.setLandingPageValue(info.getQueryParameters().getFirst("landingpage")));															
				}
				if (info.getQueryParameters().getFirst("colourpalette") != null) {
					searchParameters.setColourpalette(info.getQueryParameters().getFirst("colourpalette"));
				}

				if (info.getQueryParameters().getFirst("sort") != null) {
					searchParameters.setSort(info.getQueryParameters().getFirst("sort"));
				}

				// number of returned results in response
				if (info.getQueryParameters().getFirst("rows") != null) {
					String rowsString = info.getQueryParameters().getFirst("rows");
					if (rowsString != null) {
						try {
							rows = Integer.parseInt(rowsString);
							if (rows > 100) {
								return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Wrong value for parameter: rows. Maximum value is 100")).build();
							}
							searchParameters.setRows(rows);
						} catch (NumberFormatException e) {
							logger.error("Wrong value for parameter rows. Must be numeric",e);
							return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Wrong value for parameter: rows. Must be numeric")).build();							
						}catch(Exception e){
							logger.error(e.getMessage(),e);
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,e.getMessage())).build();
						}
					}
				}				

				if (info.getQueryParameters().getFirst("start") != null) {
					String startString = info.getQueryParameters().getFirst("start");
					if (startString != null) {
						try {
							int start = Integer.parseInt(startString);
							searchParameters.setStart(start);
						} catch (NumberFormatException e) {
							logger.error("Wrong value for parameter start. Must be numeric",e);
							return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Wrong value for parameter: start. Must be numeric")).build();					
						}catch(Exception e){
							logger.error(e.getMessage(),e);
							return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,e.getMessage())).build();
						}
					}
				}

				if (info.getQueryParameters().getFirst("cursor") != null) {
					searchParameters.setCursor(info.getQueryParameters().getFirst("cursor"));
				}

				if (info.getQueryParameters().getFirst("callback") != null) {
					searchParameters.setCallback(info.getQueryParameters().getFirst("callback"));
				}
				
				try {
					String searchTerm = "";				
					
					if(searchParameters.getSearchTerm() != null){
						searchTerm = searchParameters.getSearchTerm();
					}				
					
					/* adding aditional search term to query
					 * url example: query=title:war&qf=description:vietnam
					 */
					searchTerm = queryHelper.addAdditionalTerms(searchTerm,searchParameters);												
					
					if(searchTerm != null && searchTerm.length() > 0){
											
						searchParameters.setSearchField(queryHelper.hasAllSearchFields(searchTerm));					
						
						//removing double whitespaces to only one whitespace
						searchTerm = queryHelper.trimAndSingleWhitespace(searchTerm);					
						// adding upper case for iso date if necessary
						searchTerm = queryHelper.processIsoDate(searchTerm);				
						//adding backslash for iso date
						searchTerm = queryHelper.processSearchQuery(searchTerm);					
					
						// set default object_type if not specified in url parameters					
						// map with object_type term and documentType value
						Map<String,Object> map = queryHelper.setObjectTypeTerm(object);									
						if(!map.isEmpty()){
							if(map.containsKey("object_type")){
								// add object_type to start of query
								searchTerm = map.get("object_type") + searchTerm;
							}
							if(map.containsKey("docType")){
								documentType = (LuceneDocumentType) map.get("docType");
							}
						}							
										
						// if landing page parameter was specified we add search term
						if(hasLandingAttr){
							searchTerm = queryHelper.addLandingPageTerm(searchTerm,searchParameters);
						}
											
						// if thumbnail parameter was specified we add search term
						if(hasThumbnailAttr){						
							searchTerm = queryHelper.addThumbnailTerm(searchTerm,searchParameters);												
						}
						searchTerm = searchTerm.replaceAll("/", "\\\\/");					
					} else {
						documentType = LuceneDocumentType.CulturalHeritageObject;
					}						
							
					try{
						boolean entitySearch = false;
						// 	if lucene document type is not CHO, user is searching for one of the related entities					
						if(!LuceneDocumentType.CulturalHeritageObject.equals(documentType)){
							entitySearch = true;							
						}										
						luceneSearchModel = searchEngine.searchRecordsFromIndex(searchParameters.getQuery(),searchParameters.hasSearchField(), searchTerm, rows, searchParameters.getFacetWithLimits(),documentType,searchParameters.getStart(),searchParameters.getProfile(),entitySearch);
						
					} catch (org.apache.lucene.queryparser.classic.ParseException e){
						return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,e.getMessage())).build();
					}
							
					if(luceneSearchModel != null){
						searchViewModel.setTotalResults(luceneSearchModel.getTotalResults());
						searchViewModel.setApiKey(apiKey);
						searchViewModel.setSuccess(true);
						searchViewModel.setItemsCount(luceneSearchModel.getItems().size());
						//set search items to search response model
						searchViewModel.setItems(searchHelper.mapSearchItemsToSearchModel(luceneSearchModel.getItems()));
						//set facets to search response model
						searchViewModel.setFacets(searchHelper.mapFacetsToSearchModel(luceneSearchModel.getFacets()));
					}				
					
					return Response.ok().entity(searchViewModel).build();			
				} catch (Exception e1) {		
					logger.error(e1.getMessage(),e1);				
				}
				return Response.serverError().build();
			} else {
				searchViewModel.setApiKey(apiKey);
				searchViewModel.setSuccess(true);
				return Response.ok().entity(searchViewModel).build();
			}	
		}else{
			return ACCESS_DENIED;
		}		
	}

	
	/**
	 * Returns information about single CHO record from database based on CHO id
	 */
	@Override
	public Response getRecord(String id, UriInfo info) throws NotFoundException {
		
		String apiKey = info.getQueryParameters().getFirst("wskey");		

		//if(apiKey != null && ApiAuthenticator.validApiKey(apiKey)) {
			CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();			
			eu.europeana.direct.backend.model.CulturalHeritageObject culturalHeritageObjectDomain;
			eu.europeana.direct.legacy.model.record.CulturalHeritageObject legacyCulturalHeritageObject;
			try {
				String actualId = id.split("\\.")[0];			
				Long choId = Long.parseLong(actualId);
				// returns backend CHO model
				culturalHeritageObjectDomain = choLogic.getCHODomain(choId);			
				
				if (culturalHeritageObjectDomain == null) {
					return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"CHO object with ID "+id+" not found")).build();
				} else {
					RecordCulturalHeritageObjectLogic legacyChoLogic = new RecordCulturalHeritageObjectLogic(choLogic);
					// maps domain CHO model to Legacy record CHO model				
					legacyCulturalHeritageObject = legacyChoLogic.mapFromDatabase(culturalHeritageObjectDomain);				
					ResponseObject responseObject = new ResponseObject(true,legacyCulturalHeritageObject);			
									
					return Response.ok().entity(responseObject).build();
				}
			} catch (NumberFormatException e) {
				logger.error("Wrong ID. Must be numeric",e);
				return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Wrong ID: "+id+". Must be numeric")).build();
			}catch (Exception e){
				logger.error(e.getMessage(),e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,e.getMessage())).build();
			}finally {
				choLogic.close();			
			}	
		/*} else {
			return ACCESS_DENIED;
		}*/			
	}

	@Override
	public Response getRemovedRecords(UriInfo info) throws NotFoundException {
		String apiKey = info.getQueryParameters().getFirst("wskey");		

		if(apiKey != null && ApiAuthenticator.validApiKey(apiKey)){
			// default number of records returned
			int rows = 12;
			SearchViewModel searchViewModel = new SearchViewModel();
			LuceneSearchModel luceneSearchModel = null;

			Parameters searchParameters = new Parameters();		

			if (info.getQueryParameters().getFirst("query") != null) {
						
				String searchTerm = info.getQueryParameters().getFirst("query");
				
				// number of returned results in response
				if (info.getQueryParameters().getFirst("rows") != null) {
					String rowsString = info.getQueryParameters().getFirst("rows");			
					searchParameters.setRows(Integer.parseInt(rowsString));				
				}

				if (info.getQueryParameters().getFirst("start") != null) {
					String startString = info.getQueryParameters().getFirst("start");				
					searchParameters.setStart(Integer.parseInt(startString));				
				}

				try {							

					// removing double whitespaces to only one whitespace
					searchTerm = queryHelper.trimAndSingleWhitespace(searchTerm);
					// adding upper case for iso date if necessary
					searchTerm = queryHelper.processIsoDate(searchTerm);
					// adding backslash for iso date
					searchTerm = queryHelper.processSearchQuery(searchTerm);				

					try {
						
						luceneSearchModel = searchEngine.searchRemovedRecords(searchTerm,rows,searchParameters.getStart());
					} catch (org.apache.lucene.queryparser.classic.ParseException e) {
						return Response.status(Response.Status.BAD_REQUEST)
								.entity(new ApiResponseMessage(ApiResponseMessage.ERROR, e.getMessage())).build();
					}

					searchViewModel.setTotalResults(luceneSearchModel.getTotalResults());
					searchViewModel.setApiKey(apiKey);
					searchViewModel.setSuccess(true);
					if (luceneSearchModel.getItems() != null) {
						searchViewModel.setItemsCount(luceneSearchModel.getItems().size());
						// set search items to search response model					
						searchViewModel.setItems(searchHelper.mapSearchItemsToSearchModel(luceneSearchModel.getItems()));
					}

					return Response.ok().entity(searchViewModel).build();
				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
				return Response.serverError().build();
			} else {
				searchViewModel.setApiKey(apiKey);
				searchViewModel.setSuccess(true);
				return Response.ok().entity(searchViewModel).build();
			}	
		} else {
			return ACCESS_DENIED;
		}				
	}

	
	@Override
	public Response reIndex(String fromId, String toId, String apiKey) throws NotFoundException {
		// only for admin -semantika
		if(apiKey != null && apiKey.equals("g31u2hg4qcnoo2hupe06o21re2")){
			CulturalHeritageObjectLogic logic = new CulturalHeritageObjectLogic();
			try{
				
				List<eu.europeana.direct.backend.model.CulturalHeritageObject> list = logic.getFromTo(Long.parseLong(fromId), Long.parseLong(toId));
				
				for(eu.europeana.direct.backend.model.CulturalHeritageObject cho : list){								
					LuceneIndexing.getInstance().updateLuceneIndex(logic.mapFromDatabase(cho),true,false,cho.getEuropeanaDataObject().getCreated(),true);
				}			
			}catch (Exception e){
				
			}finally{			
				logic.close();
			}
			
			return Response.ok().build();		
		} else {
			return ACCESS_DENIED;
		}		
	}

	@Override
	public Response fullReindex(String apiKey) throws NotFoundException {
		// only for admin -semantika
		if(apiKey != null && apiKey.equals("g31u2hg4qcnoo2hupe06o21re2")){
			CulturalHeritageObjectLogic choLogic = new CulturalHeritageObjectLogic();
			try{
				int startFromRow = 0;
				int recordsNum = 500;
				
				List<eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject> list = null;
				HarvestThread workerThread = new HarvestThread(3);
														
				while(true){				
					list = choLogic.getByLimit(startFromRow, recordsNum);												
					if(list == null){
						break;
					}
					startFromRow += list.size();
					workerThread.execute(new IndexChoWorker(list));	

				}			
			}catch(Exception e){
				e.printStackTrace();		
			}finally{
				choLogic.close();
				LuceneIndexing.getInstance().commitIndex(true);			
			}
			return Response.ok().build();
		} else {
			return ACCESS_DENIED;
		}
	}

	@Override
	public Response merge(String apiKey) throws NotFoundException {
		// only for admin - semantika
		if(apiKey != null && apiKey.equals("g31u2hg4qcnoo2hupe06o21re2")){
			try{
				LuceneIndexing.getInstance().fullOptimize();	
			}catch (Exception e){
				return Response.serverError().entity("Server error :"+e.getMessage()).build();
			}
			
			return Response.ok().build();	
		} else {
			return ACCESS_DENIED;
		}
	}
}
