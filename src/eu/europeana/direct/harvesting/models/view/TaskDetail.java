package eu.europeana.direct.harvesting.models.view;

/**
 * 
 * View model for representation of harvesting tasks
 *
 */
public class TaskDetail {

	private String jobId;
	private String source;
	private String status;
	private String jobGroup;
	
	public TaskDetail() {
		super();
	}
	public TaskDetail(String jobId, String source, String status, String jobGroup) {
		super();
		this.jobId = jobId;
		this.source = source;
		this.status = status;
		this.jobGroup = jobGroup;
	}
	
	/**
	 * 
	 * Unique ID of harvesting job
	 */
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	
	/**
	 * Url of harvester's source
	 */
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	/**
	 * 
	 * Current status of harvesting task (Stopped or Running)
	 */
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getJobGroup() {
		return jobGroup;
	}
	public void setJobGroup(String jobGroup) {
		this.jobGroup = jobGroup;
	}
}
