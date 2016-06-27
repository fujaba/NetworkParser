package de.uniks.networkparser.list;

public class MapEntry extends SimpleEntity<String, Object> {
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new MapEntry();
	}

	@Override
	public MapEntry withKeyItem(Object key) {
		if (key instanceof String) {
			withKey((String) key);
		}
		return this;
	}

	@Override
	public MapEntry withValueItem(Object value) {
		this.withValue(value);
		return this;
	}
}
