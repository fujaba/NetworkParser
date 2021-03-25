package de.uniks.ludo.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class Meeple {
	protected PropertyChangeSupport listeners = null;

	public boolean firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		if (listeners != null) {
			listeners.firePropertyChange(propertyName, oldValue, newValue);
			return true;
		}
		return false;
	}

	public boolean addPropertyChangeListener(PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(listener);
		return true;
	}

	public boolean addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		if (listeners == null) {
			listeners = new PropertyChangeSupport(this);
		}
		listeners.addPropertyChangeListener(propertyName, listener);
		return true;
	}

	public boolean removePropertyChangeListener(PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(listener);
		}
		listeners.removePropertyChangeListener(listener);
		return true;
	}

	public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(propertyName, listener);
		}
		return true;
	}

	public static final String PROPERTY_FIELD = "field";

	private Field field = null;

	public Field getField() {
		return this.field;
	}

	public boolean setField(Field value) {
		if (this.field == value) {
			return false;
		}
		Field oldValue = this.field;
		if (this.field != null) {
			this.field = null;
			oldValue.setMeeple(null);
		}
		this.field = value;
		if (value != null) {
			value.withMeeple(this);
		}
		firePropertyChange(PROPERTY_FIELD, oldValue, value);
		return true;
	}

	public Meeple withField(Field value) {
		this.setField(value);
		return this;
	}

	public Field createField() {
		Field value = new Field();
		withField(value);
		return value;
	}

	public static final String PROPERTY_PLAYER = "player";

	private Player player = null;

	public Player getPlayer() {
		return this.player;
	}

	public boolean setPlayer(Player value) {
		if (this.player == value) {
			return false;
		}
		Player oldValue = this.player;
		if (this.player != null) {
			this.player = null;
			oldValue.withoutMeeple(this);
		}
		this.player = value;
		if (value != null) {
			value.withMeeple(this);
		}
		firePropertyChange(PROPERTY_PLAYER, oldValue, value);
		return true;
	}

	public Meeple withPlayer(Player value) {
		this.setPlayer(value);
		return this;
	}

	public Player createPlayer() {
		Player value = new Player();
		withPlayer(value);
		return value;
	}
}