package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleKeyValueList;

public class GraphLiteral implements GraphMember{
	private String id;
	private SimpleKeyValueList<String, Object> values=new SimpleKeyValueList<String, Object>();
	
	@Override
	public String getId() {
		return id;
	}
	
	public GraphLiteral withId(String id) {
		this.id = id;
		return this;
	}
	public GraphLiteral withKeyValue(String key, Object value) {
		this.values.put(key, value);
		return this;
	}
}
