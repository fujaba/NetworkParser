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

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.value.WritableListValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TableComponent extends BorderPane implements PropertyChangeListener, TableComponentInterface {
	private ArrayList<TableColumnInterface> columns = new ArrayList<TableColumnInterface>();
	public static final String PROPERTY_COLUMN = "column";
	public static final String PROPERTY_ITEM = "item";
	protected IdMap map;
	protected Object source;
	protected SendableEntityCreator sourceCreator;
	private String property;
	protected UpdateSearchList updateItemListener;
	protected TableView<Object> centerTableViewer;
	protected TableView<Object> leftTableViewer;
	protected TableView<Object> rightTableViewer;
	protected boolean isToolTip;

	protected TableList sourceList;
	protected WritableListValue<Object> list;
	protected TableFilterView tableFilterView;
	
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
		TableView<Object> resultTableViewer=null;
		if (browserId.equals(GUIPosition.WEST)) {
			resultTableViewer = leftTableViewer;
		}else if (browserId.equals(GUIPosition.CENTER)) {
			resultTableViewer = centerTableViewer;
		}else if (browserId.equals(GUIPosition.EAST)) {
			resultTableViewer = rightTableViewer;
		}
		
		if (resultTableViewer == null) {
			resultTableViewer = new TableView<Object>();
			resultTableViewer.setItems(list);
//				for (Node n : resultTableViewer.lookupAll(".scroll-bar")) {
//					System.out.println(n);
//					if (n instanceof ScrollBar) {
//						ScrollBar bar = (ScrollBar) n;
//		//				System.out.println(bar.getOrientation() + ": range "
//		//						+ bar.getMin() + " => " + bar.getMax() + ", value "
//		//						+ bar.getValue());
//						if (bar.getOrientation().equals(Orientation.VERTICAL)) {
//							System.out.println(bar);
////							bar.setValue(pos);
//						}
//					}
//				}

			
			this.setOnScroll(new EventHandler<ScrollEvent>() {
				  @Override
				  public void handle(ScrollEvent event) {
					  System.out.println(centerTableViewer.getChildrenUnmodifiable().size());
					  System.out.println(event);
					  System.out.println("Scene.x: " + event.getSceneX());
					  System.out.println("Screen.x: " + event.getScreenX());
					  System.out.println(event.getDeltaY()+"::"+event.getSceneY()+"::"+event.getScreenY()+"::"+event.getTotalDeltaY()+"::"+event.getY());
//					  withScrollPosition(event.getTotalDeltaY());
				  }
			});
			
//			this.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
//	            @Override
//	            public void handle(ScrollEvent scrollEvent) {
//	               System.out.println("Scrolled Filter.");
//	            }
//	     });
//			this.addEventHandler(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
//	            @Override
//	            public void handle(ScrollEvent scrollEvent) {
//	               System.out.println("Scrolled Event.");
//	            }
//	     });
//			this.setOnScrollStarted(new EventHandler<ScrollEvent>() {
//	            @Override
//	            public void handle(ScrollEvent scrollEvent) {
//	               System.out.println("setOnScrollStarted Event.");
//	            }
//	     });
//			this.setOnScrollFinished(new EventHandler<ScrollEvent>() {
//	            @Override
//	            public void handle(ScrollEvent scrollEvent) {
//	               System.out.println("setOnScrollFinished Event.");
//	            }
//	     });
	
			
			 
			
			
			
//			resultTableViewer.addEventHandler(Event, arg1);
//			resultTableViewer.addListener(SWT.KeyDown, this);
//			resultTableViewer.addListener(SWT.MouseMove, this);
//			resultTableViewer.addListener(SWT.MouseUp, this);
//			resultTableViewer.addListener(SWT.MouseExit, this);
//			resultTableViewer.addListener(SWT.SELECTED, this);

			if (browserId.equals(GUIPosition.CENTER)) {
				this.setCenter(resultTableViewer);
				centerTableViewer = resultTableViewer; 
			}else if (browserId.equals(GUIPosition.WEST)) {
				this.setLeft(resultTableViewer);
				leftTableViewer = resultTableViewer; 
			}else if (browserId.equals(GUIPosition.EAST)) {
				this.setRight(resultTableViewer);
				rightTableViewer = resultTableViewer;
			}
			
//			if(centerTableViewer!=null && rightTableViewer != null){
//				DoubleProperty wProperty = new SimpleDoubleProperty();
//			    wProperty.bind(centerTableViewer.); // bind to Hbox width chnages
//			    wProperty.addListener(new ChangeListener() {
//			        @Override
//			        public void changed(ObservableValue ov, Object t, Object t1) {
//			           //when ever Hbox width chnages set ScrollPane Hvalue
//			         chatBoxScrollPane.setHvalue(chatBoxScrollPane.getHmax()); 
//			        }
//			    }) ;
//			}
			
			
		}
//			table.setMenu(headerMenu);
//
		return resultTableViewer;
	}
	
	public void test(){
        final ScrollBar  scrollBarV = (ScrollBar) centerTableViewer.lookup(".scroll-bar");
        scrollBarV.addEventFilter(ScrollEvent.ANY, new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
               System.out.println("Scrolled Filter.");
            }
     });
        scrollBarV.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent scrollEvent) {
               System.out.println("Scrolled Filter.");
            }
     });
//        scrollBarV.setVisible(false);
	}
	
	public TableComponent withScrollPosition(double pos){
		setPosition(leftTableViewer, pos);
		setPosition(centerTableViewer, pos);
		setPosition(rightTableViewer, pos);
		return this;
	}
	
	private void setPosition(TableView<Object> table, double pos) {
		if(table!=null){
			for (Node n : table.lookupAll(".scroll-bar")) {
				if (n instanceof ScrollBar) {
					ScrollBar bar = (ScrollBar) n;
	//				System.out.println(bar.getOrientation() + ": range "
	//						+ bar.getMin() + " => " + bar.getMax() + ", value "
	//						+ bar.getValue());
					if (bar.getOrientation().equals(Orientation.VERTICAL)) {
						bar.setValue(pos);
					}
				}
			}
		}
	}

	
//	public void setVisibleFixedColumns(boolean visible) {
//		if (fixedTableViewerLeft != null && !visible) {
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

		TableColumnFX columnFX = new TableColumnFX().withColumn(column);
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
		if(list==null){
			this.list = new SimpleListProperty<Object>(javafx.collections.FXCollections.observableList(new ArrayList<Object>()));
//			this.list.addPropertyChangeListener(this);
//			this.list.setIdMap(map);
			
			
			//FIXME
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
			if (sourceList.equals(event.getSource())) {
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
			
			
			
//FIXME			if (list.equals(event.getSource())) {
//				if (event.getPropertyName().equals(TableList.PROPERTY_ITEMS)) {
//					if (event.getOldValue() == null && event.getNewValue() != null) {
//						// ADD a new Item
//						if (fixedTableViewerLeft != null) {
//							fixedTableViewerLeft.add(event.getNewValue());
//						}
//						if (tableViewer != null) {
//							tableViewer.add(event.getNewValue());
//						}
//					} else if (event.getOldValue() != null
//							&& event.getNewValue() == null) {
//						if (fixedTableViewerLeft != null) {
//							fixedTableViewerLeft.remove(event.getOldValue());
//						}
//						if (tableViewer != null) {
//							tableViewer.remove(event.getOldValue());
//						}
//					}
//				}
//			} else if (source.equals(event.getSource())) {
//				if (event.getPropertyName().equals(property)) {
//					if (event.getOldValue() != null && event.getNewValue() == null) {
//						removeItem(event.getOldValue());
////						list.set(property + IdMap.REMOVE, evt.getOldValue());
////						refreshViewer();
//					}
//				}
//			} else{ 
////				if(lastUpdate < new Date().getTime()-3000){
////				lastUpdate = new Date().getTime();
//				// Must be an update
//				//refresh(event.getSource(), new String[] { event.getPropertyName() });
//				refresh(event.getSource(), new String[] { event.getPropertyName() });
//			}		
			
		}
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
}