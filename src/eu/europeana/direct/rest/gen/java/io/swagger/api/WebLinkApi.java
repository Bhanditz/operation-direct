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

import eu.europeana.direct.rest.gen.java.io.swagger.model.WebLink;
import eu.europeana.direct.rest.main.java.io.swagger.api.factories.WebLinkApiServiceFactory;

@Path("/weblink")
@Consumes({ "application/json" })
@Produces({ "application/json" })
public class WebLinkApi {

	private final WebLinkApiService delegate = WebLinkApiServiceFactory.getWebLinkApi();

	@POST
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesWebLinkPost( WebLink link,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesWebLinkPost(link,apiKey,securityContext);
    }
	
	@GET
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesWebLinkGet( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesWebLinkGet(id,apiKey,securityContext);
    }
    
    
    
    @DELETE
    @Consumes({ "application/json" })
    @Produces({ "application/json" })
    public Response entitiesWebLinkDelete( @QueryParam("id") BigDecimal id,@QueryParam("apikey") String apiKey,@Context SecurityContext securityContext)
    throws NotFoundException {
        return delegate.registriesWebLinkDelete(id,apiKey,securityContext);
    }
}
