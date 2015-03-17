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
import com.sun.javafx.scene.control.skin.TableHeaderRow;
import com.sun.javafx.scene.control.skin.TableRowSkin;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Menu;
import javafx.scene.control.Skin;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;

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
				TableCellFX cell = new TableCellFX().withTableComponent(tableComponent).withColumn(TableColumnFX.this.column).withEditFieldMap(TableColumnFX.this.tableComponent.getFieldFactory());
				return cell;
			}
		});
		setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,TableCellValue>, ObservableValue<TableCellValue>>() {
			@Override
			public ObservableValue<TableCellValue> call(
					javafx.scene.control.TableColumn.CellDataFeatures<Object, TableCellValue> arg0) {
				SendableEntityCreator creator = TableColumnFX.this.tableComponent.getCreator(arg0.getValue());
				return new TableCellValueFX().withColumn(TableColumnFX.this.column).withCreator(creator).withItem(arg0.getValue());
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
	
	
	public void refreshCell(int row) {
		TableView<Object> browserView = tableComponent.getBrowserView(GUIPosition.CENTER);
        TableViewSkin<?> tvs = (TableViewSkin<?>) browserView.getSkin();
        ObservableList<Node> children = tvs.getChildren();
        for(Node node : children) {
        	if(node instanceof VirtualFlow<?>) {
        		VirtualFlow<?> vf = (VirtualFlow<?>) node;
        		IndexedCell<?> cell = vf.getCell(row);
        		Skin<?> skin = cell.getSkin();
        		if(skin instanceof TableRowSkin<?>) {
        			TableRowSkin<?> trs = (TableRowSkin<?>) skin;
        			for(Node trsc : trs.getChildren() ) {
        				if(trsc instanceof TableCellFX) {
        					((TableCellFX)trsc).updateIndex(row);
        				}
        			}
        			System.out.println(trs.getChildren());
        		}
        	}
        }
	}
}
