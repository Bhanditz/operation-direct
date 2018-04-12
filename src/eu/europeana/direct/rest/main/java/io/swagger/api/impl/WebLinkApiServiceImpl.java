package eu.europeana.direct.rest.main.java.io.swagger.api.impl;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import org.apache.log4j.Logger;

import eu.europeana.direct.authentication.ApiAuthenticator;
import eu.europeana.direct.logic.entities.WebLinkLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.api.ApiResponseMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.api.NotFoundException;
import eu.europeana.direct.rest.gen.java.io.swagger.api.WebLinkApiService;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;

public class WebLinkApiServiceImpl extends WebLinkApiService{

	final static Logger logger = Logger.getLogger(WebLinkApiServiceImpl.class);
	private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
			.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Unauthorized")).build();
	
	/**
	 * Finds WebLink database model based on id, maps it into Operation Direct WebLink model and returns it
	 */
	@Override
	public Response registriesWebLinkGet(BigDecimal id, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {			
			WebLinkLogic logic = new WebLinkLogic();
			WebLink cho = null;
			try{			
				cho = logic.getWebLink(id.longValueExact());
				if (cho != null) {
					return Response.ok().entity(cho).build();
				} else {
					logger.info("WebLink ID "+id.longValueExact()+" not found");
					return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.WARNING,"WebLink entity "+id+" not found")).build();			
				}	
			}finally{
				logic.close();
			}	
		} else {
			return ACCESS_DENIED;
		}				
	}

	/**
	 * Maps Operation Direct API WebLink model into database model and saves
	 */
	@Override
	public Response registriesWebLinkPost(WebLink weblink, String apiKey,
			SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {			
			WebLinkLogic logic = new WebLinkLogic();
			try {
				logic.startTransaction();
				long id = logic.mapAndSaveWebLink(weblink);
				logic.commitTransaction();
				logic.sendIndexing(id);
				return Response.ok()
						.entity(Long.valueOf(id)).build();
			} catch (Exception ex) {
				logic.rollbackTransaction();
				logger.info("Weblink Object received & parsed, save failed: "+ex.getMessage(),ex);
				return Response
						.ok()
						.entity(new ApiResponseMessage(ApiResponseMessage.WARNING,
								"Object received & parsed, save failed: "
										+ ex.getMessage())).build();
			}finally {
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}		
	}
	
	/**
	 * Deletes WebLink model from database
	 */
	@Override
	public Response registriesWebLinkDelete(BigDecimal id, String apiKey, SecurityContext securityContext) throws NotFoundException {
		if(ApiAuthenticator.validApiKey(apiKey)) {			
			WebLinkLogic logic = new WebLinkLogic();
			try{
				if(id.longValueExact() > 0){
					int del = logic.deleteWebLink(id.longValue());
					if(del == 1){
						logic.deleteIndex(id.longValue());
						return Response.status(Response.Status.OK).entity(new ApiResponseMessage(ApiResponseMessage.OK,"WebLink entity "+id+" successfully deleted")).build();	
					}else if(del == 2){
				        return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"WebLink entity cannot be deleted because it's related to at least one Cultural Heritage Object")).build();			
					}else{
						return Response.status(Response.Status.NOT_FOUND).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"WebLink entity "+id+" not found")).build();
					}							
				}else{
					logger.info("WebLink delete not triggered, invalid ID value: "+id);
					return Response.status(Response.Status.BAD_REQUEST).entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Invalid ID value "+id)).build();				
				}
			}catch(Exception e){
				logger.error("WebLink delete failed: "+e.getMessage(),e);
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(new ApiResponseMessage(ApiResponseMessage.WARNING,"Delete failed: "+e.getMessage())).build();
			}finally {
				logic.close();
			}
		} else {
			return ACCESS_DENIED;
		}		
	}
}
