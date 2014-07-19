package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedHashSet;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ColorPickerList implements SendableEntityCreator {
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

	public static final String PROPERTY_COLORS="colors"; 
	private LinkedHashSet<ColorPlayer> colors=new LinkedHashSet<ColorPlayer>();
	@Override
	public String[] getProperties() {
		return new String[]{PROPERTY_COLORS};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ColorPickerList();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return ((ColorPickerList)entity).getColors();
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		((ColorPlayer)entity).withColor(""+value);
		return true;
	}

	public LinkedHashSet<ColorPlayer> getColors() {
		return colors;
	}

	public ColorPickerList withColor( ColorPlayer color) {
		this.colors.add(color);
		getPropertyChangeSupport().firePropertyChange(PROPERTY_COLORS, null, color);
		return this;
	}
}
