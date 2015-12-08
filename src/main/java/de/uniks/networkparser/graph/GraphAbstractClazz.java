package de.uniks.networkparser.graph;

public abstract class GraphAbstractClazz extends GraphNode implements GraphType {
	private String className;
	private boolean external;
	private GraphAnnotation annotation;

	public String getClassName(boolean shortName) {
		if (!shortName || className == null || className.lastIndexOf(".") < 0) {
			return className;
		}
		return className.substring(className.lastIndexOf(".") + 1);
	}

	public GraphAbstractClazz withClassName(String className) {
		this.className = className;
		return this;
	}

	public String getName(boolean shortName) {
		if (this.className == null) {
			return null;
		}
		if (!shortName || className.lastIndexOf(".") < 0) {
			return className;
		}
		return className.substring(className.lastIndexOf(".") + 1);
	}

	public String getTyp(String typ, boolean shortName) {
		if (typ.equals(GraphIdMap.OBJECT)) {
			return getId();
		} else if (typ.equals(GraphIdMap.CLASS)) {
			return getClassName(shortName);
		}
		return "";
	}

	public GraphAbstractClazz withTyp(String typ, String value) {
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
	
	public boolean isExternal() {
		return this.external;
	}

	public boolean setExternal(boolean value) {
		if (this.external != value) {
			this.external = value;
			return true;
		}
		return false;
	}

	public GraphAbstractClazz withExternal(boolean value) {
		setExternal(value);
		return this;
	}
	
	
	public GraphAnnotation getAnnotations() {
		return this.annotation;
	}

	public GraphAbstractClazz with(GraphAnnotation value) {
		this.annotation = value;
		return this;
	}
}
