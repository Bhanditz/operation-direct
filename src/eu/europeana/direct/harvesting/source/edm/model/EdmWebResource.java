package eu.europeana.direct.harvesting.source.edm.model;

import java.util.*;

public class EdmWebResource {

	private Map<String,String> creator = new HashMap<>();
	private Map<String,String> description = new HashMap<>();
	private Map<String,String> format = new HashMap<>();
	private List<String> rights = new ArrayList<String>();
	private List<String> source = new ArrayList<String>();
	private List<String> conformsTo = new ArrayList<String>();
	private List<String> created = new ArrayList<String>();
	private List<String> extent = new ArrayList<String>();
	private List<String> hasPart = new ArrayList<String>();
	private List<String> isFormatOf = new ArrayList<String>();
	private List<String> isPartOf = new ArrayList<String>();
	private List<String> isReferencedBy = new ArrayList<String>();
	private List<String> issued = new ArrayList<String>();
	private List<String> isNextInSequence = new ArrayList<String>();
	private String edmRights;
	private List<String> sameAs = new ArrayList<String>();
	private List<String> type = new ArrayList<String>();
	private String link;
	
	public Map<String, String> getCreator() {
		return creator;
	}
	public void setCreator(Map<String, String> creator) {
		this.creator = creator;
	}
	public Map<String, String> getDescription() {
		return description;
	}
	public void setDescription(Map<String, String> description) {
		this.description = description;
	}
	public Map<String, String> getFormat() {
		return format;
	}
	public void setFormat(Map<String, String> format) {
		this.format = format;
	}
	public List<String> getRights() {
		return rights;
	}
	public void setRights(List<String> rights) {
		this.rights = rights;
	}
	public List<String> getSource() {
		return source;
	}
	public void setSource(List<String> source) {
		this.source = source;
	}
	public List<String> getConformsTo() {
		return conformsTo;
	}
	public void setConformsTo(List<String> conformsTo) {
		this.conformsTo = conformsTo;
	}
	public List<String> getCreated() {
		return created;
	}
	public void setCreated(List<String> created) {
		this.created = created;
	}
	public List<String> getExtent() {
		return extent;
	}
	public void setExtent(List<String> extent) {
		this.extent = extent;
	}
	public List<String> getHasPart() {
		return hasPart;
	}
	public void setHasPart(List<String> hasPart) {
		this.hasPart = hasPart;
	}	
	public List<String> getIsFormatOf() {
		return isFormatOf;
	}
	public void setIsFormatOf(List<String> isFormatOf) {
		this.isFormatOf = isFormatOf;
	}
	public List<String> getIsPartOf() {
		return isPartOf;
	}
	public void setIsPartOf(List<String> isPartOf) {
		this.isPartOf = isPartOf;
	}	
	public List<String> getIsReferencedBy() {
		return isReferencedBy;
	}
	public void setIsReferencedBy(List<String> isReferencedBy) {
		this.isReferencedBy = isReferencedBy;
	}	
	public List<String> getIssued() {
		return issued;
	}
	public void setIssued(List<String> issued) {
		this.issued = issued;
	}
	public List<String> getIsNextInSequence() {
		return isNextInSequence;
	}
	public void setIsNextInSequence(List<String> isNextInSequence) {
		this.isNextInSequence = isNextInSequence;
	}
	public String getEdmRights() {
		return edmRights;
	}
	public void setEdmRights(String edmRights) {
		this.edmRights = edmRights;
	}
	public List<String> getSameAs() {
		return sameAs;
	}
	public void setSameAs(List<String> sameAs) {
		this.sameAs = sameAs;
	}
	public List<String> getType() {
		return type;
	}
	public void setType(List<String> type) {
		this.type = type;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	
}
