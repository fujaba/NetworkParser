package de.uniks.networkparser.gui.controls;

import java.util.Set;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class AutoCompletePopupSkin implements Skin<AutoCompletePopup> {
	public final int LIST_CELL_HEIGHT = 24;
    private AutoCompletePopup control;
    private ListView<String> suggestionList;
	private ObservableList<String> suggestions;

    public AutoCompletePopupSkin(AutoCompletePopup control){
        this.control = control;
        
        suggestions = FXCollections.observableArrayList();
        suggestionList = new ListView<String>(suggestions);

        suggestionList.getStyleClass().add(AutoCompletePopup.DEFAULT_STYLE_CLASS);

        suggestionList.setOnMouseClicked(new EventHandler<MouseEvent>() {
        	@Override
        	public void handle(MouseEvent event) {
        		if (event.getButton() == MouseButton.PRIMARY){
                  onSuggestionChoosen(suggestionList.getSelectionModel().getSelectedItem());
              }	
        	}
        });
        suggestionList.setOnKeyPressed(new EventHandler<KeyEvent>() {
        	@Override
        	public void handle(KeyEvent event) {
              if(event.getCode()==KeyCode.ENTER) {
                  onSuggestionChoosen(suggestionList.getSelectionModel().getSelectedItem());
              }
        	}
		});
        
        suggestionList.prefHeightProperty().bind(
                Bindings.size(suggestionList.getItems()).multiply(LIST_CELL_HEIGHT)
                .add(15));
    }
    
    public AutoCompletePopupSkin withSuggestions(Set<String> value) {
    	suggestions.clear();
    	suggestions.addAll(value);
    	return this;
    }
    

    private void onSuggestionChoosen(String suggestion){
    	control.accept(suggestion);
    }


    @Override
    public Node getNode() {
        return suggestionList;
    }

    @Override
    public AutoCompletePopup getSkinnable() {
        return control;
    }

    @Override
    public void dispose() {
    	control.hide();
    }
    
    public AutoCompletePopupSkin withWidth(double width) {
    	suggestionList.setPrefWidth(width);
		return this;
    }
}
