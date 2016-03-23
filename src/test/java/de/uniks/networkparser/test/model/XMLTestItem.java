package de.uniks.networkparser.test.model;

public class XMLTestItem {
	public static final String PROPERTY_ID = "id";
	public static final String PROPERTY_BODY = ".body.txt";
	public static final String PROPERTY_VALUE = ".body.";
	public static final String PROPERTY_USER = ".user.";

	private int id;
	private String body;
	private String value;
	private String user;


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
}

