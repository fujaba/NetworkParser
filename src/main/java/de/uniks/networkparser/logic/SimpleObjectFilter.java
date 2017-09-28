package de.uniks.networkparser.logic;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class SimpleObjectFilter implements ObjectCondition {
	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent event = (SimpleEvent) value;
		if(event.getDepth()>1) {
			return false;
		}
		if(event.getNewValue() == null) {
			return false;
		}
		String type = event.getNewValue().getClass().getSimpleName();
		return EntityUtil.isPrimitiveType(type);
	}

}
