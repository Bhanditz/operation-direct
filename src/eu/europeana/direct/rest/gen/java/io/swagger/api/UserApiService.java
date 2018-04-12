package eu.europeana.direct.rest.gen.java.io.swagger.api;

import java.math.BigDecimal;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public abstract class UserApiService {

	   public abstract Response getUserByUsername(String username, SecurityContext securityContext)
			      throws NotFoundException;		
	   
	   public abstract Response getUserByMail(String email, SecurityContext securityContext)
			      throws NotFoundException;
	
	   public abstract Response approveUser(String username, String apikey)
			      throws NotFoundException;
	
}
