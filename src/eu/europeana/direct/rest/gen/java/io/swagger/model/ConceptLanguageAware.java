package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Language aware (multilingual) object for Concept
 **/
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class ConceptLanguageAware implements ObjectInformation,Serializable{
  
  private String language = null;
  private String preferredLabel;
  private List<String> alternativeLabel = new ArrayList<String>();
  private List<KeyValuePair> customFields = new ArrayList<KeyValuePair>();

  
  /**
   * Two letter ISO language code of the language in which the data are provided.
   **/
  
  @JsonProperty("language")
  public String getLanguage() {
    return language;
  }
  public void setLanguage(String language) {
    this.language = language;
  }

  
	/**
	 * The label to be used for displaying the concept.
	 **/
  @JsonProperty("preferredLabel")
  public String getPreferredLabel() {
    return preferredLabel;
  }
  public void setPreferredLabel(String preferredLabel) {
    this.preferredLabel = preferredLabel;
  }

  
  /**
	 * The alternative label to be used for displaying the concept.
	 **/
  
  @JsonProperty("alternativeLabel")
  public List<String> getAlternativeLabel() {
    return alternativeLabel;
  }
  public void setAlternativeLabel(List<String> alternativeLabel) {
    this.alternativeLabel = alternativeLabel;
  }

  
  /**
   *  List of other fields describing the object. Can contain, but is not limited to all the EDM fields, documented in the EDM specification"
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
    ConceptLanguageAware conceptLanguageAware = (ConceptLanguageAware) o;
    return Objects.equals(language, conceptLanguageAware.language) &&
        Objects.equals(preferredLabel, conceptLanguageAware.preferredLabel) &&
        Objects.equals(alternativeLabel, conceptLanguageAware.alternativeLabel) &&
        Objects.equals(customFields, conceptLanguageAware.customFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(language, preferredLabel, alternativeLabel, customFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ConceptLanguageAware {\n");
    
    sb.append("    language: ").append(toIndentedString(language)).append("\n");
    sb.append("    preferredLabel: ").append(toIndentedString(preferredLabel)).append("\n");
    sb.append("    alternativeLabel: ").append(toIndentedString(alternativeLabel)).append("\n");
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
		List<String> conceptLangAwareDateFields = new ArrayList<String>();
		return conceptLangAwareDateFields;
	}
	@Override
	public List<String> getNumericFields() {
		List<String> conceptLangAwareNumericFields = new ArrayList<String>();
		return conceptLangAwareNumericFields;
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

