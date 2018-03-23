package de.uniks.networkparser.gui;

/*
The MIT License

Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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

public class Event extends JsonObject implements SendableEntityCreator {
	public static final String CURRENT_TARGET = "currentTarget";
	public static final String TIME_STAMP = "timeStamp";
	public static final String TYPE = "type";
	public static final String EVENT_TYPE = "eventType";
	public static final String ID = "id";
	public static final String EVENT = "event";

	// Optional Values
	public static final String ALTKEY = "altKey";
	public static final String CTRKEY = "ctrlKey";
	public static final String SHIFTKEY = "shiftKey";
	public static final String CODE = "code";

	public static final String BUTTON = "button";
	public static final String BUTTONS = "buttons";
	public static final String CLIENTX = "clientX";
	public static final String CLIENTY = "clientY";
	public static final String LAYERX = "layerX";
	public static final String LAYERY = "layerY";
	public static final String MOVEMENTX = "movementX";
	public static final String MOVEMENTY = "movementY";
	public static final String OFFSETX = "offsetX";
	public static final String OFFSETY = "offsetY";
	public static final String PAGEX = "pageX";
	public static final String PAGEY = "pageY";
	public static final String SCREENX = "screenX";
	public static final String SCREENY = "screenY";
	public static final String X = "x";
	public static final String Y = "x";

	protected Object currentTarget;
	protected int timeStamp;
	protected EventTypes eventType;
	protected Object event;
	protected String id;
	protected boolean active = true;
	static protected String[] properties = {CURRENT_TARGET, TIME_STAMP, EVENT_TYPE, ID};

	public Object getCurrentTarget() {
		return currentTarget;
	}

	public int getTimeStamp() {
		return timeStamp;
	}

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if (!(entity instanceof Event)) {
			return null;
		}
		Event e = (Event) entity;
		if(TIME_STAMP.equals(attribute)) {
			return e.getTimeStamp();
		}
		if(CURRENT_TARGET.equals(attribute)) {
			return e.getCurrentTarget();
		}
		return e.get(attribute);
	}


	public boolean setValue(String attribute, Object value) {
		return setValue(this, attribute, value, SendableEntityCreator.NEW);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (!(entity instanceof Event)) {
			return false;
		}
		Event e = (Event) entity;
		if(TIME_STAMP.equals(attribute)) {
			e.timeStamp = Integer.valueOf(""+value);
			return true;
		}
		if(EVENT_TYPE.equals(attribute)) {
			e.eventType = (EventTypes) value;
			return true;
		}
		if(CURRENT_TARGET.equals(attribute)) {
			e.currentTarget = value;
			return true;
		}
		if(EVENT.equals(attribute)) {
			e.event = value;
			return true;
		}
		if(ID.equals(attribute)) {
			this.id = ""+value;
			return true;
		}
		return e.add(attribute, value);
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Event();
	}

	public String getId() {
		return id;
	}

	public EventTypes getEventType() {
		return eventType;
	}

	public Object getEvent() {
		return event;
	}

	public boolean isActive() {
		return active;
	}
}
