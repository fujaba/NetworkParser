package de.uniks.networkparser.gui.table;

/*
NetworkParser
Copyright (c) 2011 - 2013, Stefan Lindel
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
1. Redistributions of source code must retain the above copyright
notice, this list of conditions and the following disclaimer.
2. Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.
3. All advertising materials mentioning features or use of this software
must display the following acknowledgement:
This product includes software developed by Stefan Lindel.
4. Neither the name of contributors may be used to endorse or promote products
derived from this software without specific prior written permission.

THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableListValue;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import org.sdmlib.serialization.DefaultTextItems;
import org.sdmlib.serialization.IdMap;
import org.sdmlib.serialization.TextItems;
import org.sdmlib.serialization.gui.table.Column;
import org.sdmlib.serialization.gui.table.TableColumnInterface;
import org.sdmlib.serialization.gui.table.TableComponentInterface;
import org.sdmlib.serialization.gui.table.TableFilterView;
import org.sdmlib.serialization.gui.table.TableList;
import org.sdmlib.serialization.interfaces.GUIPosition;
import org.sdmlib.serialization.interfaces.SendableEntityCreator;

public class TableComponent extends BorderPane implements PropertyChangeListener, TableComponentInterface, ChangeListener<Number> {
	private ArrayList<TableColumnInterface> columns = new ArrayList<TableColumnInterface>();
	public static final String PROPERTY_COLUMN = "column";
	public static final String PROPERTY_ITEM = "item";
	protected IdMap map;
	protected Object source;
	protected SendableEntityCreator sourceCreator;
	private String property;
	protected UpdateSearchList updateItemListener;
	protected TableViewFX[] tableViewer=new TableViewFX[3];
	protected boolean isToolTip;
	protected ContextMenu contextMenu;

	protected TableList sourceList;
	private WritableListValue<Object> list;
	protected TableFilterView tableFilterView;
	private Menu visibleItems;
	
	public IdMap getMap() {
		return map;
	}
	
	public Node withAnchor(Node node){
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
		AnchorPane.setBottomAnchor(node, 0.0);
		return node;
	}
	
	public TableComponent withList(TableList item) {
		return withList(item, TableList.PROPERTY_ITEMS);
	}
	
	public TableComponent createFromCreator(SendableEntityCreator creator, boolean edit) {
		if(creator==null){
			Iterator<Object> iterator = list.iterator();
			if(iterator.hasNext()){
				Object value = iterator.next();
				creator = map.getCreatorClass(value);
			}			
		}
		if(creator==null){
			return this;
		}
		String[] properties = creator.getProperties();
		Object prototyp = creator.getSendableInstance(true);
		for (String property : properties) {
			Object value = creator.getValue(prototyp, property);
			if (!(value instanceof Collection<?>)) {
				withColumn(new Column().withAttrName(property, edit)
						.withGetDropDownListFromMap(true));
			}
		}
		return this;
	}
	
	public TableView<Object> getBrowserView(GUIPosition browserId) {
		if (browserId.equals(GUIPosition.WEST)) {
			if(tableViewer[0]!=null){
				return tableViewer[0];
			}
		}else if (browserId.equals(GUIPosition.CENTER)) {
			if(tableViewer[1]!=null){
				return tableViewer[1];
			}
		}else if (browserId.equals(GUIPosition.EAST)) {
			if(tableViewer[2]!=null){
				return tableViewer[2];
			}
		}
		TableViewFX resultTableViewer=new TableViewFX();
		resultTableViewer.withListener(this);
		resultTableViewer.setItems(list);
		if (browserId.equals(GUIPosition.CENTER)) {
			this.setCenter(resultTableViewer);
			tableViewer[1] = resultTableViewer; 
		}else if (browserId.equals(GUIPosition.WEST)) {
			this.setLeft(resultTableViewer);
			tableViewer[0] = resultTableViewer; 
		}else if (browserId.equals(GUIPosition.EAST)) {
			this.setRight(resultTableViewer);
			tableViewer[2]= resultTableViewer;
		}
		return resultTableViewer;
	}

	public TableComponent withScrollPosition(double pos){
		for(TableViewFX table : tableViewer){
			if(table!=null){
				table.setScrollValue(pos);
			}
		}
		return this;
	}
	
//			for (TableColumnView item : columns) {
//				if (item.getColumn().getBrowserId().equals(GUIPosition.WEST)) {
//					item.setVisible(false);
//				}
//			}
//
//			fixedTableViewerLeft.getTable().dispose();
//			fixedTableViewerLeft = null;
//			tableSyncronizer.dispose();
//		} else if (fixedTableViewerLeft == null && visible) {
//			fixedTableViewerLeft = createBrowser(GUIPosition.WEST);
//
//			tableSyncronizer = new TableSyncronizer(this,
//					fixedTableViewerLeft.getTable(), tableViewer.getTable());
//			tableViewer.getTable().addMouseWheelListener(tableSyncronizer);
//			tableViewer.getTable().addListener(SWT.Selection, tableSyncronizer);
//			if (tableViewer.getTable().getVerticalBar() != null) {
//				tableViewer.getTable().getVerticalBar()
//						.addListener(SWT.Selection, tableSyncronizer);
//			}
//			fixedTableViewerLeft.getTable().addListener(SWT.Selection,
//					tableSyncronizer);
//
//			for (TableColumnView item : columns) {
//				if (item.getColumn().getBrowserId().equals(GUIPosition.WEST)) {
//					item.setVisible(true);
//				}
//			}
//		}
//	}


	public TableComponent withColumn(Column column) {
		TableView<Object> browserView = getBrowserView(column.getBrowserId());

		TableColumnFX columnFX = new TableColumnFX().withColumn(column, visibleItems, this);
		columnFX.setContextMenu(contextMenu);
		
		this.columns.add(columnFX);
		if (column.getAltAttribute() != null) {
			if (!isToolTip) {
				isToolTip = true;
			}
		}
		if (getParent() instanceof PropertyChangeListener) {
			((PropertyChangeListener) getParent())
					.propertyChange(new PropertyChangeEvent(this,
							PROPERTY_COLUMN, null, column));
		}
		browserView.getColumns().add(columnFX);
		return this;
	}

//		if(item!=null){
//			
//				if(creatorClass!=null){
//					String[] properties = creatorClass.getProperties();
//					for(int z=0;z<properties.length;z++){
//						
//
////						TableColumnHeaderFX header=new TableColumnHeaderFX(this.getTableViewer(), col);
////						TextField header = new TextField("Pink Elephants");
// 						
////						col.setGraphic(header);
//						
////						getTableViewer().
////				        TableViewSkin<?> skin = (TableViewSkin<?>) getTableViewer().getSkin();
//
////				        TableHeaderRow tableHeader = skin.getTableHeaderRow();
////				        NestedTableColumnHeader rootHeader = tableHeader.getRootHeader();
//
//						addColumn(col);
//						System.out.println(col.getTableView());
////						columns.get
////						TableViewSkin<?> skin = (TableViewSkin<?>)getTableViewer().getSkin();
////						skin.getTableHeaderRow().
//					}
//				}
//			}
//		}
//		return result; 
//	}
	
	public TableComponent withMap(IdMap map){
		this.map = map;
		return this;
	} 

	public TableComponent withSearchProperties(String... searchProperties) {
		tableFilterView.setSearchProperties(searchProperties);
		tableFilterView.refresh();
		return this;
	}
	
	public void init(){
		withAnchor(this);
		
		if(contextMenu==null){
			contextMenu = new ContextMenu();
			visibleItems = new Menu();
			visibleItems.setText(getText(DefaultTextItems.COLUMNS));
			contextMenu.getItems().add(visibleItems);
		}

		if(list==null){
			this.list = new SimpleListProperty<Object>(javafx.collections.FXCollections.observableList(new ArrayList<Object>()));
//			this.list.addPropertyChangeListener(this);
//			this.list.setIdMap(map);
			
			
//			list.withTableComponent(this);
//			ObservableList<Object> theList = list;
			
//			theList.addListener(new ListChangeListener<Object>(){
//				@Override
//				public void onChanged(
//						javafx.collections.ListChangeListener.Change<? extends Object> arg0) {
//					System.out.println("HHH");
//				}
//			});
		}
//		         public void onChanged(Change<TableListFX> c) {
//		             while (c.next()) {
////		                 if (c.wasPermutated()) {
////		                     for (int i = c.getFrom(); i < c.getTo(); ++i) {
////		                          //permutate
////		                     }
////		                 } else if (c.wasUpdated()) {
////		                          //update item
////		                 } else {
////		                     for (Item remitem : c.getRemoved()) {
////		                         remitem.remove(Outer.this);
////		                     }
////		                     for (Item additem : c.getAddedSubList()) {
////		                         additem.add(Outer.this);
////		                     }
////		                 }
//		             }
//		         }
		if(sourceList==null){
			this.sourceList = new TableList();
			this.sourceList.addPropertyChangeListener(this);
			this.sourceList.setIdMap(map);
		}

		if(tableFilterView==null){
			tableFilterView = new TableFilterView(this);
		}
		
		if(this.updateItemListener==null){
			this.updateItemListener = new UpdateSearchList(this);
		}
	}

	public boolean addItem(Object item) {
		if(sourceList==null){
			init();
		}
		if (!sourceList.contains(item)) {
			sourceList.add(item);
			if (tableFilterView.matchesSearchCriteria(item)) {
				if (getParent() instanceof PropertyChangeListener) {
					((PropertyChangeListener) getParent())
							.propertyChange(new PropertyChangeEvent(this,
									PROPERTY_ITEM, null, item));
				}
			}
			this.updateItemListener.addItem(item);
			tableFilterView.refreshCounter();
			return true;
		}
		return false;
	}

	public boolean removeItem(Object item) {
		if (sourceList.contains(item)) {
			sourceCreator.setValue(source, property, item, IdMap.REMOVE);
			this.updateItemListener.removeItem(item);
			sourceList.remove(item);
			if (getParent() instanceof PropertyChangeListener) {
				((PropertyChangeListener) getParent())
						.propertyChange(new PropertyChangeEvent(this,
								PROPERTY_ITEM, item, null));
			}
//			tableViewer.refresh();
			tableFilterView.refreshCounter();
			return true;
		}
		return false;
	}
	public TableComponent withList(Object item, String property) {
		if (map == null) {
			return this;
		}
		this.source = item;
		this.sourceCreator = map.getCreatorClass(source);
		this.property = property;
		if (sourceCreator == null) {
			return this;
		}
		
		// Copy Sources
		Object sourceList = sourceCreator.getValue(source, property);
		if(sourceList instanceof Collection<?>){
			init();
			for(Iterator<?> iterator = ((Collection<?>)sourceList).iterator();iterator.hasNext();){
				Object itemList = iterator.next();
				this.sourceList.add(itemList);
				this.list.add(itemList);
			}
		}
		

		// Copy all Elements to TableList
		Collection<?> collection = (Collection<?>) sourceCreator.getValue(item,
				property);
		if (collection != null) {
			Object[] items = collection.toArray(new Object[collection.size()]);
			for(int i=0;i<items.length;i++){
				addItem(items[i]);
			}
		}
		addUpdateListener(source);
		return this;
	}
	
	public TableColumnInterface getColumn(Column column) {
		if (column != null) {
			for (Iterator<TableColumnInterface> i = this.columns.iterator(); i
					.hasNext();) {
				TableColumnInterface item = i.next();
				if (item.getColumn().equals(column)) {
					return item;
				}
			}
		}
		return null;
	}
	public void addUpdateListener(Object list) {
		this.updateItemListener.addItem(list);
	}

	public String getProperty() {
		return property;
	}

	protected String getText(String label) {
		if (this.map != null) {
			SendableEntityCreator textItemClazz = map
					.getCreatorClasses(TextItems.class.getName());
			if (textItemClazz != null) {
				return ((TextItems) textItemClazz).getText(label, source, this);
			}
		}
		return label;
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event != null) {
			if (source.equals(event.getSource())) {
				if (event.getOldValue() == null && event.getNewValue() != null) {
					sourceList.add(event.getNewValue());
				}
			}else if (sourceList.equals(event.getSource())) {
//				if(TableListFX.SETALL.equalsIgnoreCase(event.getPropertyName())){
//					// Must be a Sort
//					ObservableList<TableColumn<Object, ?>> sortOrder = this.tableViewer.getSortOrder();
////					sortOrder.
//					if(sortOrder.size()>0){
//						TableColumn<Object, ?> tableColumn = sortOrder.get(0);
//						list.clear();
//						sourceList.setSort(tableColumn.textProperty().getValue());
////						list.addAll(sourceList);
//					}
//					
//					return;
//				}
//				
				
				
				
				if (event.getOldValue() == null && event.getNewValue() != null) {
					addItem(event.getNewValue());
				}else if (event.getPropertyName().equals(TableList.PROPERTY_ITEMS)) {
					if (event.getOldValue() != null && event.getNewValue() == null) {
						removeItem(event.getOldValue());
					}
				}
			}
		}
	}

	public SendableEntityCreator getCreator(Object entity) {
		return getMap().getCreatorClass(entity);
	}
	@Override
	public void refreshViewer() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Object> getItems(boolean all) {
		if(all){
			return sourceList;
		}
		return list;
	}

	@Override
	public void changed(ObservableValue<? extends Number> arg0, Number arg1,
			Number arg2) {
		withScrollPosition((Double) arg2);
	}
	
	public void findAllScrollBars() {
		for(TableViewFX table : tableViewer){
			if(table!=null){
				table.getScrollbar();
			}
		}
	}

	public WritableListValue<Object> getItems() {
		return list;
	}

}
