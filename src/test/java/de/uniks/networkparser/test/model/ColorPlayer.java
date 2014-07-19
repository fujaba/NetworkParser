package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ColorPlayer implements SendableEntityCreator {
	protected final PropertyChangeSupport listeners = new PropertyChangeSupport(
			this);

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String property,
			PropertyChangeListener listener) {
		getPropertyChangeSupport()
				.addPropertyChangeListener(property, listener);
	}

	public void removePropertyChangeListener(String property,
			PropertyChangeListener listener) {
		getPropertyChangeSupport().removePropertyChangeListener(property,
				listener);
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
	}

	public static final String PROPERTY_COLOR="color"; 
	private String color;
	@Override
	public String[] getProperties() {
		return new String[]{PROPERTY_COLOR};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ColorPlayer();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((ColorPlayer)entity).getColor();
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		((ColorPlayer)entity).withColor(""+value);
		return true;
	}

	public String getColor() {
		return color;
	}

	public ColorPlayer withColor(String color) {
		if(color!=this.color){
			System.out.println(color);
			String oldValue = this.color;
			this.color = color;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_COLOR, oldValue, color);
		}
		return this;
	}
}
