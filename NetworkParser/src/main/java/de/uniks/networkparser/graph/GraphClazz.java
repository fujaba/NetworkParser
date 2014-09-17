package de.uniks.networkparser.graph;

public class GraphClazz extends GraphNode{
	private String className;
	
	public String getClassName(boolean shortName) {
		if (!shortName || className == null || className.lastIndexOf(".") < 0) {
			return className;
		}
		return className.substring(className.lastIndexOf(".") + 1);
	}

	public GraphClazz withClassName(String className) {
		this.className = className;
		return this;
	}

	public String getClassName() {
		return className;
	}
	
	public String getTyp(String typ, boolean shortName) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			return getId();
		} else if (typ.equals(GraphIdMap.CLASS)) {
			return getClassName(shortName);
		}
		return "";
	}

	@Override
	public GraphClazz withTyp(String typ, String value) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			withId(value);
		} else if (typ.equals(GraphIdMap.CLASS)) {
			withClassName(value);
		}
		return this;
	}
	
	@Override
	public String toString() {
		if (getId() == null) {
			return className;
		}
		return getId();
	}
	
	public GraphClazz withId(String id) {
		super.withId(id);
		return this;
	}
}
