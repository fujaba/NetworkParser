package de.uniks.networkparser.gui.table;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableColumnFX extends TableColumn<Object, Object> implements TableColumnInterface{
	private Column column;

	public TableColumnFX withColumn(Column column){
		this.column = column;
		this.setText(column.getLabelOrAttrName());
		setCellValueFactory(new PropertyValueFactory<Object, Object>(column.getAttrName()));
		return this;
	}
	
	@Override
	public Column getColumn() {
		return column;
	}

}
