package eu.europeana.direct.harvesting.servlets;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.matchers.KeyMatcher;

import eu.europeana.direct.backend.repositories.LogRepository;
import eu.europeana.direct.harvesting.helpers.ScheduleHelper;
import eu.europeana.direct.harvesting.jobs.HarvestData;
import eu.europeana.direct.harvesting.jobs.QuartzJobListener;
import eu.europeana.direct.harvesting.logging.model.ImportLog;
import eu.europeana.direct.harvesting.logging.model.ImportLogDetail;
import eu.europeana.direct.harvesting.models.view.ScheduledTask;
import eu.europeana.direct.legacy.api.impl.LegacyApiServiceImpl;
import net.redhogs.cronparser.CronExpressionDescriptor;

/**
 * Servlet implementation class Schedules
 */
@SuppressWarnings("unchecked")
@WebServlet("/Schedules")
public class Schedules extends HttpServlet {
	private static final long serialVersionUID = 1L;       
	private Scheduler scheduler;
	private LogRepository logRepository = new LogRepository();
	final static Logger logger = Logger.getLogger(LegacyApiServiceImpl.class);	

    /**
     * @see HttpServlet#HttpServlet()
     */
    public Schedules() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			scheduler = new StdSchedulerFactory("quartz.properties").getScheduler();																	
			
			// list of all scheduled tasks stored in database
			List<ScheduledTask> scheduledTasks = new ArrayList<ScheduledTask>();

			// loop through all scheduled tasks
			for (JobKey jobKey : scheduler.getJobKeys(GroupMatcher.groupEquals("harvesting"))) {
				String jobName = jobKey.getName();

				// get job's trigger
				List<Trigger> triggers = (List<Trigger>) scheduler.getTriggersOfJob(jobKey);
				Date nextFireTime = triggers.get(0).getFireTimeAfter(DateUtils.addHours(new Date(), 2));
				SimpleDateFormat sdf = new SimpleDateFormat("dd/M/yyyy");
				String source = scheduler.getJobDetail(jobKey).getJobDataMap().getString("source");
				String[] frequency = null;
				String schdl = "";
				String s = "";

				try {
					// parse cron expression to sentence
					CronTrigger cronTrigger = (CronTrigger) triggers.get(0);
					String cronExpr = cronTrigger.getCronExpression();
					frequency = CronExpressionDescriptor.getDescription(cronExpr).split(",");

					if (frequency.length > 1 && frequency.length <= 3) {
						schdl = frequency[1];
					} else {
						schdl = frequency[0];
					}
					if (frequency.length > 3) {
						schdl = frequency[2];
					}
					if (frequency.length == 2) {
						schdl = "Every day";
					}

					schdl = schdl.trim();
					s = schdl.substring(0, 1).toUpperCase() + schdl.substring(1);

				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				scheduledTasks.add(new ScheduledTask(jobName, source, s, "Active", sdf.format(nextFireTime).toString(),
						triggers.get(0).getKey().getName(), triggers.get(0).getKey().getGroup()));
			}

			request.setAttribute("taskList", scheduledTasks);

		} catch (SchedulerException e2) {
			logger.error(e2.getMessage(),e2);
			e2.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
		}
		
		RequestDispatcher rd = request.getRequestDispatcher("schedules.jsp");
		rd.forward(request, response);

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Trigger oldTrigger;
		Trigger newTrigger = null;
	
		// job rescheduling
		if(request.getParameter("scheduleValues") != null && request.getParameter("scheduleNumber") != null){
			try {

				ScheduleHelper scheduleHelper = new ScheduleHelper();
				
				//schedule unit (day,week or month)
				String schedule = request.getParameter("scheduleValues");				
				//number of days/weeks/months
				String scheduleFrequency = request.getParameter("scheduleNumber");
				//name of task's trigger
				String triggerName = request.getParameter("trigger");				
				//group name of task's trigger
				String group = request.getParameter("group");
				
				
				scheduler = new StdSchedulerFactory("quartz.properties").getScheduler();
				//retrieves old trigger
				oldTrigger = scheduler.getTrigger(new TriggerKey(triggerName, group));
				
				
				newTrigger =  scheduleHelper.createNewTrigger(triggerName,group,schedule,scheduleFrequency);
				
				if(newTrigger != null){
					// replacing old trigger with new one and rescheduling job
					scheduler.rescheduleJob(oldTrigger.getKey(), newTrigger);				

					ImportLog importLog = new ImportLog();
					ImportLogDetail importLogDetail = new ImportLogDetail();
					TimeZone.setDefault(TimeZone.getTimeZone("CEST"));

					importLog.setJobname(oldTrigger.getJobKey().getName());
					importLog.setStartTime(DateUtils.addHours(new Date(), 2));
					importLog.setErrors(0);
					importLog.setSuccesses(0);
					importLog.setWarnings(0);
					
					long logId = logRepository.saveImportLog(importLog);
					
					if(logId > 0){
						importLogDetail.setLogId(Math.toIntExact(logId));
						importLogDetail.setStatus("success");
						SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy HH:mm");
						importLogDetail.setPayload(""+logId);
						importLogDetail.setMessage(oldTrigger.getJobKey().getName()+" rescheduled. Next start: "+sdf.format(newTrigger.getFireTimeAfter((DateUtils.addHours(new Date(), 2)))));
						logRepository.saveImportLogDetail(importLogDetail);
					}	
				}																
				
			} catch (SchedulerException | ParseException e) {
				logger.error("Failed rescheduling job "+e.getMessage(),e);
			}finally {
				logRepository.close();
			}		
		}		
		doGet(request, response);
	}
}
