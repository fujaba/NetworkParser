package de.uniks.jism.event;

import java.util.Map.Entry;

public class JsonEntry implements Entry<Object, Object>{
	private Object key;
	private Object value;

	public Object setKey(Object key) {
		this.key=key;
		return key;
	}
	@Override
	public Object getKey() {
		return key;
	}

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	public Object setValue(Object value) {
		this.value=value;
		return value;
	}
}
