package de.uniks.networkparser.gui.table;


import javafx.event.EventHandler;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;

public class TableViewFX extends TableView<Object> {
	private ScrollBar scrollbar;
	private TableComponent parent;
	
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
	
	public ScrollBar getScrollbar() {
		if(scrollbar==null){
			ScrollBar bar = (ScrollBar) lookup(".scroll-bar:vertical");
			if(bar!=null && bar != TableViewFX.this.scrollbar){
				TableViewFX.this.scrollbar = bar;
				bar.valueProperty().addListener(parent);
			}
		}
		return scrollbar;
	}
	public void setScrollValue(double pos) {
		if(getScrollbar()!=null){
			getScrollbar().setValue(pos);
		}
	}
}
