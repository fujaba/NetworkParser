package de.uniks.networkparser.list;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import de.uniks.networkparser.interfaces.FactoryEntity;
import de.uniks.networkparser.listold.AbstractArray;
import de.uniks.networkparser.listold.AbstractKeyValueList;

public class SimpleKeyValueList<K, V> extends AbstractArray implements Map<K, V> {
	public Object getValueItem(Object key) {
		int pos = getIndex(key);
		if (pos >= 0) {
			return this.values.get(pos);
		}
		if (!(key instanceof String)) {
			return null;
		}
		String keyString = "" + key;
		int len = 0;
		int end = 0;
		int id = 0;
		for (; len < keyString.length(); len++) {
			char temp = keyString.charAt(len);
			if (temp == '[') {
				for (end = len + 1; end < keyString.length(); end++) {
					temp = keyString.charAt(end);
					if (keyString.charAt(end) == ']') {
						end++;
						break;
					} else if (temp > 47 && temp < 58 && id >= 0) {
						id = id * 10 + temp - 48;
					} else if (temp == 'L') {
						id = -2;
					}
				}
				if (end == keyString.length()) {
					end = 0;
				}
				break;
			} else if (temp == '.') {
				end = len;
				id = -1;
				break;
			}
		}
		if (end == 0 && len == keyString.length()) {
			id = -1;
		}

		Object child = get(keyString.substring(0, len));
		if (child != null) {
			if (end == 0) {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractList<?>) {
						AbstractList<?> list = (AbstractList<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return list.get(id);
						}
					}
				} else {
					return child;
				}
			} else {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractArray) {
						if (end == len + 2) {
							// Get List
							if (this instanceof FactoryEntity) {
								AbstractArray<?> result = (AbstractArray<?>) ((FactoryEntity) this)
										.getNewArray();
								AbstractArray<?> items = (AbstractArray<?>) child;
								for (int z = 0; z < items.size(); z++) {
									result.with(((AbstractKeyValueList<?, ?>) items
											.get(z)).getValueItem(keyString
											.substring(end + 1)));
								}
								return result;
							}
						}
						AbstractArray<?> list = (AbstractArray<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							return ((AbstractKeyValueList<?, ?>) list.get(id))
									.getValueItem(keyString.substring(end + 1));
						}
					}
				} else {
					return ((AbstractKeyValueList<?, ?>) child)
							.getValueItem(keyString.substring(end + 1));
				}
			}
		}
		return null;
	}
	
	private int getIndex(Object key) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Set a Value to Entity With this Method it is possible to set a Value of a
	 * Set by using a [Number] or [L] for Last
	 *
	 * @param key
	 *            the Key to add
	 * @param value
	 *            the Value to add
	 * @return Itself
	 */
	@SuppressWarnings("unchecked")
	public AbstractKeyValueList<K, V> setValueItem(Object key, Object value) {
		int pos = getIndex(key);
		if (pos >= 0) {
			V oldValue = this.values.get(pos);
			int position = getPositionValue(oldValue);
			this.values.set(pos, (V) value);
			if (this.hashTableValues != null && position >= 0) {
				this.hashTableValues[position] = value;
			}
			return this;
		}
		if (!(key instanceof String)) {
			return this;
		}
		String keyString = "" + key;

		int len = 0;
		int end = 0;
		int id = 0;
		for (; len < keyString.length(); len++) {
			char temp = keyString.charAt(len);
			if (temp == '[') {
				for (end = len + 1; end < keyString.length(); end++) {
					temp = keyString.charAt(end);
					if (keyString.charAt(end) == ']') {
						end++;
						break;
					} else if (temp > 47 && temp < 58 && id >= 0) {
						id = id * 10 + temp - 48;
					} else if (temp == 'L') {
						id = -2;
					}
				}
				if (end == keyString.length()) {
					end = 0;
				}
				break;
			} else if (temp == '.') {
				end = len;
				id = -1;
				break;
			}
		}
		if (end == 0 && len == keyString.length()) {
			id = -1;
		}

		Object child = get(keyString.substring(0, len));
		if (child != null) {
			if (end == 0) {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractArray) {
						AbstractArray<Object> list = (AbstractArray<Object>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							if (value == null) {
								list.remove(id);
							} else {
								list.set(id, value);
							}
						}
					}
				} else {
					if (value == null) {
						remove(keyString.substring(0, len));
					} else {
						put((K) keyString.substring(0, len), (V) value);
					}
				}
			} else {
				if (id >= 0 || id == -2) {
					if (child instanceof AbstractArray) {
						AbstractArray<?> list = (AbstractArray<?>) child;
						if (id == -2) {
							id = list.size() - 1;
						}
						if (list.size() >= id) {
							((AbstractKeyValueList<K, ?>) list.get(id))
									.setValueItem(
											(K) keyString.substring(end + 1),
											value);
						}
					}
				} else {
					((AbstractKeyValueList<K, ?>) child).setValueItem(
							(K) keyString.substring(end + 1), value);
				}
			}
		} else {
			put((K) keyString.substring(0, len), (V) value);
		}
		return this;
	}

	
	@Override
	public Set<K> keySet() {
		if(isBig()) {
			return new SimpleSet<K>().init((Object[])this.elements[SMALL_KEY]);
		}
		return new SimpleSet<K>().init(this.elements);
	}

	@Override
	public boolean containsKey(Object key) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsValue(Object value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public V get(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V put(K key, V value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public V remove(Object key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		// TODO Auto-generated method stub

	}


	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	public SimpleKeyValueList<K, V> withMap(Map<?, ?> map) {
		if (map != null) {
			for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
				java.util.Map.Entry<?, ?> mapEntry = (Entry<?, ?>) i.next();
				Object item = mapEntry.getValue();
				Object key = mapEntry.getKey();
				if (item != null) {
					this.withValue(key, item);
				}
			}
		}
		return this;
	}

	@Override
	public SimpleKeyValueList<K, V> withAllowDuplicate(boolean allowDuplicate) {
		super.withAllowDuplicate(allowDuplicate);
		return this;
	}
}
