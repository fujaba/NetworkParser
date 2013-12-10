package de.uniks.networkparser.gui.controls;

import de.uniks.networkparser.gui.table.FieldTyp;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TextEditorControl extends EditControl<TextField>{
	@Override
	public Object getValue(boolean convert) {
		return this.control.getText();
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		return FieldTyp.TEXT;
	}

	@Override
	public void setValue(Object value) {
		getControl().setText(""+value);
		getControl().selectAll();
	}

	@Override
	public TextField createControl() {
		TextField textField = new TextField();
		textField.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>()
	            {
	                public void handle(KeyEvent t)
	                {
	                	if(t.getCode()==KeyCode.ENTER){
	                		apply();
	                	}
//	                    System.out.println("char = '" + t.getCharacter() + "'");
	                }
	            });
		return textField;
	}

}
