package de.uniks.networkparser.graph;

public enum GraphEdgeTypes {
	ASSOC("assoc"), EDGE("edge"), GENERALISATION("generalisation");
	
	private GraphEdgeTypes(String value) {
		this.setValue(value);
	}
	
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	private String value;
}
