package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Language non-aware (non-multilingual) object for Concept
 **/
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class ConceptLanguageNonAware implements ObjectInformation,Serializable{
  
  private String role = null;
  private List<KeyValuePair> customFields = new ArrayList<KeyValuePair>();

  
  /**
   * The role the contextual object has for the cultural heritage object in which it is included. This field is used only for linking contextual objects to their Cultural Heritage Object
   **/
  
  @JsonProperty("role")
  public String getRole() {
    return role;
  }
  public void setRole(String role) {
    this.role = role;
  }

  
  /**
   * List of other fields describing the object. Can contain, but is not limited to EDM fields, not documented explicitly.
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
    ConceptLanguageNonAware conceptLanguageNonAware = (ConceptLanguageNonAware) o;
    return Objects.equals(role, conceptLanguageNonAware.role) &&
        Objects.equals(customFields, conceptLanguageNonAware.customFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, customFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConceptLanguageNonAware {\n");
    
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
		List<String> conceptLangNonAwareDateFields = new ArrayList<String>();
		return conceptLangNonAwareDateFields;
	}
	@Override
	public List<String> getNumericFields() {
		List<String> conceptLangNonAwareNumericFields = new ArrayList<String>();
		return conceptLangNonAwareNumericFields;
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

