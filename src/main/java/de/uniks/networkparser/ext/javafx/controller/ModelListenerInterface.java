package de.uniks.networkparser.ext.javafx.controller;

import java.beans.PropertyChangeListener;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.Condition;

public interface ModelListenerInterface extends PropertyChangeListener {
	public Object getBean();
	public boolean setBean(Object value);
	public String getName();
	public void addListener(Object listener);
	public void removeListener(Object listener);
	
	public void setValue(Object value);
	public Object getValue();
//	public Object parseValue(Object value);
	
	public Object getItemValue();
	public void executeCallBack();
	public void bind(Object value);
	public void unbind();
	public boolean isBound();
	public void bindBidirectional(Object value);
	public void unbindBidirectional(Object value);
	public ModelListenerInterface withCallBack(Condition<SimpleEvent> listener);
	
	public void invalidated(Object observable);

	public Object getProxy();
}
