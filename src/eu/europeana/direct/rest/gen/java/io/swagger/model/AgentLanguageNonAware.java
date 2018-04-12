package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Language non-aware (non-multilingual) object for Agent
 **/
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
public class AgentLanguageNonAware implements ObjectInformation,Serializable {

	private String role = null;
	private List<KeyValuePair> customFields = new ArrayList<KeyValuePair>();

	private String dateOfBirth = null;
	private String dateOfDeath = null;
	private String dateOfEstablishment = null;
	private String dateOfTermination = null;

	/**
	 * The role the contextual object has for the cultural heritage object in
	 * which it is included. This field is used only for linking contextual
	 * objects to their Cultural Heritage Object
	 **/

	@JsonProperty("role")
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	/**
	 * List of other fields describing the object. Can contain, but is not
	 * limited to EDM fields, not documented explicitly.
	 **/

	@JsonProperty("customFields")
	public List<KeyValuePair> getCustomFields() {
		return customFields;
	}

	public void setCustomFields(List<KeyValuePair> customFields) {
		this.customFields = customFields;
	}

	/**
	 * The date the agent (person) was born.
	 */
	@JsonProperty("dateOfBirth")
	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	/**
	 * The date the agent (person) died.
	 **/

	@JsonProperty("dateOfDeath")
	public String getDateOfDeath() {
		return dateOfDeath;
	}

	public void setDateOfDeath(String dateOfDeath) {
		this.dateOfDeath = dateOfDeath;
	}

	/**
	**/

	@JsonProperty("dateOfEstablishment")
	public String getDateOfEstablishment() {
		return dateOfEstablishment;
	}

	public void setDateOfEstablishment(String dateOfEstablishment) {
		this.dateOfEstablishment = dateOfEstablishment;
	}

	/**
	**/

	@JsonProperty("dateOfTermination")
	public String getDateOfTermination() {
		return dateOfTermination;
	}

	public void setDateOfTermination(String dateOfTermination) {
		this.dateOfTermination = dateOfTermination;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		AgentLanguageNonAware agentLanguageNonAware = (AgentLanguageNonAware) o;
		return Objects.equals(role, agentLanguageNonAware.role)
				&& Objects.equals(dateOfBirth, agentLanguageNonAware.dateOfBirth)
				&& Objects.equals(dateOfDeath, agentLanguageNonAware.dateOfDeath)
				&& Objects.equals(dateOfEstablishment, agentLanguageNonAware.dateOfEstablishment)
				&& Objects.equals(dateOfTermination, agentLanguageNonAware.dateOfTermination)
				&& Objects.equals(customFields, agentLanguageNonAware.customFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(role, customFields, dateOfBirth, dateOfDeath, dateOfEstablishment, dateOfTermination);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class AgentLanguageNonAware {\n");

		sb.append("    role: ").append(toIndentedString(role)).append("\n");
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
		dateFields.add("dateOfBirth");
		dateFields.add("dateOfDeath");
		dateFields.add("dateOfEstablishment");
		dateFields.add("dateOfTermination");
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
