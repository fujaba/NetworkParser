package de.uniks.networkparser.gui.controls;

/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
