package de.uniks.networkparser.gui.javafx.controls;

import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import de.uniks.networkparser.gui.javafx.resource.Styles;

public class AutoCompleteContextMenu extends ContextMenu{
	private TextField control;

	public AutoCompleteContextMenu(TextField control){
        this.control = control;
        control.setContextMenu(this);
        AutoCompleteContextMenu.this.getScene().getStylesheets().add(Styles.getPath());
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