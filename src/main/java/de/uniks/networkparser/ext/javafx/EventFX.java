package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.gui.Event;
import de.uniks.networkparser.gui.EventTypes;
import netscape.javascript.JSObject;

public class EventFX extends Event {
	// The jsObject for BackupPurposes
	public JSObject jsObject;

	public static EventFX create(JSObject obj) {
//		boolean isEvent = (boolean) obj.eval("this instanceof Event");
		EventFX event = new EventFX();
		Object value;
		
		value = obj.getMember(TIME_STAMP);
		if(value != null) {
			if(value instanceof Double) {
				event.timeStamp = ((Double)value).intValue();
			} else if(value instanceof Integer) {
				event.timeStamp = (int)value;
			}
		}
		value = obj.getMember(ID);
		if(value != null) {
			event.id = ""+value;
		}
		value = obj.getMember(EVENT_TYPE);
		if(value != null) {
			String eventName = ""+value;
			event.eventType = EventTypes.valueOf(eventName.toUpperCase());
		}
		
		event.jsObject = obj;
		value = obj.getMember(TYPE);
		if(value != null) {
			event.type = ""+value;
		}
		value = obj.getMember(CURRENT_TARGET);
		if(value != null) {
			event.currentTarget = new JsonObjectLazy(value);
		}
		return event;
	}
}
