package de.uniks.networkparser.listold;

import java.util.Map;

import de.uniks.networkparser.listold.not.KeyValueMap;

public class SimpleSmallKeyValue<K, V> extends SimpleSmallList<K> implements KeyValueMap<K, V> {
	Object[] elementValue; // non-private to simplify nested class access
	/**
	 * Put a key/value pair in the Entity. If the value is null, then the key
	 * will be removed from the Entity if it is present.
	 *
	 * @param key
	 *            A key string.
	 * @param value
	 *            An object which is the value. It should be of one of these
	 *            types: Boolean, Double, Integer, EntityList, Entity, Long or
	 *            String object.
	 * @return this.
	 */
	@Override
	public V put(K key, V value) {
		if(!isAllowEmptyValue() && value == null) {
			return null;
		}
		if (!isAllowDuplicate()) {
			int pos = getPositionKey(key);
			if (pos >= 0) {
				return this.setValue(pos, value);
			}
		}
		addEntity(key, value);

		return value;
	}
	
	@SuppressWarnings("unchecked")
	public V setValue(int index, V element) {
		Object oldValue = elementValue[index];
		elementValue[index] = element;
        return (V) oldValue;
	}
	
	public boolean addEntity(K key, V value) {
		if (key == null)
			return false;
		if (cpr != null) {
			for (int i = 0; i < size(); i++) {
				int result = comparator().compare(get(i), key);
				if (result >= 0) {
					if (!isAllowDuplicate() && get(i) == key) {
						return false;
					}
					addKey(i, key);
					addValue(i, value);
					K beforeElement = null;
					if (i > 0) {
						beforeElement = this.get(i - 1);
					}
					fireProperty(null, key, beforeElement, value);
					return true;
				}
			}
		}

		if (!isAllowDuplicate()) {
			if (this.contains(key)) {
				return false;
			}
		}

		if (addKey(-1, key) < 0) {
			return false;
		}
		addValue(-1, value);
		K beforeElement = null;
		if (size() > 1) {
			beforeElement = this.get(size() - 1);
		}
		fireProperty(null, key, beforeElement, value);
		return true;
	}
	
	public int addValue(int pos, V value) {
		if (pos == -1) {
			this.values.add(value);
			pos = this.values.size();
			this.hashTableAddKey(value, pos);
			return pos;
		}
		this.values.add(pos, value);
		return -1;
	}
}
