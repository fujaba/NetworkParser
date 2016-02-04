package de.uniks.networkparser.interfaces;

import de.uniks.networkparser.list.SimpleList;

public interface EntityList extends BaseItem{

	public SimpleList<EntityList> getChildren();
	
	public String toString(int indentFactor, int intent);
}
