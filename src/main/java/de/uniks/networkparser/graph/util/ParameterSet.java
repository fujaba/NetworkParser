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
	
	public DateTypeSet getDataTypes() {
		DateTypeSet collection = new DateTypeSet();
		for(Parameter item : this) {
			collection.add(item.getType());
		}
		return collection;
	}
	
	@Override
	public ParameterSet filter(Condition<Parameter> newValue) {
		ParameterSet newList = new ParameterSet();
		filterItems(newList, newValue);
		return newList;
	}
	
	public ParameterSet hasName(String otherValue) {
		return filter(Parameter.NAME.equals(otherValue));
	}
}
