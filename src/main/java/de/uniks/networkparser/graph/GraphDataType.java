package de.uniks.networkparser.graph;

public class GraphDataType implements GraphType

{
	public static final String PROPERTY_VALUE = "value";

	public static final GraphDataType VOID = new GraphDataType("void");
	public static final GraphDataType INT = new GraphDataType("int");
	public static final GraphDataType LONG = new GraphDataType("long");
	public static final GraphDataType DOUBLE = new GraphDataType("double");
	public static final GraphDataType STRING = new GraphDataType("String");
	public static final GraphDataType BOOLEAN = new GraphDataType("boolean");
	public static final GraphDataType OBJECT = new GraphDataType("Object");
	public static final GraphDataType CHAR = new GraphDataType("char");
	public static final GraphDataType BYTE = new GraphDataType("byte");

	protected GraphClazz value;

	GraphDataType(String value) {
		this.with(value);
	}

	GraphDataType(GraphClazz value) {
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

	public GraphClazz getClazz() {
		return value;
	}

	public GraphDataType with(String value) {
		this.value = new GraphClazz().with(value);
		return this;
	}

	public static GraphDataType ref(String value) {
		return new GraphDataType(value);
	}

	public static GraphDataType ref(Class<?> value) {
		return new GraphDataType(value.getName().replace("$", "."));
	}

	public static GraphDataType ref(String value, boolean external) {
		return new GraphDataType(new GraphClazz().with(value).withExternal(external));
	}

	public static GraphDataType ref(Class<?> value, boolean external) {
		GraphClazz clazz = new GraphClazz().with(value.getName().replace("$", ".")).withExternal(external);
		return new GraphDataType(clazz);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof GraphDataType)) {
			return false;
		}
		GraphDataType other = (GraphDataType) obj;
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

	public static GraphDataType ref(GraphClazz value) {
		return new GraphDataType(value);
	}
}