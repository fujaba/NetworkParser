package de.uniks.networkparser.gui.javafx.table;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import de.uniks.networkparser.interfaces.GUIPosition;

public class TableViewFX extends TableView<Object> {
	private ScrollBar scrollbar;
	private TableComponent parent;
	private GUIPosition position;
	
	public TableViewFX withPosition(GUIPosition position){
		this.position = position;
		return this;
	}
	
	public GUIPosition getPosition(){
		return position;
	}
	
	public TableViewFX withListener(TableComponent tableComponent){
		this.parent = tableComponent;
		this.addEventFilter(MouseEvent.ANY, new EventHandler<MouseEvent>(){
			@Override
			public void handle(MouseEvent arg0) {
				parent.findAllScrollBars();
			}
		});
		return this;
	}
	public TableViewFX withItems(ObservableList<Object> items){
		setItems(items);
		return this;
	}
	
	public ScrollBar getScrollbar() {
		ScrollBar bar = (ScrollBar) lookup(".scroll-bar:vertical");
		if(bar != null){
			if(scrollbar==null){
				if(bar != TableViewFX.this.scrollbar){
					this.scrollbar = bar;
					bar.valueProperty().addListener(parent);
				}
			}
			parent.showScrollbar(this);
		}
		return scrollbar;
	}
	
	public ScrollBar getScrollbar(String orientation) {
		return (ScrollBar) lookup(".scroll-bar:"+orientation);
	}

	public void setScrollValue(double pos) {
		if(getScrollbar()!=null){
			getScrollbar().setValue(pos);
		}
	}
}
