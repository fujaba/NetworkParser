package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.gui.Event;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class GUIEvent extends Event {
	public static final int ESCAPE = 27;
	// The jsObject for BackupPurposes
	private ObjectCondition listerner;


	public GUIEvent withListener(ObjectCondition value) {
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
	
	public void actionPerformed(Object e) {
		if(this.listerner != null) {
			this.listerner.update(e);
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
	
	public boolean isSubEventName(String name) {
		if(name == null || this.event == null) {
			return false;
		}
		String subName = this.event.getClass().getName();
		return subName.equals(name);
	}
	
	public ObjectCondition match(Object other) {
		if(other == null) {
			return null;
		}
		if(other instanceof ObjectCondition) {
			return (ObjectCondition) other;
		}
		if(other instanceof GUIEvent) {
			GUIEvent otherEvt = (GUIEvent) other;
			if(this.eventType==EventTypes.KEYPRESS && otherEvt.getEventType() == EventTypes.KEYPRESS) {
				if(this.getCode() == otherEvt.getCode()) {
					return otherEvt.getListener();
				}
			}
		}
		return null;
	}
	
	public static GUIEvent create(Object obj) {
//			boolean isEvent = (boolean) obj.eval("this instanceof Event");
		GUIEvent event = new GUIEvent();
		if(obj == null) {
			return event;
		}
		String name = obj.getClass().getName();
		if("javafx.scene.input.KeyEvent".equals(name)) {
			// KeyEvent
			event.setValue(EVENT_TYPE, EventTypes.KEYPRESS);
			event.put(ALTKEY, ReflectionLoader.call("isAltDown", obj));
			event.put(CTRKEY, ReflectionLoader.call("isControlDown", obj));
			event.put(SHIFTKEY, ReflectionLoader.call("isShiftDown", obj));
			event.withCode((Integer)ReflectionLoader.callChain(obj, "getCode", "impl_getCode"));

			event.setValue(CURRENT_TARGET, ReflectionLoader.call("getTarget", obj));
			event.setValue(EVENT, obj);
			return event;
		}
		if("javafx.stage.WindowEvent".equals(name)) {
			event.setValue(EVENT_TYPE, EventTypes.WINDOWEVENT);
			event.setValue(CURRENT_TARGET, ReflectionLoader.call("getTarget", obj));
			
			String type = ""+ReflectionLoader.call("getEventType", obj);
			event.active = "WINDOW_CLOSE_REQUEST".equals(type) == false;
			event.setValue(EVENT, obj);
			return event;
		}
		if("java.awt.event.ActionEvent".equals(name)) {
			// KeyEvent
			event.setValue(EVENT_TYPE, EventTypes.CLICK);
			Long longValue = (Long) ReflectionLoader.call("getWhen", obj);
			event.setValue(TIME_STAMP, longValue.intValue());
			event.setValue(CURRENT_TARGET, ReflectionLoader.call("getSource", obj));
			event.setValue(ID, ""+ReflectionLoader.callChain(obj, "getSource","getLabel"));
			event.setValue(EVENT, obj);
			return event;
		}
		Object value;
		value = getMember(obj, TIME_STAMP);
		if(value != null) {
			if(value instanceof Double) {
				event.timeStamp = ((Double)value).intValue();
			} else if(value instanceof Integer) {
				event.timeStamp = (Integer)value;
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
		
		event.event = obj;
		value = getMember(obj, TYPE);
		if(value != null) {
			event.put(TYPE, ""+value);
		}
		value = getMember(obj, CURRENT_TARGET);
		if(value != null) {
			event.currentTarget = new JsonObjectLazy(value);
		}
		return event;
	}

	public ObjectCondition getListener() {
		return this.listerner;
	}
	
	public GUIEvent withCode(int value) {
		this.put(CODE, value);
		return this;
	}

	public int getCode() {
		Object object = this.get(CODE);
		if(object == null) {
			return 0;
		}
		return (Integer)object;
	}
}
