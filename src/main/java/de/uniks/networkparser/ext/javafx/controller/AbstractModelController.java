package de.uniks.networkparser.ext.javafx.controller;

/*
NetworkParser
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;

import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.SendableEntity;

public abstract class AbstractModelController implements PropertyChangeListener {
	@SuppressWarnings("unchecked")
	public <ST extends AbstractModelController> ST init(Object model, Object gui) {
		if (model != null && gui != null) {
			try {
				Method method = this.getClass().getMethod("initPropertyChange" + model.getClass().getSimpleName(),
						model.getClass(), ReflectionLoader.NODE);
				method.invoke(this, model, gui);
			} catch (ReflectiveOperationException e) {
				this.initPropertyChange(model, gui);
			} catch (SecurityException e) {
				this.initPropertyChange(model, gui);
			} catch (IllegalArgumentException e) {
				this.initPropertyChange(model, gui);
			}
		}
		return (ST) this;
	}

	public abstract void initPropertyChange(Object model, Object gui);

	public boolean addListener(Object item, String property) {
		return addListener(item, property, this);
	}

	public boolean addListener(Object item, String property, PropertyChangeListener listener) {
		if (item == null) {
			return false;
		}
		GenericCreator creator = new GenericCreator(item);
		if (property != null) {
			if (item instanceof SendableEntity) {
				((SendableEntity) item).addPropertyChangeListener(property, listener);
				listener.propertyChange(
						new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return true;
			}
			if (item instanceof PropertyChangeSupport) {
				((PropertyChangeSupport) item).addPropertyChangeListener(property, listener);
				listener.propertyChange(
						new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return true;
			}
			try {
				Method method = item.getClass().getMethod("addPropertyChangeListener", String.class,
						java.beans.PropertyChangeListener.class);
				method.invoke(item, property, listener);
				listener.propertyChange(
						new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return true;
			} catch (ReflectiveOperationException e) {
			}
		}
		try {
			Method method = item.getClass().getMethod("getPropertyChangeSupport");
			PropertyChangeSupport pc = (PropertyChangeSupport) method.invoke(item);
			if (property == null) {
				pc.addPropertyChangeListener(listener);
				listener.propertyChange(new PropertyChangeEvent(item, property, null, null));
			} else {
				pc.addPropertyChangeListener(property, listener);
				listener.propertyChange(
						new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			}
			return true;
		} catch (ReflectiveOperationException e) {
		}
		try {
			Method method = item.getClass().getMethod("addPropertyChangeListener",
					java.beans.PropertyChangeListener.class);
			method.invoke(item, listener);
			listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return true;
		} catch (ReflectiveOperationException e) {
		}
		return false;
	}
}
