package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleKeyValueList;

public class GraphMapDataType extends GraphDataType {

	private GraphType genericKey;
	
	private GraphType genericValue;
	
	GraphMapDataType() {
		super(SimpleKeyValueList.class.getName());
		this.value.withExternal(true);
	}
	
	public static GraphMapDataType ref(GraphClazz key, GraphClazz value) {
		GraphMapDataType result = new GraphMapDataType().withGenericKey(GraphDataType.ref(key)).withGenericValue(GraphDataType.ref(value));
		return result;
	}
	public static GraphMapDataType ref(String key, String value) {
		GraphMapDataType result = new GraphMapDataType().withGenericKey(GraphDataType.ref(key)).withGenericValue(GraphDataType.ref(value));
		return result;
	}
	
	public static GraphMapDataType ref(GraphDataType key, GraphDataType value) {
		GraphMapDataType result = new GraphMapDataType().withGenericKey(key).withGenericValue(value);
		return result;
	}

	private GraphMapDataType withGenericKey(GraphType key) {
		this.genericKey = key;
		return this;
	}

	private GraphMapDataType withGenericValue(GraphType value) {
		this.genericValue = value;
		return this;
	}
	
	public GraphType getGenericKey() {
		return genericKey;
	}
	
	public GraphType getGenericValue() {
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
