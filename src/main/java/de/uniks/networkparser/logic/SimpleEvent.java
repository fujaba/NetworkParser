package de.uniks.networkparser.logic;

import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;

/**
 * Event for Changes in IdMap
 *
 * 	 typ the typ of Message: NEW UPDATE, REMOVE or SENDUPDATE
 * 	@author Stefan Lindel
 */
public final class SimpleEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	/** Variable for Deep from Root. */
	private int deep;
	private Entity entity;
	private Object value;
	private String type;
	private Object beforeElement;

	public SimpleEvent(String type, BaseItem source, String property) {
		super(source, property, null, null);
		this.type = type;
	}
	
	public SimpleEvent(String type, BaseItem source, String property, Object oldValue, Object newValue, Object beforeElement, Object value) {
		super(source, property, oldValue, newValue);
		this.type = type;
		this.value = value;
		this.beforeElement = beforeElement;
	}

	public SimpleEvent(String type, BaseItem source, String property, Object oldValue, Object newValue) {
		super(source, property, oldValue, newValue);
		this.type = type;
	}

	public SimpleEvent(String type, BaseItem source, Entity entity, Object newValue) {
		super(source, null, null, newValue);
		this.entity = entity;
		this.type = type;
	}

	public SimpleEvent(PropertyChangeEvent source, String type, IdMap map, Entity entity) {
		super(map, source.getPropertyName(), source.getOldValue(), source.getNewValue());
		this.value = source.getSource();
		this.type = type;
		this.entity = entity;
	}

	@Override
	public BaseItem getSource() {
		return (BaseItem) super.getSource();
	}

	public int getDeep() {
		return deep;
	}

	public SimpleEvent with(int deep) {
		this.deep = deep;
		return this;
	}

	public Entity getEntity() {
		return entity;
	}

	public SimpleEvent with(Entity entity) {
		this.entity = entity;
		return this;
	}

	public Object getModelItem() {
		return value;
	}

	public SimpleEvent withModelItem(Object modelItem) {
		this.value = modelItem;
		return this;
	}

	public String getType() {
		return type;
	}

	public SimpleEvent with(String type) {
		this.type = type;
		return this;
	}
	
	public boolean isNewEvent() {
		return IdMap.NEW.equals(this.type);
	}
	
	public boolean isUpdateEvent() {
		return IdMap.UPDATE.equals(this.type);
	}

	/** @return the beforeElement */
	public Object getBeforeElement() {
		return beforeElement;
	}
}
