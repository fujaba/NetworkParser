package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.JavaBridge;
import de.uniks.networkparser.gui.controls.Control;

public class JavaBridgeFX extends JavaBridge {
	public JavaBridgeFX() {
		this(null);
	}

	public JavaBridgeFX(IdMap map) {
		super(map, new JavaAdapter());
	}
	
	@Override
	public void addListener(Control c, EventTypes type, String methodName, Object object) {
		addEventListener(c, type, new MethodCallbackListener(object, methodName));
	}
}
