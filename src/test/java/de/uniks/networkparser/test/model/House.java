package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class House implements SendableEntityCreator, SendableEntity{
	public static final String PROPERTY_NAME = "name";
	public static final String PROPERTY_FLOOR = "floor";
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private String name;
	private int floor;
	public Object getValue(Object entity, String attribute) {
		if(PROPERTY_NAME.equals(attribute)){
			return ((House)entity).getName();
		}
		if(PROPERTY_FLOOR.equals(attribute)){
			return ((House)entity).getFloor();
		}
		return null;
	}
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(PROPERTY_NAME.equals(attribute)){
			((House)entity).setName(""+value);
			return true;
		}
		if(PROPERTY_FLOOR.equals(attribute)){
			((House)entity).setFloor((Integer)value);
			return true;
		}
		return false;
	}
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
	public String[] getProperties() {return new String[]{PROPERTY_NAME, PROPERTY_FLOOR};}
	public Object getSendableInstance(boolean prototyp) {return new House();}
	public int getFloor() {return floor;}
	public String getName() {return name;}
	public PropertyChangeSupport getPropertyChangeSupport(){return listeners;}
	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {getPropertyChangeSupport().addPropertyChangeListener(propertyName, listener);return true;}
	public boolean addPropertyChangeListener(PropertyChangeListener listener) {getPropertyChangeSupport().addPropertyChangeListener(listener);return true;}
	public boolean removePropertyChangeListener(PropertyChangeListener listener) {getPropertyChangeSupport().removePropertyChangeListener(listener);return true;}
}
