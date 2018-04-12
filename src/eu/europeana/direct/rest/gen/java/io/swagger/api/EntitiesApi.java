package eu.europeana.direct.rest.gen.java.io.swagger.api;

import java.math.BigDecimal;

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

import eu.europeana.direct.rest.gen.java.io.swagger.model.Agent;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Concept;
import eu.europeana.direct.rest.gen.java.io.swagger.model.Place;
import eu.europeana.direct.rest.gen.java.io.swagger.model.TimeSpan;
import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;
import eu.europeana.direct.rest.main.java.io.swagger.api.factories.RegistriesApiServiceFactory;

@Path("/entities")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class EntitiesApi  {
   private final EntitiesApiService delegate = RegistriesApiServiceFactory.getRegistriesApi();

    @GET
    @Path("/agent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesAgentGet( @QueryParam("id") BigDecimal id, @QueryParam("apikey") String apiKey, @Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesAgentGet(id,apiKey,securityContext);
    }
    @POST
    @Path("/agent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesAgentPost( Agent agent,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesAgentPost(agent,apiKey,securityContext);
    }
    
    @DELETE
    @Path("/agent")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesAgentDelete( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesAgentDelete(id,apiKey,securityContext);
    }
    
    @GET
    @Path("/concept")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesConceptGet( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesConceptGet(id,apiKey,securityContext);
    }
    @POST
    @Path("/concept")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesConceptPost( Concept concept,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesConceptPost(concept,apiKey,securityContext);
    }
    
    @DELETE
    @Path("/concept")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesConceptDelete( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesConceptDelete(id,apiKey,securityContext);
    }
    
    @GET
    @Path("/place")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesPlaceGet( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesPlaceGet(id,apiKey,securityContext);
    }
    @POST
    @Path("/place")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesPlacePost( Place place,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesPlacePost(place,apiKey,securityContext);
    }
    
    @DELETE
    @Path("/place")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesPlaceDelete( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesPlaceDelete(id,apiKey,securityContext);
    }
    
    @GET
    @Path("/timespan")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesTimespanGet( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesTimespanGet(id,apiKey,securityContext);
    }
    @POST
    @Path("/timespan")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesTimespanPost( TimeSpan timeSpan,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesTimespanPost(timeSpan,apiKey,securityContext);
    }
    
    @DELETE
    @Path("/timespan")
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesTimespanDelete( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesTimespanDelete(id,apiKey,securityContext);
    }
    
 
}
