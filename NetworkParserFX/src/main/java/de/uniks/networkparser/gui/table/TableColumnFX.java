package de.uniks.networkparser.gui.table;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.TreeTableView.EditEvent;
import javafx.scene.control.TreeView;
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
				System.out.println(arg0); 
				return new TableCellFX().withTableComponent(tableComponent).withColumn(TableColumnFX.this.column).withEditFieldMap(TableColumnFX.this.tableComponent.getFieldFactory());
			}
		});
		setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,TableCellValue>, ObservableValue<TableCellValue>>() {
			@Override
			public ObservableValue<TableCellValue> call(
					javafx.scene.control.TableColumn.CellDataFeatures<Object, TableCellValue> arg0) {
//				System.out.println(arg0.getTableColumn());;
				SendableEntityCreator creator = TableColumnFX.this.tableComponent.getCreator(arg0.getValue());
				return new TableCellValueFX()
						.withItem(arg0.getValue())
						.withColumn(TableColumnFX.this.column)
						.withCreator(creator);
			}
		});
		
		this.setOnEditStart(new EventHandler<TableColumn.CellEditEvent<Object, TableCellValue>>() {
	        @Override
	        public void handle(TableColumn.CellEditEvent<Object, TableCellValue> t) {
	        	System.out.println(t);
	        }
	    });

		
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
	
	
	
	
	//FIXME REMOVE
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
