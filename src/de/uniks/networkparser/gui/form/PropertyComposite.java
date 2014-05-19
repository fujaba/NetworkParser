package de.uniks.networkparser.gui.form;

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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.EventListener;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.controls.EditControl;
import de.uniks.networkparser.gui.table.CellEditorElement;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.FieldTyp;
import de.uniks.networkparser.gui.table.TableCellFX;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;


public class PropertyComposite extends HBox implements PropertyChangeListener, CellEditorElement{
	private Label westLabel = new Label();
	private Node centerComposite;
	private Label eastLabel = new Label();
	private GUIPosition labelOrientation=GUIPosition.WEST;
	private String labelPostText=": ";
	private TableCellFX field=new TableCellFX();
	private SendableEntityCreator creator;
	private Object item;
	private Column column=new Column();
	private IdMap map;
	private ArrayList<EventListener> listeners=new ArrayList<EventListener>();

	public PropertyComposite() {
		
	}
	public String getLabelText() {
		return column.getLabel();
	}
//		if(this.getParent() instanceof ModelForm){
//			ModelForm modelForm=(ModelForm) this.getParent();
//			modelForm.addProperty(this);
//			
//			if(modelForm.getMap()!=null){
//				setDataBinding(modelForm.getMap(), modelForm.getItem());
//			}
//		}
//	}
//	
//
//	public void setLabelText(String value) {
//		this.column.withLabel(value);
//		setDataBinding();
//	}
//	
//	public void setLabel(String value) {
//		if(value != null){
//			if(this.map!=null){
//				TextItems textClazz = (TextItems) map.getCreatorClasses(TextItems.class.getName());
//				if(textClazz !=null){
//					column.withLabel(textClazz.getText(value, item, this));
//				}
//			}else{
//				column.withLabel(value);
//			}
//		}
//		setDataBinding();
//	}
//	
//	public void setFieldType(EditFields type){
//		this.field.setFormat(type);
//	}
//
//	
//
//	public LabelPosition getLabelOrientation() {
//		return labelOrientation;
//	}
//	
//	public void initLabel() {
//		if(LabelPosition.WEST.equals(labelOrientation)){
//			westLabel.setVisible(true);
//			
//			eastLabel.setVisible(false);
//			String labelText = getLabelText();
//			if(labelText!=null){
//				westLabel.setText(labelText+labelPostText);
//			}
//		}else if(LabelPosition.EAST.equals(labelOrientation)){
//			westLabel.setVisible(false);
//			eastLabel.setVisible(true);
//			String labelText = getLabelText();
//			if(labelText != null){
//				eastLabel.setText(labelText);
//			}
//		}else{
//			westLabel.setVisible(false);
//			eastLabel.setVisible(false);
//		}
//	}
//
//
//	public void setLabelOrientation(LabelPosition labelOrientation) {
//		this.labelOrientation = labelOrientation;
//		initLabel();
//	}
//	
//	private CLabel getLabelControl(){
//		if(labelOrientation==null){
//		}else if(labelOrientation.equals(LabelPosition.WEST)){
//			return westLabel;
//		}else if(labelOrientation.equals(LabelPosition.EAST)){
//			return eastLabel;
//		}
//		return null;
//	}
//	
//	public int getLabelLength(){
//		CLabel control = getLabelControl();
//		if(control!=null){
//			Point size = control.computeSize(SWT.DEFAULT, SWT.DEFAULT);
//			return size.x;
//		}
//		return 0;
//	}
//	
//	public void setLabelLength(int width){
//		CLabel control = getLabelControl();
//		if(control!=null){
//			layout.setFixWestSize(width);
//			control.layout();
//		}
//	}
//
//	public String getLabelPostText() {
//		return labelPostText;
//	}
//
//
//	public void setLabelPostText(String value) {
//		this.labelPostText = value;
//	}
//		
//	public void setProperty(String value){
//		this.column.withAttrName(value);
//		
//		setDataBinding();
//	}
//	public String getProperty(){
//		return column.getAttrName();
//	}
//	
//	private void setDataBinding() {
//		setDataBinding(map, item, column);
//	}
//
//	public void setDataBinding(IdMap map, Object item) {
//		setDataBinding(map, item, column);
//	}
//	public void setDataBinding(Column column) {
//		if(column!=null){
//			this.column = column;
//		}
//		field.init(column, "");
//		initControl();
//		
//		initLabel();
//	}
//	public void setDataBinding(IdMap map, Object item, Column column) {
//		this.item = item;
//		if(column!=null){
//			this.column = column;
//		}
//		if(map!=null){
//			this.creator = map.getCreatorClass(item);
//			this.map = map;
//			field.init(item, map, this.column);
//		}
//		initControl();
//		if(creator!=null && column.getAttrName() != null){
//			field.setValue(creator.getValue(item, this.column.getAttrName()), false);
//		}
//		initLabel();
//		if(item instanceof SendableEntity) {
//			((SendableEntity) item).addPropertyChangeListener(this.column.getAttrName(), this);
//		}
//	}
//	
//	private void initControl(){
//		field.setParent(this, centerComposite);
//	}
//
//	public void focusnext(){
//		if(this.getParent() instanceof ModelForm){
//			ModelForm parent=(ModelForm) this.getParent();
//			parent.focusnext();
//		}
//	}
//
//	public void reload() {
//		Object value = creator.getValue(item, column.getAttrName());
//		field.setValue(value, false);
//		
//	}
//
//	public void save() {
//		try {
//			creator.setValue(item, column.getAttrName(), field.getEditorValue(true), IdMap.UPDATE);
//		} catch (ParseException e) {
//		}
//	}
//
//	public void handleDefaultSelection(SelectionEvent event) {
////		field.dispose();
//	}
//
//	@Override
//	public Object getEditorValue(boolean convert) throws ParseException {
//		return field.getEditorValue(convert); 
//	}
//	
//	public Control getEditorField(){
//		return field.getControl();
//	}
//	
//	public void setFormatLayout(String value){
//		field.setNumberFormat(value);
//	}
//	public String getFormatLayout(){
//		return field.getNumberFormat();
//	}
//	
//	public void addChoiceList(Object value){
//		field.addChoiceList(value);
//	}
//	
//	public String toString(){
//		if(column!=null){
//			return field.getNumberFormat()+" "+column.getLabel()+":"+column.getAttrName();
//		}
//		return super.toString();
//	}
//
//	@Override
//	public void focusGained(FocusEvent e) {
//		for(EventListener listener : listeners){
//			if(listener instanceof FocusListener){
//				((FocusListener)listener).focusGained(e);
//			}
//		}
//		if (this.getParent() instanceof ModelForm) {
//			ModelForm parent = (ModelForm) this.getParent();
//			parent.onFocus(this);
//		}
//	}
//
//	@Override
//	public void focusLost(FocusEvent e) {
//		for(EventListener listener : listeners){
//			if(listener instanceof FocusListener){
//				((FocusListener)listener).focusLost(e);
//			}
//		}
//	}
//
//	@Override
//	public void keyPressed(KeyEvent e) {
//		for(EventListener listener : listeners){
//			if(listener instanceof KeyListener){
//				((KeyListener)listener).keyPressed(e);
//			}
//		}
//		if (this.getParent() instanceof ModelForm) {
//			ModelForm parent = (ModelForm) this.getParent();
//			parent.onKeyPressed(e);
//		}
//	}
//
//	@Override
//	public void keyReleased(KeyEvent e) {
//		for(EventListener listener : listeners){
//			if(listener instanceof KeyListener){
//				((KeyListener)listener).keyReleased(e);
//			}
//		}
//		if (this.getParent() instanceof ModelForm) {
//			ModelForm parent = (ModelForm) this.getParent();
//			parent.onKeyReleased(e);
//		}
//	}
//	
//	public void addFieldListener(EventListener listener) {
//		this.listeners.add(listener);
//	}
//
//	@Override
//	public void keyTraversed(TraverseEvent e) {
//		if (this.getParent() instanceof ModelForm) {
//			ModelForm parent = (ModelForm) this.getParent();
//			parent.onKeyTraversed(e);
//		}
//	}
//
//	@Override
//	public void dispose() {
//		if(item instanceof SendableEntity) {
//			((SendableEntity) item).removePropertyChangeListener(this);
//		}
//		super.dispose();
//	}
//
	@Override
	public CellEditorElement withColumn(Column column) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}
//
//	@Override
//	public boolean setFocus(boolean value) {
//		if(field!=null){
//			return field.setFocus(value);
//		}
//		return false;
//	}

	@Override
	public boolean onActive(boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean nextFocus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void apply() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object getValue(boolean convert) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CellEditorElement withValue(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setFocus(boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName()!=null){
		if(evt.getPropertyName().equals(column.getAttrName())){
			// Test Thread and restarten
//			field.setValue(evt.getNewValue(), false);
		}
	}
	}
}
