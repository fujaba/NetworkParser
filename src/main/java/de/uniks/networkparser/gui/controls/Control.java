package de.uniks.networkparser.gui.controls;

import java.util.List;

import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public abstract class Control extends SimpleObject {
	/* Constants */
	public static final String PROPERTY = "property";
	private SimpleKeyValueList<EventTypes, List<UpdateListener>> events;
	
	public boolean addEventListener(EventTypes type, UpdateListener listener) {
		if(events == null) {
			events = new SimpleKeyValueList<EventTypes, List<UpdateListener>>();
		}
		List<UpdateListener> list = events.get(type);
		if(list == null) {
			list = new SimpleList<UpdateListener>();
			list.add(listener);
			return events.add(type, list);
		}
		list.add(listener);
		return true;
	}
	
	public boolean addClickListener(UpdateListener listener) {
		return addEventListener(EventTypes.CLICK, listener);
	}
	public boolean addDoubleClickListener(UpdateListener listener) {
		return addEventListener(EventTypes.DOUBLECLICK, listener);
	}
	public boolean addMouseUpListener(UpdateListener listener) {
		return addEventListener(EventTypes.MOUSEUP, listener);
	}
	public boolean addMouseDownListener(UpdateListener listener) {
		return addEventListener(EventTypes.MOUSEDOWN, listener);
	}
	public boolean addMouseEnterListener(UpdateListener listener) {
		return addEventListener(EventTypes.MOUSEENTER, listener);
	}
	public boolean addMouseLeaveListener(UpdateListener listener) {
		return addEventListener(EventTypes.MOUSELEAVE, listener);
	}
	public boolean addMouseMoveListener(UpdateListener listener) {
		return addEventListener(EventTypes.MOUSEMOVE, listener);
	}
	public boolean addKeyPressListener(UpdateListener listener) {
		return addEventListener(EventTypes.KEYPRESS, listener);
	}
	public boolean addKeyDownListener(UpdateListener listener) {
		return addEventListener(EventTypes.KEYDOWN, listener);
	}
	public boolean addKeyUpListener(UpdateListener listener) {
		return addEventListener(EventTypes.KEYUP, listener);
	}
	public boolean addResizeListener(UpdateListener listener) {
		return addEventListener(EventTypes.RESIZE, listener);
	}
	public boolean addDragStartListener(UpdateListener listener) {
		return addEventListener(EventTypes.DRAGSTART, listener);
	}
	public boolean addDragOverListener(UpdateListener listener) {
		return addEventListener(EventTypes.DRAGOVER, listener);
	}
	public boolean addDropListener(UpdateListener listener) {
		return addEventListener(EventTypes.DROP, listener);
	}
	public boolean addChangeListener(UpdateListener listener) {
		return addEventListener(EventTypes.CHANGE, listener);
	}
	
	public List<UpdateListener> getEvents(EventTypes type) {
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
		if (this.property != value) {
			this.property = value;
			return true;
		}
		return false;
	}


	public Object getValue(String key) {
		if (PROPERTY.equalsIgnoreCase(key)) {
			return this.property;
		}
		return super.getValue(key);
	}


	@Override
	public boolean setValue(String key, Object value) {
		if (PROPERTY.equalsIgnoreCase(key)) {
			this.property = "" + value;
			return true;
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
