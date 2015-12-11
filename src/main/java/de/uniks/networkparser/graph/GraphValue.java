package de.uniks.networkparser.graph;

public abstract class GraphValue extends GraphMember {
	public static final String PROPERTY_INITIALIZATION = "initialization";
	public static final String PROPERTY_TYPE = "type";

	protected GraphDataType type = null;
	protected String value = null;

	public GraphValue with(GraphDataType value) {
		if ((this.type == null && value != null)
				|| (this.type != null && this.type != value)) {
			this.type = value;
		}
		return this;
	}

	public GraphValue with(GraphClazz value) {
		this.type = new GraphDataType(value);
		return this;
	}
	
	public String getType(boolean shortName) {
		if(type==null) {
			return "?";
		}
		return type.getName(shortName);
	}

	public GraphValue withValue(String value) {
		this.value = value;
		return this;
	}

	public String getValue() {
		return this.value;
	}
}
