package de.uniks.networkparser.gui;

import de.uniks.networkparser.json.JsonObject;

public interface Editor {
	public void open(Object logic, String... args);
	public boolean generate(JsonObject model);
	public String getIcon();
}
