package eu.europeana.direct.rest.gen.java.io.swagger.api;

import java.io.File;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.websocket.server.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import eu.europeana.direct.backend.model.EuropeanaDataObjectEuropeanaDataObject;
import eu.europeana.direct.backend.repositories.AgentRepository;
import eu.europeana.direct.helpers.RestClient;
import eu.europeana.direct.logic.CulturalHeritageObjectLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.model.CulturalHeritageObject;
import eu.europeana.direct.rest.main.java.io.swagger.api.factories.CulturalHeritageObjectApiServiceFactory;

@Path("/object")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class CulturalHeritageObjectApi  {
	
	private final CulturalHeritageObjectApiService delegate = CulturalHeritageObjectApiServiceFactory.getCulturalHeritageObjectApi();

    @GET
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response culturalHeritageObjectGet( @QueryParam("id") BigDecimal id, @QueryParam("language") String language, @QueryParam("apikey") String apiKey, @Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.culturalHeritageObjectGet(id,language,apiKey,securityContext);
    }
    
    @POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response culturalHeritageObjectPost( CulturalHeritageObject culturalHeritageObject,  @QueryParam("apikey") String apiKey, @Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.culturalHeritageObjectPost(culturalHeritageObject,apiKey,securityContext);
    }
    
    @DELETE
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response culturalHeritageObjectDelete( @QueryParam("id") BigDecimal id, @QueryParam("onlyCHO") Boolean choOnly,  @QueryParam("apikey") String apiKey, @Context SecurityContext securityContext)
    throws NotFoundException {    
        return delegate.culturalHeritageObjectDelete(id,choOnly,apiKey,securityContext);
    }
 
    @GET
	@Path("/delete")
    public Response culturalHeritageObjectDeleteByDataOwner( @QueryParam("dataOwner") String dataOwner,  @QueryParam("apikey") String apiKey, @Context SecurityContext securityContext)
    throws NotFoundException {        	
        return delegate.culturalHeritageObjectDeleteByDataOwner(dataOwner, apiKey, securityContext);
    }
 
    
}
