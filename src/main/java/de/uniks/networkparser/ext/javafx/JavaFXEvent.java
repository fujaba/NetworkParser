package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.gui.Event;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class JavaFXEvent extends Event {
	// The jsObject for BackupPurposes
	private ObjectCondition listerner;
	public Object jsObject;
	
	public JavaFXEvent withListener(ObjectCondition value) {
		this.listerner = value;
		return this;
	}
	
	public void handle(Object event) {
		if(this.listerner != null) {
			this.listerner.update(event);
		}
	}
	public void invalidated(Object event) {
		if(this.listerner != null) {
			this.listerner.update(event);
		}
	}
	
	public void changed(Object observable, Object oldValue, Object newValue) {
		if(this.listerner != null) {
			this.listerner.update(newValue);
		}
	}
	
	private static Object getMember(Object obj, String value) {
		return ReflectionLoader.call("getMember", obj, String.class, value);
	}
	
	public static JavaFXEvent create(Object obj) {
//			boolean isEvent = (boolean) obj.eval("this instanceof Event");
		JavaFXEvent event = new JavaFXEvent();
		Object value;
		
		value = getMember(obj, TIME_STAMP);
		if(value != null) {
			if(value instanceof Double) {
				event.timeStamp = ((Double)value).intValue();
			} else if(value instanceof Integer) {
				event.timeStamp = (int)value;
			}
		}
		value = getMember(obj, ID);
		if(value != null) {
			event.id = ""+value;
		}
		value = getMember(obj, EVENT_TYPE);
		if(value != null) {
			String eventName = ""+value;
			event.eventType = EventTypes.valueOf(eventName.toUpperCase());
		}
		
		event.jsObject = obj;
		value = getMember(obj, TYPE);
		if(value != null) {
			event.type = ""+value;
		}
		value = getMember(obj, CURRENT_TARGET);
		if(value != null) {
			event.currentTarget = new JsonObjectLazy(value);
		}
		return event;
	}
}
