package de.uniks.networkparser.event;

import java.util.Set;

import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.Entity;

public abstract class SimpleEvent<V extends Entity> implements Entity{
	protected V children;
	public V getChildren() {
		return null;
	}
	
	@Override
	public boolean containsKey(Object key) {
		if(children == null) {
			return false;
		}
		return children.containsKey(key);
	}

	@Override
	public String getString(String key) {
		if(children == null) {
			return null;
		}
		return children.getString(key);
	}

	@Override
	public boolean getBoolean(String key) {
		if(children == null) {
			return false;
		}
		return children.getBoolean(key);
	}

	@Override
	public double getDouble(String key) {
		if(children == null) {
			return -1;
		}
		return children.getDouble(key);
	}

	@Override
	public int getInt(String key) {
		if(children == null) {
			return -1;
		}
		return children.getInt(key);
	}

	@Override
	public Object getValue(String key) {
		if(children == null) {
			return null;
		}
		return children.getValue(key);
	}

	@Override
	public int size() {
		if(children == null) {
			return 0;
		}
		return children.size();
	}

	@Override
	public Entity without(String key) {
		if(children == null) {
			return this;
		}
		children.without(key);
		return this;
	}

	@Override
	public String getKeyByIndex(int pos) {
		if(children == null) {
			return null;
		}
		return children.getKeyByIndex(pos);
	}

	@Override
	public Object getValueByIndex(int pos) {
		if(children == null) {
			return null;
		}
		return children.getValueByIndex(pos);
	}

	@Override
	public boolean has(String key) {
		if(children == null) {
			return false;
		}
		return children.has(key);
	}

	@Override
	public Object remove(Object key) {
		if(children == null) {
			return null;
		}
		return children.remove(key);
	}

	@Override
	public Object put(String key, Object value) {
		if(children == null) {
			return null;
		}
		return children.put(key, value);
	}

	@Override
	public Set<String> keySet() {
		if(children == null) {
			return null;
		}
		return children.keySet();
	}
	@Override
	public String toString(Converter converter) {
		if(converter == null) {
			return null;
		}
		return converter.encode(this);
	}

}
