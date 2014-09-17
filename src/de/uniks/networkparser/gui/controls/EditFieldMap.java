package de.uniks.networkparser.gui.controls;

import java.util.HashSet;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.event.util.DateCreator;
import de.uniks.networkparser.gui.table.CellEditorElement;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.FieldTyp;
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
		
		// Set the value to the Controll
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

	public void apply() {
		if(owner!=null){
			this.owner.apply();
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

	public IdMapEncoder getMap() {
		return map;
	}
}
