package de.uniks.networkparser.list;

public class SimpleMapEntry<K, V> extends SimpleEntity<K, V> {
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SimpleMapEntry<K, V>();
	}

	@Override
	@SuppressWarnings("unchecked")
	public SimpleMapEntry<K, V> withKeyItem(Object key) {
		withKey((K) key);
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public SimpleMapEntry<K, V> withValueItem(Object value) {
		this.withValue((V) value);
		return this;
	}
}
