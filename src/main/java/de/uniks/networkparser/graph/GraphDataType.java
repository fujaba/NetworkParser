package de.uniks.networkparser.graph;

public class GraphDataType

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

	private GraphClazz value;

	GraphDataType(String value) {
		this.with(value);
	}

	GraphDataType( GraphClazz value )
	   {
	      this.value = value;
	   }
	
	public String getValue() {
		if (this.value == null) {
			return null;
		}
		return this.value.getId();
	}

	public GraphClazz getClazz() {
		return value;
	}
	public String getValue(boolean shortName) {
		String result = getValue();
		if (!shortName || result == null || result.lastIndexOf(".") < 0) {
			return result;
		}
		return result.substring(result.lastIndexOf(".") + 1);
	}

	public GraphDataType with(String value) {
		this.value = new GraphClazz().withId(value);
		return this;
	}

	public static GraphDataType ref(String value) {
		return new GraphDataType(value);
	}

	public static GraphDataType ref(Class<?> value) {
		return new GraphDataType(value.getName().replace("$", "."));
	}

	public static GraphDataType ref(GraphClazz value) {
		return new GraphDataType(value.getClassName());
	}
	
	   public static GraphDataType ref(String value, boolean external) {
		   return new GraphDataType(new GraphClazz().withId(value).withExternal(external));
	   }

	   public static GraphDataType ref(Class<?> value, boolean external) {
		   GraphClazz clazz = new GraphClazz().withId(value.getName().replace("$", ".")).withExternal(external);
		   return new GraphDataType(clazz);
	   }
	
	public boolean equals(Object obj) {
		if (!(obj instanceof GraphDataType)) {
			return false;
		}
		GraphDataType other = (GraphDataType) obj;
		if (this.getValue() == null) {
			return other.getValue() == null;
		}
		return getValue().equals(other.getValue());
	}

	@Override
	public String toString() {
		if ("void int long double String boolean Object".indexOf(this.getValue()) >= 0) {
			return "DataType." + this.getValue().toUpperCase();
		} else {
			return "DataType.ref(\"" + this.getValue() + "\")";
		}
	}

//FIXME	   public static DataType ref(Enumeration value)
//	   {
//	      return new DataType(value.getFullName());
//	   }
}
