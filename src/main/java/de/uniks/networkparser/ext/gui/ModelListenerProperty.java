package de.uniks.networkparser.ext.gui;

/*
NetworkParser
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.Collection;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class ModelListenerProperty.
 *
 * @author Stefan
 */
public class ModelListenerProperty implements ModelListenerInterface, SendableEntityCreator {
	
	/** The Constant PROPERTY_MODEL. */
	public static final String PROPERTY_MODEL = "model";
	
	/** The Constant PROPERTY_PROPERTY. */
	public static final String PROPERTY_PROPERTY = "property";
	
	/** The Constant PROPERTY_VIEW. */
	public static final String PROPERTY_VIEW = "view";
	
	/** The Constant PROPERTY_CREATOR. */
	public static final String PROPERTY_CREATOR = "creator";

	protected Object model;
	protected Object view;
	protected String property;
	protected SendableEntityCreator creator;
	private SimpleKeyValueList<Object, ObjectCondition> events;
	private SimpleSet<Object> listeners = new SimpleSet<Object>();
	private SimpleSet<Object> invalidationListeners = new SimpleSet<Object>();
	protected Object observable = null;
	protected Condition<SimpleEvent> callBack;
	protected DataType type;
	private Object viewProperty;
	private Object proxy;

	/**
	 * Instantiates a new model listener property.
	 */
	public ModelListenerProperty() {
	}

	/**
	 * Instantiates a new model listener property.
	 *
	 * @param creator the creator
	 * @param item the item
	 * @param property the property
	 * @param type the type
	 */
	public ModelListenerProperty(SendableEntityCreator creator, Object item, String property, DataType type) {
		this.creator = creator;
		this.property = property;
		this.model = item;
		this.type = type;
		addPropertyChange(item);
	}

	/**
	 * Adds the property change.
	 *
	 * @param item the item
	 */
	public void addPropertyChange(Object item) {
		if (item == null) {
			return;
		}
		if (item instanceof SendableEntity) {
			((SendableEntity) item).addPropertyChangeListener(property, this);
			return;
		}
		if (item instanceof PropertyChangeSupport) {
			((PropertyChangeSupport) item).addPropertyChangeListener(property, this);
			return;
		}
		try {
			Method method = item.getClass().getMethod("addPropertyChangeListener", String.class,
					java.beans.PropertyChangeListener.class);
			method.invoke(item, property, this);
			return;
		} catch (ReflectiveOperationException e) {
		}

		try {
			Method method = item.getClass().getMethod("getPropertyChangeSupport");
			PropertyChangeSupport pc = (PropertyChangeSupport) method.invoke(item);
			pc.addPropertyChangeListener(property, this);
			return;
		} catch (ReflectiveOperationException e) {
		}
		try {
			Method method = item.getClass().getMethod("addPropertyChangeListener",
					java.beans.PropertyChangeListener.class);
			method.invoke(item, this);
		} catch (ReflectiveOperationException e) {
		}
	}

	/**
	 * Gets the bean.
	 *
	 * @return the bean
	 */
	public Object getBean() {
		return model;
	}

	/**
	 * Sets the bean.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setBean(Object value) {
		if (value != this.model) {
			this.model = value;
			return true;
		}
		return false;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return property;
	}

	/**
	 * Adds the listener.
	 *
	 * @param listener the listener
	 */
	public void addListener(Object listener) {
		if(listener == null) {
			return;
		}
		if (ReflectionLoader.CHANGELISTENER != null) {
			if (ReflectionLoader.CHANGELISTENER.isAssignableFrom(listener.getClass())) {
				listeners.add(listener);
			}
		}
		if (ReflectionLoader.INVALIDATIONLISTENER != null) {
			if (ReflectionLoader.INVALIDATIONLISTENER.isAssignableFrom(listener.getClass())) {
				invalidationListeners.add(listener);
			}
		}
	}

	/**
	 * Removes the listener.
	 *
	 * @param listener the listener
	 */
	public void removeListener(Object listener) {
		if(listener == null) {
			return;
		}
		if (ReflectionLoader.CHANGELISTENER != null) {
			if (ReflectionLoader.CHANGELISTENER.isAssignableFrom(listener.getClass())) {
				listeners.remove(listener);
			}
		}
		if (ReflectionLoader.INVALIDATIONLISTENER != null) {
			if (ReflectionLoader.INVALIDATIONLISTENER.isAssignableFrom(listener.getClass())) {
				invalidationListeners.remove(listener);
			}
		}
	}

	/**
	 * Bind.
	 *
	 * @param newObservable the new observable
	 * @return true, if successful
	 */
	public boolean bind(Object newObservable) {
		if (newObservable == null) {
			return false;
		}
		this.viewProperty = newObservable;
		if (!newObservable.equals(observable)) {
			unbind();
			observable = newObservable;
			ReflectionLoader.call(observable, "addListener", ReflectionLoader.INVALIDATIONLISTENER, this);
		}
		return true;
	}

	/**
	 * Bind bidirectional.
	 *
	 * @param other the other
	 * @return true, if successful
	 */
	public boolean bindBidirectional(Object other) {
		if (other == null) {
			return false;
		}
		ReflectionLoader.call(other, "bindBidirectional", ReflectionLoader.PROPERTY, this.getProxy());
		this.viewProperty = other;
		return true;
	}

	/**
	 * Checks if is bound.
	 *
	 * @return true, if is bound
	 */
	public boolean isBound() {
		return observable != null;
	}

	/**
	 * Unbind.
	 */
	public void unbind() {
		if (observable != null) {
			ReflectionLoader.call(observable, "removeListener", ReflectionLoader.OBSERVABLEVALUE, this);
			observable = null;
			this.view = null;
			this.viewProperty = null;
		}
	}

	/**
	 * Unbind bidirectional.
	 *
	 * @param other the other
	 * @return true, if successful
	 */
	public boolean unbindBidirectional(Object other) {
		ReflectionLoader.call(other, "unbindBidirectional", ReflectionLoader.PROPERTY, this.getProxy());
		this.view = null;
		this.viewProperty = null;
		return true;
	}

	/**
	 * Gets the item value.
	 *
	 * @return the item value
	 */
	public Object getItemValue() {
		if (creator == null) {
			return null;
		}
		Object value = creator.getValue(model, property);
		if (value instanceof Collection<?>) {
			return ((Collection<?>) value).size();
		}
		return value;
	}

	/**
	 * Gets the modell.
	 *
	 * @return the modell
	 */
	public Object getModell() {
		return this.model;
	}

	/**
	 * Gets the view.
	 *
	 * @return the view
	 */
	public Object getView() {
		return this.view;
	}

	/**
	 * Gets the view property.
	 *
	 * @return the view property
	 */
	public Object getViewProperty() {
		return this.viewProperty;
	}

	/**
	 * Property change.
	 *
	 * @param evt the evt
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		for (Object listener : listeners) {
			Object event = ReflectionLoader.newInstance(ReflectionLoader.SIMPLEOBJECTPROPERTY);
			Object oldValue = parseValue(evt.getOldValue());
			Object newValue = parseValue(evt.getNewValue());
			ReflectionLoader.call(listener, "changed", ReflectionLoader.OBSERVABLEVALUE, event, Object.class, oldValue,
					Object.class, newValue);
		}
		for (Object listener : invalidationListeners) {
			ReflectionLoader.call(listener, "invalidated", ReflectionLoader.INVALIDATIONLISTENER, this);
		}
		executeCallBack();
	}

	/**
	 * Execute call back.
	 */
	public void executeCallBack() {
		if (callBack != null) {
			SimpleEvent event = new SimpleEvent(this.model, this.property, null, getItemValue());
			if (callBack.update(event)) {
				ReflectionLoader.call(observable, "set", String.class, "" + event.getModelValue());
			}
		}
	}

	/**
	 * With call back.
	 *
	 * @param listener the listener
	 * @return the model listener property
	 */
	public ModelListenerProperty withCallBack(Condition<SimpleEvent> listener) {
		this.callBack = listener;
		return this;
	}

	/**
	 * Invalidated.
	 *
	 * @param observable the observable
	 */
	public void invalidated(Object observable) {
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public Object getValue() {
		return getItemValue();
	}

	/**
	 * Parses the value.
	 *
	 * @param value the value
	 * @return the object
	 */
	public Object parseValue(Object value) {
		if (this.type == DataType.COLOR) {
			if (value != null && ReflectionLoader.COLOR.isAssignableFrom(value.getClass())) {
				return value;
			}
			if (value instanceof String) {
				return ReflectionLoader.call(DataType.COLOR, "web", String.class, value);
			}
			return ReflectionLoader.call(DataType.COLOR, "web", String.class, "#FFFFFF");
		}
		if (this.type == DataType.STRING) {
			return "" + value;
		}
		if (this.type == DataType.BOOLEAN) {
			if (value instanceof Boolean) {
				return value;
			}
			return Boolean.valueOf("" + value);
		}
		if (this.type == DataType.INT) {
			if (value instanceof Integer) {
				return value;
			}
			return Integer.valueOf("" + value);
		}
		if (this.type == DataType.LONG) {
			if (value instanceof Long) {
				return value;
			}
			return Long.valueOf("" + value);
		}
		if (this.type == DataType.FLOAT) {
			if (value instanceof Float) {
				return value;
			}
			return Float.valueOf("" + value);
		}
		if (this.type == DataType.DOUBLE) {
			if (value instanceof Double) {
				return value;
			}
			return Double.valueOf("" + value);
		}
		if (value instanceof Number) {
			return value;
		}
		return value;
	}

	/**
	 * Gets the proxy.
	 *
	 * @return the proxy
	 */
	public Object getProxy() {
		if (this.proxy == null) {
			this.proxy = ReflectionLoader.createProxy(this,
					new Class[] { ModelListenerInterface.class, ReflectionLoader.PROPERTY });
		}
		return this.proxy;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(Object value) {
		if (creator != null) {
			creator.setValue(model, property, value, SendableEntityCreator.NEW);
		}
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ModelListenerProperty();
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return new String[] { PROPERTY_MODEL, PROPERTY_CREATOR, PROPERTY_PROPERTY, PROPERTY_VIEW };
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
		if (!(entity instanceof ModelListenerProperty)) {
			return false;
		}
		ModelListenerProperty property = (ModelListenerProperty) entity;
		if (PROPERTY_MODEL.equalsIgnoreCase(attribute)) {
			property.model = value;
			property.addPropertyChange(value);
			return true;
		}
		if (PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
			property.property = (String) value;
			return true;
		}
		if (PROPERTY_CREATOR.equalsIgnoreCase(attribute)) {
			property.creator = (SendableEntityCreator) value;
			return true;
		}
		if (PROPERTY_VIEW.equalsIgnoreCase(attribute)) {
			Object guiProp = ModelListenerFactory.getProperty(value);
			if (guiProp == null) {
				return false;
			}
			property.view = value;
			property.bindBidirectional(guiProp);
			if (this.events != null && value != null) {
				for (int i = 0; i < this.events.size(); i++) {
					Object eventType = this.events.getKeyByIndex(i);
					ObjectCondition condition = this.events.getValueByIndex(i);
					GUIEvent event = new GUIEvent();
					event.withListener(condition);

					Object proxy = ReflectionLoader.createProxy(event, ReflectionLoader.EVENTHANDLER);
					Class<?> eventTypeClass = ReflectionLoader.getClass("javafx.event.EventType");
					ReflectionLoader.call(value, "addEventHandler", eventTypeClass, eventType,
							ReflectionLoader.EVENTHANDLER, proxy);
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Register event.
	 *
	 * @param eventtype the eventtype
	 * @param conditions the conditions
	 * @return true, if successful
	 */
	public boolean registerEvent(Object eventtype, ObjectCondition conditions) {
		if (eventtype != null && conditions != null) {
			if (this.events == null) {
				this.events = new SimpleKeyValueList<Object, ObjectCondition>();
			}
			this.events.add(eventtype, conditions);
			return true;
		}
		return false;
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
		if (!(entity instanceof ModelListenerProperty) || attribute == null) {
			return null;
		}
		ModelListenerProperty prop = (ModelListenerProperty) entity;
		if (PROPERTY_MODEL.equalsIgnoreCase(attribute)) {
			return prop.getModell();
		}
		if (PROPERTY_PROPERTY.equalsIgnoreCase(attribute)) {
			return prop.getProperties();
		}
		if (PROPERTY_CREATOR.equalsIgnoreCase(attribute)) {
			return prop.creator;
		}
		if (PROPERTY_VIEW.equalsIgnoreCase(attribute)) {
			return prop.getView();
		}
		return null;
	}
}
