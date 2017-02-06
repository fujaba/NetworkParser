package de.uniks.networkparser.interfaces;

import java.beans.PropertyChangeListener;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.json.JsonObject;

public interface MapListener extends PropertyChangeListener{
	public Object execute(JsonObject updateMessage, Filter filter);
	public MapListener withFilter(UpdateListener filter);
}
