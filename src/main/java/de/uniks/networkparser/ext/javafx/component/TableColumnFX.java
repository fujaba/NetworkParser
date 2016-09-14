package de.uniks.networkparser.ext.javafx.component;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.IndexedCell;
import javafx.scene.control.Menu;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;
import com.sun.javafx.scene.control.skin.TableRowSkin;
import com.sun.javafx.scene.control.skin.TableViewSkin;
import com.sun.javafx.scene.control.skin.VirtualFlow;
import de.uniks.networkparser.ext.javafx.controls.EditFieldMap;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TableColumnFX extends TableColumn<Object, TableCellValue> implements EventHandler<ActionEvent>{
	private Column column;
	private CheckMenuItem menueItem;
	private TableComponent tableComponent;
	private TableCellFactory cellCreator;

	public TableColumnFX withColumn(Column column, Menu visibleItems, TableComponent value, TableCellFactory cellCreator){
		this.column = column;
		this.cellCreator = cellCreator;
		this.tableComponent = value;
		if(column.getComparator()!=null){
			this.setComparator(column.getComparator());
		}
		this.setText(column.getLabelOrAttrName());
		this.setResizable(column.isResizable());
		this.setEditable(column.isEditable());
		menueItem = new CheckMenuItem();
		menueItem.setSelected(true);
		menueItem.setText(column.getLabelOrAttrName());
		menueItem.setOnAction(this);
		visibleItems.getItems().add(menueItem);

		setCellFactory(new Callback<TableColumn<Object,TableCellValue>, TableCell<Object,TableCellValue>>() {
			@Override
			public TableCell<Object, TableCellValue> call(
					TableColumn<Object, TableCellValue> arg0) {
				Column column = TableColumnFX.this.column;
				EditFieldMap editFieldMap = TableColumnFX.this.tableComponent.getFieldFactory();
				return TableColumnFX.this.cellCreator.create(tableComponent, column, editFieldMap);
			}
		});
		setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Object,TableCellValue>, ObservableValue<TableCellValue>>() {
			@Override
			public ObservableValue<TableCellValue> call(
					javafx.scene.control.TableColumn.CellDataFeatures<Object, TableCellValue> arg0) {
				Object value = arg0.getValue();
				SendableEntityCreator creator = TableColumnFX.this.tableComponent.getCreator(value);
				return new TableCellValueFX().withColumn(TableColumnFX.this.column).withCreator(creator).withItem(value);
			}
		});

		widthProperty().addListener(new ChangeListener<Number>() {
			public void changed(javafx.beans.value.ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				TableColumnFX.this.getColumn().getOrCreateStyle().withWidth(newValue.doubleValue());
			};
		});
		if(column.getStyle() != null) {
			this.setPrefWidth(getColumn().getStyle().getWidth());
		}
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

	public synchronized void refreshCell(int row) {
		TableViewSkin<?> skin = (TableViewSkin<?>) this.getTableView().getSkin();
		int columnId = this.getTableView().getVisibleLeafIndex(this);
		ObservableList<Node> children = skin.getChildren();
		for(Node node : children) {
			if(node instanceof VirtualFlow<?>) {
				VirtualFlow<?> vf = (VirtualFlow<?>) node;

				if(row >= 0 ) {
					IndexedCell<?> cell;
					try{
						cell = vf.getVisibleCell(row);
						if(cell == null) {
							continue;
						}
					}catch(Exception e) {
						continue;
					}
					TableRowSkin<?> cellSkin = (TableRowSkin<?>)cell.getSkin();
					TableCellFX tableCell = (TableCellFX) (cellSkin).getChildren().get(columnId);
					tableCell.updateIndex(row);
				} else {
				}
			}
		}
	}
}
