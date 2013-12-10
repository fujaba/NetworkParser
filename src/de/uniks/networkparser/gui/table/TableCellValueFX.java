package de.uniks.networkparser.gui.table;

import javafx.beans.property.SimpleObjectProperty;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TableCellValueFX extends SimpleObjectProperty<TableCellValue> implements TableCellValue{

	private Column column;
	private SendableEntityCreator creator;
	private Object item;
	
	public TableCellValueFX withItem(Object item) {
		this.item = item;
		this.set(this);
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
		if(creator==null){
			return "";
		}
		return "" + this.column.getListener().getValue(item, creator);
	}

	@Override
	public Object getSimpleValue() {
//		Object value = getCreator().getValue(item, getColumn().getAttrName());
//		if(value instanceof String){
//			return new ModelListenerStringProperty(getCreator(), item, getColumn().getAttrName());
//		}else if(value instanceof Number){
//			return new ModelListenerNumberProperty(getCreator(), item, getColumn().getAttrName());
//		}
		return getCreator().getValue(item, getColumn().getAttrName());
	}
}
