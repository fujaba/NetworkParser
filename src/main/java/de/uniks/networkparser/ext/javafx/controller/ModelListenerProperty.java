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
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.Collection;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleSet;

public class ModelListenerProperty implements ModelListenerInterface {
	public enum PROPERTYTYPE{STRING, COLOR, BOOLEAN, INTEGER, LONG, FLOAT, DOUBLE, OBJECT};

	protected Object item;
	protected String property;
	protected SendableEntityCreator creator;
	private SimpleSet<Object> listeners=new SimpleSet<Object>();
	private SimpleSet<Object> invalidationListeners=new SimpleSet<Object>();
	protected Object observable = null;
	protected Condition<SimpleEvent> callBack;
	protected PROPERTYTYPE type;

	public ModelListenerProperty(SendableEntityCreator creator, Object item, String property, PROPERTYTYPE type) {
		this.creator = creator;
		this.property = property;
		this.item = item;
		this.type = type;
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

	public String getName() {
		return property;
	}

	public void addListener(Object listener) {
		if(ReflectionLoader.CHANGELISTENER != null) {
			if(ReflectionLoader.CHANGELISTENER.isAssignableFrom(listener.getClass())) {
				listeners.add(listener);
			}
		}
		if(ReflectionLoader.INVALIDATIONLISTENER != null) {
			if(ReflectionLoader.INVALIDATIONLISTENER.isAssignableFrom(listener.getClass())) {
				invalidationListeners.add(listener);
			}
		}
	}

	public void removeListener(Object listener) {
		if(ReflectionLoader.CHANGELISTENER != null) {
			if(ReflectionLoader.CHANGELISTENER.isAssignableFrom(listener.getClass())) {
				listeners.remove(listener);
			}
		}
		if(ReflectionLoader.INVALIDATIONLISTENER != null) {
			if(ReflectionLoader.INVALIDATIONLISTENER.isAssignableFrom(listener.getClass())) {
				invalidationListeners.remove(listener);
			}
		}
	}

	public void bind(Object newObservable) {
		if (newObservable == null) {
			throw new NullPointerException("Cannot bind to null");
		}
		if (!newObservable.equals(observable)) {
			unbind();
			observable = newObservable;
			ReflectionLoader.call("addListener", observable, ReflectionLoader.INVALIDATIONLISTENER, this);
		}
	}

	public void bindBidirectional(Object other) {
		ReflectionLoader.call("bindBidirectional", null, ReflectionLoader.PROPERTY, this,ReflectionLoader.PROPERTY, other);
	}

	public boolean isBound() {
		 return observable != null;
	}

	public void unbind() {
		if (observable != null) {
			ReflectionLoader.call("removeListener", observable, ReflectionLoader.OBSERVABLEVALUE, this);
			observable = null;
		}
	}

	public void unbindBidirectional(Object other) {
		ReflectionLoader.call("unbindBidirectional", null, ReflectionLoader.PROPERTY, this, ReflectionLoader.PROPERTY, other);
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
		for(Object listener: listeners) {
			Object event = ReflectionLoader.newInstance(ReflectionLoader.SIMPLEOBJECTPROPERTY);
			Object oldValue = parseValue(evt.getOldValue());
			Object newValue = parseValue(evt.getNewValue());
			ReflectionLoader.call("changed", listener, ReflectionLoader.OBSERVABLEVALUE, event, Object.class, oldValue, Object.class, newValue);
		}
		for(Object listener : invalidationListeners) {
			ReflectionLoader.call("invalidated", listener, ReflectionLoader.INVALIDATIONLISTENER, this);
		}
		executeCallBack();
	}

	public void executeCallBack() {
		if(callBack != null) {
			SimpleEvent event = new SimpleEvent(this.item, this.property, null, getItemValue());
			if(callBack.update(event)) {
				ReflectionLoader.call("set", observable, String.class, ""+event.getModelValue());
			}
		}
	}

	public ModelListenerProperty withCallBack(Condition<SimpleEvent> listener) {
		this.callBack = listener;
		return this;
	}

	public void invalidated(Object observable) {
	}

	public Object getValue() {
		return getItemValue();
	}

	public Object parseValue(Object value){
		if(this.type == PROPERTYTYPE.COLOR) {
			if(value != null && ReflectionLoader.COLOR.isAssignableFrom(value.getClass())) {
				return value;
			}
			if(value instanceof String) {
				return ReflectionLoader.call("web", PROPERTYTYPE.COLOR, String.class, value);
			}
			return ReflectionLoader.call("web", PROPERTYTYPE.COLOR, String.class, "#FFFFFF");
		}
		if(this.type == PROPERTYTYPE.STRING) {
			return ""+value;
		}
		if(this.type == PROPERTYTYPE.BOOLEAN) {
			if(value instanceof Boolean){
				return value;
			}
			return Boolean.valueOf(""+value);
		}
		if(this.type == PROPERTYTYPE.INTEGER) {
			if(value instanceof Integer){
				return value;
			}
			return Integer.valueOf(""+value);
		}
		if(this.type == PROPERTYTYPE.LONG) {
			if(value instanceof Long){
				return value;
			}
			return Long.valueOf(""+value);
		}
		if(this.type == PROPERTYTYPE.FLOAT) {
			if(value instanceof Float){
				return value;
			}
			return Float.valueOf(""+value);
		}
		if(this.type == PROPERTYTYPE.DOUBLE) {
			if(value instanceof Double){
				return value;
			}
			return Double.valueOf(""+value);
		}
		if(value instanceof Number){
			return value;
		}
		return value;
	}

	public Object getProxy() {
		return ReflectionLoader.createProxy(this, new Class[]{ModelListenerInterface.class, ReflectionLoader.PROPERTY});
	}

	public void setValue(Object value) {
//		if()
		creator.setValue(item, property, value, SendableEntityCreator.NEW);
	}

//FIXME	@Override
//	public void setValue(Object value) {
//		if( value instanceof Color == false) {
//			return;
//		}
//		Color color = (Color) value;
//		 int green = (int) (color.getGreen()*255);
//		 String greenString = (green<16 ? "0" : "") + Integer.toHexString(green);
//
//		 int red = (int) (color.getRed()*255);
//		 String redString = (red<16 ? "0" : "") + Integer.toHexString(red);
//
//		 int blue = (int) (color.getBlue()*255);
//		 String blueString = (blue<16 ? "0" : "") + Integer.toHexString(blue);
//
//		 String hexColor = "#"+redString+greenString+blueString;
//
//		creator.setValue(item, property, hexColor, SendableEntityCreator.NEW);
////		super.setValue(value);
//	}

}

