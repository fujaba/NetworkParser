package de.uniks.networkparser.logic;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class MapCondition implements ObjectCondition {
	private IdMap map;
	private SendableEntityCreator creator;
	private String property;
	private ObjectCondition condition;

	public MapCondition withMap(IdMap map) {
		this.map = map;
		return this;
	}

	public MapCondition withProperty(String property) {
		this.property = property;
		return this;
	}

	public MapCondition withCreator(SendableEntityCreator creator) {
		this.creator = creator;
		return this;
	}

	@Override
	public boolean update(Object value) {
		if (value instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent evt = (SimpleEvent) value;
		Object source = evt.getSource();
		if (creator != null) {

		} else if (map != null) {
			creator = map.getCreatorClass(source);
		}
		if (creator == null) {
			return false;
		}
		Object itemValue = creator.getValue(source, property);

		SimpleEvent event = new SimpleEvent(source, property, null, itemValue);
		if (condition != null) {
			return condition.update(event);
		}
		return false;
	}

	public Object getValue(SimpleEvent evt) {
		if (evt == null) {
			return null;
		}
		Object source = evt.getSource();
		if (creator != null) {

		} else if (map != null) {
			creator = map.getCreatorClass(source);
		}
		if (creator == null) {
			return false;
		}
		Object itemValue = creator.getValue(source, property);
		return itemValue;
	}

}
