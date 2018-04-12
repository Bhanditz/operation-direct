package eu.europeana.direct.harvesting.helpers;

import java.text.ParseException;

import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

public class ScheduleHelper {

	public Trigger createNewTrigger(String triggerName, String group, String schedule, String scheduleFrequency) throws ParseException {
		
		String cronString = null;
		
		switch (schedule) {

		case "day":
			//number of days
			int dailyFrequency = Integer.parseInt(scheduleFrequency);						
			cronString = "0 0 0 1/"+dailyFrequency+" * ? *";										
		case "week":								
			// 7 * number of days = actual week frequency
			int weekly = 7 * Integer.parseInt(scheduleFrequency);;
			cronString = "0 0 0 1/"+weekly+" * ? *";						
		case "month":					
			cronString = "0 0 0 1 1/"+scheduleFrequency+" ? *";									
		}
		
		if(cronString != null){
			//set schedule to new trigger based on inserted values
			return TriggerBuilder.newTrigger().withIdentity(triggerName, group).startNow()
			.withSchedule(CronScheduleBuilder.cronSchedule(cronString)).build();
		}else{
			return null;	
		}				
	}
		
	
}
