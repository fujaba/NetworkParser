package de.uniks.template.file.methods;

import de.uniks.template.Template;

public abstract class TemplateMethod {

	protected String signature;
	
	public void setSignature(String signature) {
		this.signature = signature;
	}
	
	public String getSignature() {
		return this.signature;
	}
	
	protected String body;
	
	public void setBody(String body) {
		this.body = body;
	}
	
	public String getBody() {
		return this.body;
	}
	
}
