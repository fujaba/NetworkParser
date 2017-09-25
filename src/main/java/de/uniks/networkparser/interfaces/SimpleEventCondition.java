package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.SimpleEvent;

public abstract class SimpleEventCondition implements ObjectCondition{

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		return update((SimpleEvent) value);
	}
	
	public abstract boolean update(SimpleEvent event);
}
