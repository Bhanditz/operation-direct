package eu.europeana.direct.rest.gen.java.io.swagger.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Language non-aware (non-multilingual) object for Place
 **/
@JsonInclude(JsonInclude.Include.NON_EMPTY) //
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class PlaceLanguageNonAware implements ObjectInformation,Serializable{
  
  private String role = null;
  private Double longitude = null;
  private Double latitude = null;
  private Double altitude = null;
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
   * The longitude of the place.
   **/
  
  @JsonProperty("longitude")
  public Double getLongitude() {
    return longitude;
  }
  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  
  /**
   * The latitude of the place.
   **/
  
  @JsonProperty("latitude")
  public Double getLatitude() {
    return latitude;
  }
  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  
  /**
   * The latitude of the place.
   **/
  
  @JsonProperty("altitude")
  public Double getAltitude() {
    return altitude;
  }
  public void setAltitude(Double altitude) {
    this.altitude = altitude;
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
    PlaceLanguageNonAware placeLanguageNonAware = (PlaceLanguageNonAware) o;
    return Objects.equals(role, placeLanguageNonAware.role) &&
        Objects.equals(longitude, placeLanguageNonAware.longitude) &&
        Objects.equals(latitude, placeLanguageNonAware.latitude) &&
        Objects.equals(altitude, placeLanguageNonAware.altitude) &&
        Objects.equals(customFields, placeLanguageNonAware.customFields);
  }

  @Override
  public int hashCode() {
    return Objects.hash(role, longitude, latitude, altitude, customFields);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class PlaceLanguageNonAware {\n");
    
    sb.append("    role: ").append(toIndentedString(role)).append("\n");
    sb.append("    longitude: ").append(toIndentedString(longitude)).append("\n");
    sb.append("    latitude: ").append(toIndentedString(latitude)).append("\n");
    sb.append("    altitude: ").append(toIndentedString(altitude)).append("\n");
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
		numericFields.add("latitude");
		numericFields.add("longitude");
		numericFields.add("altitude");
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

