package de.uniks.networkparser.xml;

import de.uniks.networkparser.EntityStringConverter;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;

/**
 * The Class Licence.
 *
 * @author Stefan
 */
public class Licence implements SendableEntityCreatorTag, BaseItem {
	
	/** The Constant PROPERTY_NAME. */
	public static final String PROPERTY_NAME="name?";
	
	/** The Constant PROPERTY_URL. */
	public static final String PROPERTY_URL="url?";
	
	/** The Constant PROPERTY_DISTRIBUTION. */
	public static final String PROPERTY_DISTRIBUTION="distribution?";
	
	/** The Constant PROPERTY_COMMENTS. */
	public static final String PROPERTY_COMMENTS="comments?";
	private String tag = "licence";

	private String name;
	private String url;
	private String distribution;
	private String comments;
	
	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	private Licence withName(String value) {
		this.name = value;
		return this;
	}
	
	/**
	 * Gets the url.
	 *
	 * @return the url
	 */
	public String getURL() {
		return url;
	}
	
	private Licence withURL(String value) {
		this.url = value;
		return this;
	}
	
	/**
	 * Gets the distribution.
	 *
	 * @return the distribution
	 */
	public String getDistribution() {
		return distribution;
	}
	
	private Licence withDistribution(String value) {
		this.distribution = value;
		return this;
	}
	
	/**
	 * Gets the comments.
	 *
	 * @return the comments
	 */
	public String getComments() {
		return comments;
	}
	
	private Licence withComments(String value) {
		this.comments = value;
		return this;
	}
	
	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] {PROPERTY_NAME, PROPERTY_URL, PROPERTY_DISTRIBUTION, PROPERTY_COMMENTS};
	}
	
	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
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
	
	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
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


	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Licence();
	}
	
	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
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

	/**
	 * To string.
	 *
	 * @param indentFactor the indent factor
	 * @param indent the indent
	 * @return the string
	 */
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
	
	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
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
	
	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new Licence();
	}
	
	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		return 1;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	@Override
	public String getTag() {
		return tag;
	}

	/**
	 * With value.
	 *
	 * @param xmlEntity the xml entity
	 * @return the licence
	 */
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
