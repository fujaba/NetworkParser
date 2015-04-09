package de.uniks.networkparser.graph;

public class GraphLabel {
	private String value;
	private String style;
	
	public static GraphLabel create(String value) {
		return new GraphLabel().withValue(value);
	}
	
	public static GraphLabel create(String value, String style) {
		return new GraphLabel().withValue(value).withStyle(style);
	}

	public String getValue() {
		return value;
	}

	public GraphLabel withValue(String value) {
		this.value = value;
		return this;
	}

	public String getStyle() {
		return style;
	}

	public GraphLabel withStyle(String style) {
		this.style = style;
		return this;
	}
}
