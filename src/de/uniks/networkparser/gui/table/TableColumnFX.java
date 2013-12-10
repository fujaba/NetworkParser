package de.uniks.networkparser.gui.table;

import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TableColumnFX extends TableColumn<Object, TableCellValue> implements TableColumnInterface, EventHandler<ActionEvent>{
	private Column column;
	private CheckMenuItem menueItem;
	private TableComponent tableComponent;

	public TableColumnFX withColumn(Column column, Menu visibleItems, TableComponent tableComponent){
		this.column = column;
		this.tableComponent = tableComponent;
		if(column.getComparator()!=null){
			this.setComparator(column.getComparator());
		}
		this.setText(column.getLabelOrAttrName());
			
		menueItem = new CheckMenuItem();
		menueItem.setSelected(true);
		menueItem.setText(column.getLabelOrAttrName());
		menueItem.setOnAction(this);
		visibleItems.getItems().add(menueItem);
		
		setCellFactory(new Callback<TableColumn<Object,TableCellValue>, TableCell<Object,TableCellValue>>() {
			@Override
			public TableCell<Object, TableCellValue> call(
					TableColumn<Object, TableCellValue> arg0) {
				return new TableCellFX().withColumn(TableColumnFX.this.column).withMap(TableColumnFX.this.tableComponent.getMap());
			}
		});
		setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,TableCellValue>, ObservableValue<TableCellValue>>() {
			@Override
			public ObservableValue<TableCellValue> call(
					javafx.scene.control.TableColumn.CellDataFeatures<Object, TableCellValue> arg0) {
				SendableEntityCreator creator = TableColumnFX.this.tableComponent.getCreator(arg0.getValue());
				return new TableCellValueFX()
						.withItem(arg0.getValue())
						.withColumn(TableColumnFX.this.column)
						.withCreator(creator);
			}
		});
		return this;
	}
	public void sort() {
		System.out.println("JJJ");
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
//	@Override
//	public ObservableValue<TableCellValue> call(
//			CellDataFeatures<Object, TableCellValue> arg0) {
//		return new TableCellFX().withColumn(column).withTableComponent(tableComponent);
//////	.withCreator(tableComponent.getCreatorClass(arg0.getValue()));
//	}

//	@Override
//	public ObservableValue<TableCellValue> call(CellDataFeatures<Object, TableCellValue> arg0) {
//	public TableCell<Object, TableCellValue> call(TableColumn<Object, TableCellValue> arg0) {
//		return new TableCellFX().withColumn(column).withTableComponent(tableComponent);
//setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,TableCellValue>, ObservableValue<TableCellValue>>() {
//			
//			@Override
//			public ObservableValue<TableCellValue> call(CellDataFeatures<Object, TableCellValue> arg0) {
//				 return new TableCellValueFX().withItem(arg0.getValue()).withColumn(TableColumnFX.this.column).withCreator(TableColumnFX.this.tableComponent.getMap().getCreatorClass(arg0.getValue()));
//			}
//		});
//	}
//	public TableCell<Object, TableCellValue> call(
//			TableColumn<Object, TableCellValue> arg0) {
//		System.out.println(arg0);
//		arg0.get
//		
//	}
}
