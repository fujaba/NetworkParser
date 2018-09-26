package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.SimpleEvent;
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
//		System.out.println("handle: "+ event);
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
			this.listerner.update(new SimpleEvent(observable, "State", oldValue,newValue));
		}
	}

	private static Object getMember(Object obj, String value) {
		if(obj == null || obj.getClass().getName().startsWith("javafx") == false) {
			return null;
		}
		return ReflectionLoader.call(obj, "getMember", String.class, value);
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
			event.put(ALTKEY, ReflectionLoader.call(obj, "isAltDown"));
			event.put(CTRKEY, ReflectionLoader.call(obj, "isControlDown"));
			event.put(SHIFTKEY, ReflectionLoader.call(obj, "isShiftDown"));
			if(obj != null) {
				Object value = ReflectionLoader.callChain(obj, false, "getCode", "getCode");
				if(value != null) {
					event.withCode((Integer)value);
				}
				
				event.setValue(CURRENT_TARGET, ReflectionLoader.call(obj, "getTarget"));
				event.setValue(EVENT, obj);
			}
			return event;
		}
		if("javafx.stage.WindowEvent".equals(name)) {
			event.setValue(EVENT_TYPE, EventTypes.WINDOWEVENT);
			event.setValue(CURRENT_TARGET, ReflectionLoader.call(obj, "getTarget"));

			String type = ""+ReflectionLoader.call(obj, "getEventType");
			event.active = "WINDOW_CLOSE_REQUEST".equals(type) == false;
			event.setValue(EVENT, obj);
			return event;
		}
		event.setValue(EVENT, obj);
		if("java.awt.event.ActionEvent".equals(name)) {
			// KeyEvent
			event.setValue(EVENT_TYPE, EventTypes.CLICK);
			Long longValue = (Long) ReflectionLoader.call(obj, "getWhen");
			event.setValue(TIME_STAMP, longValue.intValue());
			event.setValue(CURRENT_TARGET, ReflectionLoader.call(obj, "getSource"));
			event.setValue(ID, ""+ReflectionLoader.callChain(obj, "getSource", "getLabel"));
			return event;
		}
		Object value;
		if(name.startsWith("javafx") == false) {
			return event;
		}
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
