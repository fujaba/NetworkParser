package de.uniks.networkparser.graph;

import de.uniks.networkparser.EntityUtil;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/

public class DataType {
	public static final String PROPERTY_VALUE = "value";

	public static final DataType VOID = new DataType("void");
	public static final DataType INT = new DataType("int");
	public static final DataType LONG = new DataType("long");
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

	String getInternName(boolean shortName, boolean primitivAllow) {
		if (this.value == null) {
			return null;
		}
		String result = this.value.getName(shortName);
		if(primitivAllow) {
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
		return new DataType(typ.getName().replace("$", "."));
	}

	public static DataType create(String typ, boolean external) {
		return new DataType(new Clazz().with(typ).withExternal(external));
	}

	public static DataType create(Class<?> typ, boolean external) {
		Clazz clazz = new Clazz().with(typ.getName().replace("$", ".")).withExternal(external);
		return new DataType(clazz);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof DataType)) {
			return false;
		}
		DataType other = (DataType) obj;
		if (this.getName(false) == null) {
			return other.getName(false) == null;
		}
		return getName(false).equals(other.getName(false));
	}

	@Override
	public String toString() {
		String internName = this.getInternName(false, true);
		if ("void int long double String boolean Object".indexOf(internName) >= 0) {
			return "DataType." + internName.toUpperCase();
		} else {
			return "DataType.ref(\"" + internName + "\")";
		}
	}
}