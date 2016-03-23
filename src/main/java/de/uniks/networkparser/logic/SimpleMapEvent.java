package de.uniks.networkparser.logic;

import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.Entity;

/**
 * Event for Changes in IdMap
 *
 * 	 typ the typ of Message: NEW UPDATE, REMOVE or SENDUPDATE
 */
public final class SimpleMapEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	/** Variable for Deep from Root. */
	private int deep;
	private Entity entity;
	private Object modelItem;
	private String type;

	public SimpleMapEvent(String type, IdMap source, String property) {
		super(source, property, null, null);
		this.type = type;
	}

	public SimpleMapEvent(String type, IdMap source, String property, Object oldValue, Object newValue) {
		super(source, property, oldValue, newValue);
		this.type = type;
	}
	
	public SimpleMapEvent(String type, IdMap source, Entity entity, Object newValue) {
		super(source, null, null, newValue);
		this.entity = entity;
		this.type = type;
	}

	public SimpleMapEvent(PropertyChangeEvent source, String type, IdMap map, Entity entity) {
		super(map, source.getPropertyName(), source.getOldValue(), source.getNewValue());
		this.modelItem = source.getSource();
		this.type = type;
		this.entity = entity;
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
	
	public String getType() {
		return type;
	}

	public SimpleMapEvent with(String type) {
		this.type = type;
		return this;
	}
}
