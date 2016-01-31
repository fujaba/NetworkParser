package de.uniks.networkparser.gui.javafx.table;

import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.gui.javafx.controls.EditFieldMap;
import javafx.scene.control.TableCell;

public class TableCellFactory {
	private UpdateItemCell updateListener;
	public TableCell<Object, TableCellValue> create(TableComponent tableComponent, Column column, EditFieldMap editFieldMap) {
		return new TableCellFX().withTableComponent(tableComponent).withColumn(column).withEditFieldMap(editFieldMap).withUpdateListener(updateListener);
	}

	public TableCellFactory withUpdateListener(UpdateItemCell updateListener) {
		this.updateListener = updateListener;
		return this;
	}
}
