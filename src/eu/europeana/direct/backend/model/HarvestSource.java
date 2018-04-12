package eu.europeana.direct.backend.model;

import java.awt.SecondaryLoop;
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
 * Database representation of harvesting tasks
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "harvest_source")
@SequenceGenerator(name = "harvest_source_id_seq", sequenceName = "harvest_source_id_seq", allocationSize=1)
@NamedQueries({
@NamedQuery(name = "HarvestSource.loadAll", query = "SELECT h FROM HarvestSource h")	
})
public class HarvestSource implements Serializable{

	
	private int id;
	private String type;
	private String name;
	private Date created;
	private String configuration;
	
	/**
	 * Unique ID of the harvesting task	
	 */
	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE,generator="harvest_source_id_seq")
	@Column(name="id")
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * 
	 * Type of the harvesting task (OAI-PMN, API,..)
	 */
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	/**
	 * 
	 * Name of the harvesting task
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * 
	 * Date of creation of the harvesting task
	 */
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	
	/**
	 * 
	 * Configuration of the harvesting task
	 */
	public String getConfiguration() {
		return configuration;
	}
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
	
}
