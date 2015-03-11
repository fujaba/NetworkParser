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
import java.beans.PropertyChangeSupport;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import de.uniks.networkparser.DefaultTextItems;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.Style;
import de.uniks.networkparser.gui.TableList;
import de.uniks.networkparser.gui.controls.EditFieldMap;
import de.uniks.networkparser.gui.resource.Styles;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.logic.InstanceOf;

public class TableComponent extends BorderPane implements PropertyChangeListener, ChangeListener<Number> {
	private ArrayList<TableColumnFX> columns = new ArrayList<TableColumnFX>();
	public static final String PROPERTY_COLUMN = "column";
	public static final String PROPERTY_ITEM = "item";
	protected JsonIdMap map;
	protected Object source;
	protected SendableEntityCreator sourceCreator;
	private String property;
	protected TableViewFX[] tableViewer=new TableViewFX[3];
	protected boolean isToolTip;
	protected ContextMenu contextMenu;

	protected TableList sourceList;
	private ObservableList<Object> list;
	private TableFilterView tableFilterView;
	private Menu visibleItems;
	private SelectionListener listener;
	private EditFieldMap field=new EditFieldMap();
	private BorderPane northComponents;
	private TextField searchText;
	
	public IdMapEncoder getMap() {
		return map;
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
				withColumn(new Column().withAttrName(property, edit).withStyle(new Style().withWidth(100)));
			}
		}
		return this;
	}
	
	public TableView<Object> getBrowserView(GUIPosition browserId) {
		if (browserId.equals(GUIPosition.WEST)) {
			if(tableViewer[0]==null){
				tableViewer[0]=getBrowser();
				tableViewer[0].withPosition(browserId);
				this.setLeft(tableViewer[0]);
			}
			return tableViewer[0];
		}else if (browserId.equals(GUIPosition.CENTER)) {
			if(tableViewer[1]==null){
				tableViewer[1]=getBrowser();
				tableViewer[1].withPosition(browserId);
				this.setCenter(tableViewer[1]);
			}
			return tableViewer[1];
		}else if (browserId.equals(GUIPosition.EAST)) {
			if(tableViewer[2]==null){
				tableViewer[2]=getBrowser();
				tableViewer[2].withPosition(browserId);
				this.setRight(tableViewer[2]);
			}
			return tableViewer[2];
		}
		return null;
	}
	private TableViewFX getBrowser(){
		TableViewFX resultTableViewer=new TableViewFX();
		resultTableViewer.withListener(this).withItems(list);
		resultTableViewer.setEditable(true);
		
		resultTableViewer.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // just in case you didnt already set the selection model to multiple selection.
		resultTableViewer.getSelectionModel().getSelectedIndices().addListener(listener);
		listener.withTableViewer(resultTableViewer);
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

	public TableComponent withColumn(Column column) {
		init();
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

		// Recalculate Width
		TableViewFX table = (TableViewFX) browserView;
		showScrollbar(table);
		
		return this;
	}

	public void showScrollbar(TableViewFX table) {
		if(table.getPosition() == GUIPosition.CENTER){
			return;
		}
		if(table.widthProperty().get()<1){
			return;
		}
		ObservableList<TableColumn<Object, ?>> listOfColumns = table.getColumns(); 
		double withBrowser=0;
		for(Iterator<TableColumn<Object, ?>> iterator = listOfColumns.iterator();iterator.hasNext();){
			TableColumn<Object, ?> item = iterator.next();
			withBrowser += item.getWidth();
		}
		withBrowser += 2;
		ScrollBar scrollbar = table.getScrollbar("vertical");
		if(scrollbar.isVisible()){
			withBrowser += scrollbar.getWidth()+3;
		}else{
			ScrollBar scrollbarHorizontal = table.getScrollbar("horizontal");
			if(scrollbarHorizontal!=null){
				if(scrollbarHorizontal.isVisible()){
					withBrowser += scrollbar.getWidth()+3;
				}
			}
		}
		BorderPane.clearConstraints(getLeft());
		table.setPrefWidth(withBrowser);
		BorderPane.clearConstraints(getLeft());
	}

	
	public TableComponent withMap(JsonIdMap map){
		this.map = map;
		this.field.withMap(map);
		return this;
	}

	public TableComponent withSearchProperties(String... searchProperties) {
		if(searchText == null) {
			createNothElement();
			searchText = new TextField();
	        searchText.getStylesheets().add(Styles.getPath());
	        searchText.getStyleClass().add("searchbox");
	        searchText.setPromptText(getText(DefaultTextItems.SEARCH));
	        searchText.setMinHeight(24);
	        searchText.setPrefSize(200, 24);
	        searchText.setEditable(true);
	        searchText.textProperty().addListener(tableFilterView);
	        northComponents.setCenter(searchText);
		}
	    tableFilterView.setSearchProperties(searchProperties);
	    tableFilterView.refresh();
		return this;
	}
	
	void createNothElement() {
		if(northComponents==null){
			this.setTop(northComponents = new BorderPane());
//			withAnchor(northComponents);
		}
	}
	
	public TableComponent withElement(Node... elements) {
		createNothElement();
		Node element = this.northComponents.getRight();
		if(element == null) {
			HBox hBox = new HBox();
			hBox.setAlignment(Pos.CENTER);
			element  = hBox;
			this.northComponents.setRight(element);
		}
		if(element instanceof HBox) {
			HBox parent = (HBox) element;
			for(Node item : elements) {
				parent.getChildren().add(item);
				HBox.setMargin(item, new Insets(0, 5, 0, 5));
			}
		}
		return this;
	}
	
	void init(){
//		withAnchor(this);
		
		if(contextMenu==null){
			contextMenu = new ContextMenu();
			visibleItems = new Menu();
			visibleItems.setText(getText(DefaultTextItems.COLUMNS));
			
			Menu saveAs= new Menu(getText(DefaultTextItems.SAVEAS));
			saveAs.getItems().addAll(new CSVExporter(this), new ExcelExporter(this));
			
			contextMenu.getItems().add(visibleItems);
			contextMenu.getItems().add(saveAs);
		}

		if(list==null){
			this.list = FXCollections.observableArrayList();
		}
		
		if(listener==null){
			this.listener = new SelectionListener();
		}
		
		if(sourceList==null){
			this.sourceList = new TableList();
			this.sourceList.addPropertyChangeListener(this);
			this.sourceList.setIdMap(map);
		}

		if(tableFilterView==null){
			tableFilterView = new TableFilterView(this);
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
				this.list.add(item);
			}
			this.addUpdateListener(item);
			tableFilterView.refreshCounter();
			return true;
		}
		return false;
	}

	public boolean removeItem(Object item) {
		if (sourceList.contains(item)) {
			sourceCreator.setValue(source, property, item, IdMapEncoder.REMOVE);
			this.removeUpdateListener(item);
			sourceList.remove(item);
			if (getParent() instanceof PropertyChangeListener) {
				((PropertyChangeListener) getParent())
						.propertyChange(new PropertyChangeEvent(this,
								PROPERTY_ITEM, item, null));
			}
			tableFilterView.refreshCounter();
			return true;
		}
		return false;
	}
	
	public TableComponent withList(TableList item) {
		return withList(item, TableList.PROPERTY_ITEMS);
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
				Object entity = iterator.next();
				addItem(entity);
			}
		}
		addUpdateListener(source);
		return this;
	}
	
	public TableColumnFX getColumn(Column column) {
		if (column != null) {
			for (Iterator<TableColumnFX> i = this.columns.iterator(); i
					.hasNext();) {
				TableColumnFX item = i.next();
				if (item.getColumn().equals(column)) {
					return item;
				}
			}
		}
		return null;
	}
	
	void addUpdateListener(Object item){
		if (item instanceof SendableEntity) {
			((SendableEntity) item).addPropertyChangeListener(this);
		} else if(item instanceof PropertyChangeSupport){
			((PropertyChangeSupport) item).addPropertyChangeListener(this);
		}else {
			try {
				Method method = item.getClass().getMethod("addPropertyChangeListener", java.beans.PropertyChangeListener.class );
				method.invoke(item, this);
			} catch (Exception e) {
				
			}
		}
	}
	
	void removeUpdateListener(Object item){
		if (item instanceof SendableEntity) {
			((SendableEntity) item).removePropertyChangeListener(this);
		} else if(item instanceof PropertyChangeSupport){
			((PropertyChangeSupport) item).removePropertyChangeListener(this);
		}else {
			try {
				Method method = item.getClass().getMethod("removePropertyChangeListener", java.beans.PropertyChangeListener.class );
				method.invoke(item, this);
			} catch (Exception e) {
				
			}
		}
	}

	public String getProperty() {
		return property;
	}

	protected String getText(String label) {
		if (this.map != null) {
			SendableEntityCreator textItemClazz = map
					.getCreator(TextItems.class.getName(), true);
			if (textItemClazz != null) {
				return ((TextItems) textItemClazz).getText(label, source, this);
			}
		}
		return label;
	}

	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		
		if (event == null) {
			return;
		}
		Platform.runLater(new TablePropertyChange(this, event, source, property, sourceList));
	}
	
	public Iterator<TableColumnFX> getColumnIterator() {
		return columns.iterator();
	}

	public SendableEntityCreator getCreator(Object entity) {
		return getMap().getCreatorClass(entity);
	}
	
	public Object getElement(int row) {
		return list.get(row);
	}

	public List<Object> getItems(boolean all) {
		if(all){
			return sourceList;
		}
		return list;
	}
	
	public ArrayList<TableColumnFX> getColumns() {
		return columns;
	}
	
	@Override
	public void changed(ObservableValue<? extends Number> arg0, Number arg1,
			Number arg2) {
		withScrollPosition((Double) arg2);
	}
	
	void findAllScrollBars() {
		for(TableViewFX table : tableViewer){
			if(table!=null){
				table.getScrollbar();
			}
		}
	}

	public EditFieldMap getFieldFactory() {
		return field;
	}
	public TableComponent withCounterColumn(Column column) {
		tableFilterView.withCounterColumn(column);
		return this;
	}
	
	public ObservableList<Object> getSelection() {
		return getBrowserView(GUIPosition.CENTER).getSelectionModel().getSelectedItems();
	}
	
	public JsonArray saveColumns() {
		JsonArray list=new JsonArray();
		for(TableColumnFX column : columns) {
			list.add(map.encode(column.getColumn(), Filter.regard(InstanceOf.value(Style.class))));
		}
		return list;
	}
	
	public boolean loadColumns(JsonArray columns) {
		if(columns==null || columns.size()<this.columns.size()) {
			return false;
		}
		if(!(map instanceof JsonIdMap)) {
			return false;
		}
		for(int i=0;i<this.columns.size();i++) {
			TableColumnFX column = this.columns.get(i);
			map.decode(column.getColumn(), columns.getJSONObject(i));
			column.refresh();
		}
		return true;
	}

}
