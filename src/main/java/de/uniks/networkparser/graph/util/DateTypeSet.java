package de.uniks.networkparser.graph.util;

import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.list.SimpleSet;

public class DateTypeSet extends SimpleSet<DataType> {
	@Override
	public DateTypeSet filter(Condition<DataType> newValue) {
		DateTypeSet collection = new DateTypeSet();
		filterItems( collection, newValue);
		return collection;
	}
}
