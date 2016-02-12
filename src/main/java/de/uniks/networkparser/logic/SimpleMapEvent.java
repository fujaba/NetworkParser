package de.uniks.networkparser.logic;

import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.Entity;

public class SimpleMapEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	/** Variable for Deep from Root. */
	private int deep;
	private IdMap map;
	private Entity entity;
	private Object modelItem;

	public SimpleMapEvent(IdMap source, String property) {
		super(source, property, null, null);
	}

	public SimpleMapEvent(IdMap source, String property, Object oldValue, Object newValue) {
		super(source, property, oldValue, newValue);
		this.map = source;
	}
	
	public SimpleMapEvent(IdMap source, Entity entity, Object newValue) {
		super(source, null, null, newValue);
		this.entity = entity;
		this.map = source;
	}

	public SimpleMapEvent(PropertyChangeEvent source, IdMap map, Entity entity) {
		super(source.getSource(), source.getPropertyName(), source.getOldValue(), source.getNewValue());
		this.map = map;
		this.entity = entity;
	}

	public SimpleMapEvent with(IdMap map) {
		this.map = map;
		return this;
	}
	
	@Override
	public IdMap getSource() {
		Object item = super.getSource();
		if(item instanceof IdMap) {
			return (IdMap) item;
		}
		return null;
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

	public Entity getEntity() {
		return entity;
	}

	public SimpleMapEvent with(Entity entity) {
		this.entity = entity;
		return this;
	}

	public Object getModelItem() {
		return modelItem;
	}

	public SimpleMapEvent withModelItem(Object modelItem) {
		this.modelItem = modelItem;
		return this;
	}
}
