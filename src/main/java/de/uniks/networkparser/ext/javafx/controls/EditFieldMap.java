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
