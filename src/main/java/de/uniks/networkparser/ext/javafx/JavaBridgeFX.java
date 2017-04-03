package de.uniks.networkparser.ext.javafx;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.JavaBridge;

public class JavaBridgeFX extends JavaBridge {
	public JavaBridgeFX() {
		this(null);
	}

	public JavaBridgeFX(IdMap map) {
		super(map, new JavaAdapter());
	}
}
