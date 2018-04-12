package eu.europeana.direct.harvesting.models.view;

/**
 * 
 * View model for the representation of Harvesters
 *
 */
public class HarvestSourceView {

	private String type;
	private String name;
	private String created;
	private String configuration;
	
	public HarvestSourceView(){}
	
	public HarvestSourceView(String type, String name, String created, String configuration) {
		super();
		this.type = type;
		this.name = name;
		this.created = created;
		this.configuration = configuration;
	}
	
	/**
	 * Type of harvester (OAI-PMH, API,..)
	 */
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;		
	}
	
	/**
	 * 
	 * Name of harvester
	 */
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Date of creation of harvester
	 */
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	
	/**
	 * Configuration of harvester
	 */
	public String getConfiguration() {
		return configuration;
	}
	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
	
	
	
	
}
