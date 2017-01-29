package de.uniks.template.file;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

import de.uniks.template.file.attributes.TemplateAttribute;
import de.uniks.template.file.links.TemplateLink;
import de.uniks.template.file.methods.TemplateMethod;

public abstract class TemplateClass {

	private String name;

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	protected LinkedHashMap<String, String> imports;

	protected LinkedHashSet<TemplateAttribute> attributes;
	
	protected LinkedHashSet<TemplateLink> links;
	
	protected LinkedHashSet<TemplateMethod> methods;
	
	protected boolean isInterface = false;
	
	public abstract TemplateClass enableInterface();
	
	public boolean isInterface() {
		return isInterface;
	}
	
}
