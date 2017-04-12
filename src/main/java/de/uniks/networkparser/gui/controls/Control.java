package de.uniks.networkparser.gui.controls;

import java.util.List;

import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public abstract class Control extends SimpleObject {
	/* Constants */
	public static final String PROPERTY = "property";
	private SimpleKeyValueList<EventTypes, List<ObjectCondition>> events;
	
	public boolean addEventListener(EventTypes type, ObjectCondition listener) {
		if(events == null) {
			events = new SimpleKeyValueList<EventTypes, List<ObjectCondition>>();
		}
		List<ObjectCondition> list = events.get(type);
		if(list == null) {
			list = new SimpleList<ObjectCondition>();
			list.add(listener);
			return events.add(type, list);
		}
		list.add(listener);
		return true;
	}
	
	public boolean addClickListener(ObjectCondition listener) {
		return addEventListener(EventTypes.CLICK, listener);
	}
	public boolean addDoubleClickListener(ObjectCondition listener) {
		return addEventListener(EventTypes.DOUBLECLICK, listener);
	}
	public boolean addMouseUpListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSEUP, listener);
	}
	public boolean addMouseDownListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSEDOWN, listener);
	}
	public boolean addMouseEnterListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSEENTER, listener);
	}
	public boolean addMouseLeaveListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSELEAVE, listener);
	}
	public boolean addMouseMoveListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSEMOVE, listener);
	}
	public boolean addKeyPressListener(ObjectCondition listener) {
		return addEventListener(EventTypes.KEYPRESS, listener);
	}
	public boolean addKeyDownListener(ObjectCondition listener) {
		return addEventListener(EventTypes.KEYDOWN, listener);
	}
	public boolean addKeyUpListener(ObjectCondition listener) {
		return addEventListener(EventTypes.KEYUP, listener);
	}
	public boolean addResizeListener(ObjectCondition listener) {
		return addEventListener(EventTypes.RESIZE, listener);
	}
	public boolean addDragStartListener(ObjectCondition listener) {
		return addEventListener(EventTypes.DRAGSTART, listener);
	}
	public boolean addDragOverListener(ObjectCondition listener) {
		return addEventListener(EventTypes.DRAGOVER, listener);
	}
	public boolean addDropListener(ObjectCondition listener) {
		return addEventListener(EventTypes.DROP, listener);
	}
	public boolean addChangeListener(ObjectCondition listener) {
		return addEventListener(EventTypes.CHANGE, listener);
	}
	
	public List<ObjectCondition> getEvents(EventTypes type) {
		if(this.events == null) {
			return null;
		}
		return this.events.get(type);
	}

	public Control() {
		this.addBaseElements(PROPERTY);
	}

	/* Variables */
	protected String property;


	/**
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * @param value the property to set
	 * @return success for setting
	 */
	public boolean setProperty(String value) {
		String oldValue = this.property;
		this.property = value;
		return firePropertyChange(PROPERTY, oldValue, value);
	}

	public Object getValue(String key) {
		if (PROPERTY.equalsIgnoreCase(key)) {
			return this.property;
		}
		return super.getValue(key);
	}

	@Override
	public boolean setValue(String key, Object value) {
		key = key.trim();
		if (PROPERTY.equalsIgnoreCase(key)) {
			return this.setProperty("" + value);
		}
		return super.setValue(key, value);
	}


//	public void fireEvent(String method, Object value) {
//		if (!this.events.containsKey(method)) {
//			return;
//		}
//		List<UpdateListener> list = this.eventListeners.get(method);
//		if (list == null) {
//			return;
//		}
//
//		for (UpdateListener updateListener : list) {
//			updateListener.update(value);
//		}
//	}
}
