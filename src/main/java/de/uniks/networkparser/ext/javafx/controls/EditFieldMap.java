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
import java.util.HashSet;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.CellEditorElement;
import de.uniks.networkparser.gui.CellEditorElement.APPLYACTION;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.FieldTyp;
import de.uniks.networkparser.interfaces.DateCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class EditFieldMap {
	protected HashSet<EditControl<?>> fields=new HashSet<EditControl<?>>();
	private IdMap map;
	private CellEditorElement owner;

	public EditFieldMap(){
		withEditControls( new CheckBoxEditControl().withListener(this) );
		withEditControls( new ComboEditControl().withListener(this) );
		withEditControls( new DateTimeEditControl().withListener(this) );
		withEditControls( new SpinnerIntegerEditControl().withListener(this) );
		withEditControls( new SpinnerDoubleEditControl().withListener(this) );
		withEditControls( new TextEditorControl().withListener(this) );
		withEditControls( new PasswordEditControl().withListener(this) );
	}

	public EditFieldMap withEditControls(EditControl<?> field){
		fields.add(field);
		return this;
	}

	public EditFieldMap withOwner(CellEditorElement owner){
		this.owner = owner;
		return this;
	}

	public EditControl<?> getControl(FieldTyp typ, Column column, Object value, CellEditorElement owner){
		EditControl<?> newFieldControl = null;
		if(typ==null){
			typ = getControllForTyp(column, value);
		}

		for(EditControl<?> item : fields){
			FieldTyp newTyp = item.getControllForTyp(value);
			if(newTyp==typ){
				newFieldControl = item;
				break;
			}
		}

		if(newFieldControl==null){
			return null;
		}

		newFieldControl.withOwner(owner);
		newFieldControl.withColumn(column);
		newFieldControl.withMap(map);

		// Set the value to the Control
		if(value!=null){
			newFieldControl.withValue(value);
		}
		return newFieldControl;
	}

	public void cancel() {
		if(owner!=null){
			this.owner.cancel();
		}
	}

	public boolean setFocus(boolean value) {
		if(owner!=null){
			return this.owner.setFocus(value);
		}
		return false;
	}

	public boolean onActive(boolean value) {
		if(owner!=null){
			return this.owner.onActive(value);
		}
		return false;
	}

	public boolean nextFocus() {
		if(owner!=null){
			return this.owner.nextFocus();
		}
		return false;
	}

	public void apply(APPLYACTION action) {
		if(owner!=null){
			owner.apply(action);
		}
	}

	public FieldTyp getControllForTyp(Column column, Object value) {
		FieldTyp typ;
		if( column == null ){
			return null;
		}
		typ = column.getFieldTyp();
		if(typ!=null){
			return typ;
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
			if(column.getNumberFormat()==null){
				column.withNumberFormat("#####");
			}
			return FieldTyp.INTEGER;
		}else if(value instanceof Double){
			if(column.getNumberFormat()==null){
				column.withNumberFormat("#####.##");
			}
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

	public void dispose() {
		if(owner!=null){
			this.owner.dispose();
		}
	}

	public EditFieldMap withMap(IdMap map) {
		if(map == null || map==this.map){
			return this;
		}
		this.map = map;
		return this;
	}

	public IdMap getMap() {
		return map;
	}
}
