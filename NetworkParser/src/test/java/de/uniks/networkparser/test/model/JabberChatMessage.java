package de.uniks.networkparser.test.model;

public class JabberChatMessage {
	public static final String PROPERTY_TO = "to";
	public static final String PROPERTY_TYPE = "type";
	public static final String PROPERTY_ID = "id";
	public static final String PROPERTY_FROM = "from";
	public static final String PROPERTY_BODY = "&body";
	private String to;
	private String type;
	private String id;
	private String from;
	private String body;

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Object get(String attrName) {
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_TO)) {
			return getTo();
		} else if (attribute.equalsIgnoreCase(PROPERTY_TYPE)) {
			return getType();
		} else if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			return getId();
		} else if (attribute.equalsIgnoreCase(PROPERTY_FROM)) {
			return getFrom();
		} else if (attribute.equalsIgnoreCase(PROPERTY_BODY)) {
			return getBody();
		}
		return null;
	}

	public boolean set(String attribute, Object value) {
		if (attribute.equalsIgnoreCase(PROPERTY_TO)) {
			setTo((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_TYPE)) {
			setType((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			setId((String) value);
		} else if (attribute.equalsIgnoreCase(PROPERTY_FROM)) {
			setFrom((String) value);
			return true;
		} else if (attribute.equalsIgnoreCase(PROPERTY_BODY)) {
			setBody((String) value);
			return true;
		}
		return false;
	}
}
