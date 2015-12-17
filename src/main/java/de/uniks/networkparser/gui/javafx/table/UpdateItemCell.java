package de.uniks.networkparser.gui.javafx.table;

import de.uniks.networkparser.gui.TableCellValue;

@FunctionalInterface
public interface UpdateItemCell {
	public boolean updateItem(TableCellFX cell, TableCellValue item, boolean empty);
}
