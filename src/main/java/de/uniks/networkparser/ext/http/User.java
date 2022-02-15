package de.uniks.networkparser.ext.http;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class User.
 *
 * @author Stefan
 */
public class User implements SendableEntityCreator {
	
	/** The Constant PROPERTY_NAME. */
	public static final String PROPERTY_NAME = "name";
	
	/** The Constant PROPERTY_PASSWORD. */
	public static final String PROPERTY_PASSWORD = "password";
	
	/** The Constant properties. */
	public static final String[] properties = new String[] { PROPERTY_NAME, PROPERTY_PASSWORD };
	private String name;
	private String password;
	private SimpleKeyValueList<String, String> tokens;

	protected PropertyChangeSupport listeners = null;

	/**
	 * Fire property change.
	 *
	 * @param propertyName the property name
	 * @param oldValue the old value
	 * @param newValue the new value
	 * @return true, if successful
	 */
	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	/**
	 * Adds the property change listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	/**
	 * Adds the property change listener.
	 *
	 * @param propertyName the property name
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
			listeners.addPropertyChangeListener(propertyName, listener);
		}
		return true;
	}

	/**
	 * Removes the property change listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		return true;
	}

	/**
	 * Removes the property change listener.
	 *
	 * @param propertyName the property name
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(propertyName, listener);
		}
		return true;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Sets the name.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setName(String value) {
		if (this.name != value) {
			String oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
			return true;
		}
		return false;
	}

	/**
	 * With name.
	 *
	 * @param value the value
	 * @return the user
	 */
	public User withName(String value) {
		setName(value);
		return this;
	}

	/**
	 * Gets the password.
	 *
	 * @return the password
	 */
	public String getPassword() {
		return this.password;
	}

	/**
	 * Sets the password.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setPassword(String value) {
		if (this.password != value) {
			String oldValue = this.password;
			this.password = value;
			firePropertyChange(PROPERTY_PASSWORD, oldValue, value);
			return true;
		}
		return false;
	}

	/**
	 * With password.
	 *
	 * @param value the value
	 * @return the user
	 */
	public User withPassword(String value) {
		setPassword(value);
		return this;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return properties;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new User();
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
		if (attribute == null || entity instanceof User == false) {
			return null;
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return getName();
		}
		if (PROPERTY_PASSWORD.equalsIgnoreCase(attribute)) {
			return getPassword();
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
		if (attribute == null || entity instanceof User == false) {
			return false;
		}
		if (PROPERTY_NAME.equalsIgnoreCase(attribute)) {
			return setName("" + value);
		}
		if (PROPERTY_PASSWORD.equalsIgnoreCase(attribute)) {
			return setPassword("" + password);
		}
		return false;
	}

	/**
	 * With.
	 *
	 * @param name the name
	 * @param password the password
	 * @return the user
	 */
	public User with(String name, String password) {
		this.setName(name);
		this.setPassword(password);
		return this;
	}

	/**
	 * Adds the token.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean addToken(String key, String value) {
		if (tokens == null) {
			tokens = new SimpleKeyValueList<String, String>();
		}
		return tokens.add(key, value);
	}

	/**
	 * Contains.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean contains(String key, String value) {
		if (tokens == null) {
			return false;
		}
		String token = this.tokens.getString(key);
		return (token != null && token.equals(value));
	}

}
