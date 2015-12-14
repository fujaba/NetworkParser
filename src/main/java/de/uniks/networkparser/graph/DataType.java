package de.uniks.networkparser.graph;

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

	protected Clazz value;

	DataType(String value) {
		this.with(value);
	}

	DataType(Clazz value) {
		this.value = value;
	}

	public String getName(boolean shortName) {
		if (this.value == null) {
			return null;
		}
		String result = this.value.getName();
		if (!shortName || result == null || result.lastIndexOf(".") < 0) {
			return result;
		}
		return result.substring(result.lastIndexOf(".") + 1);
	}

	public Clazz getClazz() {
		return value;
	}

	public DataType with(String typ) {
		this.value = new Clazz().with(typ);
		return this;
	}

	public static DataType ref(String typ) {
		return new DataType(typ);
	}

	public static DataType ref(Class<?> typ) {
		return new DataType(typ.getName().replace("$", "."));
	}

	public static DataType ref(String typ, boolean external) {
		return new DataType(new Clazz().with(typ).withExternal(external));
	}

	public static DataType ref(Class<?> typ, boolean external) {
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
		if ("void int long double String boolean Object".indexOf(this.getName(false)) >= 0) {
			return "DataType." + this.getName(false).toUpperCase();
		} else {
			return "DataType.ref(\"" + this.getName(false) + "\")";
		}
	}

	public static DataType ref(Clazz typ) {
		return new DataType(typ);
	}
}