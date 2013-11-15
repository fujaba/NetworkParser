package de.uniks.networkparser.gui.table;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class TableColumnFX extends TableColumn<Object, Object> implements TableColumnInterface, EventHandler<ActionEvent>, Callback<TableColumn<Object, Object>, TableCell<Object, Object>>{
	private Column column;
	private CheckMenuItem menueItem;

	public TableColumnFX withColumn(Column column, Menu visibleItems){
		this.column = column;
		this.setText(column.getLabelOrAttrName());
		setCellValueFactory(new PropertyValueFactory<Object, Object>(column.getAttrName()));
		menueItem = new CheckMenuItem();
		menueItem.setSelected(true);
		menueItem.setText(column.getLabelOrAttrName());
		menueItem.setOnAction(this);
		visibleItems.getItems().add(menueItem);
		
		setCellFactory(this);
		
		return this;
	}
	
	@Override
	public Column getColumn() {
		return column;
	}

	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		if(source==menueItem){
			if(!menueItem.isSelected()){
				this.setVisible(false);	
			}else{
				this.setVisible(true);
			}
		}
	}

	@Override
	public TableCell<Object, Object> call(TableColumn<Object, Object> arg0) {
		return new TableCellFX().withColumn(column);
	}		
}
