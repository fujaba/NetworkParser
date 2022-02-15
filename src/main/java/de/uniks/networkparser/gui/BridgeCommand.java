package de.uniks.networkparser.gui;

import de.uniks.networkparser.json.JsonObject;

/**
 * The Class BridgeCommand.
 *
 * @author Stefan
 */
public class BridgeCommand {
	private static final String COMMAND = "bridge.load(";
	private static final String REGISTER = "bridge.registerListener(\"";

	/**
	 * Load.
	 *
	 * @param command the command
	 * @return the string
	 */
	public static final String load(String command) {
		return COMMAND + command + ");";
	}

	/**
	 * Load.
	 *
	 * @param command the command
	 * @return the string
	 */
	public static final String load(JsonObject command) {
		return COMMAND + command + ");";
	}

	/**
	 * Register.
	 *
	 * @param event the event
	 * @param id the id
	 * @return the string
	 */
	public static final String register(EventTypes event, String id) {
		return REGISTER + event + "\", \"" + id + "\");";
	}

	/**
	 * Register.
	 *
	 * @param event the event
	 * @param id the id
	 * @param callBack the call back
	 * @return the string
	 */
	public static final String register(EventTypes event, String id, String callBack) {
		return REGISTER + event + "\", \"" + id + "\",\"" + callBack + "\");";
	}
}
