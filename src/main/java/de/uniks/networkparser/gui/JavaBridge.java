package de.uniks.networkparser.gui;

import java.util.List;
import java.util.Map;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;

public abstract class JavaBridge implements ObjectCondition {

	protected static final String JAVA_BRIDGE = "JavaBridge";

	protected IdMap map;
	protected SimpleKeyValueList<String, Control> controls = null;
	private boolean isApplyingChangeMSG;

	public JavaBridge() {
		this(null);
	}

	public JavaBridge(IdMap map) {
		if (map == null) {
			map = new IdMap();
		}
		this.map = map;
		map.with(this);
	}

	@Override
	public boolean update(Object event) {
		if(isApplyingChangeMSG) {
			return false;
		}
		SimpleEvent simpleEvent = (SimpleEvent) event;
		if (simpleEvent.isNewEvent() == false) {
			return true;
		}
		JsonObject jsonObject = (JsonObject) simpleEvent.getEntity();

		executeScript(BridgeCommand.load(jsonObject));

		return true;
	}
	
	public IdMap getMap() {
		return this.map;
	}

	public String addControl(Control c) {
		String key = null;
		if (this.controls != null) {
			key = this.controls.getKey(c);
		}
		if(key != null) {
			return key;
		}
		// Add Control
		JsonObject jsonObject = map.toJsonObject(c, Filter.SIMPLEFORMAT);
		Object result = executeScript(BridgeCommand.load(jsonObject));
		String id = null;
		if(result instanceof JsonObject) {
			JsonObject json = (JsonObject) result;
			id = json.getString("id");
			c.setId(id);
			c.setOwner(this);
			getControls().put(id, c);
		}
		return id;
	}

	protected Map<String, Control> getControls() {
		if (this.controls == null) {
			this.controls = new SimpleKeyValueList<String, Control>();
		}
		return this.controls;
	}

	public abstract Object executeScript(String script);

	public void addEventListener(Control c, EventTypes eventType, ObjectCondition eventListener) {
		executeScript(BridgeCommand.register(eventType, c.getId()));
		c.addEventListener(eventType, eventListener);
	}
	
	public void fireEvent(Event event) {
		Control control = getControls().get(event.getId());
		if(control != null) {
			List<ObjectCondition> events = control.getEvents(event.getEventType());
			if(events!= null) {
				for(ObjectCondition listener : events) {
					listener.update(event);
				}
			}
		}
	}
	public void fireControlChange(Control control, String property, Object value) {
		executeScript(BridgeCommand.load("{id:\""+control.getId()+"\", "+property+":\""+value+"\"}"));
	}
	
	public void fireEvent(JsonObject event) {
		
	}

	public boolean setApplyingChangeMSG(boolean value) {
		this.isApplyingChangeMSG = value;
		return this.isApplyingChangeMSG;
	}
	
	
	
//	addClickListener(String, DynamicEventCallback)
//	addDoubleClickListener(String, DynamicEventCallback)
//	addMouseUpListener(String, DynamicEventCallback)
//	addMouseDownListener(String, DynamicEventCallback)
//	addMouseEnterListener(String, DynamicEventCallback)
//	addMouseLeaveListener(String, DynamicEventCallback)
//	addMouseMoveListener(String, DynamicEventCallback)
//	addKeyPressListener(String, DynamicEventCallback)
//	addKeyDownListener(String, DynamicEventCallback)
//	addKeyUpListener(String, DynamicEventCallback)
//	addResizeListener(DynamicEventCallback)
//	addDragStartListener(String, DynamicEventCallback)
//	addDragOverListener(String, DynamicEventCallback)
//	addDropListener(String, DynamicEventCallback)
//	addChangeListener(String, DynamicEventCallback)
//	createDragDrop(String, String...)
//	createModelBinding(String, SendableEntity, String)
//	createTableColumn(String, SendableEntityCreator, String)
//	setTableItems(String, SendableEntity, String, SendableEntityCreator)
//	setTableItems(String, String, String, SendableEntityCreator)
//	setValueToUIElement(String, Object)
//	getStringValueFromUIElement(String)
//	getNumberValueFromUIElement(String)
//	getSelectedTableItem(String)
}
