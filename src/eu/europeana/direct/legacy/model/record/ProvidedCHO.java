package eu.europeana.direct.legacy.model.record;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ProvidedCHO {

	private String about;
	private String[] owlSameAs;
	
	public ProvidedCHO(){}
	
	/**
	 * Defines the resource being described
	 */
	public String getAbout() {
		return about;
	}
	public void setAbout(String about) {
		this.about = about;
	}
	
	/**	 
	 * 	owl:sameAs links an individual to an individual. Such an owl:sameAs statement indicates that two URI references actually refer to the same thing: the individuals have the same "identity".
	 */
	public String[] getOwlSameAs() {
		return owlSameAs;
	}
	public void setOwlSameAs(String[] owlSameAs) {
		this.owlSameAs = owlSameAs;
	}	
}
