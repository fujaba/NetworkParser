package de.uniks.networkparser.gui.controls;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.List;

import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class Control.
 *
 * @author Stefan
 */
public abstract class Control extends SimpleObject {
	
	/** The Constant PROPERTY. */
	/* Constants */
	public static final String PROPERTY = "property";
	
	/** The Constant PROPERTY_ELEMENTS. */
	public static final String PROPERTY_ELEMENTS = "elements";
	private SimpleKeyValueList<EventTypes, List<ObjectCondition>> events;

	/**
	 * Adds the event listener.
	 *
	 * @param type the type
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addEventListener(EventTypes type, ObjectCondition listener) {
		if (events == null) {
			events = new SimpleKeyValueList<EventTypes, List<ObjectCondition>>();
		}
		List<ObjectCondition> list = events.get(type);
		if (list == null) {
			list = new SimpleList<ObjectCondition>();
			list.add(listener);
			return events.add(type, list);
		}
		list.add(listener);
		return true;
	}

	/**
	 * Adds the click listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addClickListener(ObjectCondition listener) {
		return addEventListener(EventTypes.CLICK, listener);
	}

	/**
	 * Adds the double click listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addDoubleClickListener(ObjectCondition listener) {
		return addEventListener(EventTypes.DOUBLECLICK, listener);
	}

	/**
	 * Adds the mouse up listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addMouseUpListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSEUP, listener);
	}

	/**
	 * Adds the mouse down listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addMouseDownListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSEDOWN, listener);
	}

	/**
	 * Adds the mouse enter listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addMouseEnterListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSEENTER, listener);
	}

	/**
	 * Adds the mouse leave listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addMouseLeaveListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSELEAVE, listener);
	}

	/**
	 * Adds the mouse move listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addMouseMoveListener(ObjectCondition listener) {
		return addEventListener(EventTypes.MOUSEMOVE, listener);
	}

	/**
	 * Adds the key press listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addKeyPressListener(ObjectCondition listener) {
		return addEventListener(EventTypes.KEYPRESS, listener);
	}

	/**
	 * Adds the key down listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addKeyDownListener(ObjectCondition listener) {
		return addEventListener(EventTypes.KEYDOWN, listener);
	}

	/**
	 * Adds the key up listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addKeyUpListener(ObjectCondition listener) {
		return addEventListener(EventTypes.KEYUP, listener);
	}

	/**
	 * Adds the resize listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addResizeListener(ObjectCondition listener) {
		return addEventListener(EventTypes.RESIZE, listener);
	}

	/**
	 * Adds the drag start listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addDragStartListener(ObjectCondition listener) {
		return addEventListener(EventTypes.DRAGSTART, listener);
	}

	/**
	 * Adds the drag over listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addDragOverListener(ObjectCondition listener) {
		return addEventListener(EventTypes.DRAGOVER, listener);
	}

	/**
	 * Adds the drop listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addDropListener(ObjectCondition listener) {
		return addEventListener(EventTypes.DROP, listener);
	}

	/**
	 * Adds the change listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean addChangeListener(ObjectCondition listener) {
		return addEventListener(EventTypes.CHANGE, listener);
	}

	/**
	 * Gets the events.
	 *
	 * @param type the type
	 * @return the events
	 */
	public List<ObjectCondition> getEvents(EventTypes type) {
		if (this.events == null) {
			return null;
		}
		return this.events.get(type);
	}

	/**
	 * Instantiates a new control.
	 */
	public Control() {
		this.addBaseElements(PROPERTY);
	}

	/* Variables */
	protected String property;

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Sets the property.
	 *
	 * @param value the property to set
	 * @return success for setting
	 */
	public boolean setProperty(String value) {
		String oldValue = this.property;
		this.property = value;
		return firePropertyChange(PROPERTY, oldValue, value);
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(String key) {
		if (PROPERTY.equalsIgnoreCase(key)) {
			return this.property;
		}
		return super.getValue(key);
	}

	/**
	 * Sets the value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(String key, Object value) {
		key = key.trim();
		if (PROPERTY.equalsIgnoreCase(key)) {
			return this.setProperty("" + value);
		}
		return super.setValue(key, value);
	}

	/**
	 * New instance.
	 *
	 * @return the control
	 */
	public abstract Control newInstance();
}
