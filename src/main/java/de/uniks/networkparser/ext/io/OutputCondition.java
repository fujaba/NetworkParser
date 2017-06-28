package de.uniks.networkparser.ext.io;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class OutputCondition implements ObjectCondition {

	@Override
	public boolean update(Object value) {
		if(value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent event = (SimpleEvent) value;
		if(NetworkParserLog.ERROR.equals(event.getType())) {
			System.err.println(event.getType() +": "+event.getNewValue());
		}else {
			System.out.println(event.getType() +": "+event.getNewValue());
		}
		return false;
	}

}
