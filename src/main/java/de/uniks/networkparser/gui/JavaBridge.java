package de.uniks.networkparser.gui;

import java.util.List;
import java.util.Map;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;

public abstract class JavaBridge implements ObjectCondition {
	public static String CONTENT_TYPE_INCLUDE = "INCLUDE";

	public static String CONTENT_TYPE_EXCLUDE = "EXCLUDE";

	protected static final String JAVA_BRIDGE = "JavaBridge";

	protected IdMap map;

	protected SimpleKeyValueList<String, Control> controls = null;

	private boolean isApplyingChangeMSG;

	private JavaViewAdapter webView;

	private NetworkParserLog logger;


	public JavaBridge() {
		this(null, null);
	}


	public JavaBridge(IdMap map, JavaViewAdapter webView) {
		if (map == null) {
			map = new IdMap();
		}
		this.map = map;
		map.add(this);

		this.webView = webView;
		this.webView.withOwner(this);

		HTMLEntity entity = init(CONTENT_TYPE_INCLUDE, "var bridge = new DiagramJS.Bridge();");
		this.webView.load(entity);
	}


	public HTMLEntity init(String type, String script) {
		//		script = "classEditor = new ClassEditor(\"board\");";
		HTMLEntity entity = new HTMLEntity();
		entity.withScript(script);

		if (CONTENT_TYPE_EXCLUDE.equals(type)) {
			entity.withHeader("./res/diagram.js");
			entity.withHeader("./res/material.css");
		}
		else {
			entity.withHeaderScript(readFile("./res/diagram.js"));
			entity.withHeaderStyle(readFile("./res/material.css"));
		}
		return entity;
	}


	protected String readFile(String file) {
		return this.webView.readFile(file);
	}


	@Override
	public boolean update(Object event) {
		if (isApplyingChangeMSG) {
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
		if (key != null) {
			return key;
		}
		// Add Control
		JsonObject jsonObject = map.toJsonObject(c, Filter.SIMPLEFORMAT);
		Object result = executeScript(BridgeCommand.load(jsonObject));
		String id = null;
		if (result instanceof JsonObject) {
			JsonObject json = (JsonObject) result;
			id = json.getString("id");
			c.setId(id);
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


	public Object executeScript(String script) {
		System.out.println(script);
		return this.webView.executeScript(script);
	}


	public void addEventListener(Control c, EventTypes eventType, ObjectCondition eventListener) {
		if(c.getEvents(eventType) != null){
			executeScript(BridgeCommand.register(eventType, c.getId()));
		}
		c.addEventListener(eventType, eventListener);
	}


	public void fireEvent(JsonObject event) {

	}


	public void fireEvent(Event event) {
		Control control = getControls().get(event.getId());
		if (control != null) {
			List<ObjectCondition> events = control.getEvents(event.getEventType());
			if (events != null) {
				for (ObjectCondition listener : events) {
					listener.update(event);
				}
			}
		}
	}


	public void fireControlChange(Control control, String property, Object value) {
		executeScript(BridgeCommand.load("{id:\"" + control.getId() + "\", " + property + ":\"" + value + "\"}"));
	}


	public boolean setApplyingChangeMSG(boolean value) {
		this.isApplyingChangeMSG = value;
		return this.isApplyingChangeMSG;
	}


	public JavaViewAdapter getViewAdapter() {
		return webView;
	}


	public Object getWebView() {
		return webView.getWebView();
	}


	public JavaBridge withWebView(JavaViewAdapter webView) {
		this.webView = webView;
		return this;
	}


	public void logScript(String msg, String level, Object owner, String method) {
		if (logger != null) {
			this.logger.log(msg, level, owner, method);
		}
	}


	/**
	 * Register a Listener on the Control, that invokes a function, that has the given name, on the given object.
	 * 
	 * @param c the control
	 * @param type the eventType
	 * @param methodName the name of the function that is invoked
	 * @param object the object on which the method is invoked
	 */
	public void addListener(Control c, EventTypes type, String methodName, Object object) {
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
