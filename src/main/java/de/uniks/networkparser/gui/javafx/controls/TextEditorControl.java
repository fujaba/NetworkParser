package de.uniks.networkparser.gui.javafx.controls;

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
import java.util.Set;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Side;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;

public class TextEditorControl extends EditControl<TextField>{
	private AutoCompletionList completion;
	private AutoCompleteContextMenu autoCompleteContextMenu;

	@Override
	public Object getValue(boolean convert) {
		return this.control.getText();
	}
	
	public String getText() {
		return this.control.getText();
	}
	
	public void setText(String value) {
		this.control.setText(value);
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
		autoCompleteContextMenu = new AutoCompleteContextMenu(textField);

		textField.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {
			
			@Override
			public void handle(KeyEvent event) {
				
				AutoCompletionList listener = TextEditorControl.this.completion;
				if(listener != null) {
					TextField control = TextEditorControl.this.getControl();
					Set<String> list = listener.items(control.getText());
					
					if(list.size()>0 ) {
						if(!autoCompleteContextMenu.isShowing()) {
							autoCompleteContextMenu.show(control, Side.BOTTOM, 0, 0.0);
						}
						autoCompleteContextMenu.withSuggestions(list);
					}
				}
			}
		});
		return textField;
	}
	
	public TextEditorControl withAutoCompleting(AutoCompletionList value) {
		this.completion = value;
		return this;
	}

	public <T extends Event> void addEventHandler(EventType<T> eventType, EventHandler<T> eventHandler) {
		getControl().addEventHandler(eventType, eventHandler);
	}
}
