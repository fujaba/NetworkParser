package de.uniks.networkparser.gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import org.sdmlib.serialization.IdMap;
import org.sdmlib.serialization.interfaces.SendableEntity;
import org.sdmlib.serialization.interfaces.SendableEntityCreator;


public abstract class ModelListenerProperty<T> implements javafx.beans.property.Property<T>, PropertyChangeListener, ObservableValue<T>{
    private Object item;
    private String property;
    private SendableEntityCreator creator;
    private LinkedHashSet<ChangeListener<? super T>> listeners=new LinkedHashSet<ChangeListener<? super T>>();
    private LinkedHashSet<InvalidationListener> invalidationListeners=new LinkedHashSet<InvalidationListener>();

    public ModelListenerProperty(SendableEntityCreator creator, Object item, String property) {
        this.item = item;
        this.creator = creator;
        this.property = property;
        try {
			Method method = item.getClass().getMethod("addPropertyChangeListener", String.class, java.beans.PropertyChangeListener.class );
			method.invoke(item, property, this);
		} catch (Exception e) {
			if (item instanceof SendableEntity) {
				((SendableEntity) item).addPropertyChangeListener(property, this);
			}else if(item instanceof PropertyChangeSupport){
				((PropertyChangeSupport) item).addPropertyChangeListener(property, this);
			}
		}
    }

    @Override
    public Object getBean() {
        return item;
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
	@SuppressWarnings("unchecked")
    public T getValue() {
        return (T)creator.getValue(item, property);
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
        creator.setValue(item, property, value, IdMap.NEW);
    }

    @Override
    public void bind(ObservableValue<? extends T> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void bindBidirectional(Property<T> arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isBound() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void unbind() {
        // TODO Auto-generated method stub

    }

    @Override
    public void unbindBidirectional(Property<T> arg0) {
        // TODO Auto-generated method stub

    }

	@Override
	@SuppressWarnings("unchecked")
    public void propertyChange(PropertyChangeEvent evt) {
        for(ChangeListener<? super T> listener: listeners) {
        	SimpleObjectProperty<T> objectProperty = new SimpleObjectProperty<T>();
        	objectProperty.setValue((T)evt.getSource());
        	
        	listener.changed(objectProperty, (T)evt.getOldValue(), (T)evt.getNewValue());
        }
    }
}
