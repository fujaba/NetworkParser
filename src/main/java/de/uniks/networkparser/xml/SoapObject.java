package de.uniks.networkparser.xml;

import de.uniks.networkparser.EntityStringConverter;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class SoapObject.
 *
 * @author Stefan
 */
public class SoapObject implements BaseItem, SendableEntityCreatorTag {
	
	/** The Constant XMLNS_XSI. */
	public static final String XMLNS_XSI = "http://www.w3.org/2001/XMLSchema-instance";
	
	/** The Constant XMLNS_XSD. */
	public static final String XMLNS_XSD = "http://www.w3.org/2001/XMLSchema";
	
	/** The Constant XMLNS_SOAP. */
	public static final String XMLNS_SOAP = "http://schemas.xmlsoap.org/soap/envelope/";
	private String nameSpace = "s";

	/** The Constant PROPERTY_HEADER. */
	public static final String PROPERTY_HEADER = "Header";
	
	/** The Constant PROPERTY_BODY. */
	public static final String PROPERTY_BODY = "BODY";
	private SimpleKeyValueList<String, String> headers;
	protected XMLEntity children;

	/**
	 * With body.
	 *
	 * @param body the body
	 * @return the soap object
	 */
	public SoapObject withBody(XMLEntity body) {
		this.children = body;
		return this;
	}

	/**
	 * Gets the name space.
	 *
	 * @return the name space
	 */
	public String getNameSpace() {
		return nameSpace;
	}

	/**
	 * With name space.
	 *
	 * @param value the value
	 * @return the soap object
	 */
	public SoapObject withNameSpace(String value) {
		this.nameSpace = value;
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	/**
	 * To string.
	 *
	 * @param indentFactor the indent factor
	 * @return the string
	 */
	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	protected String parseItem(EntityStringConverter converter) {
		if (converter == null) {
			return null;
		}
		CharacterBuffer sb = new CharacterBuffer();
		sb.with("<", nameSpace, ":Envelope xmlns:xsi=\"", XMLNS_XSI, "\" xmlns:xsd=\"", XMLNS_XSD, "\"");
		sb.with(" xmlns:", nameSpace, "=\"", XMLNS_SOAP, "\">");
		converter.add();
		sb.with(converter.getPrefix());
		sb.with("<", nameSpace, ":Body>");

		if (children != null) {
			sb.with(children.toString(converter));
		}
		sb.with("</", nameSpace, ":Body>");
		converter.minus();
		sb.with(converter.getPrefix());
		sb.with("</", nameSpace, ":Envelope>");
		return sb.toString();
	}

	/**
	 * With header.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the soap object
	 */
	public SoapObject withHeader(String key, String value) {
		if (this.headers == null) {
			this.headers = new SimpleKeyValueList<String, String>();
		}
		this.headers.add(key, value);
		return this;
	}

	/**
	 * Gets the header.
	 *
	 * @return the header
	 */
	public SimpleKeyValueList<String, String> getHeader() {
		return headers;
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		if (values == null) {
			return false;
		}
		for (Object item : values) {
			if (item instanceof String) {
				withNameSpace((String) item);
			} else if (item instanceof XMLEntity) {
				withBody((XMLEntity) item);
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
	public SoapObject getNewList(boolean keyValue) {
		return new SoapObject();
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(Object key) {
		if (PROPERTY_HEADER.equals(key)) {
			return headers;
		}
		if (PROPERTY_BODY.equals(key)) {
			return children;
		}
		return null;
	}

	/**
	 * Gets the body.
	 *
	 * @return the body
	 */
	public XMLEntity getBody() {
		return children;
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
	@Override
	public String toString(Converter converter) {
		if (converter == null) {
			return null;
		}
		if (converter instanceof EntityStringConverter) {
			return parseItem((EntityStringConverter) converter);
		}
		return converter.encode(this);
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { "." + nameSpace + ":" + SoapObject.PROPERTY_HEADER,
				"." + nameSpace + ":" + SoapObject.PROPERTY_BODY };
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SoapObject();
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
		if (attribute == null || !(entity instanceof SoapObject)) {
			return false;
		}
		if (attribute.endsWith(":" + SoapObject.PROPERTY_HEADER)) {
			return ((SoapObject) entity).getHeader();
		}
		if (attribute.endsWith(":" + SoapObject.PROPERTY_BODY)) {
			return ((SoapObject) entity).getBody();
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
		if (attribute == null || !(entity instanceof SoapObject)) {
			return false;
		}
		if (XMLTokener.CHILDREN.equals(type)) {
			((SoapObject) entity).add(value);
			return true;
		}
		if (attribute.toLowerCase().endsWith(":" + SoapObject.PROPERTY_BODY.toLowerCase())) {
			((SoapObject) entity).withBody(new XMLEntity().withValue("" + value));
			return true;
		}
		return false;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	@Override
	public String getTag() {
		return nameSpace + ":Envelope";
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		if (this.children == null) {
			return 0;
		}
		return children.sizeChildren();
	}
}
