package eu.europeana.direct.backend.model;

import javax.persistence.Column;

public class AgentLangNonAware {
	
	private String dateOfBirth;
	private String dateOfDeath;
	private String dateOfEstablishment;
	private String dateOfTermination;
	
	
	
	
	public AgentLangNonAware(String dateOfBirth, String dateOfDeath, String dateOfEstablishment,
			String dateOfTermination) {
		super();
		this.dateOfBirth = dateOfBirth;
		this.dateOfDeath = dateOfDeath;
		this.dateOfEstablishment = dateOfEstablishment;
		this.dateOfTermination = dateOfTermination;
	}

	public AgentLangNonAware() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * The date the agent (person) was born.
	 */
	@Column(name = "dateofbirth")
	public String getDateOfBirth() {
		return this.dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * The date the agent (person) died.
	 */
	@Column(name = "dateofdeath")
	public String getDateOfDeath() {
		return this.dateOfDeath;
	}

	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	@Column(name = "dateofestablishment")
	public String getDateOfEstablishment() {
		return this.dateOfEstablishment;
	}

	public void setDateOfEstablishment(String dateOfEstablishment) {
		this.dateOfEstablishment = dateOfEstablishment;
	}

	@Column(name = "dateoftermination")
	public String getDateOfTermination() {
		return this.dateOfTermination;
	}

	public void setDateOfTermination(String dateoftermination) {
		this.dateOfTermination = dateoftermination;
	}
}
