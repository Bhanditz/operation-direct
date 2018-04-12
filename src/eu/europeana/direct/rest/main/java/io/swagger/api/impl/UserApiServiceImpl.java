package eu.europeana.direct.rest.main.java.io.swagger.api.impl;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import eu.europeana.direct.backend.model.DirectUser;
import eu.europeana.direct.logic.UserLogic;
import eu.europeana.direct.rest.gen.java.io.swagger.api.ApiResponseMessage;
import eu.europeana.direct.rest.gen.java.io.swagger.api.NotFoundException;
import eu.europeana.direct.rest.gen.java.io.swagger.api.UserApiService;

public class UserApiServiceImpl extends UserApiService {

	final static Logger logger = Logger.getLogger(UserApiServiceImpl.class);	
	private static final Response ACCESS_DENIED = Response.status(Response.Status.UNAUTHORIZED)
			.entity(new ApiResponseMessage(ApiResponseMessage.ERROR,"Unauthorized")).build();
	public UserApiServiceImpl(){}

	@Override
	public Response getUserByUsername(String username, SecurityContext securityContext)
			throws NotFoundException {
			
		UserLogic userLogic = new UserLogic();
		String foundUsername = null;
		try{
			DirectUser user = userLogic.findByUsername(username);
			if(user != null) {
				foundUsername = user.getUsername();
			}			
		} catch (Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		} finally {
			userLogic.close();
		}
		
		return Response.ok().entity(foundUsername).build();
	}

	@Override
	public Response getUserByMail(String email, SecurityContext securityContext) throws NotFoundException {
		UserLogic userLogic = new UserLogic();
		String foundMail = null;
		try{
			DirectUser user = userLogic.findByMail(email);
			if(user != null) {
				foundMail = user.getUsername();
			}			
		} catch (Exception e){
			e.printStackTrace();
			return Response.serverError().build();
		} finally {
			userLogic.close();
		}
		
		return Response.ok().entity(foundMail).build();
	}

	@Override
	public Response approveUser(String username, String apikey) throws NotFoundException {
		
		if(username == null || apikey == null){
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		UserLogic logic = new UserLogic();
		try{
			
			DirectUser user = logic.findByApikey(apikey);
			// only admin can approve user
			if(user.isAdmin()){
				DirectUser userForApproval = logic.findByUsername(username);
				if(userForApproval != null){
					userForApproval.setApproved(true);
					logic.saveUser(userForApproval);
				}
			} else {
				return ACCESS_DENIED;
			}
			
		} catch (Exception e){
		} finally {
			logic.close();
		}				

		return Response.ok().build();
	}
}
