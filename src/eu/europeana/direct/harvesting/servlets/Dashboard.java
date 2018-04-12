package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.plaf.synth.SynthSpinnerUI;

import com.mchange.v1.util.NumberedObjectCache;

import eu.europeana.direct.harvesting.models.helpers.DashboardRecord;
import eu.europeana.direct.harvesting.models.view.DashboardView;
import eu.europeana.direct.helpers.IRestClient;
import eu.europeana.direct.helpers.InternalSearcher;
import eu.europeana.direct.helpers.RestClient;
import eu.europeana.direct.helpers.UserValidation;
import eu.europeana.direct.logic.UserLogic;

/**
 * Servlet implementation class Dashboard
 */
@WebServlet("/Dashboard")
public class Dashboard extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private InternalSearcher searcher = new InternalSearcher();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Dashboard() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
	
		Cookie providerCookie = UserValidation.getCookieValue(request, "provider");
		Cookie apikeyCookie = UserValidation.getCookieValue(request, "apikey");
		Cookie userCookie = UserValidation.getCookieValue(request, "user");
		String provider = null;
		String apikey = null;
		String user = null;
		
		if(providerCookie != null){
			 provider = providerCookie.getValue();	
		}
		
		boolean loginRequired = true;						
		if(apikeyCookie != null){
			 apikey = apikeyCookie.getValue();	
		}
		if(userCookie != null){
			 user = userCookie.getValue();	
		}
		
		
		// if api key exists also check if actualy exists in database
//		if(apikey != null){
//			if(UserValidation.LoginRequired(apikey,user)){
//				response.sendRedirect("login.jsp");
//			}			
//		}
				
		
		int startFrom = 1;
		int rows = 10;
		int currentPage = 1;
		
		if(request.getParameter("page") != null){
			currentPage = Integer.parseInt(request.getParameter("page"));
			startFrom = (currentPage * rows) - 9;
		}
		
		int begin = currentPage - 2;
		if(begin < 1){
			begin = 1;
		}		
						
		if(provider == null){
			provider = "MEDEM-3";			
		}
		// return instituion name which will be retrieved from login?
		request.setAttribute("provider", provider);	
		
		DashboardView dashboardView = searcher.getRecordsByProvider(provider, rows, startFrom, formatDomainFromRequestUrl(request));
		
		if(dashboardView != null){
					
			double numOfPages = Math.ceil(((double) dashboardView.getNumOfPages() / (double) rows));
			int end = currentPage + 2;
			if(end > numOfPages){
				end = (int) numOfPages;
			}			
			
			// records by user		
			request.setAttribute("userRecords", dashboardView.getRecords());				
			// for pagination
			request.setAttribute("currentPage", currentPage);				
			request.setAttribute("numOfPages", numOfPages);		
			request.setAttribute("begin", begin);
			request.setAttribute("end", end);				

		}	
		
		RequestDispatcher rd = request.getRequestDispatcher("dashboard.jsp");							
		rd.forward(request, response);	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}
	
	private Map<String,String> formatDomainFromRequestUrl(HttpServletRequest request){
		
		Map<String,String> map = new HashMap<String,String>();
		
		StringBuilder sb = new StringBuilder();				
		String uri = (request.getScheme() == null ? "http" : request.getScheme()) + "://" + // "http" + "://
	             request.getServerName() +       // "myhost"
	             (request.getServerPort() > 0 ? ":" + request.getServerPort() : "") + // ":" + "8080"
	             request.getRequestURI();        // "/EuropeanaDirect/Dashboard"
		
		//remove /Dashboard	            
		sb.append(uri.replace("/Dashboard", ""));		
		map.put("odDomain",sb.toString());

		// parse edmDomain for oai endpoint		
		// index where /EuropeanaDirect starts
		int indx = sb.toString().lastIndexOf("/");
		// cut /EuropeanaDirect and replace with oai endpoint
		map.put("edmDomain",sb.toString().substring(0, indx) + "/oai/OAIHandler?");
		return map;
	}
}
