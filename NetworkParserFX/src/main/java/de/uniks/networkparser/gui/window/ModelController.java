package de.uniks.networkparser.gui.window;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;

import de.uniks.networkparser.gui.GenericCreator;
import de.uniks.networkparser.interfaces.SendableEntity;

public abstract class ModelController implements PropertyChangeListener{
	public boolean addListener(Object item, String property) {
		if(item==null || property==null) {
			return false;
		}
		GenericCreator creator=new GenericCreator(item);
		if (item instanceof SendableEntity) {
			((SendableEntity) item).addPropertyChangeListener(property, this);
			propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return true;
		}
		if(item instanceof PropertyChangeSupport){
			((PropertyChangeSupport) item).addPropertyChangeListener(property, this);
			propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return  true;
		}
		try {
			Method method = item.getClass().getMethod("addPropertyChangeListener", String.class, java.beans.PropertyChangeListener.class );
			method.invoke(item, property, this);
			propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return  true;
		} catch (Exception e) {
		}

		try {
			Method method = item.getClass().getMethod("getPropertyChangeSupport");
			PropertyChangeSupport pc = (PropertyChangeSupport) method.invoke(item);
			pc.addPropertyChangeListener(property, this);
			propertyChange(new PropertyChangeEvent(item, property, null, creator.getValue(item, property)));
			return true;
		} catch (Exception e) {
		}
		return false;
	}
}
