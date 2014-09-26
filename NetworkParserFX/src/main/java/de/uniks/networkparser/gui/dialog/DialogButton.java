package de.uniks.networkparser.gui.dialog;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

public class DialogButton extends Button implements DialogElement{
	public enum Grafik{minimize,maximize,close}
	private DialogBox owner;
	
	public DialogButton withName(String value) {
		this.setText(value);
		return this;
	}
	
	public DialogButton withAction(Grafik value) {
		if(Grafik.close.equals(value)){
			this.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					owner.hide(DialogButton.this);
				}
			});
		}
		if(Grafik.minimize.equals(value)){
			this.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					owner.minimize();
				}
			});
		}
		if(Grafik.maximize.equals(value)){
			this.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent event) {
					owner.maximize();
				}
			});
		}
		return this;
	}
	
	public DialogButton withGrafik(Grafik type) {
		this.withAction(type);
        this.setFocusTraversable(false);

        getStyleClass().setAll("window-button", "window-"+type+"-button");
        StackPane graphic = new StackPane();
        graphic.getStyleClass().setAll("graphic");
        setGraphic(graphic);
        setMinSize(17, 17);
        setPrefSize(17, 17);
	
		return this;
	}
	
	@Override
	public DialogElement withOwner(DialogBox value) {
		this.owner = value;
		return this;
	}

	public DialogBox getOwner() {
		return owner;
	}
}
