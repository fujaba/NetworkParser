package de.uniks.template.file.links;

import de.uniks.template.Template;
import de.uniks.template.file.TemplateClass;

public abstract class TemplateLink {

	public static final String CARDINALITY_ONE = "one";
	
	public static final String CARDINALITY_MANY = "many";
	
	public static final String EDGE = "edge";

	public static final String SUPERCLASS = "superclass";
	
	protected TemplateClass source = null;
	
	public TemplateClass getSource() {
		return source;
	}
	
	protected String sourceName = "";
	
	protected String sourceType = "";
	
	protected TemplateClass other = null;
	
	public TemplateClass getOther() {
		return other;
	}
	
	protected String otherName = "";
	
	protected String otherType = "";
	
	public boolean isSuperLink() {
		return sourceType.equals(TemplateLink.EDGE) && otherType.equals(TemplateLink.SUPERCLASS);
	}
	
}
