package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.gui.BridgeCommand;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.gui.JavaViewAdapter;
import de.uniks.networkparser.gui.StateListener;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class JavaAdapter implements JavaViewAdapter {
	private SimpleKeyValueList<Object, String> callBack = new SimpleKeyValueList<Object, String>();
	private JavaBridge owner;
	private Object webView;
	private Object engine;
	private SimpleList<String> queue=new SimpleList<String>(); 
	
	public JavaAdapter() {
		this.webView = ReflectionLoader.newInstance(ReflectionLoader.WEBVIEW);
		this.engine = ReflectionLoader.call("getEngine", this.webView);
		ReflectionLoader.call("setMaxSize", this.webView, double.class, Double.MAX_VALUE, double.class, Double.MAX_VALUE);
	}
	
	public JavaAdapter withOwner(JavaBridge owner) {
		this.owner = owner;
		return this;
	}

	@Override
	public boolean load(HTMLEntity entity) {
		// Add Dummy Script
		XMLEntity headers = entity.getHeaders();
		for(int i=0;i<headers.sizeChildren();i++) {
			XMLEntity child = (XMLEntity) headers.getChild(i);
			if(HTMLEntity.SCRIPT.equalsIgnoreCase(child.getTag())) {
				// Load Script from File
				Object value = child.getValue(HTMLEntity.KEY_SRC);
				if(value != null) {
					// External Script
					this._execute(readFile(""+value));
				} else {
					// Inline Script
					this._execute(child.getValue());
				}
			}
		}
		
		// Call Body Script
		XMLEntity body = entity.getHeaders();
		for(int i=0;i<body.sizeChildren();i++) {
			XMLEntity child = (XMLEntity) body.getChild(i);
			if(HTMLEntity.SCRIPT.equalsIgnoreCase(child.getTag())) {
				if(child.has(HTMLEntity.KEY_SRC) == false) {
					this._execute(child.getValue());
				}
			}
		}
		
		Object loader = ReflectionLoader.call("getLoadWorker", this.engine);
		Object stateProperty = ReflectionLoader.call("stateProperty", loader);
		
		Object changeProxy = ReflectionLoader.createProxy(new StateListener(this), ReflectionLoader.CHANGELISTENER);
		
		ReflectionLoader.call("addListener", stateProperty, ReflectionLoader.CHANGELISTENER, changeProxy);

		// Load Real Content
		ReflectionLoader.call("loadContent", this.engine, String.class, entity.toString());
		
//FIXME		
//		((WebEngine)engine).setOnAlert(t -> {
//			System.out.println(t.getData());
//		});
//
//		((WebEngine)engine).setOnError(e -> {
//			System.err.println(e);
//		});
		
		return true;
	}
	
	public boolean executeChange(String value) {
		owner.setApplyingChangeMSG(true);
		JsonObject json = JsonObject.create(value);
		IdMap map = owner.getMap();
		Object encode = map.decode(json);
		if (encode == null) {
			SimpleObject newItem = SimpleObject.create(json);
			map.put(newItem.getId(), newItem, false);
		}
		owner.setApplyingChangeMSG(false);
		return true;
	}

	@Override
	public boolean update(Object value) {
		if(value == null) {
			return false;
		}
		if(value instanceof String) {
			JsonObject data = new JsonObject().withValue(""+value);
			owner.fireEvent(data);
			return true;
		}
		if(ReflectionLoader.JSOBJECT.isAssignableFrom(value.getClass())) {
			JavaFXEvent event = JavaFXEvent.create(value);
			owner.fireEvent(event);
			return true;
		}
		return false;
	}
	
	/**
	 * Reads the file and returns the content of the file as a string.
	 * @param file the path of the file, that should be loaded
	 * @return the content of the file as a string
	 */
	@Override
	public String readFile(String file) {
		FileBuffer buffer = new FileBuffer().withFile(file);
		return buffer.toString();
	}

	/**
	 * Asynchronous execute of the script.
	 * @param script Script for executing
	 * @return return value from Javascript
	 */
	@Override
	public Object executeScript(String script) {
		this.owner.logScript(script, NetworkParserLog.LOGLEVEL_INFO, this, "executeScript");
		if(this.queue != null) {
			// Must be cached
			this.queue.add(script);
		}
		return _execute(script);
	}
	
	/**
	 * synchronous Execute of script
	 * @param script Script for executing
	 * @return return value from Javascript
	 */
	private Object _execute(String script) {
		System.out.println(script);
		Object jsObject = ReflectionLoader.call("executeScript", this.engine, String.class, script);
		if(jsObject != null && ReflectionLoader.JSOBJECT.isAssignableFrom(jsObject.getClass())){
			JsonObject item = convertJSObject(jsObject);
			return item;
		}
		else return jsObject;
	}

	/**
	 * Converts a JSObject to a JsonObject Lazy and forces a load of the LazyJsonObject.
	 * @param element Element to Convert
	 * @return JsonObjectLazy return new JsonObjectLazy
	 */
	private JsonObject convertJSObject(Object element) {
		JsonObjectLazy result = new JsonObjectLazy(element);
		result.lazyLoad();
		return result;
	}

	@Override
	public Object getWebView() {
		return webView;
	}
	
	
	protected void addAdapter(ObjectCondition eventListener) {
		JsonObjectLazy executeScript = (JsonObjectLazy) _execute("bridge.addAdapter(new DiagramJS.DelegateAdapter());");
		Object reference = executeScript.getReference();
//		String callBackName = getCallBackName(eventListener)+".update";
//		reference.setMember("callBackfunction", callBackName);
		ReflectionLoader.call("setAdapter", reference, Object.class, eventListener);
//		reference.call("setAdapter", eventListener);
	}

	@Override
	public void loadFinish() {
		addAdapter(this);
		// REGISTER LISTENER
//	}
		while(this.queue.size() > 0 ) {
			String command = this.queue.remove(0);
			this._execute(command);
		}
		this.queue = null; // Disable QUEUE
	}

	public boolean addListener(Control control, EventTypes type, String functionName, Object callBackClazz) {
		this.owner.addControl(control);
		String id = control.getId();

		if (callBackClazz != null) {
			String callBackName = getCallBackName(callBackClazz);
			executeScript(BridgeCommand.register(type, id, callBackName + "." + functionName));
			return true;
		}
		executeScript("bridge.registerListener(" + type + ", \"" + id + "\");");
		return true;
	}

	/**
	 * 
	 * @param clazz Class for CallBack
	 * @return return JavascriptCallbackname
	 */
	public String getCallBackName(Object clazz) {
		String callBackName = callBack.get(clazz);
		Object window = this._execute("window");
		if (callBackName == null) {
			callBackName = "_callBack" + (callBack.size() + 1);
			callBack.put(clazz, callBackName);
			ReflectionLoader.call("setMember", window, String.class, callBackName, Object.class, clazz);
			System.out.println("regiter: " + clazz);
		}
		return callBackName;
	}
}
