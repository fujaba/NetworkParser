package de.uniks.networkparser.bytes;

import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;

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
/**
 * The Class ByteMessage.
 */

public class ByteMessage implements SendableEntityCreatorTag {
	/** The properties. */
	private final String[] properties = new String[] { PROPERTY_VALUE };

	/** The Constant PROPERTY_VALUE. */
	public static final String PROPERTY_VALUE = "value";

	/** The value. */
	private byte[] value = new byte[] {};

	/**
	 * Generic Getter for Attributes
	 * 
	 * @param entity   ByteMessage Entity
	 * @param attrName Name of Attribute
	 * @return Value of Attribute
	 */
	public Object getValue(Object entity, String attrName) {
		if (entity instanceof ByteMessage == false) {
			return null;
		}
		String attribute;
		int pos = attrName.indexOf(".");
		if (pos > 0) {
			attribute = attrName.substring(0, pos);
		} else {
			attribute = attrName;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			return ((ByteMessage) entity).value;
		}
		return null;
	}

	/**
	 * Generic Setter for Attributes
	 * 
	 * @param attribute the Name of Attribute
	 * @param value     the Value of Attribute
	 * @return success
	 */

	/* Setter for ByteMessage */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity instanceof ByteMessage == false) {
			return false;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_VALUE)) {
			((ByteMessage) entity).withValue((byte[]) value);
			return true;
		}
		return false;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public byte[] getValue() {
		return this.value;
	}

	/**
	 * Gets the value As String
	 *
	 * @return the value
	 */
	public String getValueString() {
		return new String(this.value);
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 * @return Itself
	 */
	public ByteMessage withValue(byte[] value) {
		this.value = value;
		return this;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 * @return Itself
	 */
	public ByteMessage withValue(String value) {
		if (value != null) {
			this.value = value.getBytes();
		}
		return this;
	}

	/** return the Properties */
	@Override
	public String[] getProperties() {
		return properties;
	}

	/** Create new Instance of ByteMessage */
	@Override
	public Object getSendableInstance(boolean reference) {
		return new ByteMessage();
	}

	/** Get the EventType of BasicMessage (0x42) UTF-8 */
	@Override
	public String getTag() {
		return new String(new byte[] { 0x42 });
	}

}
