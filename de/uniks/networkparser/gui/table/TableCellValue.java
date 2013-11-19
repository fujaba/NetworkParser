package de.uniks.networkparser.gui.table;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public interface TableCellValue {
	public Column getColumn();
	public SendableEntityCreator getCreator();
	public Object getItem();
	public Object getSimpleValue();
}
