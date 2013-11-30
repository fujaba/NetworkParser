package de.uniks.networkparser.gui.table;


import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

import org.sdmlib.serialization.DefaultTextItems;

import de.uniks.networkparser.gui.resource.Styles;

public class SearchTableComponent extends TableComponent{
	private BorderPane northComponents;
	private TextField searchText;
	private TableFilterViewFX filter;

	@Override
	public void init(){
		if(tableFilterView==null){
			filter = new TableFilterViewFX(this);
			tableFilterView = filter;
		}
		
		super.init();
		if(northComponents==null){
			this.setTop(northComponents = new BorderPane());
			withAnchor(northComponents);
			
			searchText = new TextField();
			System.out.println(Styles.getPath());
	        searchText.getStylesheets().add(Styles.getPath());
	        searchText.getStyleClass().add("searchbox");
	        searchText.setPromptText(getText(DefaultTextItems.SEARCH));
	        searchText.setMinHeight(24);
	        searchText.setPrefSize(200, 24);
	        
			searchText.textProperty().addListener(filter);
			withAnchor(searchText);
			northComponents.setCenter(searchText);
		}
	}

//	public void refreshNorthLayout(){
//		if(firstNorth!=null){
//			if(firstNorth.getChildren().length<1){
//				firstNorth.setVisible(false);
//				firstNorth.dispose();
//				firstNorth=null;
//			}
//			northComponents.setLayout(new GridLayout(northComponents.getChildren().length, false));
//			this.layout(true);
//		}
//	}
//	
//	public boolean finishDataBinding(Object item, String property) {
//		boolean result=super.finishDataBinding(item, property);
//		refreshNorthLayout();
//		
//		return result;
//	}
//	
//	public Composite getNorth() {
//		return northComponents;
//	}
//
//	public Composite getFirstNorth() {
//		return firstNorth;
//	}
//
//	@Override
//	public void executeEvent(PropertyChangeEvent event) {
//		super.executeEvent(event);
//		
//		if (event != null && source.equals(event.getSource()) ){
//			if(getProperty()!=null && getProperty().equals(event.getPropertyName())) {
//				if(event.getNewValue()==null){
//					if(event.getOldValue()!=null){
//						//REMOVE ENTRY
//						removeItem(event.getOldValue());
//
//					}
//				}else if(event.getOldValue()==null){
//					// add Item
//					addItem(event.getNewValue());
//				}
//			}
//		}
//	}
//
//	public void setKeyListener(KeyListener listener) {
//		searchText.addKeyListener(listener);
//	}
//	
//	public Text getSearchField() {
//		return searchText;
//	}
//	@Override
//	public void addKeyListener(KeyListener listener) {
//		super.addKeyListener(listener);
//		getSearchField().addKeyListener(listener);
//	}

}
