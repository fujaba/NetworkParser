package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;

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

	/**
	 * Constructor for example Filter Regard or Convertable
	 * @param type		typ of Event
	 * @param source	List Container
	 * @param property	Property of Event
	 * @param oldValue	Old Element
	 * @param newValue	new Element
	 */
	public SimpleEvent(String type, BaseItem source, String property, Object oldValue, Object newValue, int deep, Object modelItem) {
		super(source, property, oldValue, newValue);
		this.deep = deep;
		this.type = type;
		this.value = modelItem;
	}
	
	/**
	 * Constructor for example Filter and UpdateJson
	 * @param type		typ of Event
	 * @param entity	source Entity
	 * @param source	List Container
	 * @param property	Property of Event
	 * @param oldValue	Old Element
	 * @param newValue	new Element
	 */
	public SimpleEvent(String type, Entity entity, BaseItem source, String property, Object oldValue, Object newValue) {
		super(source, property, oldValue, newValue);
		this.entity = entity;
		this.type = type;
	}
	/**
	 * Constructor for example UpdateJson
	 * @param type		typ of Event
	 * @param entity	source Entity
	 * @param source	source PropertyChange
	 * @param map		IdMap 
	 */
	public SimpleEvent(String type, Entity entity, PropertyChangeEvent source, IdMap map) {
		super(map, source.getPropertyName(), source.getOldValue(), source.getNewValue());
		this.value = source.getSource();
		this.type = type;
		this.entity = entity;
	}

	/**
	 * Constructor for example Event of List
	 * @param type		typ of Event
	 * @param source	List Container
	 * @param property	Property of Event
	 * @param oldValue	Old Element
	 * @param newValue	new Element
	 * @param beforeElement	beforeElement
	 * @param newValue	Value of KeyValue List
	 */
	public SimpleEvent(String type, BaseItem source, String property, Object oldValue, Object newValue, Object beforeElement, Object value) {
		super(source, property, oldValue, newValue);
		this.type = type;
		this.value = value;
		this.beforeElement = beforeElement;
	}

	@Override
	public BaseItem getSource() {
		return (BaseItem) super.getSource();
	}

	public int getDeep() {
		return deep;
	}

	public Entity getEntity() {
		return entity;
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
