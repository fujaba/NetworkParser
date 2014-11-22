package de.uniks.networkparser.test.model;

public class XMLTestEntity {
		public static final String PROPERTY_TEXT = "&";
		public static final String PROPERTY_SENDER = "sender";
		private String text;
		private String sender;
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public Object get(String attrName) {
			String attribute;
			int pos = attrName.indexOf(".");
			if (pos > 0) {
				attribute = attrName.substring(0, pos);
			} else {
				attribute = attrName;
			}
			if (attribute.equalsIgnoreCase(PROPERTY_TEXT)) {
				return getText();
			} else if (attribute.equalsIgnoreCase(PROPERTY_SENDER)) {
				return getSender();
			}
			return null;
		}

		public boolean set(String attribute, Object value) {
			if (attribute.equalsIgnoreCase(PROPERTY_TEXT)) {
				setText((String) value);
				return true;
			} else if (attribute.equalsIgnoreCase(PROPERTY_SENDER)) {
				setSender((String) value);
				return true;
			}
			return false;
		}
		public String getSender() {
			return sender;
		}
		public void setSender(String sender) {
			this.sender = sender;
		}
	}
