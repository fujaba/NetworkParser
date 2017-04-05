package de.uniks.networkparser;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleEntity;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class SimpleObject implements SendableEntityCreator, SendableEntity {
	
	protected String className;

	protected String id;

	private String[] properties;

	private boolean dirty = false;

	protected PropertyChangeSupport propertyChangeSupport;

	private SimpleKeyValueList<String, Object> values = new SimpleKeyValueList<String, Object>();
	private SimpleList<String> baseElements = new SimpleList<String>();
	
	public String getClassName() {
		return className;
	}
	
	protected void addBaseElements(String... elements) {
		if(elements == null) {
			return;
		}
		for(String item : elements) {
			if(this.baseElements.add(item)) {
				this.dirty = true;
			}
		}
	}

	public boolean setClassName(String value) {
		if(value != this.className) {
			this.className = value;
			return true;
		}
		return false;
	}

	public String getId() {
		return id;
	}

	public boolean setId(String value) {
		if(value != this.id) {
			this.id = value;
			return true;
		}
		return false;
	}
	
	public Object getValue(String key) {
		if(IdMap.ID.equals(key)) {
			return this.getId();
		} else if(IdMap.CLASS.equals(key)) {
			return this.getClassName();
		}
		return values.get(key);
	}

	public Object getValue() {
		if (values.size() == 1) {
			return values.getValueByIndex(0);
		}
		return null;
	}
	
	public boolean setValue(String key, Object value) {
		boolean checked = false;
		Object oldValue = null;
		if(value instanceof String) {
			if(IdMap.ID.equals(key)) {
				checked = true;
				oldValue = this.getId();
				this.setId((String) value);
			} else if(IdMap.CLASS.equals(key)) {
				oldValue = this.getClassName();
				checked = true;
				this.setClassName((String) value);
			}
		}
		if(!checked){
			int pos = this.values.indexOf(key);
	
			if (pos < 0) {
				oldValue = null;
				if(this.values.add(key, value)) {
					this.baseElements.add(key);				
					this.dirty = true;
				}
			}
			else {
				oldValue = this.values.setValue(pos, value);
			}
		}
		return firePropertyChange(key, oldValue, value);
	}
	
	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (propertyChangeSupport != null) {
			if (isChanged(oldValue, newValue)) {
				propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
				return true;
			}
		}
		return false;
	}
	
	public boolean isChanged(Object oldValue, Object newValue) {
		return !(oldValue != null && oldValue.equals(newValue) || oldValue == newValue);
	}
	
	public Object withoutValue(String key) {
		Object result =  this.values.remove(key);
		if(result!= null) {
			this.baseElements.remove(key);
			this.dirty = true;
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static SimpleObject create(SimpleEntity<String, Object>... values) {
		SimpleObject result = new SimpleObject();
		if (values != null) {
			for (SimpleEntity<String, Object> item : values) {
				result.setValue(item.getKey(), item.getValue());
			}
		}
		return result;
	}


	public static SimpleObject create(String className, String key, Object value) {
		SimpleObject result = new SimpleObject();
		result.setClassName(className);
		result.setValue(key, value);
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
				result.setValue(key, value);
			}
		}
		return result;
	}

	@Override
	public String[] getProperties() {
		if (this.dirty) {
			this.properties = this.baseElements.toArray(new String[this.baseElements.size()]);
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

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		return setValue(attribute, value);
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
