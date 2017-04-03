package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntity;

public class House implements SendableEntity{
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_FLOOR = "floor";
	private String name;
	private int floor;

	public void setFloor(int value) {
		if(value!=this.floor){
			Object oldValue = this.floor;
			this.floor = value;
			firePropertyChange(PROPERTY_FLOOR, oldValue, value);
		}
	}
	public void setName(String value) {
		if(value!=this.name){
			Object oldValue = this.name;
			this.name = value;
			firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	public int getFloor() {return floor;}
	public String getName() {return name;}
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
		return true;
	}

	public boolean removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(property, listener);
		}
		return true;
	}
}
