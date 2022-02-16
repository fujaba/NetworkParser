package de.uniks.networkparser.ext.gui;

import java.io.File;
import java.util.concurrent.CountDownLatch;

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
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.ext.JSEditor;
import de.uniks.networkparser.ext.Os;
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

/**
 * The Class JavaAdapter.
 *
 * @author Stefan
 */
public class JavaAdapter implements JavaViewAdapter, Runnable {
	private SimpleKeyValueList<Object, String> callBack = new SimpleKeyValueList<Object, String>();
	protected JavaBridge owner;
	protected Object webView;
	protected Object webEngine;
	private SimpleList<String> queue = new SimpleList<String>();
	
	/** The Constant TYPE_EXPORT. */
	public static final String TYPE_EXPORT = "EXPORT";
	
	/** The Constant TYPE_EDITOR. */
	public static final String TYPE_EDITOR = "EDITOR";
	
	/** The Constant TYPE_EXPORTALL. */
	public static final String TYPE_EXPORTALL = "EXPORTALL";
	
	/** The Constant TYPE_CONTENT. */
	public static final String TYPE_CONTENT = "CONTENT";
	
	/** The Constant TYPE_EDOBS. */
	public static final String TYPE_EDOBS = "EDOBS";
	protected String type = TYPE_EXPORT;
	private HTMLEntity entity;
	private CountDownLatch doneLatch;
	private Runnable newTask;
	private NetworkParserLog logger;

	/**
	 * With owner.
	 *
	 * @param owner the owner
	 * @return the java adapter
	 */
	public JavaAdapter withOwner(JavaBridge owner) {
		this.owner = owner;
		return this;
	}

	/**
	 * Load.
	 *
	 * @param item the item
	 * @return true, if successful
	 */
	@Override
	public boolean load(Object item) {
		if (item instanceof String) {
			ReflectionLoader.call(webEngine, "load", item);
			return true;
		}
		if (item instanceof File) {
			File file = ((File) item);
			if (!file.exists() && logger != null) {
				logger.error(this, "load", "FILE NOT FOUND");
			}
			ReflectionLoader.call(webEngine, "load", file.toURI().toString());
			return true;
		}

		if (!(item instanceof HTMLEntity)) {
			return false;
		}
		HTMLEntity entity = (HTMLEntity) item;
		if (TYPE_CONTENT.equalsIgnoreCase(type)) {
			ReflectionLoader.call(webEngine, "loadContent", entity.toString());
			return true;
		}
		/* Add Dummy Script */
		XMLEntity headers = entity.getHeader();
		for (int i = 0; i < headers.sizeChildren(); i++) {
			XMLEntity child = (XMLEntity) headers.getChild(i);
			if (HTMLEntity.SCRIPT.equalsIgnoreCase(child.getTag())) {
				/* Load Script from File */
				Object value = child.getValue(HTMLEntity.KEY_SRC);
				if (value != null) {
					/* External Script */
					this._execute(readFile("" + value), false);
				} else {
					/* Inline Script */
					this._execute(child.getValue(), false);
				}
			}
		}

		/* Call Body Script */
		XMLEntity body = entity.getHeader();
		for (int i = 0; i < body.sizeChildren(); i++) {
			XMLEntity child = (XMLEntity) body.getChild(i);
			if (HTMLEntity.SCRIPT.equalsIgnoreCase(child.getTag()) && !child.has(HTMLEntity.KEY_SRC)) {
				this._execute(child.getValue(), false);
			}
		}
		Object engine = getWebEngine();
		if (engine == null) {
			this.entity = entity;
			try {
				ReflectionLoader.call(ReflectionLoader.PLATFORM, "startup", Runnable.class, this);
			} catch (Throwable e) {
				JavaAdapter.execute(this);
			}
			return true;
		}
		registerListener(this);
		/* Load Real Content */
		ReflectionLoader.call(this.webEngine, "loadContent", String.class, entity.toString());
		return true;
	}

	/**
	 * Run.
	 */
	@Override
	public void run() {
		if (doneLatch != null && newTask != null) {
			try {
				Runnable task = newTask;
				newTask = null;
				task.run();
			} finally {
				doneLatch.countDown();
			}
		}
		registerListener(this);
		/* Load Real Content */
		if (this.webEngine != null && entity != null) {
			ReflectionLoader.call(this.webEngine, "loadContent", String.class, entity.toString());
		}
	}

	/**
	 * Register listener.
	 *
	 * @param listener the listener
	 * @return true, if successful
	 */
	public boolean registerListener(ObjectCondition listener) {
		Object engine = getWebEngine();
		if (engine != null) {
			Object stateProperty = ReflectionLoader.callChain(this.webEngine, "getLoadWorker", "stateProperty");
			GUIEvent eventListener = new GUIEvent().withListener(listener);

			Object proxy = ReflectionLoader.createProxy(eventListener, ReflectionLoader.CHANGELISTENER,
					ReflectionLoader.EVENTHANDLER);
			ReflectionLoader.call(stateProperty, "addListener", ReflectionLoader.CHANGELISTENER, proxy);
			ReflectionLoader.call(webEngine, "setOnError", ReflectionLoader.EVENTHANDLER, proxy);
			ReflectionLoader.call(webEngine, "setOnAlert", ReflectionLoader.EVENTHANDLER, proxy);

			ReflectionLoader.call(webView, "setOnDragExited", ReflectionLoader.EVENTHANDLER, proxy);
			ReflectionLoader.call(webView, "setOnDragOver", ReflectionLoader.EVENTHANDLER, proxy);
			ReflectionLoader.call(webView, "setOnDragDropped", ReflectionLoader.EVENTHANDLER, proxy);
			ReflectionLoader.call(webView, "setOnDragDone", ReflectionLoader.EVENTHANDLER, proxy);
		}
		return true;
	}

	/**
	 * Update.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	/* CallBack Functions */
	@Override
	public boolean update(Object value) {
		if (value == null) {
			return false;
		}
		if (value instanceof String) {
			JsonObject data = new JsonObject().withValue("" + value);
			owner.fireEvent(data);
			return true;
		}
		if (ReflectionLoader.JSOBJECT.isAssignableFrom(value.getClass())) {
			GUIEvent event = GUIEvent.create(value);
			owner.fireEvent(event);
			return true;
		}
		return false;
	}

	/**
	 * Changed.
	 *
	 * @param event the event
	 * @return true, if successful
	 */
	public boolean changed(SimpleEvent event) {
		if (event != null) {
			if (SUCCEEDED.equals("" + event.getNewValue())) {
				/* FINISH */
				this.loadFinish();
				return true;
			}
		}
		return false;
	}

	/**
	 * Show alert.
	 *
	 * @param value the value
	 */
	public void showAlert(String value) {
		if (value != null && value.length() > 0) {
			if (logger != null) {
				logger.error(this, "showAlert", value);
			}
		}
	}

	/**
	 * Execute change.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
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
	 * 
	 * @param file the path of the file, that should be loaded
	 * @return the content of the file as a string
	 */
	@Override
	public String readFile(String file) {
		if (file == null) {
			return "";
		}
		FileBuffer buffer = new FileBuffer().withFile(file);
		return buffer.toString().trim();
	}

	/**
	 * Asynchronous execute of the script.
	 * 
	 * @param script Script for executing
	 * @return return value from Javascript
	 */
	@Override
	public Object executeScript(String script) {
		this.owner.logScript(script, NetworkParserLog.LOGLEVEL_INFO, this, "executeScript");
		if (this.queue != null) {
			/* Must be cached */
			this.queue.add(script);
		}
		if (!Os.isFXThread()) {
			JavaAdapter.execute(new JSEditor(this).withScript(script));
			return null;
		}
		return _execute(script, true);
	}

	/**
	 * Asynchronous execute of the script.
	 * 
	 * @param script  Script for executing
	 * @param convert convert Result
	 * @return return value from Javascript
	 */
	public Object executeScript(String script, boolean convert) {
		if (this.owner != null) {
			this.owner.logScript(script, NetworkParserLog.LOGLEVEL_INFO, this, "executeScript");
		}
		if (this.queue != null) {
			/* Must be cached */
			this.queue.add(script);
		}
		return _execute(script, convert);
	}

	/**
	 * synchronous Execute of script
	 * 
	 * @param script  Script for executing
	 * @param convert convert Result
	 * @return return value from Javascript
	 */
	private Object _execute(String script, boolean convert) {
		Object jsObject = ReflectionLoader.call(getWebEngine(), "executeScript", String.class, script);
		if (convert && jsObject != null && ReflectionLoader.JSOBJECT.isAssignableFrom(jsObject.getClass())) {
			JsonObject item = convertJSObject(jsObject);
			return item;
		}
		return jsObject;
	}

	/**
	 * Converts a JSObject to a JsonObject Lazy and forces a load of the
	 * LazyJsonObject.
	 * 
	 * @param element Element to Convert
	 * @return JsonObjectLazy return new JsonObjectLazy
	 */
	private JsonObject convertJSObject(Object element) {
		JsonObjectLazy result = new JsonObjectLazy(element);
		result.lazyLoad();
		return result;
	}

	/**
	 * Gets the web view.
	 *
	 * @return the web view
	 */
	@Override
	public Object getWebView() {
		if (webView == null) {
			if (ReflectionLoader.WEBVIEW != null && Os.isJavaFX()) {
				if (Os.isFXThread()) {
					this.webView = ReflectionLoader.newInstance(ReflectionLoader.WEBVIEW);
					this.webEngine = ReflectionLoader.call(this.webView, "getEngine");
					ReflectionLoader.call(this.webView, "setMaxSize", double.class, Double.MAX_VALUE, double.class,
							Double.MAX_VALUE);
				}
			}
		}
		return webView;
	}

	/**
	 * Gets the web engine.
	 *
	 * @return the web engine
	 */
	@Override
	public Object getWebEngine() {
		if (webEngine == null) {
			getWebView();
		}
		return webEngine;
	}

	protected void addAdapter(ObjectCondition eventListener) {
		if (!TYPE_EDITOR.equals(type)) {
			JsonObjectLazy executeScript = (JsonObjectLazy) _execute(
					"bridge.addAdapter(new DiagramJS.DelegateAdapter());", true);
			if (executeScript != null) {
				Object reference = executeScript.getReference();
				ReflectionLoader.calling(reference, "setAdapter", false, null, Object.class, eventListener);
			}
		}
	}

	/**
	 * Load finish.
	 */
	@Override
	public void loadFinish() {
		addAdapter(this);
		/* REGISTER LISTENER */
		if (this.queue != null) {
			while (this.queue.size() > 0) {
				String command = this.queue.remove(0);
				this._execute(command, false);
			}
		}
		this.queue = null; /* Disable QUEUE */
	}

	/**
	 * Adds the listener.
	 *
	 * @param control the control
	 * @param type the type
	 * @param functionName the function name
	 * @param callBackClazz the call back clazz
	 * @return true, if successful
	 */
	public boolean addListener(Control control, EventTypes type, String functionName, Object callBackClazz) {
		if(this.owner == null) {
			return false;
		}
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
	 * Gets the call back name.
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
			ReflectionLoader.call(window, "setMember", String.class, callBackName, Object.class, clazz);
		}
		return callBackName;
	}

	/**
	 * Execute.
	 *
	 * @param runnable the runnable
	 */
	public static void execute(final Runnable runnable) {
		if (!Os.isReflectionTest()) {
			ReflectionLoader.call(ReflectionLoader.PLATFORMIMPL, "startup", Runnable.class, runnable);
		}
	}

	/**
	 * Execute and wait.
	 *
	 * @param runnable the runnable
	 * @return the java adapter
	 */
	public static JavaAdapter executeAndWait(final Runnable runnable) {
		if (runnable == null) {
			return null;
		}
		if (Os.isFXThread()) {
			runnable.run();
			return null;
		}
		JavaAdapter task = new JavaAdapter();
		task.doneLatch = new CountDownLatch(1);
		task.newTask = runnable;
		execute(task);
		try {
			task.doneLatch.await();
		} catch (InterruptedException e) {
		}
		task.doneLatch = null;
		return task;
	}

	/**
	 * Enables Firebug Lite for debugging a webEngine.
	 */
	public void enableDebug() {
		/* https://getfirebug.com/firebug-lite.js#startOpened */
		/*
		 * firebugLite=
		 * "http://getfirebug.com/releases/lite/1.2/firebug-lite-compressed.js";
		 */
		String firebugLite = "https://getfirebug.com/releases/lite/latest/firebug-lite.js";
		String script = "if (!document.getElementById('FirebugLite')) {var E = document['createElementNS'] && document.documentElement.namespaceURI;E = E ? document.createElementNS(E, 'script') : document.createElement('script');E.setAttribute('id', 'FirebugLite');E.setAttribute('src', '"
				+ firebugLite
				+ "');E.setAttribute('FirebugLite', '4');(document.getElementsByTagName('head')[0] || document.getElementsByTagName('body')[0]).appendChild(E);}";
		String script2 = "console.log = function(message) { java.log(message); }"; /*
																					 * Now where ever console.log is
																					 * called in your html you will get
																					 * a log in Java console
																					 */
		boolean isFX = Os.isFXThread();
		if (this.webEngine == null || !isFX) {
			JSEditor editor = new JSEditor(this).withScript(script);
			JavaAdapter.execute(editor);

			editor = new JSEditor(this).withScript(script2);
			JavaAdapter.execute(editor);
			return;
		}
		executeScript(script);
		executeScript(script2);
	}
}
