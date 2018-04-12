package eu.europeana.direct.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import eu.europeana.direct.backend.model.DirectUser;
import eu.europeana.direct.logic.UserLogic;

public class UserValidation {

	private static final String EMAIL_PATTERN =
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
	
	private static Pattern pattern = Pattern.compile(EMAIL_PATTERN);
	private static Matcher matcher;
	
	public static List<String> isValid(DirectUser user, String institutionTitle) {
		
		List<String> errorMessages = new ArrayList<String>();
		
		if(institutionTitle == null || institutionTitle.trim().length() < 4){
			errorMessages.add("Institution title must have at least 4 characters.");
		}
		
		if(user.getName() == null || user.getName().trim().length() < 4){
			errorMessages.add("Name must have at least 4 characters.");
		}
		
		if(user.getLastname() == null || user.getLastname().trim().length() < 4){
			errorMessages.add("Lastname must have at least 4 characters.");
		}
		
		UserLogic userLogic = new UserLogic();
		try{
			if(user.getUsername() != null){
				if(userLogic.findByUsername(user.getUsername()) != null){
					errorMessages.add("Username: "+user.getUsername()+" already exists!");
				}	
			}
			
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			userLogic.close();
		}				
				
		if(user.getUsername() == null || user.getUsername().trim().length() < 6){
			errorMessages.add("Username must have at least 6 characters.");
		}
		
		if(user.getMailAddress() == null){
			errorMessages.add("Email address is required!");
		}else if(!validMail(user.getMailAddress())){
			errorMessages.add("Email address is invalid!");
		}
		
		if(user.getPassword() == null || user.getPassword().trim().length() < 6){
			errorMessages.add("Password must have at least 6 characters");
		}
		
		return errorMessages;
	}
	
	private static boolean validMail(String mail){		
		matcher = pattern.matcher(mail);
		return matcher.matches();
	}

	public static Map<String,String> authenticate(String username, String password) {
		Map<String,String> map = new HashMap<String,String>();
		
		UserLogic userLogic = new UserLogic();
		DirectUser user = new DirectUser();
		
		try{
			
			user = userLogic.findByUsername(username);
			if(user == null){
				map.put("usernameError", "Username "+username+" does not exists!");
			} else {
				// validate password				
				String saltedPw = user.getPassword();
				
				String encPw = new String(PasswordUtil.getEncryptedPassword(password, user.getSalt().getBytes()));
				// if password not equals, wrong password
				if(!saltedPw.equals(encPw)){
					map.put("passwordError", "Invalid password");
				}												
			}
		} catch (Exception e){
			e.printStackTrace();
		} finally {
			userLogic.close();
		}
	
		return map;
	}
	
	// Check if apikey from cookies actually exists in database and if it owned by the username from cookie
	public static boolean LoginRequired(String apikey, String username) {
		boolean loginRequired = true;
		UserLogic userLogic = new UserLogic();
		try {
			DirectUser user = userLogic.findByApikey(apikey);
			if (user != null) {
				if(user.getUsername().equals(username)){
					loginRequired = false;	
				}				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			userLogic.close();
		}	
		
		return loginRequired;
	}
	
	public static Cookie getCookieValue(HttpServletRequest request, String cookieName) {
		
		if(request.getCookies() != null && request.getCookies().length > 0) {			
			Cookie[] cookies = request.getCookies();			
			for(int i=0; i < cookies.length; i++) {				
				// also save provider, for statistics purposes
				if(cookies[i].getName().equals(cookieName)) {
					return cookies[i];					
				}				
			}
		}
		
		return null;
	}

}
