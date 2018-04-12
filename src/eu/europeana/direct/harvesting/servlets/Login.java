package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.europeana.direct.backend.model.DirectUser;
import eu.europeana.direct.backend.model.Provider;
import eu.europeana.direct.helpers.UserValidation;
import eu.europeana.direct.logic.ProviderLogic;
import eu.europeana.direct.logic.UserLogic;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		RequestDispatcher rd = null;				
					
		String apikey = UserValidation.getCookieValue(request, "apikey").getValue();
		String user = UserValidation.getCookieValue(request, "user").getValue();
		
		if(UserValidation.LoginRequired(apikey,user)) {
			rd = request.getRequestDispatcher("login.jsp");			
		} else {
			// redirect to Dashboard if user authenticated
			rd = request.getRequestDispatcher("Dashboard");	
		}
				
		rd.forward(request, response);			
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		RequestDispatcher rd = null;
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		String usernameError = null;
		String passwordError = null;
		
		boolean hasPw = true;
		if(password == null){
			passwordError = "Password required!";
			hasPw = false;
		}
		
		if(username == null){			
			usernameError = "Username required!";
		} else {
			// check if user exists, only if password provided
			if(hasPw){
				// validate for password and username
				Map<String,String> map = UserValidation.authenticate(request.getParameter("username"),request.getParameter("password"));
				if(map.size() > 0){
					if(map.containsKey("usernameError")){
						usernameError = map.get("usernameError");	
					}
					if(map.containsKey("passwordError")){
						passwordError = map.get("passwordError");	
					}
				}
			}
		}				
		
		// if no errors redirect to Dashboard		
		if(passwordError == null && usernameError == null) {
			// get provider by username
			ProviderLogic providerLogic = new ProviderLogic();
			UserLogic userLogic = new UserLogic();
			try{
				DirectUser user = userLogic.findByUsername(username);
				if(user != null){
					Provider provider = providerLogic.getProvider(user.getInstitution().getId());
					if(provider != null){						
						// store apikey and user into cookies
						Cookie apikeyCookie = new Cookie("apikey",user.getApiKey());												
						apikeyCookie.setMaxAge(21600); //6 hours							
						Cookie providerCookie = new Cookie("provider",provider.getName());												
						providerCookie.setMaxAge(21600); //6 hours	
						Cookie userCookie = new Cookie("user",user.getUsername());												
						userCookie.setMaxAge(21600); //6 hours	
						response.addCookie(apikeyCookie);
						response.addCookie(providerCookie);
						response.addCookie(userCookie);							
					}	
				}				
			} catch (Exception e){
				e.printStackTrace();
			} finally {
				userLogic.close();
				providerLogic.close();				
			}		
			
			rd = request.getRequestDispatcher("Dashboard");
			rd.forward(request, response);
			
		} else {
			request.setAttribute("passwordError", passwordError);
			request.setAttribute("usernameError", usernameError);
			
			rd = request.getRequestDispatcher("login.jsp");
			rd.forward(request, response);
		}										
	}

}
