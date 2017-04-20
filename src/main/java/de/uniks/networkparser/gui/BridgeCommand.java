package de.uniks.networkparser.gui;

import de.uniks.networkparser.json.JsonObject;

public class BridgeCommand {
	private static final String COMMAND="bridge.load(";
	private static final String REGISTER="bridge.registerListener(\"";
	public static final String load(String command) {
		return COMMAND+command+");";
	}
	public static final String load(JsonObject command) {
		return COMMAND+command+");";
	}
	public static final String register(EventTypes event, String id) {
		return REGISTER+event+"\", \""+id+"\");";
	}
	public static final String register(EventTypes event, String id, String callBack) {
		return REGISTER+event+"\", \""+id+"\",\""+callBack+"\");";
	}
}
