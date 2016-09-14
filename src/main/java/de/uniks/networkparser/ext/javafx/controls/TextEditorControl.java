package de.uniks.networkparser.ext.javafx.controls;

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
