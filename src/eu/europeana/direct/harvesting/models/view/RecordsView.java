package eu.europeana.direct.harvesting.models.view;

import java.util.Date;

//frontend model for showing log records (importlog + importlogDetail)
/**
 * 
 * View model for showing harvesting log records
 *
 */
public class RecordsView {
	
	private String task;
	private String start;
	private int errors;
	private int warnings;
	private int successes;
	private String message;
	private String Status;

	public RecordsView() {
		super();
		// TODO Auto-generated constructor stub
	}

	public RecordsView(String task, String start, int errors, int warnings, int successes, String message,
			String status) {
		super();
		this.task = task;
		this.start = start;
		this.errors = errors;
		this.warnings = warnings;
		this.successes = successes;
		this.message = message;
		Status = status;
	}
	
	/**
	 * Name of harvester's task	
	 */
	public String getTask() {
		return task;
	}
	public void setTask(String task) {
		this.task = task;
	}
	
	/**
	 * Date of log appeared	 
	 */
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	
	/**
	 * Number of errors appeared when trying to save harvesting record to database	 
	 */
	public int getErrors() {
		return errors;
	}
	public void setErrors(int errors) {
		this.errors = errors;
	}
	

	/**
	 * Number of warnings appeared when trying to save harvesting record to database	 
	 */
	public int getWarnings() {
		return warnings;
	}
	public void setWarnings(int warnings) {
		this.warnings = warnings;
	}
	
	/**
	 * Number of successfully saved harvesting records	 
	 */
	public int getSuccesses() {
		return successes;
	}
	public void setSuccesses(int successes) {
		this.successes = successes;
	}
	
	/**
	 * Information about what was happening	 
	 */
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}	
}
