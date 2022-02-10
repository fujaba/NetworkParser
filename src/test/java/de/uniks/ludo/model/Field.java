package de.uniks.ludo.model;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;


public class Field {
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

	public static final String PROPERTY_PREV = "prev";

	private Field prev = null;

	public Field getPrev() {
		return this.prev;
	}

	public boolean setPrev(Field value) {
		if (this.prev == value) {
			return false;
		}
		Field oldValue = this.prev;
		if (this.prev != null) {
			this.prev = null;
			oldValue.setNext(null);
		}
		this.prev = value;
		if (value != null) {
			value.withNext(this);
		}
		firePropertyChange(PROPERTY_PREV, oldValue, value);
		return true;
	}

	public Field withPrev(Field value) {
		this.setPrev(value);
		return this;
	}

	public Field createPrev() {
		Field value = new Field();
		withPrev(value);
		return value;
	}

	public static final String PROPERTY_NEXT = "next";

	private Field next = null;

	public Field getNext() {
		return this.next;
	}

	public boolean setNext(Field value) {
		if (this.next == value) {
			return false;
		}
		Field oldValue = this.next;
		if (this.next != null) {
			this.next = null;
			oldValue.setPrev(null);
		}
		this.next = value;
		if (value != null) {
			value.withPrev(this);
		}
		firePropertyChange(PROPERTY_NEXT, oldValue, value);
		return true;
	}

	public Field withNext(Field value) {
		this.setNext(value);
		return this;
	}

	public Field createNext() {
		Field value = new Field();
		withNext(value);
		return value;
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
			oldValue.withoutField(this);
		}
		this.game = value;
		if (value != null) {
			value.withField(this);
		}
		firePropertyChange(PROPERTY_GAME, oldValue, value);
		return true;
	}

	public Field withGame(Ludo value) {
		this.setGame(value);
		return this;
	}

	public Ludo createGame() {
		Ludo value = new Ludo();
		withGame(value);
		return value;
	}

	public static final String PROPERTY_MEEPLE = "meeple";

	private Meeple meeple = null;

	public Meeple getMeeple() {
		return this.meeple;
	}

	public boolean setMeeple(Meeple value) {
		if (this.meeple == value) {
			return false;
		}
		Meeple oldValue = this.meeple;
		if (this.meeple != null) {
			this.meeple = null;
			oldValue.setField(null);
		}
		this.meeple = value;
		if (value != null) {
			value.withField(this);
		}
		firePropertyChange(PROPERTY_MEEPLE, oldValue, value);
		return true;
	}

	public Field withMeeple(Meeple value) {
		this.setMeeple(value);
		return this;
	}

	public Meeple createMeeple() {
		Meeple value = new Meeple();
		withMeeple(value);
		return value;
	}
}