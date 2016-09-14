package de.uniks.networkparser.ext.javafx.dialog;

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
