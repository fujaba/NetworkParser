package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import de.uniks.networkparser.interfaces.SendableEntity;

public class House implements SendableEntity{
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_FLOOR = "floor";
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private String name;
	private int floor;

	public void setFloor(int value) {
		if(value!=this.floor){
			Object oldValue = this.floor;
			this.floor = value;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_FLOOR, oldValue, value);
		}
	}
	public void setName(String value) {
		if(value!=this.name){
			Object oldValue = this.name;
			this.name = value;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_NAME, oldValue, value);
		}
	}

	public int getFloor() {return floor;}
	public String getName() {return name;}
	public PropertyChangeSupport getPropertyChangeSupport(){return listeners;}
	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);return true;}
	public boolean addPropertyChangeListener(PropertyChangeListener listener) {getPropertyChangeSupport().addPropertyChangeListener(listener);return true;}
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {getPropertyChangeSupport().removePropertyChangeListener(listener);return true;}
}
