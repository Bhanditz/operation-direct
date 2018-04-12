package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


/**
 * Represents the Operation Direct entity Concept.
 **/
@JsonInclude(Include.NON_EMPTY)
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class Concept implements ObjectInformation,Serializable{
  
  private BigDecimal id = null;
  private List<ConceptLanguageAware> languageAwareFields = new ArrayList<ConceptLanguageAware>();
  private ConceptLanguageNonAware languageNonAwareFields = null;

  
  /**
   * Represents the unique Operation Direct ID of the Concept.
   **/
  
  @JsonProperty("id")
  public BigDecimal getId() {
    return id;
  }
  public void setId(BigDecimal id) {
    this.id = id;
  }

  
  /**
   * Language aware (multilingual) fields of the object
   **/
  
  @JsonProperty("languageAwareFields")
  public List<ConceptLanguageAware> getLanguageAwareFields() {
    return languageAwareFields;
  }
  public void setLanguageAwareFields(List<ConceptLanguageAware> languageAwareFields) {
    this.languageAwareFields = languageAwareFields;
  }

  
  /**
   * Language non-aware (non-multilingual) fields of the object
   **/
  
  @JsonProperty("languageNonAwareFields")
  public ConceptLanguageNonAware getLanguageNonAwareFields() {
    return languageNonAwareFields;
  }
  public void setLanguageNonAwareFields(ConceptLanguageNonAware languageNonAwareFields) {
    this.languageNonAwareFields = languageNonAwareFields;
  }

  

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Concept concept = (Concept) o;
    return Objects.equals(id, concept.id) &&
        Objects.equals(languageAwareFields, concept.languageAwareFields) &&
        Objects.equals(languageNonAwareFields, concept.languageNonAwareFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, languageAwareFields, languageNonAwareFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Concept {\n");
    
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    languageAwareFields: ").append(toIndentedString(languageAwareFields)).append("\n");
    sb.append("    languageNonAwareFields: ").append(toIndentedString(languageNonAwareFields)).append("\n");
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
		List<String> langAwareDateFields = new ConceptLanguageAware().getDateFields();
		List<String> langNonAwareDateFields = new ConceptLanguageNonAware().getDateFields();
		
		List<String> conceptDateFields = new ArrayList<String>(langAwareDateFields);
		
		for(String field : conceptDateFields){
			if(langNonAwareDateFields.contains(field)){
				langNonAwareDateFields.remove(field);
			}
		}
		conceptDateFields.addAll(langNonAwareDateFields);
		
		return conceptDateFields;		
	}

	@Override
	public List<String> getNumericFields() {
		List<String> langAwareNumericFields = new ConceptLanguageAware().getNumericFields();
		List<String> langNonAwareNumericFields = new ConceptLanguageNonAware().getNumericFields();
		
		List<String> conceptNumericFields = new ArrayList<String>(langAwareNumericFields);
		
		for(String field : conceptNumericFields){
			if(langNonAwareNumericFields.contains(field)){
				langNonAwareNumericFields.remove(field);
			}
		}
		conceptNumericFields.addAll(langNonAwareNumericFields);
		
		return conceptNumericFields;
	}
	@Override
	public List<String> getAllFields() {
		List<String> conceptLangNonAwareFields = new ConceptLanguageNonAware().getAllFields();
		List<String> conceptLangAwareFields = new ConceptLanguageAware().getAllFields();
		List<String> fields = new ArrayList<String>(conceptLangNonAwareFields);
		
		for(String field : fields){
			if(conceptLangAwareFields.contains(field)){
				conceptLangAwareFields.remove(field);
			}
		}
		
		fields.addAll(conceptLangAwareFields);
		return fields;
	}
}

