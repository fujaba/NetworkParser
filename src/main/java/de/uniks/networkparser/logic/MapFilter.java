package de.uniks.networkparser.logic;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class MapFilter implements ObjectCondition {

	private SimpleKeyValueList<Object, Entity> map = new SimpleKeyValueList<Object, Entity>();

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent event=(SimpleEvent) value;
		Object item = event.getModelValue();
		if(map.containsKey(item)) {
			return false;
		}
		map.put(item, event.getEntity());
		return true;
	}

	public Entity getValue(Object item) {
		return map.get(item);
	}

}
