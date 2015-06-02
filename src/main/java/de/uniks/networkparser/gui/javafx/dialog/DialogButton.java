package de.uniks.networkparser.gui.javafx.dialog;

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
