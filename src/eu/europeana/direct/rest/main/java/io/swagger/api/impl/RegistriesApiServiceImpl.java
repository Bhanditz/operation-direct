package eu.europeana.direct.rest.main.java.io.swagger.api.impl;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

import eu.europeana.direct.authentication.ApiAuthenticator;
import eu.europeana.direct.logic.entities.AgentLogic;
import eu.europeana.direct.logic.entities.ConceptLogic;
import eu.europeana.direct.logic.entities.LocationLogic;
import eu.europeana.direct.logic.entities.TimespanLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.api.ApiResponseMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.api.EntitiesApiService;
import eu.europeana.direct.rest.gen.java.io.swagger.api.NotFoundException;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;

public class RegistriesApiServiceImpl extends EntitiesApiService {
	final static Logger logger = Logger.getLogger(RegistriesApiServiceImpl.class);
	private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
			.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Unauthorized")).build();

	/**
	 * Finds Agent database model based on id, maps it into Operation Direct Agent model and returns it
	 */
	@Override
	public Response registriesAgentGet(BigDecimal id, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {
			AgentLogic logic = new AgentLogic();
			Agent cho = null;
			try{
				try {
					cho = logic.getAgent(id.longValueExact());	
				} catch (NullPointerException e){
					logger.error("ID parameter for retrieving Agent not found",e);			
			        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure that Agent ID parameter was specified in the correct way")).build();
				}		
				if (cho != null) {
					return Response.ok().entity(cho).build();
				} else {
					logger.error("Agent entity with ID "+id+" not found");
					return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.WARNING,"Agent entity with ID "+id+" not found")).build();
				}	
			} finally {
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}						
	}
	
	/**
	 * Maps Operation Direct API Agent model into database model and saves
	 */
	@Override
	public Response registriesAgentPost(Agent agent, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {
			AgentLogic logic = new AgentLogic();
			try {
				logic.startTransaction();
				long id = logic.mapAndSaveAgent(agent);
				logic.commitTransaction();
				logic.sendIndexing(id);
				return Response.ok()
						.entity(Long.valueOf(id)).build();
			} catch (Exception ex) {
				logic.rollbackTransaction();
				logger.info("Agent Object received & parsed, save failed: "+ex.getMessage(),ex);
				return Response
						.ok()
						.entity(new ApiResponseMessage(ApiResponseMessage.WARNING,
								"Agent Object received & parsed, save failed: "
										+ ex.getMessage())).build();
			} finally {
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}	
	}

	/**
	 * Finds Concept database model based on id, maps it into Operation Direct Concept model and returns it
	 */
	@Override
	public Response registriesConceptGet(BigDecimal id, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		
		if(ApiAuthenticator.validApiKey(apiKey)) {
			ConceptLogic logic = new ConceptLogic();
			Concept cho = null;
			try {
				try {
					cho = logic.getConcept(id.longValueExact());			
				} catch (NullPointerException e){
					logger.error("ID parameter for retrieving Concept not found",e);
			        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure that Concept ID parameter was specified in the correct way")).build();
				}
				if (cho != null) {
					return Response.ok().entity(cho).build();
				} else {
					logger.info("Concept entity with ID "+id.longValueExact()+" not found");
					return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.WARNING,"Concept entity with ID "+id+" not found")).build();
				}
		
			} finally {
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}			
	}

	/**
	 * Maps Operation Direct API Concept model into database model and saves
	 */
	@Override
	public Response registriesConceptPost(Concept concept, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
	
		if(ApiAuthenticator.validApiKey(apiKey)) {
			ConceptLogic logic = new ConceptLogic();
			try {
				logic.startTransaction();
				long id = logic.mapAndSaveConcept(concept);
				logic.commitTransaction();
				logic.sendIndexing(id);
				return Response.ok()
						.entity(Long.valueOf(id)).build();
			} catch (Exception ex) {
				logic.rollbackTransaction();
				logger.info("Concept Object received & parsed, save failed: "+ex.getMessage());
				return Response
						.ok()
						.entity(new ApiResponseMessage(ApiResponseMessage.WARNING,
								"Concept Object received & parsed, save failed: "
										+ ex.getMessage())).build();
			}finally {
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}				
	}

	/**
	 * Finds Place database model based on id, maps it into Operation Direct Place model and returns it
	 */
	@Override
	public Response registriesPlaceGet(BigDecimal id, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		
		if(ApiAuthenticator.validApiKey(apiKey)) {
			LocationLogic logic = new LocationLogic();
			Place cho = null;
			try{
				try{
					cho = logic.getPlace(id.longValueExact());	
				}catch (NullPointerException e){
					logger.error("ID parameter for retrieving Place not found",e);
			        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure that Place ID parameter was specified in the correct way")).build();
				}		
				if (cho != null) {
					return Response.ok().entity(cho).build();
				} else {
					logger.info("Place entity with ID "+id.longValueExact()+" not found");
					return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.WARNING,"Place entity with ID "+id+" not found")).build();					
				}	
			}finally{
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}				
	}

	/**
	 * Maps Operation Direct API Place model into database model and saves
	 */
	@Override
	public Response registriesPlacePost(Place place, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {
			LocationLogic logic = new LocationLogic();
			try {
				logic.startTransaction();
				long id = logic.mapAndSavePlace(place);
				logic.commitTransaction();
				logic.sendIndexing(id);
				return Response.ok()
						.entity(Long.valueOf(id)).build();
			} catch (Exception ex) {
				logic.rollbackTransaction();
				logger.info("Place Object received & parsed, save failed: "+ex.getMessage());
				return Response
						.ok()
						.entity(new ApiResponseMessage(ApiResponseMessage.WARNING,
								"Place Object received & parsed, save failed: "
										+ ex.getMessage())).build();
			}finally {
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}				
	}

	/**
	 * Finds Timespan database model based on id, maps it into Operation Direct Timespan model and returns it
	 */
	@Override
	public Response registriesTimespanGet(BigDecimal id, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		
		if(ApiAuthenticator.validApiKey(apiKey)) {
			TimespanLogic logic = new TimespanLogic();
			TimeSpan cho = null;
			try{
				try{
					cho = logic.getTimeSpan(id.longValueExact());	
				}catch(NullPointerException e){
					logger.error("ID parameter for retrieving Timespan not found",e);
			        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure that Timespan ID parameter was specified in the correct way")).build();
				}
				
				if (cho != null) {
					return Response.ok().entity(cho).build();
				} else {
					logger.info("Timespan entity with ID "+id.longValueExact()+" not found");
					return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Timespan entity with ID "+id+" not found")).build();					
				}	
			}finally{
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}						
	}

	/**
	 * Maps Operation Direct API Timespan model into database model and saves
	 */
	@Override
	public Response registriesTimespanPost(TimeSpan timeSpan, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {
			TimespanLogic logic = new TimespanLogic();
			try {
				logic.startTransaction();
				long id = logic.mapAndSaveTimespan(timeSpan);
				logic.commitTransaction();
				logic.sendIndexing(id);
				return Response.ok()
						.entity(Long.valueOf(id)).build();
			} catch (Exception ex) {
				logic.rollbackTransaction();
				logger.info("Timespan Object received & parsed, save failed: "+ex.getMessage());
				return Response
						.ok()
						.entity(new ApiResponseMessage(ApiResponseMessage.WARNING,
								"Timespan Object received & parsed, save failed: "
										+ ex.getMessage())).build();
			}finally {
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}						
	}


	/**
	 * Deletes Agent from database
	 */
	@Override
	public Response registriesAgentDelete(BigDecimal id, String apiKey, SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {
			AgentLogic logic = new AgentLogic();
			try{			
				try{
					if(id.longValueExact() > 0){
						int del = logic.deleteAgent(id.longValue());
						if(del == 1){	
							logic.deleteIndex(id.longValue());
							return Response.status(Response.Status.OK).entity(new ApiResponseMessage(ApiResponseMessage.OK,"Agent entity "+id+" successfully deleted")).build();
						}else if(del == 2){
					        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Agent entity cannot be deleted because it's related to at least one Cultural Heritage Object")).build();
						}else{
							return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Agent entity with ID "+id+" not found")).build();
						}
					}else{
						logger.error("Agent delete not triggered, invalid ID value: "+id);
				        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Invalid ID value: "+id)).build();
					}	
				}catch(NullPointerException e){
					logger.error("Agent ID parameter not specified on delete request");
					return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure Agent ID parameter is specified in the correct way")).build();
				}					
			}catch(Exception e){
				logger.error("Agent delete failed: "+e.getMessage(),e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Delete failed: "+e.getMessage())).build();
			}finally {
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}
			
	}	

	/**
	 * Deletes Concept from database
	 */
	@Override
	public Response registriesConceptDelete(BigDecimal id, String apiKey, SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {
			ConceptLogic logic = new ConceptLogic();
			try{
				try{			
					if(id.longValueExact() > 0){
						long del = logic.deleteConcept(id.longValue());
						if(del == 1){
							logic.deleteIndex(id.longValue());
							return Response.status(Response.Status.OK).entity(new ApiResponseMessage(ApiResponseMessage.OK,"Concept entity "+id+" successfully deleted")).build();
						}else if(del == 2){
					        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Concept entity cannot be deleted because it's related to at least one Cultural Heritage Object")).build();
						}else{
							return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Concept entity "+id+" not found")).build();
						}					
					}else{
						logger.error("Concept delete not triggered, invalid ID value: "+id);
						return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Invalid ID value: "+id)).build();
					}
				}catch(NullPointerException e){
					logger.error("Concept ID parameter not specified on delete request");
					return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure Concept ID parameter is specified in the correct way")).build();
				}			
			}catch(Exception e){
				logger.error("Concept delete failed: "+e.getMessage(),e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Delete failed: "+e.getMessage())).build();
			}finally {
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}			
	}

	/**
	 * Deletes Place from database
	 */
	@Override
	public Response registriesPlaceDelete(BigDecimal id, String apiKey, SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {
			LocationLogic logic = new LocationLogic();
			try{
				try{
					if(id.longValueExact() > 0){
						int del = logic.deletePlace(id.longValue());
						if(del == 1){
							logic.deleteIndex(id.longValue());
							return Response.status(Response.Status.OK).entity(new ApiResponseMessage(ApiResponseMessage.OK,"Place entity "+id+" successfully deleted")).build();
						}else if(del == 2){
					        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Place entity cannot be deleted because it's related to at least one Cultural Heritage Object")).build();
						}else{
							return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Place entity "+id+" not found")).build();
						}					
					}else{
						logger.error("Place delete not triggered, invalid ID value: "+id);
						return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Invalid ID value "+id)).build();
					}	
				}catch(NullPointerException e){
					logger.error("Place ID parameter not specified on delete request");
					return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure Place ID parameter is specified in the correct way")).build();
				}
				
			}catch(Exception e){
				logger.error("Place delete failed: "+e.getMessage(),e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Delete failed: "+e.getMessage())).build();
			}finally {
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}				
	}

	/**
	 * Deletes Timespan from database
	 */
	@Override
	public Response registriesTimespanDelete(BigDecimal id, String apiKey, SecurityContext securityContext) throws NotFoundException {
		
		if(ApiAuthenticator.validApiKey(apiKey)) {
			TimespanLogic logic = new TimespanLogic();
			try{
				try{
					if(id.longValueExact() > 0){
						int del = logic.deleteTimespan(id.longValue());
						if(del == 1){
							logic.deleteIndex(id.longValue());
							return Response.status(Response.Status.OK).entity(new ApiResponseMessage(ApiResponseMessage.OK,"Timespan entity "+id+" successfully deleted")).build();
						}else if(del == 2){
					        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Timespan entity cannot be deleted because it's related to at least one Cultural Heritage Object")).build();
						}else{
							return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Timespan entity "+id+" not found")).build();
						}									
					}else{
						logger.info("Timespan delete not triggered, invalid ID value: "+id);
						return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Invalid ID value "+id)).build();					
					}				
				}catch(NullPointerException e){
					logger.error("Timespan ID parameter not specified on delete request");
					return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Make sure Timespan ID parameter is specified in the correct way")).build();
				}			
			}catch(Exception e){
				logger.error("Timespan delete failed: "+e.getMessage(),e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Delete failed: "+e.getMessage())).build();
			}finally {
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}			
	}

}
