package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleKeyValueList;

public class DataTypeMap extends DataType {

	private DataType genericKey;
	
	private DataType genericValue;
	
	DataTypeMap() {
		super(SimpleKeyValueList.class.getName());
		this.value.withExternal(true);
	}
	
	public static DataTypeMap ref(Clazz key, Clazz value) {
		DataTypeMap result = new DataTypeMap().withGenericKey(DataType.ref(key)).withGenericValue(DataType.ref(value));
		return result;
	}
	public static DataTypeMap ref(String key, String value) {
		DataTypeMap result = new DataTypeMap().withGenericKey(DataType.ref(key)).withGenericValue(DataType.ref(value));
		return result;
	}
	
	public static DataTypeMap ref(DataType key, DataType value) {
		DataTypeMap result = new DataTypeMap().withGenericKey(key).withGenericValue(value);
		return result;
	}

	private DataTypeMap withGenericKey(DataType key) {
		this.genericKey = key;
		return this;
	}

	private DataTypeMap withGenericValue(DataType value) {
		this.genericValue = value;
		return this;
	}
	
	public DataType getGenericKey() {
		return genericKey;
	}
	
	public DataType getGenericValue() {
		return genericValue;
	}
	
	@Override
	public String getName(boolean shortName) {
		if (this.value == null) {
			return null;
		}
		return this.value.getName(shortName) + "<" + genericKey.getName(shortName) + "," + genericValue.getName(shortName) + ">";
	}

}
