package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.logic.StringCondition;

public class ParameterSet extends SimpleSet<Parameter> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Parameter item : this) {
			collection.add(item.getMethod());
		}
		return collection;
	}

	public SimpleSet<DataType> getDataTypes() {
		SimpleSet<DataType> collection = new SimpleSet<DataType>();
		for (Parameter item : this) {
			collection.add(item.getType());
		}
		return collection;
	}

	public ParameterSet hasName(String otherValue) {
		return filter(StringCondition.createEquals(Parameter.PROPERTY_NAME, otherValue));
	}
	
	@Override
	public SimpleSet<Parameter> getNewList(boolean keyValue) {
		return new ParameterSet();
	}
}
