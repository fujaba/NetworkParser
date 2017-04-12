package de.uniks.networkparser.interfaces;

import java.beans.PropertyChangeListener;

import de.uniks.networkparser.Filter;

public interface MapListener extends PropertyChangeListener {
	public Object execute(Entity updateMessage, Filter filter);
	public MapListener withFilter(Filter filter);
	public Filter getFilter();
	public boolean suspendNotification();
	public boolean resetNotification();
	public boolean resumeNotification();
}
