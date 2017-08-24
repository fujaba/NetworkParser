package de.uniks.networkparser.gui;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;

public class Event extends JsonObject implements SendableEntityCreator {

	public static final String CURRENT_TARGET = "currentTarget";
	public static final String TIME_STAMP = "timeStamp";
	public static final String TYPE = "type";
	public static final String EVENT_TYPE = "eventType";
	public static final String ID = "id";
	
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
	protected String type;
	protected EventTypes eventType;
	protected String id;

	static protected String[] properties = {TYPE, CURRENT_TARGET, TIME_STAMP, EVENT_TYPE, ID};

	public Object getCurrentTarget() {
		return currentTarget;
	}

	public int getTimeStamp() {
		return timeStamp;
	}
	
	public String getType() {
		return type;
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
		if(TYPE.equals(attribute)) {
			return e.getType();
		}
		if(TIME_STAMP.equals(attribute)) {
			return e.getTimeStamp();
		}
		if(CURRENT_TARGET.equals(attribute)) {
			return e.getCurrentTarget();
		}
		return e.get(attribute);
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if (!(entity instanceof Event)) {
			return false;
		}
		Event e = (Event) entity;
		if(TYPE.equals(attribute)) {
			e.type = ""+value;
			return true;
		}
		if(TIME_STAMP.equals(attribute)) {
			e.timeStamp = Integer.valueOf(""+value);
			return true;
		}
		if(CURRENT_TARGET.equals(attribute)) {
			e.currentTarget = value;
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
}
