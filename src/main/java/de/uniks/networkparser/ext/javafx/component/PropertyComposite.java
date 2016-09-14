package de.uniks.networkparser.ext.javafx.component;
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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.TextItems;
import de.uniks.networkparser.ext.javafx.controls.EditControl;
import de.uniks.networkparser.ext.javafx.controls.EditFieldMap;
import de.uniks.networkparser.ext.javafx.window.KeyListenerMap;
import de.uniks.networkparser.gui.CellEditorElement;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class PropertyComposite extends HBox implements PropertyChangeListener, CellEditorElement {
	private Label westLabel;
	private Node centerComposite;
	private Label eastLabel;
	private GUIPosition labelOrientation=GUIPosition.WEST;
	private String labelPostText=": ";
	private Object item;
	private Column column;
	private SendableEntityCreator creator;
	private EditFieldMap fields;
	private EditControl<?> editControl;
	private KeyListenerMap listener;
	private ModelForm owner;

	public EditFieldMap getField() {
		if(fields == null) {
			fields = new EditFieldMap();
		}
		return fields;
	}

	public PropertyComposite withEditorField(EditFieldMap value) {
		this.fields = value;
		return this;
	}

	public String getLabelText() {
		if(getColumn().getLabel()!= null){
			return getColumn().getLabel();
		}
		return getColumn().getAttrName();
	}

	public PropertyComposite withLabelText(String value) {
		getColumn().withLabel(value);
		withDataBinding();
		return this;
	}

	public PropertyComposite withLabel(String value) {
		if(value != null){
			if(getField().getMap()!=null){
				TextItems textClazz = (TextItems) getField().getMap().getCreator(TextItems.class.getName(), true);
				if(textClazz !=null){
					getColumn().withLabel(textClazz.getText(value, item, this));
				}
			}else{
				getColumn().withLabel(value);
			}
		}
		withDataBinding();
		return this;
	}

	public boolean isFocus() {
		if(editControl != null ) {
			return editControl.isFocus();
		}
		return false;
	}

	public PropertyComposite withFieldTyp(FieldTyp value) {
		getColumn().withFieldTyp(value);
		editControl = getField().getControl(null, column, getItemValue(), this);
		editControl.withListener(listener);
		 editControl.withValue(getItemValue());
		 if(this.centerComposite != null) {
			 this.getChildren().remove(this.centerComposite);
		 }
		 this.centerComposite = editControl.getControl();
		this.getChildren().add(1, this.centerComposite);
		return this;
	}

	public PropertyComposite withFieldType(FieldTyp type){
		getColumn().withFieldTyp(type);
		return this;
	}

	 private PropertyComposite withDataBinding() {
		 initLabel();
		 editControl.withValue(getItemValue() );
		 if(item instanceof SendableEntity) {
			 ((SendableEntity)item).addPropertyChangeListener(getColumn().getAttrName(), this);
		 }
		 return this;
	 }

	 public PropertyComposite withDataBinding(IdMap map, Object item, Column column) {
		 this.item = item;
		 this.column = column;
		 getField().withMap(map);
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
			 editControl = getField().getControl(null, column, getItemValue(), this);
			 editControl.withValue(getItemValue());
			 this.centerComposite = editControl.getControl();
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
		 if(creator!=null && getColumn().getAttrName() != null){
			 return creator.getValue(item, getColumn().getAttrName());
		 }
		 return null;
	}

	public void reload() {
		editControl.withValue( getItemValue() );
	}

	public void save() {
		creator.setValue(item, getColumn().getAttrName(), editControl.getValue(true), IdMap.UPDATE);
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

	public double getLabelWidth(){
		Label label = getLabelControl();
		if(label != null) {
			Text text = new Text(label.getText() );
			text.applyCss();
			return text.getLayoutBounds().getWidth();
		}
		return -1;
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
		return getField().setFocus(value);
	}

	@Override
	public void relocate(double x, double y) {
		super.relocate(x, y);
//		withDataBinding();
	}

	@Override
	public PropertyComposite withColumn(Column value) {
		this.column = value;
		return this;
	}

	public Column getColumn() {
		if(this.column==null){
			this.column = new Column();
		}
		return column;
	}

	public PropertyComposite withLabelOrientation(GUIPosition position) {
		this.labelOrientation = position;
		return this;
	}

	@Override
	public CellEditorElement withValue(Object value) {
		editControl.withValue( value );
		return this;
	}

	public EditControl<?> getEditControl(){
		return editControl;
	}

	public PropertyComposite withListener(KeyListenerMap listener) {
		this.listener = listener;
		return this;
	}

	@Override
	public boolean nextFocus() {
		if(owner != null) {
			return this.getOwner().focusnext();
		}
		return false;
	}

	@Override
	public Object getValue(boolean convert) {
		return editControl.getValue(convert);
	}

	public ModelForm getOwner() {
		return owner;
	}

	public PropertyComposite withOwner(ModelForm owner) {
		this.owner = owner;
		return this;
	}

	@Override
	public void apply(APPLYACTION action) {
		if(owner != null) {
			owner.apply(action);
		}
	}

	@Override
	public void cancel() {
	}

	@Override
	public boolean onActive(boolean value) {
		return false;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(evt.getPropertyName()!=null){
			if(evt.getPropertyName().equals(getColumn().getAttrName())){
				// Test Thread and restarten
	//			field.setValue(evt.getNewValue(), false);
			}
		}
	}
}
