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
 * Represents the Operation Direct entity Place.
 **/
@JsonInclude(Include.NON_EMPTY)
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class Place implements ObjectInformation,Serializable{
  
  private BigDecimal id = null;
  private List<PlaceLanguageAware> languageAwareFields = new ArrayList<PlaceLanguageAware>();
  private PlaceLanguageNonAware languageNonAwareFields = null;

  
  /**
   * Represents the unique Operation Direct ID of the Place.
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
  public List<PlaceLanguageAware> getLanguageAwareFields() {
    return languageAwareFields;
  }
  public void setLanguageAwareFields(List<PlaceLanguageAware> languageAwareFields) {
    this.languageAwareFields = languageAwareFields;
  }

  
  /**
   * Language non-aware (non-multilingual) fields of the object
   **/
  @JsonProperty("languageNonAwareFields")
  public PlaceLanguageNonAware getLanguageNonAwareFields() {
    return languageNonAwareFields;
  }
  public void setLanguageNonAwareFields(PlaceLanguageNonAware languageNonAwareFields) {
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
    Place place = (Place) o;
    return Objects.equals(id, place.id) &&
        Objects.equals(languageAwareFields, place.languageAwareFields) &&
        Objects.equals(languageNonAwareFields, place.languageNonAwareFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, languageAwareFields, languageNonAwareFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Place {\n");
    
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
		List<String> langNonAwareDateFields = new PlaceLanguageNonAware().getDateFields();
		List<String> langAwareDateFields = new PlaceLanguageAware().getDateFields();
		
		List<String> placeDateFields = new ArrayList<String>(langAwareDateFields);
		
		for(String field : placeDateFields){
			if(langNonAwareDateFields.contains(field)){
				langNonAwareDateFields.remove(field);
			}
		}
		placeDateFields.addAll(langNonAwareDateFields);
		
		return placeDateFields;	
	}
	@Override
	public List<String> getNumericFields() {
		List<String> langNonAwareNumericFields = new PlaceLanguageNonAware().getNumericFields();
		List<String> langAwareNumericFields = new PlaceLanguageAware().getNumericFields();
		
		List<String> placeNumericFields = new ArrayList<String>(langAwareNumericFields);
		
		for(String field : placeNumericFields){
			if(langNonAwareNumericFields.contains(field)){
				langNonAwareNumericFields.remove(field);
			}
		}
		placeNumericFields.addAll(langNonAwareNumericFields);
		
		return placeNumericFields;	
	}
	@Override
	public List<String> getAllFields() {
		List<String> placeLangNonAwareFields = new PlaceLanguageNonAware().getAllFields();
		List<String> placeLangAwareFields = new PlaceLanguageAware().getAllFields();
		List<String> fields = new ArrayList<String>(placeLangNonAwareFields);
		
		for(String field : fields){
			if(placeLangAwareFields.contains(field)){
				placeLangAwareFields.remove(field);
			}
		}
		
		fields.addAll(placeLangAwareFields);
		return fields;		
	}
}

