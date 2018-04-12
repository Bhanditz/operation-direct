package eu.europeana.direct.backend.model;

public class TimespanLangAware {
	
	private String[]  preferredlabel;
	private String[]  alternativelabel;
			
	public TimespanLangAware() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TimespanLangAware(String[] preferredlabel, String[] alternativelabel) {
		super();
		this.preferredlabel = preferredlabel;
		this.alternativelabel = alternativelabel;
	}
	
	public String[] getPreferredlabel() {
		return preferredlabel;
	}
	public void setPreferredlabel(String[] preferredlabel) {
		this.preferredlabel = preferredlabel;
	}
	public String[] getAlternativelabel() {
		return alternativelabel;
	}
	public void setAlternativelabel(String[] alternativelabel) {
		this.alternativelabel = alternativelabel;
	}		
}
