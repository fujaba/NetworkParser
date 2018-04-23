package de.uniks.networkparser.ext.javafx;

import java.io.File;
import java.util.concurrent.CountDownLatch;

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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.gui.BridgeCommand;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.gui.JavaViewAdapter;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.XMLEntity;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class JavaAdapter implements JavaViewAdapter {
	private SimpleKeyValueList<Object, String> callBack = new SimpleKeyValueList<Object, String>();
	protected JavaBridge owner;
	protected Object webView;
	protected Object webEngine;
	private SimpleList<String> queue=new SimpleList<String>();
	protected boolean loadHTMLEntity = false;

	public JavaAdapter() {
		if(ReflectionLoader.WEBVIEW != null) {
			this.webView = ReflectionLoader.newInstance(ReflectionLoader.WEBVIEW);
			this.webEngine = ReflectionLoader.call("getEngine", this.webView);
			ReflectionLoader.call("setMaxSize", this.webView, double.class, Double.MAX_VALUE, double.class, Double.MAX_VALUE);
		}
	}

	public JavaAdapter withOwner(JavaBridge owner) {
		this.owner = owner;
		return this;
	}

	@Override
	public boolean load(Object item) {
		if(item instanceof String) {
			ReflectionLoader.call("load", webEngine, item);
			return true;
		}
		if(item instanceof File) {
			File file = ((File)item);
			if(file.exists() == false) {
				System.out.println("FILE NOT FOUND");
			}
			ReflectionLoader.call("load", webEngine, file.toURI().toString());
			return true;
		}

		if(item instanceof HTMLEntity == false) {
			return false;
		}
		HTMLEntity entity = (HTMLEntity) item;
		if(loadHTMLEntity) {
			ReflectionLoader.call("loadContent", webEngine, entity.toString());
			return true;
		}
		// Add Dummy Script
		XMLEntity headers = entity.getHeader();
		for(int i=0;i<headers.sizeChildren();i++) {
			XMLEntity child = (XMLEntity) headers.getChild(i);
			if(HTMLEntity.SCRIPT.equalsIgnoreCase(child.getTag())) {
				// Load Script from File
				Object value = child.getValue(HTMLEntity.KEY_SRC);
				if(value != null) {
					// External Script
					this._execute(readFile(""+value), false);
				} else {
					// Inline Script
					this._execute(child.getValue(), false);
				}
			}
		}

		// Call Body Script
		XMLEntity body = entity.getHeader();
		for(int i=0;i<body.sizeChildren();i++) {
			XMLEntity child = (XMLEntity) body.getChild(i);
			if(HTMLEntity.SCRIPT.equalsIgnoreCase(child.getTag())) {
				if(child.has(HTMLEntity.KEY_SRC) == false) {
					this._execute(child.getValue(), false);
				}
			}
		}
		registerListener(this);
		// Load Real Content
		ReflectionLoader.call("loadContent", this.webEngine, String.class, entity.toString());
		return true;
	}

	public boolean registerListener(ObjectCondition listener) {

		Object stateProperty = ReflectionLoader.callChain(this.webEngine, "getLoadWorker", "stateProperty");
		GUIEvent eventListener = new GUIEvent().withListener(listener);

		Object proxy = ReflectionLoader.createProxy(eventListener, ReflectionLoader.CHANGELISTENER, ReflectionLoader.EVENTHANDLER);
		ReflectionLoader.call("addListener", stateProperty, ReflectionLoader.CHANGELISTENER, proxy);
WebEngine web = (WebEngine) webEngine;
//web.setOnError(new EventHandler<WebErrorEvent>() {
//	
//	@Override
//	public void handle(WebErrorEvent event) {
//		// TODO Auto-generated method stub
//		
//	}
//});
		ReflectionLoader.call("setOnError", webEngine, ReflectionLoader.EVENTHANDLER, proxy);
		ReflectionLoader.call("setOnAlert", webEngine, ReflectionLoader.EVENTHANDLER, proxy);

		ReflectionLoader.call("setOnDragExited", webView, ReflectionLoader.EVENTHANDLER, proxy);
		ReflectionLoader.call("setOnDragOver", webView, ReflectionLoader.EVENTHANDLER, proxy);
		ReflectionLoader.call("setOnDragDropped", webView, ReflectionLoader.EVENTHANDLER, proxy);
		ReflectionLoader.call("setOnDragDone", webView, ReflectionLoader.EVENTHANDLER, proxy);
		return true;
	}


	// CallBack Functions
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
			GUIEvent event = GUIEvent.create(value);
			owner.fireEvent(event);
			return true;
		}
		return false;
	}

	public boolean changed(SimpleEvent event) {
		if(SUCCEEDED.equals(""+event.getNewValue())) {
			// FINISH
			this.loadFinish();
			return true;
		}
		return false;
	}

	public void showAlert(String value) {
		if(value != null && value.length()>0) {
			System.err.println(value);
		}
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

	/**
	 * Reads the file and returns the content of the file as a string.
	 * @param file the path of the file, that should be loaded
	 * @return the content of the file as a string
	 */
	@Override
	public String readFile(String file) {
		if(file == null) {
			return "";
		}
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
		return _execute(script, true);
	}

	/**
	 * Asynchronous execute of the script.
	 * @param script Script for executing
	 * @param convert convert Result
	 * @return return value from Javascript
	 */
	public Object executeScript(String script, boolean convert) {
		this.owner.logScript(script, NetworkParserLog.LOGLEVEL_INFO, this, "executeScript");
		if(this.queue != null) {
			// Must be cached
			this.queue.add(script);
		}
		return _execute(script, convert);
	}

	/**
	 * synchronous Execute of script
	 * @param script Script for executing
	 * @param convert convert Result
	 * @return return value from Javascript
	 */
	private Object _execute(String script, boolean convert) {
		Object jsObject = ReflectionLoader.call("executeScript", this.webEngine, String.class, script);
		if(convert && jsObject != null && ReflectionLoader.JSOBJECT.isAssignableFrom(jsObject.getClass())){
			JsonObject item = convertJSObject(jsObject);
			return item;
		}
		return jsObject;
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

	@Override
	public Object getWebEngine() {
		return webEngine;
	}


	protected void addAdapter(ObjectCondition eventListener) {
		JsonObjectLazy executeScript = (JsonObjectLazy) _execute("bridge.addAdapter(new DiagramJS.DelegateAdapter());", true);
		if(executeScript != null) {
			Object reference = executeScript.getReference();
			ReflectionLoader.call("setAdapter", reference, Object.class, eventListener);
		}
	}

	@Override
	public void loadFinish() {
		addAdapter(this);
		// REGISTER LISTENER
		if(this.queue != null) {
			while(this.queue.size() > 0 ) {
				String command = this.queue.remove(0);
				this._execute(command, false);
			}
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
		Object window = this._execute("window", false);
		if (callBackName == null) {
			callBackName = "_callBack" + (callBack.size() + 1);
			callBack.put(clazz, callBackName);
			ReflectionLoader.call("setMember", window, String.class, callBackName, Object.class, clazz);
		}
		return callBackName;
	}
	
	
	public static void execute(final Runnable runnable) {
		ReflectionLoader.call("runLater", ReflectionLoader.PLATFORM, Runnable.class, runnable);
	}
	public static void executeAndWait(final Runnable runnable) {
		if(runnable == null) {
			return;
		}
		if((Boolean) ReflectionLoader.call("isFxApplicationThread",  ReflectionLoader.PLATFORM)) {
			runnable.run();
			return;
		}
		final CountDownLatch doneLatch = new CountDownLatch(1);
		Runnable task = new Runnable() {

			@Override
			public void run() {
				try {
					runnable.run();
				} finally {
					doneLatch.countDown();
				}
			}
		};
		execute(task);
		try {
			doneLatch.await();
		} catch (InterruptedException e) {
		}
	}
	/**
	 * Enables Firebug Lite for debugging a webEngine.
	 */
	public void enableDebug() {
		// https://getfirebug.com/firebug-lite.js#startOpened
		String firebugLite="http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js";
		String script = "if (!document.getElementById('FirebugLite')) {var E = document['createElementNS'] && document.documentElement.namespaceURI;E = E ? document.createElementNS(E, 'script') : document.createElement('script');E.setAttribute('id', 'FirebugLite');E.setAttribute('src', '"+firebugLite+"');E.setAttribute('FirebugLite', '4');(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(E);}";
		executeScript(script);
		script = "console.log = function(message) { java.log(message); }"; // Now where ever console.log is called in your html you will get a log in Java console
		executeScript(script);
	}

}
