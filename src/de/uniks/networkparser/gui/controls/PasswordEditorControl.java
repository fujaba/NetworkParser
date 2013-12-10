package de.uniks.networkparser.gui.controls;

import de.uniks.networkparser.gui.table.FieldTyp;
import javafx.scene.control.PasswordField;

public class PasswordEditorControl extends EditControl<PasswordField>{

	@Override
	public Object getValue(boolean convert) {
		return this.control.getText();
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.PASSWORD;
	}

	@Override
	public void setValue(Object value) {
		getControl().setText(""+value);
	}

	@Override
	public PasswordField createControl() {
		return new PasswordField();
	}

}
