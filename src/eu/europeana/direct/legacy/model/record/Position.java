package eu.europeana.direct.legacy.model.record;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Position {

	private String latLong;

	public Position(){}
	
	public Position(String latLong) {
		super();
		this.latLong = latLong;
	}

	/**
	 * A comma-separated representation of a latitude, longitude coordinate.
	 */
	public String getLatLong() {
		return latLong;
	}

	public void setLatLong(String latLong) {
		this.latLong = latLong;
	}
}
