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

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.gui.controls.EditFieldMap;
import de.uniks.networkparser.gui.table.CellEditorElement;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.FieldTyp;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;


public class PropertyComposite extends HBox implements PropertyChangeListener, CellEditorElement{
	private Label westLabel;
	private Node centerComposite;
	private Label eastLabel;
	private GUIPosition labelOrientation=GUIPosition.WEST;
	private String labelPostText=": ";
	private EditFieldMap field=new EditFieldMap();
	private Object item;
//	private ArrayList<EventListener> listeners=new ArrayList<EventListener>();
	private ModelForm owner;
	private SendableEntityCreator creator;

	public PropertyComposite withOwner(ModelForm owner) {
		this.owner = owner;
//		owner.addProperty(this);
//		if(modelForm.getMap()!=null){
//			setDataBinding(modelForm.getMap(), modelForm.getItem());
//		}
		return this;
	}
	public String getLabelText() {
		if(field.getColumn().getLabel()!= null){
			return field.getColumn().getLabel();
		}
		return field.getColumn().getAttrName();
	}

	public PropertyComposite withLabelText(String value) {
		field.getColumn().withLabel(value);
		withDataBinding();
		return this;
	}

	public PropertyComposite withLabel(String value) {
		if(value != null){
			if(field.getMap()!=null){
				TextItems textClazz = (TextItems) field.getMap().getCreator(TextItems.class.getName(), true);
				if(textClazz !=null){
					field.getColumn().withLabel(textClazz.getText(value, item, this));
				}
			}else{
				field.getColumn().withLabel(value);
			}
		}
		withDataBinding();
		return this;
	}
	
	public PropertyComposite withFieldType(FieldTyp type){
		field.getColumn().withFieldTyp(type);
		return this;
	}

	 private PropertyComposite withDataBinding() {
//		 field.init(item, map, this.column);
		 initLabel();
		 field.withValue(getItemValue() );
		 if(item instanceof SendableEntity) {
			 ((SendableEntity)item).addPropertyChangeListener(field.getColumn().getAttrName(), this);
		 }
		 return this;
	 }
	
	 public PropertyComposite withDataBinding(IdMap map, Object item, Column column) {
		 this.item = item;
		 this.field.withColumn(column);
		 this.field.withMap(map);
		 if(map!=null){
			 this.creator = map.getCreatorClass(item);
		 }
		 return withDataBinding();
	 }
	 
	 public void initLabel() {
		 if(westLabel==null){
			westLabel = new Label();
			westLabel.setPadding(new Insets(3, 0, 0, 0));
			this.getChildren().add(westLabel);
		}
		 if(this.centerComposite==null){
			 this.centerComposite = this.field.getControl(null, getItemValue());
			this.getChildren().add(1, centerComposite);
		 }
		if(GUIPosition.WEST.equals(labelOrientation)){
			
			westLabel.setVisible(true);
			if(eastLabel!=null){
				eastLabel.setVisible(false);
			}
			String labelText = getLabelText();
			if(labelText!=null){
				westLabel.setText(labelText+labelPostText);
			}
		}else if(GUIPosition.EAST.equals(labelOrientation)){
			if(eastLabel==null){
				eastLabel = new Label();
				this.getChildren().add(2, eastLabel);
			}
			if(westLabel!=null){
				westLabel.setVisible(false);
			}
			String labelText = getLabelText();
			if(labelText != null){
				eastLabel.setText(labelText);
			}
		}else{
			if(westLabel!=null){
				westLabel.setVisible(false);
			}
			if(eastLabel!=null){
				eastLabel.setVisible(false);
			}
		}
	}
	 
	 public Object getItemValue(){
		 if(creator!=null && field.getColumn().getAttrName() != null){
			 return creator.getValue(item, this.field.getColumn().getAttrName());
		 }
		 return null;
	}
	 
	public void reload() {
		field.withValue( getItemValue() );	
	}

	public void save() {
		creator.setValue(item, field.getColumn().getAttrName(), field.getEditControl().getValue(true), IdMap.UPDATE);
	}
	
	public Label getLabelControl(){
		if(labelOrientation==null){
		}else if(labelOrientation.equals( GUIPosition.WEST)){
			return westLabel;
		}else if(labelOrientation.equals(GUIPosition.EAST)){
			return eastLabel;
		}
		return null;
	}
	
	public double getLabelWidth(Pane owner){
		Text text = new Text(getLabelControl().getText() );
		text.applyCss(); 

	    return text.getLayoutBounds().getWidth();
	}
	
	public void setLabelLength(double width){
		Label control = getLabelControl();
		if(control!=null){
			control.setMinWidth(width);
		}
	}
	
	@Override
	public void dispose() {
		if(item instanceof SendableEntity) {
			((SendableEntity) item).removePropertyChangeListener(this);
		}
	}
	
	@Override
	public boolean setFocus(boolean value) {
		if(field!=null){
			return field.setFocus(value);
		}
		return false;
	}
	
	@Override
	public void relocate(double x, double y) {
		super.relocate(x, y);
		withDataBinding();
	}
	
	@Override
	public PropertyComposite withColumn(Column column) {
		field.withColumn(column);
		return this;
	}
	
	public Column getColumn() {
		return field.getColumn();
	}
	
	public PropertyComposite withLabelOrientation(GUIPosition position) {
		this.labelOrientation = position;
		initLabel();
		return this;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onActive(boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean nextFocus() {
//FIXME		this.owner.focusnext();
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
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName()!=null){
			if(evt.getPropertyName().equals(field.getColumn().getAttrName())){
				// Test Thread and restarten
	//			field.setValue(evt.getNewValue(), false);
			}
		}
	}

	//FIXME
//	public String getLabelPostText() {
//		return labelPostText;
//	}
//
//
//	public void setLabelPostText(String value) {
//		this.labelPostText = value;
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
}
