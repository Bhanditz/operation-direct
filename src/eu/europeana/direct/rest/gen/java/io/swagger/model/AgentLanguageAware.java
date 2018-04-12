package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Language aware (multilingual) object for Agent
 **/
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class AgentLanguageAware implements ObjectInformation,Serializable {

	private String language = null;
	private String preferredLabel;
	private List<String> alternativeLabel = new ArrayList<String>();
	private List<String> identifier = new ArrayList<String>();
	private String biographicalInformation;
	private String gender;
	private String professionOrOccupation;
	private String placeOfBirth;
	private String placeOfDeath;
	private List<String> sameAs = new ArrayList<String>();
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
	 * The label to be used for displaying the agent.
	 **/

	@JsonProperty("preferredLabel")
	public String getPreferredLabel() {
		return preferredLabel;
	}

	public void setPreferredLabel(String preferredLabel) {
		this.preferredLabel = preferredLabel;
	}

	/**
	 * The alternative label to be used for displaying the agent.
	 **/

	@JsonProperty("alternativeLabel")
	public List<String> getAlternativeLabel() {
		return alternativeLabel;
	}

	public void setAlternativeLabel(List<String> alternativeLabel) {
		this.alternativeLabel = alternativeLabel;
	}

	/**
	 * The identifier of the agent.
	 **/

	@JsonProperty("identifier")
	public List<String> getIdentifier() {
		return identifier;
	}

	public void setIdentifier(List<String> identifier) {
		this.identifier = identifier;
	}

	/**
	 **/

	@JsonProperty("biographicalInformation")
	public String getBiographicalInformation() {
		return biographicalInformation;
	}

	public void setBiographicalInformation(String biographicalInformation) {
		this.biographicalInformation = biographicalInformation;
	}

	/**
	 **/

	@JsonProperty("gender")
	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * The profession or occupation in which the agent works or has worked.
	 **/

	@JsonProperty("professionOrOccupation")
	public String getProfessionOrOccupation() {
		return professionOrOccupation;
	}

	public void setProfessionOrOccupation(String professionOrOccupation) {
		this.professionOrOccupation = professionOrOccupation;
	}

	/**
	 * The town, city, province, state, and/or country in which a person was
	 * born.
	 **/

	@JsonProperty("placeOfBirth")
	public String getPlaceOfBirth() {
		return placeOfBirth;
	}

	public void setPlaceOfBirth(String placeOfBirth) {
		this.placeOfBirth = placeOfBirth;
	}

	/**
	 * The town, city, province, state, and/or country in which a person died.
	 **/

	@JsonProperty("placeOfDeath")
	public String getPlaceOfDeath() {
		return placeOfDeath;
	}

	public void setPlaceOfDeath(String placeOfDeath) {
		this.placeOfDeath = placeOfDeath;
	}

	/**
	 **/

	@JsonProperty("sameAs")
	public List<String> getSameAs() {
		return sameAs;
	}

	public void setSameAs(List<String> sameAs) {
		this.sameAs = sameAs;
	}

	/**
	 * List of other fields describing the object. Can contain, but is not
	 * limited to all the EDM fields, documented in the EDM specification"
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
		AgentLanguageAware agentLanguageAware = (AgentLanguageAware) o;
		return Objects.equals(language, agentLanguageAware.language)
				&& Objects.equals(preferredLabel, agentLanguageAware.preferredLabel)
				&& Objects.equals(alternativeLabel, agentLanguageAware.alternativeLabel)
				&& Objects.equals(identifier, agentLanguageAware.identifier)
				&& Objects.equals(biographicalInformation, agentLanguageAware.biographicalInformation)
				&& Objects.equals(gender, agentLanguageAware.gender)
				&& Objects.equals(professionOrOccupation, agentLanguageAware.professionOrOccupation)
				&& Objects.equals(placeOfBirth, agentLanguageAware.placeOfBirth)
				&& Objects.equals(placeOfDeath, agentLanguageAware.placeOfDeath)
				&& Objects.equals(sameAs, agentLanguageAware.sameAs)
				&& Objects.equals(customFields, agentLanguageAware.customFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(language, preferredLabel, alternativeLabel, identifier, biographicalInformation, gender,
				professionOrOccupation, placeOfBirth, placeOfDeath, sameAs, customFields);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class AgentLanguageAware {\n");

		sb.append("    language: ").append(toIndentedString(language)).append("\n");
		sb.append("    preferredLabel: ").append(toIndentedString(preferredLabel)).append("\n");
		sb.append("    alternativeLabel: ").append(toIndentedString(alternativeLabel)).append("\n");
		sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
		sb.append("    biographicalInformation: ").append(toIndentedString(biographicalInformation)).append("\n");
		sb.append("    gender: ").append(toIndentedString(gender)).append("\n");
		sb.append("    professionOrOccupation: ").append(toIndentedString(professionOrOccupation)).append("\n");
		sb.append("    placeOfBirth: ").append(toIndentedString(placeOfBirth)).append("\n");
		sb.append("    placeOfDeath: ").append(toIndentedString(placeOfDeath)).append("\n");
		sb.append("    sameAs: ").append(toIndentedString(sameAs)).append("\n");
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
