package de.uniks.networkparser.list;

public class ObjectMapEntry extends SimpleEntity<Object, Object> {
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ObjectMapEntry();
	}

	@Override
	public ObjectMapEntry withKeyItem(Object key) {
		withKey(key);
		return this;
	}

	@Override
	public ObjectMapEntry withValueItem(Object value) {
		this.withValue(value);
		return this;
	}
}
