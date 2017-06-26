package de.uniks.networkparser.gui;

import java.util.List;
import java.util.Map;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public abstract class JavaBridge implements ObjectCondition {
	public static String CONTENT_TYPE_INCLUDE = "INCLUDE";

	public static String CONTENT_TYPE_EXCLUDE = "EXCLUDE";

	protected static final String JAVA_BRIDGE = "JavaBridge";

	protected IdMap map;

	protected SimpleKeyValueList<String, Control> controls = null;

	private boolean isApplyingChangeMSG;

	private JavaViewAdapter webView;

	private NetworkParserLog logger;

	private HTMLEntity entity;
	
	private XMLEntity debug;


	public JavaBridge() {
		this(null, null, CONTENT_TYPE_INCLUDE);
	}

	public JavaBridge withDebug(boolean value) {
		if(value) {
			this.debug = entity.createScript("");
		}else {
			this.debug = null;
		}
		return this;
	}
	

	public JavaBridge(IdMap map, JavaViewAdapter webView, String type) {
		if (map == null) {
			map = new IdMap();
		}
		this.map = map;
		map.add(this);
		if((CONTENT_TYPE_INCLUDE.equalsIgnoreCase(type) || CONTENT_TYPE_EXCLUDE.equalsIgnoreCase(type)) == false) {
			type = CONTENT_TYPE_INCLUDE;
		}

		
		this.webView = webView;
		if(webView != null) { 
			this.webView.withOwner(this);
			
		}
		entity = init(type, "var bridge = new DiagramJS.Bridge();");
		if(webView != null) {
			this.webView.load(entity);
		}
	}
	
	public HTMLEntity getEntity() {
		return entity;
	}

	public JavaBridge withLogger(NetworkParserLog logger) {
		this.logger = logger;
		return this;
	}

	public HTMLEntity init(String type, String script) {
		//		script = "classEditor = new ClassEditor(\"board\");";
		HTMLEntity entity = new HTMLEntity();
		entity.withScript(script);

		if (CONTENT_TYPE_EXCLUDE.equals(type)) {
			entity.withHeader("./res/diagram.js");
			entity.withHeader("./res/material.css");
			entity.withHeader("./res/style.css");
		}
		else {
			entity.withHeaderScript(readFile("./res/diagram.js"));
			entity.withHeaderStyle(readFile("./res/material.css"));
			entity.withHeaderStyle(readFile("./res/style.css"));
			
		}
		return entity;
	}


	protected String readFile(String file) {
		if(this.webView != null) {
			return this.webView.readFile(file);
		}
		return null;
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
		
		if(jsonObject == null){
			return false;
		}

		executeScript(BridgeCommand.load(jsonObject));

		return true;
	}


	public IdMap getMap() {
		return this.map;
	}

	public String put(SimpleObject so) {
		map.getMapListener().suspendNotification();
		JsonObject jsonObject = map.toJsonObject(so);
		map.getMapListener().resetNotification();
		Object result = executeScript(BridgeCommand.load(jsonObject));
		String id = null;
		if (result instanceof JsonObject) {
			JsonObject json = (JsonObject) result;
			id = json.getString("id");
			so.setId(id);
		}
		return id;
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
		map.getMapListener().suspendNotification();
		JsonObject jsonObject = map.toJsonObject(c, Filter.SIMPLEFORMAT);
		map.getMapListener().resetNotification();
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
		if(script == null) {
			return null;
		}
		if(debug != null) {
			String value = debug.getValue();
			if(value.length()>0) {
				value = value+BaseItem.CRLF+script;
			}else {
				value = script;
			}
			debug.withValueItem(value);
		}
		return this.webView.executeScript(script);
	}


	public void addEventListener(Control c, EventTypes eventType, ObjectCondition eventListener) {
		if(c.getEvents(eventType) == null){
			executeScript(BridgeCommand.register(eventType, c.getId()));
		}
		c.addEventListener(eventType, eventListener);
	}


	public void fireEvent(JsonObject event) {
		this.map.decode(event);
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


	public void logScript(String msg, int level, Object owner, String method) {
		if (logger != null) {
			this.logger.log(owner, method, msg, level);
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
