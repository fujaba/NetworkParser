package de.uniks.networkparser.ext.story;

import java.util.Comparator;

public interface JacocoColumnListener {
	public boolean init(Object items, Object total);
	public void footer(Object td, Object total, Object resources, Object base);
	public void item(Object td, Object item, Object resources, Object base);
	public Comparator<Object> getComparator();
}
