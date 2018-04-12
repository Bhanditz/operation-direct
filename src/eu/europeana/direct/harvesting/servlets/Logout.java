package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.europeana.direct.backend.model.DirectUser;
import eu.europeana.direct.helpers.UserValidation;
import eu.europeana.direct.logic.UserLogic;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.sendRedirect("login.jsp");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// first check if apikey and username exists in cookies and also if they are in database 
		if(allowLogout(request)) {		
			
			Cookie apikey = UserValidation.getCookieValue(request, "apikey");
			Cookie user = UserValidation.getCookieValue(request, "user");
			Cookie provider = UserValidation.getCookieValue(request, "provider");
			
			if(apikey != null && user != null && provider != null) {
				apikey.setMaxAge(0);				
				user.setMaxAge(0);
				provider.setMaxAge(0);
				
				response.addCookie(apikey);
				response.addCookie(user);
				response.addCookie(provider);				
			}						
		} 
		
		response.sendRedirect("login.jsp");
	}

	private boolean allowLogout(HttpServletRequest request) {
		String username = UserValidation.getCookieValue(request, "user").getValue();
		String apikey = UserValidation.getCookieValue(request, "apikey").getValue();
				
		if(username != null && apikey != null){
			UserLogic userLogic = new UserLogic();
			
			try {
				DirectUser user = userLogic.findByApikey(apikey);
				// if apikey actualy exists in database, also check if user with that apikey has the same username as it is in cookies
				if(user != null) {
					return user.getUsername().equals(username);
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				userLogic.close();
			}			
		}
		
		return false;
	}				
}
