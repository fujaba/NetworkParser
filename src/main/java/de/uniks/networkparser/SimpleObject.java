package de.uniks.networkparser;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreatorIndexId;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleEntity;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class SimpleObject.
 *
 * @author Stefan
 */
public class SimpleObject implements SendableEntityCreatorIndexId, SendableEntity {
	protected String className;
	protected String id;

	/* SimpleObject Creator Properties */
	private String[] properties;
	private SimpleList<String> baseElements = new SimpleList<String>();
	private boolean dirty = false;

	protected PropertyChangeSupport propertyChangeSupport;

	private SimpleKeyValueList<String, Object> values = new SimpleKeyValueList<String, Object>();

	/**
	 * Gets the class name.
	 *
	 * @return the class name
	 */
	public String getClassName() {
		return className;
	}

	protected void addBaseElements(String... elements) {
		if (elements == null) {
			return;
		}
		for (String item : elements) {
			if (this.baseElements.add(item)) {
				this.dirty = true;
			}
		}
	}

	/**
	 * Sets the class name.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setClassName(String value) {
		if (value != this.className) {
			this.className = value;
			return true;
		}
		return false;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setId(String value) {
		if (value != this.id) {
			this.id = value;
			return true;
		}
		return false;
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(String key) {
		if (SimpleMap.ID.equals(key)) {
			return this.getId();
		} else if (SimpleMap.CLASS.equals(key)) {
			return this.getClassName();
		}
		return values.get(key);
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		if (values.size() == 1) {
			return values.getValueByIndex(0);
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(String key, Object value) {
		if (key == null) {
			return false;
		}
		key = key.trim();
		boolean checked = false;
		Object oldValue = null;
		if (value instanceof String) {
			if (SimpleMap.ID.equals(key)) {
				checked = true;
				oldValue = this.getId();
				this.setId((String) value);
			} else if (SimpleMap.CLASS.equals(key)) {
				oldValue = this.getClassName();
				checked = true;
				this.setClassName((String) value);
			}
		}
		if (!checked) {
			int pos = this.values.indexOf(key);

			if (pos < 0) {
				oldValue = null;
				if (this.values.add(key, value)) {
					this.baseElements.add(key);
					this.dirty = true;
				}
			} else {
				oldValue = this.values.setValue(pos, value);
			}
		}
		return firePropertyChange(key, oldValue, value);
	}

	/**
	 * Fire property change.
	 *
	 * @param propertyName the property name
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @return true, if successful
	 */
	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (propertyChangeSupport != null) {
			if (isChanged(oldValue, newValue)) {
				propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if is changed.
	 *
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @return true, if is changed
	 */
	public boolean isChanged(Object oldValue, Object newValue) {
		return !(oldValue != null && oldValue.equals(newValue) || oldValue == newValue);
	}

	/**
	 * Without value.
	 *
	 * @param key the key
	 * @return the object
	 */
	public Object withoutValue(String key) {
		Object result = this.values.remove(key);
		if (result != null) {
			this.baseElements.remove(key);
			this.dirty = true;
		}
		return result;
	}

	/**
	 * Creates the.
	 *
	 * @param values the values
	 * @return the simple object
	 */
	@SuppressWarnings("unchecked")
	public static SimpleObject create(SimpleEntity<String, Object>... values) {
		SimpleObject result = new SimpleObject();
		if (values != null) {
			for (SimpleEntity<String, Object> item : values) {
				if (item != null) {
					result.setValue(item.getKey(), item.getValue());
				}
			}
		}
		return result;
	}

	/**
	 * Creates the.
	 *
	 * @param className the class name
	 * @param key the key
	 * @param value the value
	 * @return the simple object
	 */
	public static SimpleObject create(String className, String key, Object value) {
		SimpleObject result = new SimpleObject();
		result.setClassName(className);
		result.setValue(key, value);
		return result;
	}

	/**
	 * Creates the.
	 *
	 * @param id the id
	 * @param className the class name
	 * @param key the key
	 * @param value the value
	 * @return the simple object
	 */
	public static SimpleObject create(String id, String className, String key, Object value) {
		SimpleObject result = new SimpleObject();
		result.setId(id);
		result.setClassName(className);
		result.setValue(key, value);
		return result;
	}

	/**
	 * Creates the.
	 *
	 * @param json the json
	 * @return the simple object
	 */
	public static SimpleObject create(JsonObject json) {
		SimpleObject result = new SimpleObject();
		if (json == null) {
			return result;
		}

		result.setId(json.getString(SimpleMap.ID));
		Object className = json.get(SimpleMap.CLASS);
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

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		if (this.dirty) {
			this.properties = this.baseElements.toArray(new String[this.baseElements.size()]);
			this.dirty = false;
		}
		return this.properties;
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity instanceof SimpleObject) {
			return ((SimpleObject) entity).getValue(attribute);
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		return setValue(attribute, value);
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new SimpleObject();
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
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
		} else if (className != null) {
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

	/**
	 * Adds the property change listener.
	 *
	 * @param propertyName the property name
	 * @param listener the listener
	 * @return true, if successful
	 */
	@Override
	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (this.propertyChangeSupport == null)
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	/**
	 * Adds the property change listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	@Override
	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (this.propertyChangeSupport == null)
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeSupport.addPropertyChangeListener(listener);
		return true;
	}

	/**
	 * Removes the property change listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	@Override
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (this.propertyChangeSupport == null)
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeSupport.removePropertyChangeListener(listener);
		return true;
	}

	/**
	 * Removes the property change listener.
	 *
	 * @param propertyName the property name
	 * @param listener the listener
	 * @return true, if successful
	 */
	@Override
	public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (this.propertyChangeSupport == null)
			this.propertyChangeSupport = new PropertyChangeSupport(this);
		this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
		return true;
	}
}
