package de.uniks.networkparser.graph;

import de.uniks.networkparser.list.SimpleSet;

public class ParameterSet extends SimpleSet<Parameter> {
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for (Parameter item : this) {
			collection.add(item.getMethod());
		}
		return collection;
	}

	public DateTypeSet getDataTypes() {
		DateTypeSet collection = new DateTypeSet();
		for (Parameter item : this) {
			collection.add(item.getType());
		}
		return collection;
	}

	public ParameterSet hasName(String otherValue) {
		return filter(Parameter.NAME.equals(otherValue));
	}
}
