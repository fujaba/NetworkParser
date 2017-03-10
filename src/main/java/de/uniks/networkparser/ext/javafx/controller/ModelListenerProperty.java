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
import java.util.Collection;
import java.util.LinkedHashSet;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

public abstract class ModelListenerProperty<T> implements javafx.beans.property.Property<T>, PropertyChangeListener, ObservableValue<T>, InvalidationListener{
	public enum PROPERTYTYPE{STRING, COLOR, BOOLEAN, INT, LONG, FLOAT, DOUBLE};

	protected Object item;
	protected String property;
	protected SendableEntityCreator creator;
	private LinkedHashSet<ChangeListener<? super T>> listeners=new LinkedHashSet<ChangeListener<? super T>>();
	private LinkedHashSet<InvalidationListener> invalidationListeners=new LinkedHashSet<InvalidationListener>();
	protected ObservableValue<? extends T> observable = null;
	protected Condition<SimpleEvent> callBack;

	public ModelListenerProperty(SendableEntityCreator creator, Object item, String property) {
		this.creator = creator;
		this.property = property;
		this.item = item;
//		this.filter = new SimpleMapEvent(SendableEntityCreator.NEW, null, property).withModelItem(item);
		if (item instanceof SendableEntity) {
			((SendableEntity) item).addPropertyChangeListener(property, this);
			return;
		}
		if(item instanceof PropertyChangeSupport){
			((PropertyChangeSupport) item).addPropertyChangeListener(property, this);
			return;
		}
		try {
			Method method = item.getClass().getMethod("addPropertyChangeListener", String.class, java.beans.PropertyChangeListener.class );
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
			Method method = item.getClass().getMethod("addPropertyChangeListener", java.beans.PropertyChangeListener.class );
			method.invoke(item, this);
		} catch (ReflectiveOperationException e) {
		}
	}

	@Override
	public Object getBean() {
		return item;
	}

	public boolean setBean(Object value) {
		if(value != this.item) {
			this.item = value;
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return property;
	}

	@Override
	public void addListener(ChangeListener<? super T> listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(ChangeListener<? super T> listener) {
		listeners.remove(listener);
	}

	@Override
	public void addListener(InvalidationListener listener) {
		this.invalidationListeners.add(listener);
	}

	@Override
	public void removeListener(InvalidationListener listener) {
		this.invalidationListeners.remove(listener);
	}

	@Override
	public void setValue(T value) {
		creator.setValue(item, property, value, SendableEntityCreator.NEW);
	}

	@Override
	public void bind(ObservableValue<? extends T> newObservable) {
		if (newObservable == null) {
			throw new NullPointerException("Cannot bind to null");
		}
		if (!newObservable.equals(observable)) {
			unbind();
			observable = newObservable;
			observable.addListener(this);
		}
	}

	@Override
	public void bindBidirectional(Property<T> other) {
		Bindings.bindBidirectional(this, other);
	}

	@Override
	public boolean isBound() {
		 return observable != null;
	}

	@Override
	public void unbind() {
		if (observable != null) {
			observable.removeListener(this);
			observable = null;
		}
	}

	@Override
	public void unbindBidirectional(Property<T> other) {
		 Bindings.unbindBidirectional(this, other);
	}

	public Object getItemValue(){
		Object value = creator.getValue(item, property);
		if(value instanceof Collection<?>){
			return ((Collection<?>)value).size();
		}
		return value;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		for(ChangeListener<? super T> listener: listeners) {
			SimpleObjectProperty<T> objectProperty = new SimpleObjectProperty<T>();
			listener.changed(objectProperty, parseValue(evt.getOldValue()), parseValue(evt.getNewValue()));
		}
		for(InvalidationListener listener : invalidationListeners) {
			listener.invalidated(this);
		}
		executeCallBack();
	}
	public void executeCallBack() {
		if(callBack != null) {
			SimpleEvent event = new SimpleEvent(this.item, this.property, null, getItemValue());
			if(callBack.update(event)) {
				((SimpleStringProperty)observable).set(""+event.getModelValue());				
			}
		}
	}

	@SuppressWarnings("unchecked")
	public T parseValue(Object value){
		return (T)value;
	}
}
