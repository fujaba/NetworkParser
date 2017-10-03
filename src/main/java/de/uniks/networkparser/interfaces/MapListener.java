package de.uniks.networkparser.interfaces;

import java.beans.PropertyChangeListener;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.UpdateAccumulate;
import de.uniks.networkparser.list.SimpleList;

public interface MapListener extends PropertyChangeListener {
	public Object execute(Entity updateMessage, Filter filter);
	public MapListener withFilter(Filter filter);
	public Filter getFilter();
	public boolean suspendNotification(UpdateAccumulate... accumulates);
	public SimpleList<UpdateAccumulate> resetNotification();
}
