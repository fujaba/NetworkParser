package de.uniks.networkparser.gui.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class GUIEntity {
	public static final String PROPERTY_NUMBER="NUMBER";
	public static final String PROPERTY_COLOR="COLOR";
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	private int number;
	private String color;
	public int getNumber() {
		return number;
	}
	public GUIEntity withNumber(int value) {
		int oldValue = this.number;
		this.number = value;
		getPropertyChangeSupport().firePropertyChange(PROPERTY_NUMBER, oldValue, value);
		return this;
	}
	public String getColor() {
		return color;
	}
	public GUIEntity withColor(String value) {
		String oldValue = this.color;
		this.color = value;
		getPropertyChangeSupport().firePropertyChange(PROPERTY_COLOR, oldValue, value);
		return this;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
	}
}
