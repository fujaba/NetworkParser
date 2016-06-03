package de.uniks.networkparser.ext.javafx.controls;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
