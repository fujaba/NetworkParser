package de.uniks.networkparser.gui.controls;

import java.util.HashSet;

import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.event.creator.DateCreator;
import de.uniks.networkparser.gui.Style;
import de.uniks.networkparser.gui.table.CellEditorElement;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.FieldTyp;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import javafx.scene.Node;

public class EditFieldMap implements CellEditorElement{
	protected HashSet<EditControl<?>> fields=new HashSet<EditControl<?>>();
	private EditControl<? extends Node> editControl;
	private CellEditorElement owner;
	private Column column;
	private IdMapEncoder map;

	public EditFieldMap(){
//		addToEditControls( new CheckBoxEditControl().withOwner(this) );
//		addToEditControls( new ComboEditControl().withOwner(this) );
//		addToEditControls( new DateTimeEditControl().withOwner(this) );
//		addToEditControls( new NumberEditControl().withOwner(this) );
		withEditControls( new SpinnerDoubleEditControl().withOwner(this) );
		withEditControls( new TextEditorControl().withOwner(this) );
		withEditControls( new PasswordEditorControl().withOwner(this) );
	}
	
	public EditFieldMap withEditControls(EditControl<?> field){
		fields.add(field);
		return this;
	}
	
	public EditFieldMap withOwner(CellEditorElement owner){
		this.owner = owner;
		return this;
	}

	@Override
	public EditFieldMap withColumn(Column column) {
		if(column == null || column==this.column){
			return this;
		}
		this.column = column;
		if(owner!=null){
			this.owner.withColumn(column);
		}
		return this;
	}
	
	public Node getControl(FieldTyp typ, Object value){
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
				return editControl.getControl();
			}
		}

		if(newFieldControl==null){
			return null;
		}
		
		editControl = newFieldControl;
		editControl.withColumn(column);
		
		// Set the value to the Controll
		if(value!=null){
			editControl.withValue(value);
		}
		return editControl.getControl();
	}

	public EditControl<? extends Node> getEditControl(){
		return editControl;
	}
	
	@Override
	public void cancel() {
		if(owner!=null){
			this.owner.cancel();
		}
	}

	public Column getColumn() {
		if(column==null){
			column = new Column();
		}
		return column;
	}
	
	public Style getStyle() {
		return column.getStyle();
	}
	@Override
	public boolean setFocus(boolean value) {
		if(owner!=null){
			return this.owner.setFocus(value);
		}
		return false;
	}

	@Override
	public boolean onActive(boolean value) {
		if(owner!=null){
			return this.owner.onActive(value);
		}
		return false;
	}

	@Override
	public boolean nextFocus() {
		if(owner!=null){
			return this.owner.nextFocus();
		}
		return false;
	}

	@Override
	public void apply() {
		if(owner!=null){
			this.owner.apply();
		}
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
		FieldTyp typ;
		if( column!=null ){
			typ = column.getFieldTyp();
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
			if(column.getNumberFormat()==null){
				column.withNumberFormat("###");
			}
			return FieldTyp.INTEGER;
		}else if(value instanceof Double){
			if(column.getNumberFormat()==null){
				column.withNumberFormat("###.##");
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

	@Override
	public void dispose() {
		if(owner!=null){
			this.owner.dispose();
		}
	}

	public EditFieldMap withMap(IdMapEncoder map) {
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
