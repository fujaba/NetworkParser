package de.uniks.ludo.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class Dice {
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
		return true;
	}

	public boolean removePropertyChangeListener(String propertyName,PropertyChangeListener listener) {
		if (listeners != null) {
			listeners.removePropertyChangeListener(propertyName, listener);
		}
		return true;
	}
	public static final String PROPERTY_NUMBER = "number";

	private int number;

	public int getNumber() {
		return this.number;
	}

	public boolean setNumber(int value) {
		if (this.number != value) {
			int oldValue = this.number;
			this.number = value;
			firePropertyChange(PROPERTY_NUMBER, oldValue, value);
			return true;
		}
		return false;
	}

	public Dice withNumber(int value) {
		setNumber(value);
		return this;
	}


	public static final String PROPERTY_GAME = "game";

	private Ludo game = null;

	public Ludo getGame() {
		return this.game;
	}

	public boolean setGame(Ludo value) {
		if (this.game == value) {
			return false;
		}
		Ludo oldValue = this.game;
		if (this.game != null) {
			this.game = null;
			oldValue.setDice(null);
		}
		this.game = value;
		if (value != null) {
			value.withDice(this);
		}
		firePropertyChange(PROPERTY_GAME, oldValue, value);
		return true;
	}

	public Dice withGame(Ludo value) {
		this.setGame(value);
		return this;
	}

	public Ludo createGame() {
		Ludo value = new Ludo();
		withGame(value);
		return value;
	}
}