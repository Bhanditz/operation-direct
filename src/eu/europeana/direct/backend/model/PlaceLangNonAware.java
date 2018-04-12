package eu.europeana.direct.backend.model;

public class PlaceLangNonAware {

	private Double latitude;
	private Double longitude;
	private Double altitude;
			
	public PlaceLangNonAware() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public PlaceLangNonAware(Double latitude, Double longitude, Double altitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
	}
	
	public Double getLatitude() {
		return latitude;
	}
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}
	public Double getLongitude() {
		return longitude;
	}
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}
	public Double getAltitude() {
		return altitude;
	}
	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}		
}
