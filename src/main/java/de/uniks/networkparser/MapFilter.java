package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class MapFilter.
 *
 * @author Stefan
 */
public class MapFilter implements ObjectCondition {
	private SimpleKeyValueList<Object, Entity> map = new SimpleKeyValueList<Object, Entity>();

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
		SimpleEvent event = (SimpleEvent) value;
		Object item = event.getModelValue();
		if (map.containsKey(item)) {
			return false;
		}
		map.put(item, event.getEntity());
		return true;
	}

	/**
	 * Gets the value.
	 *
	 * @param item the item
	 * @return the value
	 */
	public Entity getValue(Object item) {
		return map.get(item);
	}

}
