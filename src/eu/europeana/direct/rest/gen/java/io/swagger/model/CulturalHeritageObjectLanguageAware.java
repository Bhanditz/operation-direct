package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 
 * Language aware (multilingual) fields of the object, such as title or
 * description for object Cultural Heritage Object
 *
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class CulturalHeritageObjectLanguageAware implements ObjectInformation,Serializable {

	private String language = null;
	private String title = null;
	private String description = null;
	private List<String> publisher = new ArrayList<String>();
	private List<String> source = new ArrayList<String>();
	private List<String> alternative = new ArrayList<String>();
	private String created;
	private String issued;
	private List<String> format = new ArrayList<String>();
	private List<String> extent = new ArrayList<String>();
	private List<String> medium = new ArrayList<String>();
	private List<String> provenance = new ArrayList<String>();
	private List<KeyValuePair> customFields = new ArrayList<KeyValuePair>();

	/**
	 * Two letter ISO language code of the language in which the data are
	 * provided.
	 **/

	@JsonProperty("language")
	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	/**
	 * The title of the object in the specified language.
	 **/

	@JsonProperty("title")
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * The description of the object in specified language.
	 **/

	@JsonProperty("description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * A list of publishers for the submitted object
	 **/

	@JsonProperty("publisher")
	public List<String> getPublisher() {
		return publisher;
	}

	public void setPublisher(List<String> publisher) {
		this.publisher = publisher;
	}

	/**
	 * A list of sources for the submitted object
	 **/

	@JsonProperty("source")
	public List<String> getSource() {
		return source;
	}

	public void setSource(List<String> source) {
		this.source = source;
	}

	/**
	 * A list of alternatives for the submitted object
	 **/

	@JsonProperty("alternative")
	public List<String> getAlternative() {
		return alternative;
	}

	public void setAlternative(List<String> alternative) {
		this.alternative = alternative;
	}

	/**
	 * Date of creation of the object (not the record)
	 **/

	@JsonProperty("created")
	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	/**
	 * Date(s) of formal issuance or publication of the object
	 **/

	@JsonProperty("issued")
	public String getIssued() {
		return issued;
	}

	public void setIssued(String issued) {
		this.issued = issued;
	}

	/**
	 * Terms generally applied to indicate the format of the object or the file
	 * format of a born digital object
	 **/

	@JsonProperty("format")
	public List<String> getFormat() {
		return format;
	}

	public void setFormat(List<String> format) {
		this.format = format;
	}

	/**
	 * The measurements of the submitted object
	 **/

	@JsonProperty("extent")
	public List<String> getExtent() {
		return extent;
	}

	public void setExtent(List<String> extent) {
		this.extent = extent;
	}

	/**
	 * Material(s) or physical carrier(s) of the object
	 **/

	@JsonProperty("medium")
	public List<String> getMedium() {
		return medium;
	}

	public void setMedium(List<String> medium) {
		this.medium = medium;
	}

	/**
	 * Statement(s) of changes in ownership and custody of the object since its
	 * creation
	 **/

	@JsonProperty("provenance")
	public List<String> getProvenance() {
		return provenance;
	}

	public void setProvenance(List<String> provenance) {
		this.provenance = provenance;
	}

	/**
	 * List of other fields describing the object. Can contain, but is not
	 * limited to all the EDM fields, documented in the EDM specification
	 **/

	@JsonProperty("customFields")
	public List<KeyValuePair> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<KeyValuePair> customFields) {
		this.customFields = customFields;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		CulturalHeritageObjectLanguageAware culturalHeritageObjectLanguageAware = (CulturalHeritageObjectLanguageAware) o;
		return Objects.equals(language, culturalHeritageObjectLanguageAware.language)
				&& Objects.equals(title, culturalHeritageObjectLanguageAware.title)
				&& Objects.equals(description, culturalHeritageObjectLanguageAware.description)
				&& Objects.equals(publisher, culturalHeritageObjectLanguageAware.publisher)
				&& Objects.equals(source, culturalHeritageObjectLanguageAware.source)
				&& Objects.equals(alternative, culturalHeritageObjectLanguageAware.alternative)
				&& Objects.equals(created, culturalHeritageObjectLanguageAware.created)
				&& Objects.equals(issued, culturalHeritageObjectLanguageAware.issued)
				&& Objects.equals(format, culturalHeritageObjectLanguageAware.format)
				&& Objects.equals(extent, culturalHeritageObjectLanguageAware.extent)
				&& Objects.equals(medium, culturalHeritageObjectLanguageAware.medium)
				&& Objects.equals(provenance, culturalHeritageObjectLanguageAware.provenance)
				&& Objects.equals(customFields, culturalHeritageObjectLanguageAware.customFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(language, title, description, publisher, source, alternative, created, issued, format,
				extent, medium, provenance, customFields);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class CulturalHeritageObjectLanguageAware {\n");

		sb.append("    language: ").append(toIndentedString(language)).append("\n");
		sb.append("    title: ").append(toIndentedString(title)).append("\n");
		sb.append("    description: ").append(toIndentedString(description)).append("\n");
		sb.append("    publisher: ").append(toIndentedString(publisher)).append("\n");
		sb.append("    source: ").append(toIndentedString(source)).append("\n");
		sb.append("    alternative: ").append(toIndentedString(alternative)).append("\n");
		sb.append("    created: ").append(toIndentedString(created)).append("\n");
		sb.append("    issued: ").append(toIndentedString(issued)).append("\n");
		sb.append("    format: ").append(toIndentedString(format)).append("\n");
		sb.append("    extent: ").append(toIndentedString(extent)).append("\n");
		sb.append("    medium: ").append(toIndentedString(medium)).append("\n");
		sb.append("    provenance: ").append(toIndentedString(provenance)).append("\n");
		sb.append("    customFields: ").append(toIndentedString(customFields)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}

	@Override
	public List<String> getDateFields() {
		List<String> dateFields = new ArrayList<String>();
		dateFields.add("created");
		dateFields.add("issued");
		return dateFields;
	}

	@Override
	public List<String> getNumericFields() {
		List<String> numericFields = new ArrayList<String>();
		return numericFields;
	}

	@Override
	public List<String> getAllFields() {
		List<String> fields = new ArrayList<String>();
		for (Field field : this.getClass().getDeclaredFields()) {
			fields.add(field.getName());
		}
		return fields;
	}
}
