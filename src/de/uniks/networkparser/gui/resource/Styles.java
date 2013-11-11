package de.uniks.networkparser.gui.resource;


public class Styles {
	public static String getPath(){
		return Styles.class.getResource("styles.css").toExternalForm();
	}
}
