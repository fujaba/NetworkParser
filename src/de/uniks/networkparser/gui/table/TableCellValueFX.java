package de.uniks.networkparser.gui.table;

import javafx.beans.property.SimpleObjectProperty;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TableCellValueFX extends SimpleObjectProperty<TableCellValue> implements TableCellValue{

	private Column column;
	private SendableEntityCreator creator;
	private Object item;
	private TableComponent tableComponent;
	
	public TableCellValueFX withItem(Object item) {
		this.item = item;
		this.set(this);
		return this;
	}
	
	public TableCellValueFX withTableComponent(TableComponent tableComponent) {
		this.tableComponent = tableComponent;
		return this;
	}
	public TableCellValueFX withColumn(Column column) {
		this.column = column;
		return this;
	}
	
	
	public TableCellValueFX withCreator(
			SendableEntityCreator creator) {
		this.creator = creator;
		return this;
	}
	
	public Object getItem(){
		return item;
	}
	public Column getColumn() {
		return column;
	}

	public SendableEntityCreator getCreator() {
		return creator;
	}
	public String toString(){
		return (String) this.column.getListener().getValue(tableComponent, item, creator);
	}

	@Override
	public Object getSimpleValue() {
		return getCreator().getValue(item, getColumn().getAttrName());
	}
}
