package eu.europeana.direct.harvesting.source.edm.model;

import java.util.*;

// class for dublin core(<dc:>) elements
@SuppressWarnings({"rawtypes","unchecked"})
public class OaiDcSource {

	private String headerIdentifier;
	private Map<String,List<String>> type = new HashMap();
	private Map<String,String> title = new HashMap();
	private Map<String,List<String>> format = new HashMap();
	private Map<String,List<String>> description = new HashMap();
	private Map<String,List<String>> rights = new HashMap();
	private Map<String,List<String>> publisher = new HashMap<>();
	private Map<String,List<String>> source = new HashMap();	
	private Map<String,List<String>> date = new HashMap();
	private List<String> audience = new ArrayList<String>();
	private Map<String,List<String>> creator = new HashMap<String,List<String>>();
	private List<String> relation = new ArrayList<String>();
	private List<String> identifier = new ArrayList<String>();
	private List<String> language = new ArrayList<String>();
	private Map<String,List<String>> subject = new HashMap<String,List<String>>();
	private List<String> contributor = new ArrayList<String>();
	private List<String> coverage = new ArrayList<String>();

	public String getHeaderIdentifier() {
		return headerIdentifier;
	}
	public void setHeaderIdentifier(String headerIdentifier) {
		this.headerIdentifier = headerIdentifier;
	}
	public Map<String, List<String>> getType() {
		return type;
	}
	public void setType(Map<String, List<String>> type) {
		this.type = type;
	}
	public Map<String, String> getTitle() {
		return title;
	}
	public void setTitle(Map<String, String> title) {
		this.title = title;
	}
	public Map<String, List<String>> getFormat() {
		return format;
	}
	public void setFormat(Map<String, List<String>> format) {
		this.format = format;
	}
	public Map<String, List<String>> getDescription() {
		return description;
	}
	public void setDescription(Map<String, List<String>> description) {
		this.description = description;
	}
	public Map<String, List<String>> getRights() {
		return rights;
	}
	public void setRights(Map<String, List<String>> rights) {
		this.rights = rights;
	}
	public List<String> getAudience() {
		return audience;
	}
	public void setAudience(List<String> audience) {
		this.audience = audience;
	}
	public Map<String, List<String>> getDate() {
		return date;
	}
	public void setDate(Map<String, List<String>> date) {
		this.date = date;
	}	
	public Map<String, List<String>> getCreator() {
		return creator;
	}
	public void setCreator(Map<String, List<String>> creator) {
		this.creator = creator;
	}
	public List<String> getRelation() {
		return relation;
	}
	public void setRelation(List<String> relation) {
		this.relation = relation;
	}
	public List<String> getIdentifier() {
		return identifier;
	}
	public void setIdentifier(List<String> identifier) {
		this.identifier = identifier;
	}
	public List<String> getLanguage() {
		return language;
	}
	public void setLanguage(List<String> language) {
		this.language = language;
	}	
	public Map<String, List<String>> getPublisher() {
		return publisher;
	}
	public void setPublisher(Map<String, List<String>> publisher) {
		this.publisher = publisher;
	}	
	public Map<String, List<String>> getSource() {
		return source;
	}
	public void setSource(Map<String, List<String>> source) {
		this.source = source;
	}
	public Map<String,List<String>> getSubject() {
		return subject;
	}
	public void setSubject(Map<String,List<String>> subject) {
		this.subject = subject;
	}
	public List<String> getContributor() {
		return contributor;
	}
	public void setContributor(List<String> contributor) {
		this.contributor = contributor;
	}
	public List<String> getCoverage() {
		return coverage;
	}
	public void setCoverage(List<String> coverage) {
		this.coverage = coverage;
	}	
}
