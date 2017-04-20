package de.uniks.networkparser.graph;

public enum ClazzType {
	CLAZZ("class"), ENUMERATION("enum"), INTERFACE("interface"), CREATOR("creator"), SET("set"), PATTERNOBJECT("pattern");
	private String value;
	
	ClazzType(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
