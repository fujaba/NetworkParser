package de.uniks.networkparser.graph;

public abstract class Value extends GraphMember {
	public static final String PROPERTY_INITIALIZATION = "initialization";
	public static final String PROPERTY_TYPE = "type";

	protected DataType type = null;
	protected String value = null;

	public Value with(DataType value) {
		if ((this.type == null && value != null)
				|| (this.type != null && this.type != value)) {
			this.type = value;
		}
		return this;
	}

	public Value with(Clazz value) {
		this.type = new DataType(value);
		return this;
	}
	
	public String getType(boolean shortName) {
		if(type==null) {
			return "?";
		}
		return type.getName(shortName);
	}
	
	public DataType getType() {
		return type;
	}
	
	public Value withValue(String value) {
		this.value = value;
		return this;
	}

	public String getValue() {
		return this.value;
	}
}
