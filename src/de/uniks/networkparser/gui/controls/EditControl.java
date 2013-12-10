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
import javafx.scene.control.Control;
import de.uniks.networkparser.gui.table.CellEditorElement;
import de.uniks.networkparser.gui.table.Column;

public abstract class EditControl<T extends Control> implements CellEditorElement{
	protected T control;
	protected CellEditorElement cellOwner;
	protected Column column;

	@Override
	public EditControl<T> withColumn(Column value) {
		this.column = value;
		return this;
	}

	
	public T getControl() {
		if (control == null ) {
			control = createControl();
		}
		return control;
	}
	
	public EditControl<T> withOwner(CellEditorElement owner){
		this.cellOwner = owner;
		return this;
	}
	
	public void setVisible(boolean value){
		 T control=getControl();
		 if(control!=null){
			 control.setVisible(value);
		 }
	}
	
	public boolean isVisible(){
		 Control control=getControl();
		 if(control!=null){
			 return control.isVisible();
		 }
		return false;
	}
	
	public boolean setFocus(boolean value){
		if(value){
			 Control control=getControl();
			 if(control!=null){
				 return control.isFocused();
			 }
		}else{
			if(cellOwner != null){
				cellOwner.setFocus(value);
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
	
	public void dispose(){
		if(isActive()){
//			control.di();
		}
		control=null;
	}
	
	@Override
	public void cancel() {
		if(cellOwner != null){
			cellOwner.cancel();
		}
	}

	@Override
	public void apply() {
		if(cellOwner != null){
			cellOwner.apply();
		}
	}
	
	public void addChoiceList(Object value){
		
	}
	public abstract T createControl();
	public abstract void setValue(Object value);
//	public abstract boolean isCorrect(Object value, EditFields field) throws ParseException;
	
//	public Point getLocation(){
//		if(control!=null){
//			return control.getLocation();
//		}
//		return null;
//	}

//	public void keyReleased(KeyEvent e) {
//		if(e.keyCode==SWT.CR){
//			apply();
//		}
//	}

	public boolean clearEditor() {
		return false;
	}
	@Override
	public boolean onActive(boolean value) {
		return false;
	}
	
	public boolean nextFocus(){
		return false;
	}
}
