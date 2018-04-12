package eu.europeana.direct.authentication;

import eu.europeana.direct.logic.UserLogic;

public class ApiAuthenticator {

	public static boolean validApiKey(String apiKey) {
		
		UserLogic userLogic = new UserLogic();
		
		try{
			return userLogic.isValidAPIKey(apiKey);
		} catch (Exception e){
			e.printStackTrace();		
		} finally {
			userLogic.close();
		}
		return false;
	}
	
}
