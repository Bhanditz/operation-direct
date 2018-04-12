package eu.europeana.direct.legacy.model.search;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Breadcrumb {

	private String display;
	private String param;
	private String value;
	private String href;
	private boolean last;		
	
	public Breadcrumb() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Breadcrumb(String display, String param, String value, String href, boolean last) {
		super();
		this.display = display;
		this.param = param;
		this.value = value;
		this.href = href;
		this.last = last;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
	public String getParam() {
		return param;
	}
	public void setParam(String param) {
		this.param = param;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getHref() {
		return href;
	}
	public void setHref(String href) {
		this.href = href;
	}
	public boolean isLast() {
		return last;
	}
	public void setLast(boolean last) {
		this.last = last;
	}
	
}
