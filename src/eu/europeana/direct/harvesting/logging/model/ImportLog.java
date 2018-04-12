package eu.europeana.direct.harvesting.logging.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * Log class for harvester
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "importlog")
@SequenceGenerator(name = "importlog_id_seq", sequenceName = "importlog_id_seq", allocationSize=1)
@NamedQueries({
@NamedQuery(name = "ImportLog.loadAll", query = "SELECT i FROM ImportLog i")	
})
public class ImportLog implements Serializable{

	private int Id;
	private String jobname;
	private Date startTime;
	private Date endTime;
	private int errors;
	private int warnings;
	private int successes;
	
	public ImportLog() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ImportLog(String jobname, Date startTime, Date endTime, int errors, int warnings, int successes) {
		super();
		this.jobname = jobname;
		this.startTime = startTime;
		this.endTime = endTime;
		this.errors = errors;
		this.warnings = warnings;
		this.successes = successes;
	}

	/**
	 * 
	 * Unique ID of Log
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="importlog_id_seq")
	@Column(name="id")
	public int getId() {
		return Id;
	}
	public void setId(int id) {
		Id = id;
	}
	
	/**
	 * Name of harvester	
	 */
	public String getJobname() {
		return jobname;
	}
	public void setJobname(String jobname) {
		this.jobname = jobname;
	}
	
	/**
	 * Time of when harvester started	 
	 */
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	/**
	 * Time of when harvester ended	 
	 */
	public Date getEndTime() {
		return endTime;
	}
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
	/**
	 * Number of harvesting records that failed saving to database 	 
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
}
