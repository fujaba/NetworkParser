package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class DataTypeSet extends DataType{
	private DataType generic;
	
	DataTypeSet() {
		super(SimpleSet.class.getName());
		this.value.withExternal(true);
	}
	
	public static DataTypeSet ref(Clazz value) {
		DataTypeSet result = new DataTypeSet().withGeneric(DataType.ref(value));
		return result;
	}
	public static DataTypeSet ref(String value) {
		DataTypeSet result = new DataTypeSet().withGeneric(DataType.ref(value));
		return result;
	}
	
	public static DataTypeSet ref(DataType value) {
		DataTypeSet result = new DataTypeSet().withGeneric(value);
		return result;
	}


	private DataTypeSet withGeneric(DataType value) {
		this.generic = value;
		return this;
	}

	public DataType getGeneric() {
		return generic;
	}
	
	@Override
	public String getName(boolean shortName) {
		if (this.value == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + generic.getName(shortName) + ">";
	}
}
