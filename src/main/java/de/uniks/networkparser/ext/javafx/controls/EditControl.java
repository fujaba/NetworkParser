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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.javafx.window.KeyListenerMap;
import de.uniks.networkparser.gui.CellEditorElement;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;

public abstract class EditControl<T extends Node> implements CellEditorElement, EventHandler<KeyEvent>, ChangeListener<Boolean> {
	protected T control;
	protected EditFieldMap listener;
	protected CellEditorElement owner;
	protected Column column;
	protected IdMap map;
	protected Object value;
	protected KeyListenerMap keyListener;

	@Override
	public EditControl<T> withColumn(Column value) {
		this.column = value;
		return this;
	}

	public EditControl<T> withMap(IdMap map) {
		this.map = map;
		return this;
	}

	public EditControl<T> withItem(Object value) {
		this.value = value;
		return this;
	}

	public EditControl<T> withOwner(CellEditorElement value) {
		this.owner = value;
		return this;
	}

	public abstract FieldTyp getControllForTyp(Object value);

	@Override
	public abstract Object getValue(boolean convert);

	public T getControl() {
		if (control == null ) {
			control = createControl(column);
			registerListener();
		}
		return control;
	}

	protected void registerListener(){
		control.setOnKeyPressed(this);
		control.focusedProperty().addListener(this);
		if(keyListener!= null) {
			control.addEventFilter(KeyEvent.ANY, keyListener);
		}
	}

	public EditControl<T> withListener(EditFieldMap owner){
		this.listener = owner;
		return this;
	}

	public EditFieldMap getListener() {
		return listener;
	}

	public void setVisible(boolean value){
		 T control=getControl();
		 if(control!=null){
			 control.setVisible(value);
		 }
	}

	public boolean isVisible(){
		 Node control=getControl();
		 if(control!=null){
			 return control.isVisible();
		 }
		return false;
	}

	@Override
	public boolean setFocus(boolean value){
		if(value){
			Node control=getControl();
			 if(control!=null){
				 return control.isFocused();
			 }
		}else{
			if(owner != null){
				owner.setFocus(value);
			}
		}
		 return false;
	}
	public boolean isActive(){
		if (control != null ) {
			return true;
		}
		return false;
	}

	@Override
	public void dispose(){
//		if(isActive()){
//		}
		control=null;
	}

	@Override
	public void cancel() {
		if(owner != null){
			owner.cancel();
		}
	}

	@Override
	public void apply(APPLYACTION action) {
		if(owner != null){
			owner.apply(action);
		}
	}

	public abstract T createControl(Column column);
	@Override
	public abstract CellEditorElement withValue(Object value);

	@Override
	public void handle(KeyEvent event) {
		if(event.getCode().equals(KeyCode.ENTER)){
			apply(APPLYACTION.ENTER);
		}else if(event.getCode().equals(KeyCode.TAB)){
			apply(APPLYACTION.TAB);
			nextFocus();
		}
	}

	public boolean clearEditor() {
		return false;
	}
	@Override
	public boolean onActive(boolean value) {
		return false;
	}

	@Override
	public boolean nextFocus(){
		if(owner != null){
			return owner.nextFocus();
		}
		return false;
	}
	@Override
	public void changed(ObservableValue<? extends Boolean> observable,
			Boolean oldValue, Boolean newValue) {
		// FOCUSLost
		if(newValue==false){
			Object newControlValue = getValue(false);
			if((value == null && newControlValue!= null) || (value != null && !value.equals(newControlValue))) {
				apply(APPLYACTION.FOCUS);
			}
		}

	}

	public EditControl<T> withListener(KeyListenerMap value) {
		this.keyListener = value;
		return this;
	}

	public boolean isFocus() {
		return control.isFocused();
	}
}
