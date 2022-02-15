package de.uniks.networkparser.gui;

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
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;

/**
 * The Class Event.
 *
 * @author Stefan
 */
public class Event extends JsonObject implements SendableEntityCreator {
	
	/** The Constant CURRENT_TARGET. */
	public static final String CURRENT_TARGET = "currentTarget";
	
	/** The Constant TIME_STAMP. */
	public static final String TIME_STAMP = "timeStamp";
	
	/** The Constant TYPE. */
	public static final String TYPE = "type";
	
	/** The Constant EVENT_TYPE. */
	public static final String EVENT_TYPE = "eventType";
	
	/** The Constant ID. */
	public static final String ID = "id";
	
	/** The Constant EVENT. */
	public static final String EVENT = "event";

	/** The Constant ALTKEY. */
	/* Optional Values */
	public static final String ALTKEY = "altKey";
	
	/** The Constant CTRKEY. */
	public static final String CTRKEY = "ctrlKey";
	
	/** The Constant SHIFTKEY. */
	public static final String SHIFTKEY = "shiftKey";
	
	/** The Constant CODE. */
	public static final String CODE = "code";

	/** The Constant BUTTON. */
	public static final String BUTTON = "button";
	
	/** The Constant BUTTONS. */
	public static final String BUTTONS = "buttons";
	
	/** The Constant CLIENTX. */
	public static final String CLIENTX = "clientX";
	
	/** The Constant CLIENTY. */
	public static final String CLIENTY = "clientY";
	
	/** The Constant LAYERX. */
	public static final String LAYERX = "layerX";
	
	/** The Constant LAYERY. */
	public static final String LAYERY = "layerY";
	
	/** The Constant MOVEMENTX. */
	public static final String MOVEMENTX = "movementX";
	
	/** The Constant MOVEMENTY. */
	public static final String MOVEMENTY = "movementY";
	
	/** The Constant OFFSETX. */
	public static final String OFFSETX = "offsetX";
	
	/** The Constant OFFSETY. */
	public static final String OFFSETY = "offsetY";
	
	/** The Constant PAGEX. */
	public static final String PAGEX = "pageX";
	
	/** The Constant PAGEY. */
	public static final String PAGEY = "pageY";
	
	/** The Constant SCREENX. */
	public static final String SCREENX = "screenX";
	
	/** The Constant SCREENY. */
	public static final String SCREENY = "screenY";
	
	/** The Constant X. */
	public static final String X = "x";
	
	/** The Constant Y. */
	public static final String Y = "x";

	protected Object currentTarget;
	protected int timeStamp;
	protected EventTypes eventType;
	protected Object event;
	protected String id;
	protected boolean active = true;
	protected static final String[] properties = { CURRENT_TARGET, TIME_STAMP, EVENT_TYPE, ID };

	/**
	 * Gets the current target.
	 *
	 * @return the current target
	 */
	public Object getCurrentTarget() {
		return currentTarget;
	}

	/**
	 * Gets the time stamp.
	 *
	 * @return the time stamp
	 */
	public int getTimeStamp() {
		return timeStamp;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return properties;
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (!(entity instanceof Event)) {
			return null;
		}
		Event e = (Event) entity;
		if (TIME_STAMP.equals(attribute)) {
			return e.getTimeStamp();
		}
		if (CURRENT_TARGET.equals(attribute)) {
			return e.getCurrentTarget();
		}
		return e.get(attribute);
	}

	/**
	 * Sets the value.
	 *
	 * @param attribute the attribute
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(String attribute, Object value) {
		return setValue(this, attribute, value, SendableEntityCreator.NEW);
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (!(entity instanceof Event)) {
			return false;
		}
		Event e = (Event) entity;
		if (TIME_STAMP.equals(attribute)) {
			e.timeStamp = Integer.parseInt("" + value);
			return true;
		}
		if (EVENT_TYPE.equals(attribute)) {
			e.eventType = (EventTypes) value;
			return true;
		}
		if (CURRENT_TARGET.equals(attribute)) {
			e.currentTarget = value;
			return true;
		}
		if (EVENT.equals(attribute)) {
			e.event = value;
			return true;
		}
		if (ID.equals(attribute)) {
			this.id = "" + value;
			return true;
		}
		return e.add(attribute, value);
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Event();
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the event type.
	 *
	 * @return the event type
	 */
	public EventTypes getEventType() {
		return eventType;
	}

	/**
	 * Gets the event.
	 *
	 * @return the event
	 */
	public Object getEvent() {
		return event;
	}

	/**
	 * Checks if is active.
	 *
	 * @return true, if is active
	 */
	public boolean isActive() {
		return active;
	}
}
