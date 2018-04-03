package de.uniks.networkparser.ext.petaf;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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

	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		boolean change = false;
		if (oldValue == null) {
			change = (newValue != null);
		} else {
			change = !oldValue.equals(newValue);
		}
		return firePropertyChange(propertyName, oldValue, newValue, change);
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
