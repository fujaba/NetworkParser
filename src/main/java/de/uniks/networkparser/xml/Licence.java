package de.uniks.networkparser.xml;

import de.uniks.networkparser.EntityStringConverter;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;

public class Licence implements SendableEntityCreatorTag, BaseItem {
	public static final String PROPERTY_NAME="name?";
	public static final String PROPERTY_URL="url?";
	public static final String PROPERTY_DISTRIBUTION="distribution?";
	public static final String PROPERTY_COMMENTS="comments?";
	private String tag = "licence";

	private String name;
	private String url;
	private String distribution;
	private String comments;
	
	public String getName() {
		return name;
	}
	
	private Licence withName(String value) {
		this.name = value;
		return this;
	}
	
	public String getURL() {
		return url;
	}
	
	private Licence withURL(String value) {
		this.url = value;
		return this;
	}
	
	public String getDistribution() {
		return distribution;
	}
	
	private Licence withDistribution(String value) {
		this.distribution = value;
		return this;
	}
	
	public String getComments() {
		return comments;
	}
	
	private Licence withComments(String value) {
		this.comments = value;
		return this;
	}
	
	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_NAME, PROPERTY_URL, PROPERTY_DISTRIBUTION, PROPERTY_COMMENTS};
	}
	@Override
	public Object getValue(Object entity, String attribute) {
		if (PROPERTY_NAME.equals(attribute)) {
			return ((Licence) entity).getName();
		}
		if (PROPERTY_URL.equals(attribute)) {
			return ((Licence) entity).getURL();
		}
		if (PROPERTY_DISTRIBUTION.equals(attribute)) {
			return ((Licence) entity).getDistribution();
		}
		if (PROPERTY_COMMENTS.equals(attribute)) {
			return ((Licence) entity).getComments();
		}
		return null;
	}
	
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (PROPERTY_NAME.equals(attribute)) {
			((Licence) entity).withName("" + value);
			return true;
		}
		if (PROPERTY_URL.equals(attribute)) {
			((Licence) entity).withURL("" + value);
			return true;
		}
		if (PROPERTY_DISTRIBUTION.equals(attribute)) {
			((Licence) entity).withDistribution("" + value);
			return true;
		}
		if (PROPERTY_COMMENTS.equals(attribute)) {
			((Licence) entity).withComments("" + value);
			return true;
		}
		return false;
	}


	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Licence();
	}
	@Override
	public String toString(Converter converter) {
		if (converter instanceof EntityStringConverter) {
			EntityStringConverter item = (EntityStringConverter) converter;
			return toString(item.getIndentFactor(), item.getIndent());
		}
		if (converter == null) {
			return null;
		}
		return converter.encode(this);
	}

	public String toString(int indentFactor, int indent) {
		String spacesChild = "";
		if (indentFactor > 0) {
			spacesChild = "\r\n" + StringUtil.repeat(' ', indent + indentFactor);
		}
		CharacterBuffer sb = new CharacterBuffer().with(StringUtil.repeat(' ', indent));
		sb.with("<", tag, ">");
		for(String subTag : getProperties()) {
			String value = (String) getValue(this, subTag);
			if(subTag.endsWith("?")) {
				subTag = subTag.substring(value.length()-1);
			}
			sb.with(spacesChild, "<", subTag, ">", value, "</", subTag, ">");
		}
		sb.with("</", tag, ">");
		return sb.toString();
	}
	
	@Override
	public boolean add(Object... values) {
		if (values == null || values.length % 2 == 1) {
			return false;
		}
		for (int i = 0; i < values.length; i += 2) {
			if (values[i] instanceof String) {
				setValue(this, (String) values[i], values[i + 1], NEW);
			}
		}
		return true;
	}
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new Licence();
	}
	@Override
	public int size() {
		return 1;
	}

	@Override
	public String getTag() {
		return tag;
	}

	public Licence withValue(XMLEntity xmlEntity) {
		if(xmlEntity == null) {
			return null;
		}
		for (String property : getProperties()) {
			boolean isValue = false;
			String propertyValue = property;
			if (propertyValue.endsWith("?")) {
				propertyValue = property.substring(0,property.length() - 1);
				isValue = true;
			}
			Entity child = xmlEntity.getElementBy(XMLEntity.PROPERTY_TAG, propertyValue);
			if (child != null) {
				if (isValue) {
					String newValue = ((XMLEntity) child).getValue();
					setValue(this, property, newValue, NEW);
				}
			}
		}
		return this;
	}
}
