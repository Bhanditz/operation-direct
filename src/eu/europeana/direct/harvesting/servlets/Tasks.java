package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.JobListener;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.Trigger.TriggerState;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

import eu.europeana.direct.backend.repositories.LogRepository;
import eu.europeana.direct.harvesting.jobs.HarvestData;
import eu.europeana.direct.harvesting.jobs.QuartzJobListener;
import eu.europeana.direct.harvesting.logging.model.ImportLog;
import eu.europeana.direct.harvesting.logging.model.ImportLogDetail;
import eu.europeana.direct.harvesting.models.view.TaskDetail;
import eu.europeana.direct.legacy.api.impl.LegacyApiServiceImpl;
import eu.europeana.direct.legacy.index.LuceneIndexing;


/**
 * Servlet implementation class Tasks
 */
@WebServlet("/Tasks")
public class Tasks extends HttpServlet{
	private static final long serialVersionUID = 1L;      
	private Scheduler scheduler;
	private ObjectMapper jsonMapper = new ObjectMapper();
	final static Logger logger = Logger.getLogger(Tasks.class);

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tasks() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
	
		try {	
			
			scheduler = new StdSchedulerFactory("quartz.properties").getScheduler();						
											
			//list of all scheduled tasks stored in database
			List<TaskDetail> scheduledTasks = new ArrayList<TaskDetail>();
			//loop through all scheduled tasks
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals("harvesting"))) {

				String jobName = jobKey.getName();
				String jobGroup = jobKey.getGroup();
				String status = "Stopped";

				// list of currently running tasks
				for (int i = 0; i < scheduler.getCurrentlyExecutingJobs().size(); i++) {
					// if job is running set proper status
					if (scheduler.getCurrentlyExecutingJobs().get(i).getJobDetail().getKey().getName()
							.equals(jobName)) {
						status = "Running";
						break;
					}
				}
				String source = scheduler.getJobDetail(jobKey).getJobDataMap().getString("source");
				scheduledTasks.add(new TaskDetail(jobName, source, status, jobGroup));

			}			
		 	request.setAttribute("taskList", scheduledTasks);					
		} catch (Exception e) {			
			logger.error(e.getMessage(),e);
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("tasks.jsp");
		rd.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//list of all scheduled tasks stored in database
		List<TaskDetail> scheduledTasks = new ArrayList<TaskDetail>();

		try {
			scheduler = new StdSchedulerFactory("quartz.properties").getScheduler();
			
			try {		
				
				// loop through all scheduled tasks
				for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals("harvesting"))) {

					String jobName = jobKey.getName();
					String jobGroup = jobKey.getGroup();
					String status = "Stopped";

					// list of currently running tasks
					for (int i = 0; i < scheduler.getCurrentlyExecutingJobs().size(); i++) {
						// if job is running set proper status
						if (scheduler.getCurrentlyExecutingJobs().get(i).getJobDetail().getKey().getName()
								.equals(jobName)) {
							status = "Running";
							break;
						}
					}
					String source = scheduler.getJobDetail(jobKey).getJobDataMap().getString("source");
					scheduledTasks.add(new TaskDetail(jobName, source, status, jobGroup));

				}					
															
			} catch (Exception e) {
				logger.error(e.getMessage(),e);
			}
			
			if (request.getParameter("jobId") != null) {
				// start job action
				String key = request.getParameter("jobId");
				String group = request.getParameter("groupId");

				try {
					JobKey jobKey = new JobKey(key, group);
					scheduler.triggerJob(jobKey);
				} catch (SchedulerException e1) {
					logger.error(e1.getMessage(), e1);
				}

			} else if (request.getParameter("id") != null) {
				// stop job action
				String key = request.getParameter("id");
				String group = request.getParameter("group");

				try {
					JobKey stopJobKey = new JobKey(key, group);
					scheduler.interrupt(stopJobKey);
				} catch (Exception e) {
					logger.error("Unable to interrupt job: " + e.getMessage(), e);
				}
			}
			
		} catch (SchedulerException e2) {
			logger.error("Failed initializing scheduler from quartz.properties: "+e2.getMessage(),e2);
		}		
										
		//send json response of scheduled tasks list
		String jsonInString = jsonMapper.writeValueAsString(scheduledTasks);
		response.setContentType("application/json"); 
	    response.setCharacterEncoding("UTF-8");
	    response.getWriter().write(jsonInString);
	}
}
