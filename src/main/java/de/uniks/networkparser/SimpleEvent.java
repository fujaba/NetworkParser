package de.uniks.networkparser;

import java.beans.PropertyChangeEvent;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

/**
 * Event for Changes in IdMap
 *
 * 	 typ the typ of Message: NEW UPDATE, REMOVE or SENDUPDATE
 * 	@author Stefan Lindel
 */
public final class SimpleEvent extends PropertyChangeEvent {
	private static final long serialVersionUID = 1L;
	/** Variable for Deep from Root. */
	private int depth;
	private Entity entity;
	private Object value;
	private String type;
	private Object beforeElement;

	/**
	 * Constructor for example Filter Regard or Convertable
	 * @param source	List Container
	 * @param property	Property of Event
	 * @param oldValue	Old Element
	 * @param newValue	new Element
	 */
	public SimpleEvent(Object source, String property, Object oldValue, Object newValue) {
		super(source, property, oldValue, newValue);
		this.value = newValue;
		this.type = SendableEntityCreator.NEW;
	}
	
	/**
	 * Constructor for example Filter Regard or Convertable
	 * @param type		typ of Event
	 * @param source	List Container
	 * @param property	Property of Event
	 * @param oldValue	Old Element
	 * @param newValue	new Element
	 * @param depth		depth of Element in Model structure
	 * @param modelItem	the original modelItem 
	 */
	public SimpleEvent(String type, BaseItem source, String property, Object oldValue, Object newValue, int depth, Object modelItem) {
		super(source, property, oldValue, newValue);
		this.depth = depth;
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
	 * @param value	Value of KeyValue List
	 */
	public SimpleEvent(String type, BaseItem source, String property, Object oldValue, Object newValue, Object beforeElement, Object value) {
		super(source, property, oldValue, newValue);
		this.type = type;
		this.value = value;
		this.beforeElement = beforeElement;
	}

	public int getDepth() {
		return depth;
	}

	public Entity getEntity() {
		return entity;
	}

	public Object getModelValue() {
		return value;
	}

	public SimpleEvent withModelValue(Object value) {
		this.value = value;
		return this;
	}
	public void setModelValue(Object value) {
		this.value = value;
	}

	public String getType() {
		return type;
	}
	
	public boolean isNewEvent() {
		return SendableEntityCreator.NEW.equals(this.type);
	}

	public boolean isUpdateEvent() {
		return SendableEntityCreator.UPDATE.equals(this.type);
	}
	
    public SimpleEvent with(Entity entity) {
        this.entity = entity;
        return this;
    }

	/** @return the beforeElement */
	public Object getBeforeElement() {
		return beforeElement;
	}
}
