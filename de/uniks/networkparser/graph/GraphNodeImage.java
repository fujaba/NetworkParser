package de.uniks.networkparser.graph;

public class GraphNodeImage implements GraphMember{
	public static final String PROPERTY_VALUE="value";

	private String value = null;

	public String getValue() {
		return value;
	}

	public GraphNodeImage with(String value) {
		this.value = value;
		return this;
	}
}
