package de.uniks.networkparser.graph;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.EntityUtil;

public class DataType {
	public static final String PROPERTY_VALUE = "value";

	public static final DataType VOID = new DataType("void");
	public static final DataType INT = new DataType("int");
	public static final DataType LONG = new DataType("long");
	public static final DataType FLOAT = new DataType("float");
	public static final DataType DOUBLE = new DataType("double");
	public static final DataType STRING = new DataType("String");
	public static final DataType BOOLEAN = new DataType("boolean");
	public static final DataType OBJECT = new DataType("Object");
	public static final DataType CHAR = new DataType("char");
	public static final DataType BYTE = new DataType("byte");
	public static final DataType CONSTRUCTOR = new DataType("");

	protected Clazz value;

	DataType(String value) {
		this.value = new Clazz().with(value);
	}

	DataType(Clazz value) {
		this.value = value;
	}

	public String getName(boolean shortName) {
		return getInternName(shortName, true);
	}

	protected String getInternName(boolean shortName, boolean primitivAllow) {
		if (this.value == null) {
			return null;
		}
		String result = this.value.getName(shortName);
		if (primitivAllow) {
			return result;
		}
		if (!shortName || result == null || result.lastIndexOf(".") < 0) {
			return EntityUtil.convertPrimitiveToObjectType(result);
		}
		return EntityUtil.convertPrimitiveToObjectType(result.substring(result.lastIndexOf(".") + 1));
	}

	public Clazz getClazz() {
		return value;
	}

	public static DataType create(Clazz typ) {
		return new DataType(typ);
	}

	public static DataType create(String typ) {
		return new DataType(typ);
	}

	public static DataType create(Class<?> typ) {
		return new DataType(new Clazz(typ));
	}

	public static DataType create(String typ, boolean external) {
		return new DataType(new Clazz().with(typ).withExternal(external));
	}

	public static DataType create(Class<?> typ, boolean external) {
		Clazz clazz = new Clazz(typ).withExternal(external);
		return new DataType(clazz);
	}

	public boolean equals(Object obj) {
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

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		String internName = this.getInternName(false, true);
		if ("void int long double String boolean Object".indexOf(internName) >= 0) {
			return "DataType." + internName.toUpperCase();
		} else {
			return "DataType.create(\"" + internName + "\")";
		}
	}
}
