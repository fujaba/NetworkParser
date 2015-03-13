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
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TableColumnFX extends TableColumn<Object, TableCellValue> implements EventHandler<ActionEvent>{
	private Column column;
	private CheckMenuItem menueItem;
	private TableComponent tableComponent;

	public TableColumnFX withColumn(Column column, Menu visibleItems, TableComponent value){
		this.column = column;
		this.tableComponent = value;
		if(column.getComparator()!=null){
			this.setComparator(column.getComparator());
		}
		this.setText(column.getLabelOrAttrName());
		this.setResizable(column.isResizable());
		menueItem = new CheckMenuItem();
		menueItem.setSelected(true);
		menueItem.setText(column.getLabelOrAttrName());
		menueItem.setOnAction(this);
		visibleItems.getItems().add(menueItem);
		
		setCellFactory(new Callback<TableColumn<Object,TableCellValue>, TableCell<Object,TableCellValue>>() {
			@Override
			public TableCell<Object, TableCellValue> call(
					TableColumn<Object, TableCellValue> arg0) {
				return new TableCellFX().withTableComponent(tableComponent).withColumn(TableColumnFX.this.column).withEditFieldMap(TableColumnFX.this.tableComponent.getFieldFactory());
			}
		});
		setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,TableCellValue>, ObservableValue<TableCellValue>>() {
			@Override
			public ObservableValue<TableCellValue> call(
					javafx.scene.control.TableColumn.CellDataFeatures<Object, TableCellValue> arg0) {
				SendableEntityCreator creator = TableColumnFX.this.tableComponent.getCreator(arg0.getValue());
				return new TableCellValueFX()
						.withInit(TableColumnFX.this.tableComponent, TableColumnFX.this.column, creator, arg0.getValue());
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
	
	public Column getColumn() {
		return column;
	}

	@Override
	public void handle(ActionEvent event) {
		Object source = event.getSource();
		if(source==menueItem){
			if(!menueItem.isSelected()){
				column.withVisible(false);
				this.setVisible(false);	
			}else{
				column.withVisible(true);
				this.setVisible(true);
			}
		}
	}

	public void UpdateCount() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				TableColumnFX.this.setText(column.getLabel() + " (" + tableComponent.getBrowserView(GUIPosition.CENTER).getItems().size() + ")");
			}
		});
	}
	
	public void refresh() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				// COLUMN
				TableColumnFX.this.setText( column.getLabel() );
				setVisible(column.isVisible());
				
				// Menue
				menueItem.setText( column.getLabel() );
				menueItem.setSelected(column.isVisible());
			}
		});
	}
}
