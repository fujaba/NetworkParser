package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class ParameterSet extends SimpleSet<Parameter>{
	public MethodSet getMethods() {
		MethodSet collection = new MethodSet();
		for(Parameter item : this) {
			collection.add(item.getMethod());
		}
		return collection;
	}
	
	@Override
	public ParameterSet filter(Condition<Parameter> newValue) {
		ParameterSet collection = new ParameterSet();
		filterItems( collection, newValue);
		return collection;
	}

	
	public ParameterSet hasName(String otherValue) {
		return filter(Parameter.NAME.equals(otherValue));
	}
}
