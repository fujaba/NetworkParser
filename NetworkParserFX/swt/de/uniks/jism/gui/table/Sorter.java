package de.uniks.jism.gui.table;

import de.uniks.jism.sort.SortingDirection;


public interface Sorter {
	public int compareTo(TableColumnView view, Column column, Object e1, Object e2);

	public void setDirection(SortingDirection direction);
	
}
