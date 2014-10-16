package de.uniks.networkparser.gui.controls;

/*
 NetworkParser
 Copyright (c) 2011 - 2014, Stefan Lindel
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
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.FieldTyp;

public class TextEditorControl extends EditControl<TextField>{
	private AutoCompletion<?> completion;

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
		this.value = value;
		getControl().setText(""+value);
		getControl().selectAll();
		return this;
	}

	@Override
	public TextField createControl(Column column) {
		TextField textField = new TextField();
		textField.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
			@Override
			public void handle(KeyEvent event) {
				AutoCompletion<?> listener = TextEditorControl.this.completion;
				if(listener != null) {
					List<?> list = listener.items(event.getText());
					
				}
			}
		});
		return textField;
	}
	
	public TextEditorControl withAutoCompleting(AutoCompletion<?> value) {
		this.completion = value;
		return this;
	}
}
