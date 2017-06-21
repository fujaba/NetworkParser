package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.gui.JavaViewAdapter;
import de.uniks.networkparser.gui.controls.Control;

public class JavaBridgeFX extends JavaBridge {
	public JavaBridgeFX() {
		this(null);
	}

	public JavaBridgeFX(IdMap map) {
		super(map, new JavaAdapter(), CONTENT_TYPE_INCLUDE);
	}
	
	public JavaBridgeFX(IdMap map, JavaViewAdapter webView, String type) {
		super(map, webView, type);
	}

	@Override
	public void addListener(Control c, EventTypes type, String methodName, Object object) {
		addEventListener(c, type, new MethodCallbackListener(object, methodName));
	}
}
