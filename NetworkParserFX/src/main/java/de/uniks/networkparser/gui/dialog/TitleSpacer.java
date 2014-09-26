package de.uniks.networkparser.gui.dialog;

import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class TitleSpacer extends Region implements DialogElement{
	private DialogBox owner;

	public TitleSpacer() {
        HBox.setHgrow(this, Priority.ALWAYS);
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
