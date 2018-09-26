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
	 * @param index		is the Index of Evententity(List) or depth of Element in Model structure
	 * @param oldValue	Old Element
	 * @param newValue	New Element
	 * @param value		Value of KeyValue List or the original modelItem
	 * @param before	Value of BeforeElement of List
	 */
	public SimpleEvent(String type, BaseItem source, String property, int index, Object oldValue, Object newValue, Object value, Object before) {
		super(source, property, oldValue, newValue);
		this.type = type;
		this.depth = index;
		this.beforeElement = before;
		this.value = value;
	}
	
	/**
	 * Constructor for example Event of List
	 * @param source	List Container
	 * @param index		is the Index of EventEntity(List)
	 * @param newCollection	the new Collection
	 * @param model		the Model
	 * @param newValue	New Element
	 * @param filter	The Filter of Getter 
	 * @param before	Value of BeforeElement of List
	 */
	public static SimpleEvent create(Object source, int index, Object newCollection, Object model, Object newValue, Object filter) {
		SimpleEvent evt = new SimpleEvent(source, "createPattern", model, newValue);
		evt.depth = index;
		evt.beforeElement = newCollection;
		evt.value = filter;
		return evt;
	}

	public int getIndex() {
		return depth;
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

	public String getType() {
		return type;
	}

	public SimpleEvent withType(String value) {
		this.type = value;
		return this;
	}

	public boolean isNewEvent() {
		return SendableEntityCreator.NEW.equals(this.type);
	}

	public boolean isIdEvent() {
		return "id".equals(this.type);
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
