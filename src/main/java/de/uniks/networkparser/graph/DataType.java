package de.uniks.networkparser.graph;

import java.util.Date;

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
import de.uniks.networkparser.StringUtil;

/**
 * The Class DataType.
 *
 * @author Stefan
 */
public class DataType {
	
	/** The Constant VOID. */
	public static final DataType VOID = new DataType("void");
	
	/** The Constant INT. */
	public static final DataType INT = new DataType("int");
	
	/** The Constant LONG. */
	public static final DataType LONG = new DataType("long");
	
	/** The Constant FLOAT. */
	public static final DataType FLOAT = new DataType("float");
	
	/** The Constant DOUBLE. */
	public static final DataType DOUBLE = new DataType("double");
	
	/** The Constant STRING. */
	public static final DataType STRING = new DataType("String");
	
	/** The Constant BOOLEAN. */
	public static final DataType BOOLEAN = new DataType("boolean");
	
	/** The Constant OBJECT. */
	public static final DataType OBJECT = new DataType("Object");
	
	/** The Constant CHAR. */
	public static final DataType CHAR = new DataType("char");
	
	/** The Constant BYTE. */
	public static final DataType BYTE = new DataType("byte");
	
	/** The Constant COLOR. */
	public static final DataType COLOR = new DataType("color");
	
	/** The Constant CONSTRUCTOR. */
	public static final DataType CONSTRUCTOR = new DataType("");
	
	/** The Constant DATE. */
	public static final DataType DATE = DataType.create(Date.class).withExternal(true);
	
	/** The Constant ARRAY. */
	public static final String ARRAY="[]";
	protected Clazz value;
	protected static final String PROPERTY_NAME = "name";
	protected static final String PROPERTY_OBJECTNAME = "objectname";
	protected static final String PROPERTY_CATEGORIE = "cat";
	protected static final String PROPERTY_CLAZZ = "clazz";
	
	/** The Constant PROPERTY_CONTAINER. */
	public static final String PROPERTY_CONTAINER = "container";
	private boolean isArray;
	

	DataType(String value) {
		this.value = new Clazz().with(value);
	}

	DataType(Clazz value) {
		this.value = value;
	}

	/**
	 * Gets the name.
	 *
	 * @param shortName the short name
	 * @return the name
	 */
	public String getName(boolean shortName) {
		return getInternName(shortName, true);
	}

	protected String getInternName(boolean shortName, boolean primitivAllow) {
		if (this.value == null) {
			return null;
		}
		String result = this.value.getName(shortName);
		if (primitivAllow) {
			if(isArray) {
				return result+ARRAY;
			}
			return result;
		}
		if (!shortName || result == null || result.lastIndexOf(".") < 0) {
			result= StringUtil.convertPrimitiveToObjectType(result);
			if(isArray) {
				return result+ARRAY;
			}
			return result;
		}
		result= StringUtil.convertPrimitiveToObjectType(result.substring(result.lastIndexOf(".") + 1));
		if(isArray) {
			return result+ARRAY;
		}
		return result;
	}

	/**
	 * Gets the clazz.
	 *
	 * @return the clazz
	 */
	public Clazz getClazz() {
		return value;
	}

	/**
	 * Creates the.
	 *
	 * @param typ the typ
	 * @return the data type
	 */
	public static DataType create(Object typ) {
		if (typ instanceof DataType) {
			return (DataType) typ;
		}
		if (typ instanceof Clazz) {
			return new DataType((Clazz) typ);
		}
		if (typ instanceof String) {
			return new DataType((String) typ);
		}
		if (typ instanceof Class<?>) {
			return new DataType(new Clazz((Class<?>) typ));
		}
		return null;
	}

	/**
	 * With external.
	 *
	 * @param external the external
	 * @return the data type
	 */
	public DataType withExternal(boolean external) {
		if (this.value != null) {
			this.value.withExternal(external);
		}
		return this;
	}
	
	/**
	 * With array.
	 *
	 * @param value the value
	 * @return the data type
	 */
	public DataType withArray(boolean value) {
		if(value) {
			DataType dataType = new DataType(this.getClazz());
			dataType.isArray = value;
			return dataType;
		}
		return this;
	}
	
	/**
	 * Equals.
	 *
	 * @param obj the obj
	 * @return true, if successful
	 */
	public boolean equals(Object obj) {
		if (obj instanceof String) {
			return ((String) obj).equalsIgnoreCase(this.getName(false));
		}
		if (!(obj instanceof DataType)) {
			return false;
		}
		if (obj.hashCode() == this.hashCode()) {
			return true;
		}
		DataType other = (DataType) obj;
		if (this.getName(false) == null) {
			return other.getName(false) == null;
		}
		return getName(false).equals(other.getName(false));
	}

	/**
	 * Hash code.
	 *
	 * @return the int
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/**
	 * To string.
	 *
	 * @param ref the ref
	 * @return the string
	 */
	public String toString(boolean ref) {
		String internName = this.getInternName(false, true);
		if(StringUtil.isPrimitiveType(internName)) {
			if (ref) {
				return DataType.class.getSimpleName() + "." + internName.toUpperCase();
			}
			return DataType.class.getName() + "." + internName.toUpperCase();
		} else {
			if (ref) {
				return DataType.class.getSimpleName() + ".create(\"" + internName + "\")";
			}
			return DataType.class.getName() + ".create(\"" + internName + "\")";
		}
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		String internName = this.getInternName(false, true);
		if (internName == null) {
			return "DataType.VOID";
		}
		if ("void char byte int long float double String boolean Object".indexOf(internName) >= 0) {
			return "DataType." + internName.toUpperCase();
		} else {
			return "DataType.create(\"" + internName + "\")";
		}
	}

	/**
	 * Gets the value.
	 *
	 * @param value the value
	 * @return the value
	 */
	public Object getValue(String value) {
		if (PROPERTY_CLAZZ.equals(value)) {
			return getClazz();
		}
		if (Clazz.PROPERTY_EXTERNAL.equals(value)) {
			return getClazz().isExternal();
		}
		if (PROPERTY_NAME.equals(value) || PROPERTY_CONTAINER.equals(value)) {
			return getClazz().getName(true);
		}
		if (PROPERTY_CATEGORIE.equals(value)) {
			if (StringUtil.isPrimitiveType(getInternName(false, true))) {
				return "PRIMITIVE";
			}
			return "OBJECT";
		}
		if (PROPERTY_OBJECTNAME.equals(value)) {
			String name = getClazz().getName(true);
			return StringUtil.getObjectType(name);
		}
		return null;
	}
}
