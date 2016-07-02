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
