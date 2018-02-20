package de.uniks.networkparser.ext.petaf;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntity;

public abstract class SendableItem implements SendableEntity {
	protected PropertyChangeSupport listeners = null;
	/** The update listener. */
	protected ObjectCondition updateListener;


	public boolean addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);
		return true;
	}

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
		return true;
	}
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
	   		listeners.removePropertyChangeListener(listener);
	   	}
		return true;
	}
	public boolean removePropertyChangeListener(String property, PropertyChangeListener listener) {
		if (listeners != null) {
	   		listeners.removePropertyChangeListener(property, listener);
	   	}
		return true;
	}

	protected PropertyChangeSupport getPropertyChangeSupport() {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		return listeners;
	}

	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		boolean change = false;
		if (oldValue == null) {
			change = (newValue != null);
		} else {
			change = !oldValue.equals(newValue);
		}
		firePropertyChange(propertyName, oldValue, newValue, change);
	}

	public boolean firePropertyChange(String property, Object oldValue, Object newValue, boolean changed) {
		if (changed == false) {
			return false;
		}
		if(listeners != null) {
			listeners.firePropertyChange(property, oldValue, newValue);
		}
		if(updateListener != null) {
			updateListener.update(new SimpleEvent(this, property, oldValue, newValue));
		}
		return true;
	}

	public boolean firePropertyChange(String propertyName, int oldValue, int newValue) {
		if (oldValue == newValue ) {
			return false;
		}
		if(listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
		}
		if(updateListener != null) {
			updateListener.update(new SimpleEvent(this, propertyName, oldValue, newValue));
		}
		return true;
	}
}
