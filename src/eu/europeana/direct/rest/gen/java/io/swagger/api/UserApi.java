package eu.europeana.direct.rest.gen.java.io.swagger.api;

import java.math.BigDecimal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import eu.europeana.direct.rest.main.java.io.swagger.api.factories.UserApiServiceFactory;

@Path("/user")
public class UserApi {
	
	   private final UserApiService delegate = UserApiServiceFactory.getUserApi();

	  	@GET		  	
	    @Produces({"text/plain"})
	    public Response getByUsername(@QueryParam("username") String username,@QueryParam("email") String email, @Context SecurityContext securityContext)
	    throws NotFoundException {
	  		if(username != null){
	  			return delegate.getUserByUsername(username, securityContext);	
	  		} else if(email != null){
	  			return delegate.getUserByMail(email, securityContext);
	  		}
	        return Response.status(Status.BAD_REQUEST).build();
	    }
	  	
	  	@GET
	  	@Path("/approve")
	  	public Response appproveUser(@QueryParam("username") String username, @QueryParam("apikey") String apikey, @Context SecurityContext securityContext) throws NotFoundException {
	  		return delegate.approveUser(username,apikey);
	  	}
}
