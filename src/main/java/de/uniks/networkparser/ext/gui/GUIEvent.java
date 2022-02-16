package de.uniks.networkparser.ext.gui;

import de.uniks.networkparser.SimpleEvent;
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
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.gui.Event;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.interfaces.ObjectCondition;

/**
 * The Class GUIEvent.
 *
 * @author Stefan
 */
public class GUIEvent extends Event {
	
	/** The Constant ESCAPE. */
	public static final int ESCAPE = 27;
	/* The jsObject for BackupPurposes */
	private ObjectCondition listerner;
	
	
	/**
	 * Sets the event.
	 *
	 * @param event the event
	 * @return the GUI event
	 */
	public GUIEvent setEvent(Object event) {
		this.event = event;
		return this;
	}

	/**
	 * With listener.
	 *
	 * @param value the value
	 * @return the GUI event
	 */
	public GUIEvent withListener(ObjectCondition value) {
		this.listerner = value;
		return this;
	}

	/**
	 * Handle.
	 *
	 * @param event the event
	 */
	public void handle(Object event) {
		if (this.listerner != null) {
			this.listerner.update(event);
		}
	}

	/**
	 * Invalidated.
	 *
	 * @param event the event
	 */
	public void invalidated(Object event) {
		if (this.listerner != null) {
			this.listerner.update(event);
		}
	}

	/**
	 * Changed.
	 *
	 * @param observable the observable
	 * @param oldValue the old value
	 * @param newValue the new value
	 */
	public void changed(Object observable, Object oldValue, Object newValue) {
		if (this.listerner != null) {
			this.listerner.update(new SimpleEvent(observable, "State", oldValue, newValue));
		}
	}

	private static Object getMember(Object obj, String value) {
		if (obj == null || !obj.getClass().getName().startsWith("javafx")) {
			return null;
		}
		return ReflectionLoader.call(obj, "getMember", String.class, value);
	}

	/**
	 * Checks if is sub event name.
	 *
	 * @param name the name
	 * @return true, if is sub event name
	 */
	public boolean isSubEventName(String name) {
		if (name == null || this.event == null) {
			return false;
		}
		String subName = this.event.getClass().getName();
		return subName.equals(name);
	}

	/**
	 * Match.
	 *
	 * @param other the other
	 * @return the object condition
	 */
	public ObjectCondition match(Object other) {
		if (other == null) {
			return null;
		}
		if (other instanceof ObjectCondition) {
			return (ObjectCondition) other;
		}
		if (other instanceof GUIEvent) {
			GUIEvent otherEvt = (GUIEvent) other;
			if (this.eventType == EventTypes.KEYPRESS && otherEvt.getEventType() == EventTypes.KEYPRESS) {
				if (this.getCode() == otherEvt.getCode()) {
					return otherEvt.getListener();
				}
			}
		}
		return null;
	}

	/**
	 * Creates the.
	 *
	 * @param obj the obj
	 * @return the GUI event
	 */
	public static GUIEvent create(Object obj) {
		GUIEvent event = new GUIEvent();
		if (obj == null) {
			return event;
		}
		String name = obj.getClass().getName();
		if ("javafx.scene.input.KeyEvent".equals(name)) {
			/* KeyEvent */
			event.setValue(EVENT_TYPE, EventTypes.KEYPRESS);
			event.put(ALTKEY, ReflectionLoader.call(obj, "isAltDown"));
			event.put(CTRKEY, ReflectionLoader.call(obj, "isControlDown"));
			event.put(SHIFTKEY, ReflectionLoader.call(obj, "isShiftDown"));
			if (obj != null) {
				Object value = ReflectionLoader.callChain(obj, false, "getCode", "getCode");
				if (value != null) {
					event.withCode((Integer) value);
				}

				event.setValue(CURRENT_TARGET, ReflectionLoader.call(obj, "getTarget"));
				event.setValue(EVENT, obj);
			}
			return event;
		}
		if ("javafx.stage.WindowEvent".equals(name)) {
			event.setValue(EVENT_TYPE, EventTypes.WINDOWEVENT);
			event.setValue(CURRENT_TARGET, ReflectionLoader.call(obj, "getTarget"));

			String type = "" + ReflectionLoader.call(obj, "getEventType");
			event.active = !"WINDOW_CLOSE_REQUEST".equals(type);
			event.setValue(EVENT, obj);
			return event;
		}
		event.setValue(EVENT, obj);
		if ("java.awt.event.ActionEvent".equals(name)) {
			/* KeyEvent */
			event.setValue(EVENT_TYPE, EventTypes.CLICK);
			Long longValue = (Long) ReflectionLoader.call(obj, "getWhen");
			event.setValue(TIME_STAMP, longValue.intValue());
			event.setValue(CURRENT_TARGET, ReflectionLoader.call(obj, "getSource"));
			event.setValue(ID, "" + ReflectionLoader.callChain(obj, "getSource", "getLabel"));
			return event;
		}
		Object value;
		if (!name.startsWith("javafx")) {
			return event;
		}
		value = getMember(obj, TIME_STAMP);
		if (value != null) {
			if (value instanceof Double) {
				event.timeStamp = ((Double) value).intValue();
			} else if (value instanceof Integer) {
				event.timeStamp = (Integer) value;
			}
		}
		value = getMember(obj, ID);
		if (value != null) {
			event.id = "" + value;
		}
		value = getMember(obj, EVENT_TYPE);
		if (value != null) {
			String eventName = "" + value;
			event.eventType = EventTypes.valueOf(eventName.toUpperCase());
		}

		value = getMember(obj, TYPE);
		if (value != null) {
			event.put(TYPE, "" + value);
		}
		value = getMember(obj, CURRENT_TARGET);
		if (value != null) {
			event.currentTarget = new JsonObjectLazy(value);
		}
		return event;
	}

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public ObjectCondition getListener() {
		return this.listerner;
	}

	/**
	 * With code.
	 *
	 * @param value the value
	 * @return the GUI event
	 */
	public GUIEvent withCode(int value) {
		this.put(CODE, value);
		return this;
	}

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		Object object = this.get(CODE);
		if (object == null) {
			return 0;
		}
		return (Integer) object;
	}
	

    /**
     * Window opened.
     *
     * @param event the event
     */
    public void windowOpened(Object event) {
    	if (this.listerner != null) {
    		this.listerner.update(event);
//    		this.listerner.update(new SimpleEvent(this.event, "windowOpened", event));
    	}
    }
    
    /**
     * Window activated.
     *
     * @param event the event
     */
    public void windowActivated(Object event) {
    	if (this.listerner != null) {
    		this.listerner.update(event);
//    		this.listerner.update(new SimpleEvent(this.event, "windowActivated", event));
    	}	
    }

    /**
     * Window deactivated.
     *
     * @param event the event
     */
    public void windowDeactivated(Object event) {
    	if (this.listerner != null) {
    		this.listerner.update(event);
//    		this.listerner.update(new SimpleEvent(this.event, "windowDeactivated", event));
    	}	
    }
    
    /**
     * Window closed.
     *
     * @param event the event
     */
    public void windowClosed(Object event) {
    	if (this.listerner != null) {
    		this.listerner.update(event);
//    		this.listerner.update(new SimpleEvent(this.event, "windowClosed", event));
    	}
    }

    /**
     * Window closing.
     *
     * @param event the event
     */
    public void windowClosing(Object event)
    {
    	if (this.listerner != null) {
    		this.listerner.update(event);
//    		this.listerner.update(new SimpleEvent(this.event, "windowClosing", event));
    	}
    }
    
    /**
     * Action performed.
     *
     * @param event the event
     */
    public void actionPerformed(Object event)
    {
    	if (this.listerner != null) {
    		this.listerner.update(event);
//FIXME    		this.listerner.update(new SimpleEvent(this.event, "actionPerformed", event));
    	}
//    	public void actionPerformed(Object e) {
//    		if (this.listerner != null) {
//    			this.listerner.update(e);
//    		}
//    	}
    }
}
