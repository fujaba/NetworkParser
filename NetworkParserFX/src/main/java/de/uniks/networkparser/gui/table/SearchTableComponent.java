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
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import de.uniks.networkparser.DefaultTextItems;
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
