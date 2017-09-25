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
	
	public static ClazzType create(String value) {
		if(value == null) {
			return CLAZZ;
		}
		String trim = value.trim().toLowerCase();
		if(trim.equals(ENUMERATION.getValue())) {
			return ENUMERATION;
		}
		if(trim.equals(INTERFACE.getValue())) {
			return INTERFACE;
		}
		if(trim.equals(CREATOR.getValue())) {
			return CREATOR;
		}
		if(trim.equals(SET.getValue())) {
			return SET;
		}
		if(trim.equals(PATTERNOBJECT.getValue())) {
			return PATTERNOBJECT;
		}
		return CLAZZ;
		
	}
}
