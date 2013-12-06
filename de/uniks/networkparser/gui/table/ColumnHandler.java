package de.uniks.networkparser.gui.table;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;


public class ColumnHandler implements ColumnListener {
	protected Column column;

	public ColumnHandler withColumn(Column column){
		this.column = column;
		return this;
	}
	
	public Object getValue(Object entity, SendableEntityCreator creator){
		return creator.getValue(entity, column.getAttrName());
	}

	public boolean canEdit(Object entity, SendableEntityCreator creator){
		return column.isEditable();
	}
	
	public void onSelection(Object entity, SendableEntityCreator creator, int x, int y){
	}
	
	public CellEditorElement onEdit(Object entity, SendableEntityCreator creator){
		return null;
	}

	public boolean setValue(Object entity, SendableEntityCreator creator, Object value) {
		if(creator==null){
			return false;
		}
		return creator.setValue(entity, column.getAttrName(), value, IdMap.UPDATE);
	}
	public void dispose(){
		
	}
	public boolean updateWidth(int oldWidth, int newWidth){
		return true;
	}

	@Override
	public void update(Object cell) {
	}
}
