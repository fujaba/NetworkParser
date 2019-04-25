package de.uniks.networkparser.ext.petaf;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
 
public class User implements SendableEntityCreator {
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_PASSWORD = "password";
	public static final String[] properties = new String[] {PROPERTY_NAME, PROPERTY_PASSWORD};
	private String name;
	private String password;
	private SimpleKeyValueList<String, String> tokens;
	
	protected PropertyChangeSupport listeners = null;

	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		listeners.removePropertyChangeListener(listener);
		return true;
	}

	public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(propertyName, listener);
		}
		return true;
	}

	public String getName() {
		return this.name;
	}

	public boolean setName(String value) {
		if (this.name != value) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
			return true;
		}
		return false;
	}

	public User withName(String value) {
		setName(value);
		return this;
	}

	public String getPassword() {
		return this.password;
	}

	public boolean setPassword(String value) {
		if (this.password != value) {
			String oldValue = this.password;
			this.password = value;
			firePropertyChange(PROPERTY_PASSWORD, oldValue, value);
			return true;
		}
		return false;
	}

	public User withPassword(String value) {
		setPassword(value);
		return this;
	}
	
	
	@Override
	public String[] getProperties() {
		return properties;
	}
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new User();
	}
	@Override
	public Object getValue(Object entity, String attribute) {
		if(attribute == null || entity instanceof User == false) {
			return null;
		}
		if(PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return getName();
		}
		if(PROPERTY_PASSWORD.equalsIgnoreCase(attribute)) {
			return getPassword();
		}
		return null;
	}
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(attribute == null || entity instanceof User == false) {
			return false;
		}
		if(PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return setName(""+value);
		}
		if(PROPERTY_PASSWORD.equalsIgnoreCase(attribute)) {
			return setPassword(""+password);
		}
		return false;
	}

	public User with(String name, String password) {
		this.setName(name);
		this.setPassword(password);
		return this;
	}
	
	public boolean addToken(String key, String value) {
		if(tokens == null) {
			tokens = new SimpleKeyValueList<String, String>();
		}
		return tokens.add(key, value);
	}
	
	public boolean contains(String key, String value) {
		if(tokens == null) {
			return false;
		}
		String token = this.tokens.getString(key);
		return (token != null && token.equals(value));
	}
	
}
