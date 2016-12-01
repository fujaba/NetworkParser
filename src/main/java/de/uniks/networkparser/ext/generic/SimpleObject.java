package de.uniks.networkparser.ext.generic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleEntity;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class SimpleObject implements SendableEntityCreator, SendableEntity {
	private String className;

	private String id;


	public String getClassName() {
		return className;
	}


	public void setClassName(String className) {
		this.className = className;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}

	private SimpleKeyValueList<String, Object> values = new SimpleKeyValueList<String, Object>();

	private String[] properties;

	private boolean dirty = false;

	private PropertyChangeSupport propertyChangeSupport;


	@SuppressWarnings("unchecked")
	public static SimpleObject create(SimpleEntity<String, Object>... values) {
		SimpleObject result = new SimpleObject();
		if (values != null) {
			for (SimpleEntity<String, Object> item : values) {
				result.addValue(item.getKey(), item.getValue());
			}
		}
		return result;
	}


	public static SimpleObject create(String className, String key, Object value) {
		SimpleObject result = new SimpleObject();
		result.setClassName(className);
		result.addValue(key, value);
		return result;
	}


	public static SimpleObject create(JsonObject json) {
		SimpleObject result = new SimpleObject();

		result.setId(json.getString(IdMap.ID));
		Object className = json.get(IdMap.CLASS);
		if (className != null) {
			result.setClassName("" + className);
		}
		Object props = json.get(JsonTokener.PROPS);
		if (props != null && props instanceof JsonObject) {
			JsonObject jsonProps = (JsonObject) props;
			for (int i = 0; i < jsonProps.size(); i++) {
				String key = jsonProps.getKeyByIndex(i);
				Object value = jsonProps.getValueByIndex(i);
				result.addValue(key, value);
			}
		}
		return result;
	}


	void addValue(String key, Object value) {
		this.values.add(key, value);
		this.dirty = true;
	}


	public Object getValue(String key) {
		return values.get(key);
	}


	public Object getValue() {
		if (values.size() == 1) {
			return values.get(values.getValueByIndex(0));
		}
		return null;
	}


	@Override
	public String[] getProperties() {
		if (this.dirty) {
			this.properties = this.values.keySet().toArray(new String[this.values.size()]);
			this.dirty = false;
		}
		return this.properties;
	}


	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity instanceof SimpleObject) {
			return ((SimpleObject) entity).getValue(attribute);
		}
		return null;
	}


	public boolean setValue(String attribute, Object value) {
		int pos = this.values.indexOf(attribute);
		Object oldValue;
		if (pos < 0) {
			oldValue = null;
			this.addValue(attribute, value);
		}
		else {
			oldValue = this.values.getValue(attribute);
			this.values.setValue(pos, value);
		}
		this.propertyChangeSupport.firePropertyChange(attribute, oldValue, value);
		return true;
	}


	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity instanceof SimpleObject) {
			if( IdMap.NEW.equals(type) && value instanceof String) {
				if(IdMap.ID.equals(attribute)) {
					this.setId((String) value);
					return true;
				} else if(IdMap.CLASS.equals(attribute)) {
					this.setClassName((String) value);
					return true;
				}
			}
			((SimpleObject) entity).setValue(attribute, value);
			return true;
		}
		return false;
	}


	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SimpleObject();
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		if (id != null) {
			sb.append(id);
			if (className != null) {
				sb.append(':');
				sb.append(className);
			}
		}
		else if (className != null) {
			sb.append(className);
		}
		if (values.size() > 0) {
			sb.append('|');
			for (int i = 0; i < values.size(); i++) {
				String key = values.getKeyByIndex(i);
				Object value = values.getValueByIndex(i);
				if (i > 0) {
					sb.append(',');
				}
				sb.append(key);
				sb.append('=');
				sb.append(value);

			}
		}
		sb.append(']');
		return sb.toString();
	}


	@Override
	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (this.propertyChangeSupport == null)
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		return true;
	}


	@Override
	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.propertyChangeSupport == null)
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeSupport.addPropertyChangeListener(listener);
		return true;
	}


	@Override
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (this.propertyChangeSupport == null)
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeSupport.removePropertyChangeListener(listener);
		return true;
	}


	@Override
	public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (this.propertyChangeSupport == null)
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
		return true;
	}
}
