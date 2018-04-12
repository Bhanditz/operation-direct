package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 
 * Language non-aware (non-multilingual) fields of the object, such as
 * identifier or extent for object Cultural Heritage Object
 *
 */
@SuppressWarnings("serial")
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class CulturalHeritageObjectLanguageNonAware implements ObjectInformation,Serializable {

	public enum TypeEnum {
		IMAGE("IMAGE"), AUDIO("AUDIO"), VIDEO("VIDEO"), _3D("3D"), TEXT("TEXT");

		private String value;

		TypeEnum(String value) {
			this.value = value;
		}

		@Override
		@JsonValue
		public String toString() {
			return value;
		}
	}

	private String objectLanguage;
	private String[] languageObject;
	private TypeEnum type = null;
	private String owner;
	private List<String> identifier = new ArrayList<String>();
	private List<String> relation = new ArrayList<String>();
	private List<KeyValuePair> customFields = new ArrayList<KeyValuePair>();
	private String dataOwner;	

	/**
	 * One of the types accepted by Europeana to support Europeana Collections
	 * functionality: TEXT, VIDEO, SOUND, IMAGE, 3D = ['IMAGE', 'AUDIO',
	 * 'VIDEO', '3D']
	 **/

	@JsonProperty("type")
	public TypeEnum getType() {
		return type;
	}

	public void setType(TypeEnum type) {
		this.type = type;
	}

	/**
	 * Identifier of the original object
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

	@JsonProperty("relation")
	public List<String> getRelation() {
		return relation;
	}

	public void setRelation(List<String> relation) {
		this.relation = relation;
	}

	
	@JsonProperty("objectLanguage")
	public String getObjectLanguage() {
		return objectLanguage;
	}

	public void setObjectLanguage(String objectLanguage) {
		this.objectLanguage = objectLanguage;
	}
	
	@JsonProperty("languageObject")
	public String[] getLanguageObject() {
		return languageObject;
	}

	public void setLanguageObject(String[] languageObject) {
		this.languageObject = languageObject;
	}
	
	/**
	 * List of other fields describing the object. Can contain, but is not
	 * limited to EDM fields, not documented explicitly. (TBD: provide the full
	 * list along with a link to online documentation)
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
		CulturalHeritageObjectLanguageNonAware culturalHeritageObjectLanguageNonAware = (CulturalHeritageObjectLanguageNonAware) o;
		return Objects.equals(type, culturalHeritageObjectLanguageNonAware.type)
				&& Objects.equals(identifier, culturalHeritageObjectLanguageNonAware.identifier)
				&& Objects.equals(objectLanguage, culturalHeritageObjectLanguageNonAware.objectLanguage)
				&& Objects.equals(relation, culturalHeritageObjectLanguageNonAware.relation)
				&& Objects.equals(dataOwner, culturalHeritageObjectLanguageNonAware.relation)
				&& Objects.equals(customFields, culturalHeritageObjectLanguageNonAware.customFields);
	}

	@Override
	public int hashCode() {
		return Objects.hash(type, identifier, relation, dataOwner,customFields);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class CulturalHeritageObjectLanguageNonAware {\n");
		sb.append("    language: ").append(toIndentedString(objectLanguage)).append("\n");
		sb.append("    type: ").append(toIndentedString(type)).append("\n");
		sb.append("    identifier: ").append(toIndentedString(identifier)).append("\n");
		sb.append("    relation: ").append(toIndentedString(relation)).append("\n");
		sb.append("    dataOwner: ").append(toIndentedString(dataOwner)).append("\n");
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

	/**
	 * Name or identifier of the organisation publishing the object. Note:
	 * design work in progress, this field will be derived from the user
	 * performing the operation and connected to Europeana's organisation
	 * profile.
	 */
	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDataOwner() {
		return dataOwner;
	}

	public void setDataOwner(String dataOwner) {
		this.dataOwner = dataOwner;
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
