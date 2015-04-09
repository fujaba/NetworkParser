package de.uniks.networkparser.gui.javafx.controller;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;

import javafx.scene.Node;
import de.uniks.networkparser.gui.javafx.GenericCreator;
import de.uniks.networkparser.interfaces.SendableEntity;

public abstract class AbstractModelController implements PropertyChangeListener {
	@SuppressWarnings("unchecked")
	public <ST extends AbstractModelController> ST init(Object model, Node gui) {
		if(model != null && gui != null) {
			try{
				Method method = this.getClass().getMethod("initPropertyChange"+model.getClass().getSimpleName(), model.getClass(), Node.class);
				method.invoke(this, model, gui);
			}catch(Exception e){
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
			}else {
				pc.addPropertyChangeListener(property, listener);
			}
			listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return true;
		} catch (Exception e) {
		}
		try {
			Method method = item.getClass().getMethod("addPropertyChangeListener",  java.beans.PropertyChangeListener.class );
			method.invoke(item, listener);
			listener.propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return  true;
		} catch (Exception e) {
		}
		return false;
	}
}
