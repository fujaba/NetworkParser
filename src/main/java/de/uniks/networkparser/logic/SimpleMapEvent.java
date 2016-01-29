package de.uniks.networkparser.logic;

import java.beans.PropertyChangeEvent;
import de.uniks.networkparser.IdMap;

public class SimpleMapEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	/** Variable for Deep from Root. */
	private int deep;
	private IdMap map;

	public SimpleMapEvent(IdMap map) {
		super(map, null, null, null);
		this.map = map;
	}

	public SimpleMapEvent(Object source, String property) {
		super(source, property, null, null);
	}

	public SimpleMapEvent(IdMap source, String property, Object oldValue, Object newValue) {
		super(source, property, oldValue, newValue);
		this.map = source;
	}

	public SimpleMapEvent(Object source, String property, Object oldValue, Object newValue, IdMap map, int deep) {
		super(source, property, oldValue, newValue);
		this.map = map;
		this.deep = deep;
	}

	public SimpleMapEvent(PropertyChangeEvent source, IdMap map) {
		super(source.getSource(), source.getPropertyName(), source.getOldValue(), source.getNewValue());
		this.map = map;
	}

	public SimpleMapEvent with(IdMap map) {
		this.map = map;
		return this;
	}

	public SimpleMapEvent withSource(Object source) {
		this.source = source;
		return this;
	}

	public int getDeep() {
		return deep;
	}

	public SimpleMapEvent with(int deep) {
		this.deep = deep;
		return this;
	}

	public IdMap getMap() {
		return map;
	}
}
