package eu.europeana.direct.rest.gen.java.io.swagger.api;

@SuppressWarnings("serial")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class NotFoundException extends ApiException {
	@SuppressWarnings("unused")
	private int code;
	public NotFoundException (int code, String msg) {
		super(code, msg);
		this.code = code;
	}
}
