package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleKeyValueList;

public class GraphMapDataType extends DataType {

	private DataType genericKey;
	
	private DataType genericValue;
	
	GraphMapDataType() {
		super(SimpleKeyValueList.class.getName());
		this.value.withExternal(true);
	}
	
	public static GraphMapDataType ref(Clazz key, Clazz value) {
		GraphMapDataType result = new GraphMapDataType().withGenericKey(DataType.ref(key)).withGenericValue(DataType.ref(value));
		return result;
	}
	public static GraphMapDataType ref(String key, String value) {
		GraphMapDataType result = new GraphMapDataType().withGenericKey(DataType.ref(key)).withGenericValue(DataType.ref(value));
		return result;
	}
	
	public static GraphMapDataType ref(DataType key, DataType value) {
		GraphMapDataType result = new GraphMapDataType().withGenericKey(key).withGenericValue(value);
		return result;
	}

	private GraphMapDataType withGenericKey(DataType key) {
		this.genericKey = key;
		return this;
	}

	private GraphMapDataType withGenericValue(DataType value) {
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
