package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.europeana.direct.backend.model.DirectUser;
import eu.europeana.direct.backend.model.Provider;
import eu.europeana.direct.helpers.APIKeyGenerator;
import eu.europeana.direct.helpers.PasswordUtil;
import eu.europeana.direct.helpers.UserValidation;
import eu.europeana.direct.logic.UserLogic;

/**
 * Servlet implementation class Registration
 */
@WebServlet("/Signup")
public class Signup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Signup() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		RequestDispatcher rd = null;
		
		if(request.getAttribute("errors") != null){			
			rd = request.getRequestDispatcher("failed.jsp");
		}else{
			request.setAttribute("userMail", request.getParameter("email"));			
			rd = request.getRequestDispatcher("confirmation.jsp");	
		}								
		rd.forward(request, response);	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		DirectUser user = new DirectUser();
		user.setName(request.getParameter("name").trim());
		user.setLastname(request.getParameter("lastname").trim());
		user.setMailAddress(request.getParameter("email").trim());
		user.setUsername(request.getParameter("username").trim());	
		user.setApproved(false);
		// only for validation, later we will encrypt password
		user.setPassword(request.getParameter("password").trim());
						
		List<String> errorMessages = UserValidation.isValid(user, request.getParameter("institution").trim());
				
		if(errorMessages.isEmpty()){
			// returns map with values: salt and salted hash password
			Map<String, String> passwordValues = PasswordUtil.generatePasswordValues(request.getParameter("password"));
			
			if(passwordValues != null && passwordValues.size() > 0){
				user.setPassword(passwordValues.get("saltedhashPassword"));
				user.setSalt(passwordValues.get("salt"));
				user.setApiKey(APIKeyGenerator.generateAPIKey());
				UserLogic logic = new UserLogic();			
											
				try {
					logic.saveUser(user,request.getParameter("institution"));
				} finally {
					logic.close();
				}	
			}	
		} else {
			request.setAttribute("errors", errorMessages);
		}				
		
		doGet(request, response);
	}

}
