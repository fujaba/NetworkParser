package de.uniks.networkparser.gui;

import de.uniks.networkparser.json.JsonObject;

public interface Editor {
	public static final String URL="de.uniks.networkparser.gui.javafx.window.DiagramEditor";
	public void open(Object logic, String... args);
	public boolean generate(JsonObject model);
	public String getIcon();
}
