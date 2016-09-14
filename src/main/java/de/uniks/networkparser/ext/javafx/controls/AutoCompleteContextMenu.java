package de.uniks.networkparser.ext.javafx.controls;

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
import java.util.Set;
import de.uniks.networkparser.ext.javafx.StyleFX;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;

public class AutoCompleteContextMenu extends ContextMenu{
	private TextField control;

	public AutoCompleteContextMenu(TextField control){
		this.control = control;
		control.setContextMenu(this);
		AutoCompleteContextMenu.this.getScene().getStylesheets().add(StyleFX.getPath());
	}

	public AutoCompleteContextMenu withSuggestions(Set<String> values) {
		getItems().clear();
		boolean alternative=false;
		for(String item : values) {
			MenuItem menuItem = new MenuItem();
			if(alternative) {
				menuItem.getStyleClass().add("suggestionscell");
			}else{
				menuItem.getStyleClass().add("suggestionscellodd");
			}
			Label text = new Label(item);
			text.setMinWidth(control.getWidth()-30);

			menuItem.setGraphic(text);
			menuItem.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent e) {
					MenuItem mnu = (MenuItem) e.getSource();
					onSuggestionChoosen(((Label)mnu.getGraphic()).getText());
				}
			});

			getItems().add(menuItem);
			alternative = !alternative;
		}
		return this;
	}

	private void onSuggestionChoosen(String suggestion){
		control.setText(suggestion);
		this.hide();
	}
}
