package de.uniks.template.java.file.methods;

import de.uniks.template.file.methods.TemplateMethod;
import de.uniks.template.java.JavaModel;

public class JavaMethod extends TemplateMethod {

private JavaModel model = null;
	
	public JavaModel getModel() {
		return model;
	}

	public void setModel(JavaModel model) {
		this.model = model;
	}

}
