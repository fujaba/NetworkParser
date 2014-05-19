package de.uniks.networkparser.gui.controls;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
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
import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.FieldTyp;

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
	public TextEditorControl withValue(Object value) {
		getControl().setText(""+value);
		getControl().selectAll();
		return this;
	}

	@Override
	public TextField createControl(Column column) {
		TextField textField = new TextField();
		textField.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>()
	            {
	                @Override
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
