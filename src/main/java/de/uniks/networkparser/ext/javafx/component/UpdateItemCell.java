package de.uniks.networkparser.ext.javafx.component;

import de.uniks.networkparser.gui.TableCellValue;

@FunctionalInterface
public interface UpdateItemCell {
	public boolean updateItem(TableCellFX cell, TableCellValue item, boolean empty);
}
