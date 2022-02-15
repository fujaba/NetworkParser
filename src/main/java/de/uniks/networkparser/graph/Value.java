package de.uniks.networkparser.graph;

import de.uniks.networkparser.StringUtil;

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
 * The Class Value.
 *
 * @author Stefan
 */
public abstract class Value extends GraphMember {
	
	/** The Constant PROPERTY_INITIALIZATION. */
	public static final String PROPERTY_INITIALIZATION = "initialization";
	
	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "type";
	
	/** The Constant PROPERTY_TYPECAT. */
	public static final String PROPERTY_TYPECAT = "typecat";
	
	/** The Constant PROPERTY_TYPECLAZZ. */
	public static final String PROPERTY_TYPECLAZZ = "typeClazz";
	
	/** The Constant PROPERTY_NAMEGETTER. */
	public static final String PROPERTY_NAMEGETTER = "namegetter";
	
	/** The Constant PROPERTY_VALUE. */
	public static final String PROPERTY_VALUE = "value";

	protected DataType type = null;
	protected String value = null;

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the value
	 */
	public Value with(DataType value) {
		if ((this.type == null && value != null) || (this.type != null && this.type != value)) {
			this.type = value;
		}
		return this;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public DataType getType() {
		return type;
	}

	/**
	 * Gets the value.
	 *
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(String attribute) {
		int pos = attribute.indexOf('.');
		String attrName;
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}
		if (PROPERTY_TYPE.equalsIgnoreCase(attrName)) {
			DataType type = this.getType();
			if (type != null && pos > 0) {
				return type.getValue(attribute.substring(pos + 1));
			}
			return type;
		}
		if (PROPERTY_TYPECLAZZ.equalsIgnoreCase(attrName)) {
			DataType dataType = this.getType();
			if (dataType != null) {
				if (pos > 0) {
					return dataType.getClazz().getValue(attribute.substring(pos + 1));
				}
				return dataType.getClazz();
			}
		}
		if (PROPERTY_TYPECAT.equalsIgnoreCase(attrName)) {
			DataType dataType = this.getType();
			if (dataType != null) {
				return dataType.getValue("cat");
			}
		}
		if (PROPERTY_NAMEGETTER.equalsIgnoreCase(attribute)) {
			if ("boolean".equals(this.type.getName(true))) {
				return "is" + StringUtil.upFirstChar(this.name);
			}
			return "get" + StringUtil.upFirstChar(this.name);
		}
		if (PROPERTY_VALUE.equalsIgnoreCase(attribute)) {
			return this.value;

		}
		return super.getValue(attribute);
	}

	/**
	 * With value.
	 *
	 * @param value the value
	 * @return the value
	 */
	public Value withValue(String value) {
		this.value = value;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
}
