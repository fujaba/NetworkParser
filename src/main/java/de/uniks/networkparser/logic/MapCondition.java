package de.uniks.networkparser.logic;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * The Class MapCondition.
 *
 * @author Stefan
 */
public class MapCondition implements ObjectCondition {
	private IdMap map;
	private SendableEntityCreator creator;
	private String property;
	private ObjectCondition condition;

	/**
	 * With map.
	 *
	 * @param map the map
	 * @return the map condition
	 */
	public MapCondition withMap(IdMap map) {
		this.map = map;
		return this;
	}

	/**
	 * With property.
	 *
	 * @param property the property
	 * @return the map condition
	 */
	public MapCondition withProperty(String property) {
		this.property = property;
		return this;
	}

	/**
	 * With creator.
	 *
	 * @param creator the creator
	 * @return the map condition
	 */
	public MapCondition withCreator(SendableEntityCreator creator) {
		this.creator = creator;
		return this;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean update(Object value) {
		if (!(value instanceof SimpleEvent)) {
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

	/**
	 * Gets the value.
	 *
	 * @param evt the evt
	 * @return the value
	 */
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
