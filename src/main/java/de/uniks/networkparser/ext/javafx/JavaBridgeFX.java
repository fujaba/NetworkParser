package de.uniks.networkparser.ext.javafx;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.gui.controls.Control;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;

public class JavaBridgeFX extends JavaBridge {

	private WebView webView;
	private SimpleKeyValueList<Object, String> callBack=new SimpleKeyValueList<Object, String>();
	
	public JavaBridgeFX() {
		this(null);
	}

	public JavaBridgeFX(IdMap map) {
		super(map);
		webView = new WebView();
		webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
		// this.setMaxHeight(Double.MAX_VALUE);
		// this.setMaxWidth(Double.MAX_VALUE);

		// this.getChildren().add(webView);
		WebEngine engine = webView.getEngine();
		
		try {
			//DevToolsDebuggerServer.startDebugServer(engine.impl_getDebugger(), 51742);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		engine.setOnAlert(t -> {
			System.out.println(t.getData());
		});

		engine.setOnError(e -> {
			System.err.println(e);
		});

		loadBridge(engine);
		// set global variable JavaBridge in order to being able to call Java
		// inside JavaScript
		
		addAdapter(new JavaAdapter(this));
	}
	
	public boolean addListener(Control control, EventTypes type, String functionName, Object callBackClazz) {
		this.addControl(control);
		String id = control.getId();
		
		
		JSObject window = (JSObject) this.webView.getEngine().executeScript("window");
		if(callBackClazz != null) {
			String string = callBack.get(callBackClazz);
			if(string == null) {
				string = "_callBack"+(callBack.size()+1);
				callBack.put(callBackClazz, string);
				window.setMember(string, callBackClazz);
			}
			executeScript("bridge.registerListener(\""+type+"\", \""+id+"\", \""+string+"."+functionName+"\");");
			return true;
		}
		executeScript("bridge.registerListener("+type+", \""+id+"\");");
		return true;
	}

	protected void addAdapter(UpdateListener eventListener) {
		JsonObjectLazy executeScript = (JsonObjectLazy) executeScript("bridge.addAdapter(new DelegateAdapter());");
		JSObject reference = executeScript.getReference();
		reference.setMember("adapter", eventListener);
	}
	
	private void loadBridge(WebEngine engine) {
		engine.executeScript(loadFile(Paths.get("./res/output.js")));
	}
	
	private String loadFile(Path path) {
		StringBuilder sb = new StringBuilder();
		// load the bridge
		try {
			List<String> readAllLines = Files.readAllLines(path);

			for (String string : readAllLines) {
				sb.append(string);
				sb.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();
	}

	public Object executeScript(String script) {
		Object jsObject = webView.getEngine().executeScript(script);
		JsonObject item = convertJSObject(jsObject);
		return item;
	}
	
	private JsonObject convertJSObject(Object element) {
		JsonObjectLazy result = new JsonObjectLazy(element);
		result.lazyLoad();
		return result;
	}

	/**
	 * @return the webView
	 */
	public WebView getWebView() {
		return webView;
	}
	
//	public void loadContent(String content, Runnable onFinishedLoading) {
//		engine.loadContent(content);
//
//		engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
//			@Override
//			public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
//				if (Worker.State.SUCCEEDED.equals(newValue)) {
//					engine.getLoadWorker().stateProperty().removeListener(this);
//
//					onFinishedLoading.run();
//					
//					addAdapter(new JavaAdapter(JavaBridgeFX.this));
//				}
//
//			}
//		});
//	}
}
