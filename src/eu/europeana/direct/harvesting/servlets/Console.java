package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sun.org.apache.bcel.internal.Repository;

import eu.europeana.direct.backend.model.DirectUser;
import eu.europeana.direct.harvesting.models.view.ApprovedUserView;
import eu.europeana.direct.logic.UserLogic;

/**
 * Servlet implementation class Console
 */
@WebServlet("/Console")
public class Console extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Console() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		boolean loginRequired = true;		
		String user = null;
		// if cookies exists, get user name from cookies
		if(request.getCookies() != null && request.getCookies().length > 0){			
			Cookie[] cookies = request.getCookies();			
			for(int i=0; i < cookies.length; i++){				
				if(cookies[i].getName().equals("apikey")){
					user = cookies[i].getValue();
					// user is logged in
					loginRequired = false;
				}				
			}
		}
		
		if(loginRequired){
			response.sendRedirect("login.jsp");
		} else {
			// find users for API KEY approval
			UserLogic logic = new UserLogic();
			try{
				List<ApprovedUserView> list = mapToUserView(logic.findUnapproved());				
				request.setAttribute("users", list);
			} catch (Exception e){				
				e.printStackTrace();
			} finally {
				logic.close();
			}			
			
			RequestDispatcher rd = request.getRequestDispatcher("console.jsp");							
			rd.forward(request, response);	
		}		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private List<ApprovedUserView> mapToUserView(List<DirectUser> domainUsers) {
		List<ApprovedUserView> list = new ArrayList<>();
		
		if(domainUsers != null && domainUsers.size() > 0){
			for(DirectUser user : domainUsers){
				list.add(new ApprovedUserView(user.getName(),user.getLastname(),user.getInstitution().getName(),user.getUsername(),user.getMailAddress()));
			}
		}
		
		return list;
	}
	
}
