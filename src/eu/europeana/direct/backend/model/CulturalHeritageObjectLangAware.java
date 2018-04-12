package eu.europeana.direct.backend.model;

public class CulturalHeritageObjectLangAware {

	private String title;
	private String description;
	private String[] publisher;
	private String[] source;
	private String[] alternative;
	private String[] created;
	private String[] issued;
	private String[] format;
	private String[] extent;
	private String[] medium;
	private String[] provenance;
	private String[] dataOwner;	
	
	public CulturalHeritageObjectLangAware() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public CulturalHeritageObjectLangAware(String title, String description, String[] publisher, String[] source,
			String[] alternative, String[] created, String[] issued, String[] format, String[] extent, String[] medium,
			String[] provenance, String[] dataOwner) {
		super();
		this.title = title;
		this.description = description;
		this.publisher = publisher;
		this.source = source;
		this.alternative = alternative;
		this.created = created;
		this.issued = issued;
		this.format = format;
		this.extent = extent;
		this.medium = medium;
		this.provenance = provenance;
		this.dataOwner = dataOwner;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String[] getPublisher() {
		return publisher;
	}
	public void setPublisher(String[] publisher) {
		this.publisher = publisher;
	}
	public String[] getSource() {
		return source;
	}
	public void setSource(String[] source) {
		this.source = source;
	}
	public String[] getAlternative() {
		return alternative;
	}
	public void setAlternative(String[] alternative) {
		this.alternative = alternative;
	}
	public String[] getCreated() {
		return created;
	}
	public void setCreated(String[] created) {
		this.created = created;
	}
	public String[] getIssued() {
		return issued;
	}
	public void setIssued(String[] issued) {
		this.issued = issued;
	}
	public String[] getFormat() {
		return format;
	}
	public void setFormat(String[] format) {
		this.format = format;
	}
	public String[] getExtent() {
		return extent;
	}
	public void setExtent(String[] extent) {
		this.extent = extent;
	}
	public String[] getMedium() {
		return medium;
	}
	public void setMedium(String[] medium) {
		this.medium = medium;
	}
	public String[] getProvenance() {
		return provenance;
	}
	public void setProvenance(String[] provenance) {
		this.provenance = provenance;
	}
	public String[] getDataOwner() {
		return dataOwner;
	}
	public void setDataOwner(String[] dataOwner) {
		this.dataOwner = dataOwner;
	}
	
	
}
