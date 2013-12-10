package de.uniks.networkparser.gui.table;

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
import java.util.HashSet;
import javafx.scene.control.Control;
import javafx.scene.control.TableCell;
import javafx.scene.text.Font;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.event.creator.DateCreator;
import de.uniks.networkparser.gui.Style;
import de.uniks.networkparser.gui.controls.EditControl;
import de.uniks.networkparser.gui.controls.PasswordEditorControl;
import de.uniks.networkparser.gui.controls.TextEditorControl;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class TableCellFX extends TableCell<Object, TableCellValue> implements CellEditorElement{
	private Column column;
	protected HashSet<EditControl<?>> fields=new HashSet<EditControl<?>>();
	private String numberFormat;
	private IdMap map;
	private EditControl<? extends Control> editControl;
	
	public TableCellFX(){
//		addToEditControls( new CheckBoxEditControl().withOwner(this) );
//		addToEditControls( new ComboEditControl().withOwner(this) );
//		addToEditControls( new DateTimeEditControl().withOwner(this) );
//		addToEditControls( new NumberEditControl().withOwner(this) );
//		addToEditControls( new SpinnerEditControl().withOwner(this) );
		addToEditControls( new TextEditorControl().withOwner(this) );
		addToEditControls( new PasswordEditorControl().withOwner(this) );
	}
	
	public void addToEditControls(EditControl<?> field){
		fields.add(field);
	}
	public TableCellFX withColumn(Column column) {
		this.column = column;
		return this;
	}

	@Override
	protected void updateItem(TableCellValue arg0, boolean arg1) {
		super.updateItem(arg0, arg1);
		if (arg0 != null) {
			setText("" + arg0);

			if (this.column.getStyle() != null) {
				Style myStyle = this.column.getStyle();
				if (myStyle.getFontFamily() != null
						&& myStyle.getFontSize() != null) {
					setFont(new Font(myStyle.getFontFamily(),
							Integer.valueOf(myStyle.getFontSize())));
				}
				String style = myStyle.toString();
				if(style.startsWith("-")){
					setStyle(style);
				}
			}
		}
	}

	@Override
	public void startEdit() {
		if (!isEmpty()) {
			super.startEdit();
			Object value = getItem().getCreator().getValue(getItem().getItem(), column.getAttrName());
			FieldTyp typ = getControllForTyp(value);
			setText(null);
			getControl(typ, value);
			setGraphic(editControl.getControl());
		}
	}
	
	@Override
	public void commitEdit(TableCellValue arg0) {
		super.commitEdit(arg0);
		apply();
	}
	
	
	
	public TableCellFX withNumberFormat(String numberFormat){
		this.numberFormat = numberFormat;
		return this;
	}
	
	public String getNumberFormat() {
		return numberFormat;
	}
	
	public TableCellFX withMap(IdMap map){
		this.map = map;
		return this;
	}
	
	public boolean getControl(FieldTyp typ, Object value){
		EditControl<?> newFieldControl = null;
		
		if(typ==null){
			typ=FieldTyp.TEXT;
		}
		
		for(EditControl<?> item : fields){
			FieldTyp newTyp = item.getControllForTyp(value);
			if(newTyp==typ){
				newFieldControl = item;
				break;
			}
		}
		
		if(editControl != null){
			if(editControl == newFieldControl){
				return false;
			}
		}

		if(newFieldControl==null){
			return false;
		}
		
		editControl = newFieldControl;
		
		// Set the value to the Controll
		if(value!=null){
			editControl.setValue(value);
		}
		return true;
	}

	@Override
	public void cancelEdit() {
		super.cancelEdit();
		cancel();
	}

	// @Override
	// public Color getBackground(Object element) {
	// return colors.getColor(column.getBackgroundColor());
	// }
	// @Override
	// public Color getForeground(Object element) {
	// return colors.getColor(column.getForgroundColor());
	// }
	// public Color getForgroundColorActiv() {
	// return colors.getColor(column.getForgroundColorActiv());
	// }
	//
	// public Color getBackgroundColorActiv() {
	// return colors.getColor(column.getBackgroundColorActiv());
	// }
	//
	// @Override
	// public String getToolTipText(Object element) {
	// String altAttribute = column.getAltAttribute();
	// if (altAttribute != null) {
	// if(altAttribute.startsWith("\"")){
	// return altAttribute.substring(1, altAttribute.length()-1);
	// }
	// SendableEntityCreator creatorClass =
	// owner.getMap().getCreatorClass(element);
	// if (creatorClass != null) {
	// String text = ""
	// + creatorClass.getValue(element, altAttribute);
	// if (text.equals("")) {
	// return null;
	// }
	// return text;
	// }
	// }
	// return getTextValue(element);
	// }

	public Column getColumn() {
		return column;
	}

	@Override
	public void cancel() {
		setText(""+getItem());
		setGraphic(null);
	}

	@Override
	public boolean setFocus(boolean value) {
		// TODO Auto-generated method stub
		return false;
	}

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
		Object value = editControl.getValue(false);
		getItem().getColumn().getListener().setValue(getItem().getItem(), getItem().getCreator(), value);
		setText(""+value);
		setGraphic(null);		
	}

	@Override
	public Object getValue(boolean convert) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setValue(Object value) {
		editControl.setValue(value);
	}

	@Override
	public FieldTyp getControllForTyp(Object value) {
		FieldTyp typ;
		if( column!=null ){
			typ = column.getFieldTyp();
			this.numberFormat = column.getNumberFormat();
			if(typ!=null){
				return typ;
			}
		}
		
		// Autodetect
		if(map!=null){
			SendableEntityCreator creator = map.getCreatorClass(value);
			if(creator!=null){
				if(creator instanceof DateCreator){
					return FieldTyp.DATE;
				}
				return FieldTyp.COMBOBOX;
			}
		}
		if(value instanceof Integer){
			withNumberFormat("###");
			return FieldTyp.INTEGER;
		}else if(value instanceof Double){
			withNumberFormat("###.##");
			return FieldTyp.DOUBLE;
		}else if(value instanceof String){
//			if( typ!=FieldTyp.COMBOBOX ){
				return FieldTyp.TEXT;
//			}s
		}else if(value instanceof Boolean){
			return FieldTyp.CHECKBOX;
		}
		return FieldTyp.TEXT;
	}

	@Override
	public void dispose() {
	}
}
