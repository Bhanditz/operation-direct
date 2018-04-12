package eu.europeana.direct.harvesting.models.view;

/**
 * 
 * View model for representation of scheduled harvesting tasks
 *
 */
public class ScheduledTask {

	private String name;
	private String source;
	private String schedule;
	private String state;
	private String nextRun;
	private String triggerName;
	private String groupName;		
	
	public ScheduledTask() {
		super();
	}
	public ScheduledTask(String name, String source, String schedule, String state, String nextRun, String triggerName,
			String groupName) {
		super();
		this.name = name;
		this.source = source;
		this.schedule = schedule;
		this.state = state;
		this.nextRun = nextRun;
		this.triggerName = triggerName;
		this.groupName = groupName;
	}
	
	/**
	 * 
	 * Name of harvesting task
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * Url of harvesting's source
	 */
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * 
	 * Frequency of repeating task again
	 */
	public String getSchedule() {
		return schedule;
	}
	public void setSchedule(String schedule) {
		this.schedule = schedule;
	}
	
	/**
	 * 
	 * Current state of harvesting state
	 */
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	
	/**
	 * 
	 * Date of next run
	 */
	public String getNextRun() {
		return nextRun;
	}
	public void setNextRun(String nextRun) {
		this.nextRun = nextRun;
	}
	
	/**
	 * 
	 * Name of trigger that triggers the harvesting task
	 */
	public String getTriggerName() {
		return triggerName;
	}
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}
