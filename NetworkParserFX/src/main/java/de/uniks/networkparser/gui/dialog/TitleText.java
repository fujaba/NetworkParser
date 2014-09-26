package de.uniks.networkparser.gui.dialog;

import javafx.scene.control.Label;

public class TitleText extends Label implements DialogElement{
    private DialogBox owner;

	public TitleText() {
    	setMaxHeight(Double.MAX_VALUE);
    	getStyleClass().add("window-title");
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
