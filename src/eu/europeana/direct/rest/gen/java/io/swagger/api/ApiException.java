package eu.europeana.direct.rest.gen.java.io.swagger.api;

@SuppressWarnings("serial")
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaResteasyServerCodegen", date = "2016-05-17T14:27:21.387Z")
public class ApiException extends Exception{
	@SuppressWarnings("unused")
	private int code;
	public ApiException (int code, String msg) {
		super(msg);
		this.code = code;
	}
}
