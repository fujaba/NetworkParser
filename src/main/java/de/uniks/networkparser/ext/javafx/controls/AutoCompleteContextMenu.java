package de.uniks.networkparser.ext.javafx.controls;

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
		int i=1;
		for(String item : values) {
			MenuItem menuItem = new MenuItem();
			if(i % 2 == 1) {
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
			i++;
		}
		return this;
	}


	private void onSuggestionChoosen(String suggestion){
		control.setText(suggestion);
		this.hide();
	}
}
