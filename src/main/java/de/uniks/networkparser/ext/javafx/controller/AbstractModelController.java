package de.uniks.networkparser.ext.javafx.controller;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;

import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.interfaces.SendableEntity;
import javafx.scene.Node;

public abstract class AbstractModelController implements PropertyChangeListener {
	@SuppressWarnings("unchecked")
	public <ST extends AbstractModelController> ST init(Object model, Node gui) {
		if(model != null && gui != null) {
			try {
				Method method = this.getClass().getMethod("initPropertyChange"+model.getClass().getSimpleName(), model.getClass(), Node.class);
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

	public abstract void initPropertyChange(Object model, Node gui);

	public boolean addListener(Object item, String property) {
		return addListener(item, property, this);
	}
	public boolean addListener(Object item, String property, PropertyChangeListener listener) {
		if(item==null) {
			return false;
		}
		GenericCreator creator=new GenericCreator(item);
		if(property!=null) {
			if (item instanceof SendableEntity) {
				((SendableEntity) item).addPropertyChangeListener(property, listener);
				listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return true;
			}
			if(item instanceof PropertyChangeSupport){
				((PropertyChangeSupport) item).addPropertyChangeListener(property, listener);
				listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return  true;
			}
			try {
				Method method = item.getClass().getMethod("addPropertyChangeListener", String.class, java.beans.PropertyChangeListener.class );
				method.invoke(item, property, listener);
				listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
				return  true;
			} catch (Exception e) {
			}
		}
		try {
			Method method = item.getClass().getMethod("getPropertyChangeSupport");
			PropertyChangeSupport pc = (PropertyChangeSupport) method.invoke(item);
			if(property == null) {
				pc.addPropertyChangeListener(listener);
				listener.propertyChange(new PropertyChangeEvent(item, property, null, null));
			}else {
				pc.addPropertyChangeListener(property, listener);
				listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			}
			return true;
		} catch (Exception e) {
		}
		try {
			Method method = item.getClass().getMethod("addPropertyChangeListener",  java.beans.PropertyChangeListener.class );
			method.invoke(item, listener);
			listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return  true;
		} catch (ReflectiveOperationException e) {
		} catch (SecurityException e) {
		} catch (IllegalArgumentException e) {
		}
		return false;
	}
}
