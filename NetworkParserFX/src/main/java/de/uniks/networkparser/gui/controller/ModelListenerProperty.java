package de.uniks.networkparser.gui.controller;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
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
import java.util.Collection;
import java.util.LinkedHashSet;

import javafx.beans.InvalidationListener;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.gui.GenericCreator;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public abstract class ModelListenerProperty<T> implements javafx.beans.property.Property<T>, PropertyChangeListener, ObservableValue<T>, InvalidationListener{
	public enum PROPERTYTYPE{STRING, COLOR, BOOLEAN, INT, LONG, FLOAT, DOUBLE};
	
	protected Object item;
	protected String property;
    protected SendableEntityCreator creator;
    private LinkedHashSet<ChangeListener<? super T>> listeners=new LinkedHashSet<ChangeListener<? super T>>();
    private LinkedHashSet<InvalidationListener> invalidationListeners=new LinkedHashSet<InvalidationListener>();
    protected ObservableValue<? extends T> observable = null;

    public ModelListenerProperty(SendableEntityCreator creator, Object item, String property) {
        this.item = item;
        this.creator = creator;
        this.property = property;
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
		} catch (Exception e) {
		}

		try {
			Method method = item.getClass().getMethod("getPropertyChangeSupport");
			PropertyChangeSupport pc = (PropertyChangeSupport) method.invoke(item);
			pc.addPropertyChangeListener(property, this);
			return;
		} catch (Exception e) {
		}

		
		try {
			Method method = item.getClass().getMethod("addPropertyChangeListener", java.beans.PropertyChangeListener.class );
			method.invoke(item, this);
			return;
		} catch (Exception e) {
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
        creator.setValue(item, property, value, IdMapEncoder.NEW);
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
        	//objectProperty.setValue(parseValue(evt.getSource()));
        	
        	listener.changed(objectProperty, parseValue(evt.getOldValue()), parseValue(evt.getNewValue()));
        }
        for(InvalidationListener listener : invalidationListeners) {
        	listener.invalidated(this);
        }
    }
	
	@SuppressWarnings("unchecked")
	public T parseValue(Object value){
		return (T)value;
	}
	
	public static ModelListenerProperty<?> create(Node node, Object item, String field){
		GenericCreator creator = new GenericCreator(item);
		if(field == null) {
			field = node.getId();
		}
		if(node instanceof ColorPicker) {
			return createProperty(PROPERTYTYPE.COLOR, ((ColorPicker)node).valueProperty(), creator, item, field);
		}
		if(node instanceof TextField) {
			return createProperty(PROPERTYTYPE.STRING, ((TextField)node).textProperty(), creator, item, field);
		}
		if(node instanceof ComboBox) {
			return createProperty(PROPERTYTYPE.STRING, ((ComboBox<?>)node).valueProperty(), creator, item, field);
		}
		if(node instanceof CheckBox) {
			return createProperty(PROPERTYTYPE.BOOLEAN, ((CheckBox)node).selectedProperty(), creator, item, field);
		}
		if(node instanceof RadioButton) {
			return createProperty(PROPERTYTYPE.BOOLEAN, ((RadioButton)node).selectedProperty(), creator, item, field);
		}
		return null;
	}
	
	public static ModelListenerProperty<?> create(Label node, Object item, String field){
		return createProperty(PROPERTYTYPE.STRING, node.textProperty(), new GenericCreator(item), item, field);
	}
	
	public static ModelListenerProperty<?> create(Property<?> property, IdMap map, Object item, String field){
		return create(property, map.getCreatorClass(item), item, field);
	}
	public static ModelListenerProperty<?> create(Property<?> property, SendableEntityCreator creator, Object item, String field){
		if(property instanceof StringProperty){
			return createProperty(PROPERTYTYPE.STRING, property, creator, item, field);
		}
		if(property instanceof BooleanProperty){
			return createProperty(PROPERTYTYPE.BOOLEAN, property, creator, item, field);
		}
		if(property instanceof IntegerProperty){
			return createProperty(PROPERTYTYPE.INT, property, creator, item, field);
		}
		if(property instanceof LongProperty){
			return createProperty(PROPERTYTYPE.LONG, property, creator, item, field);
		}
		if(property instanceof FloatProperty){
			return createProperty(PROPERTYTYPE.FLOAT, property, creator, item, field);
		}
		if(property instanceof DoubleProperty){
			return createProperty(PROPERTYTYPE.DOUBLE, property, creator, item, field);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static ModelListenerProperty<?> createProperty(PROPERTYTYPE typ, Property<?> property, SendableEntityCreator creator, Object item, String field){
		if(PROPERTYTYPE.STRING==typ) {
			ModelListenerStringProperty listener = new ModelListenerStringProperty(creator, item, field);
			((StringProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(PROPERTYTYPE.COLOR==typ) {
			ModelListenerColorProperty listener = new ModelListenerColorProperty(creator, item, field);
			((ObjectProperty<Color>)property).bindBidirectional(listener);
			return listener;
		}
		if(PROPERTYTYPE.BOOLEAN==typ) {
			ModelListenerBooleanProperty listener = new ModelListenerBooleanProperty(creator, item, field);
			((BooleanProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(PROPERTYTYPE.INT==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			((IntegerProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(PROPERTYTYPE.LONG==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			((LongProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(PROPERTYTYPE.FLOAT==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			((FloatProperty)property).bindBidirectional(listener);
			return listener;
		}
		if(PROPERTYTYPE.DOUBLE==typ) {
			ModelListenerNumberProperty listener = new ModelListenerNumberProperty(creator, item, field);
			((DoubleProperty)property).bindBidirectional(listener);
			return listener;
		}
		return null;
	}
}