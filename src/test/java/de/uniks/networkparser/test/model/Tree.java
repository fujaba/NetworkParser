package de.uniks.networkparser.test.model;

public abstract class Tree {
	public static final String PROPERTY_NAME = "name";
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
