package de.uniks.networkparser.gui;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
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
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.date.DateTimeEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class ColumnListener {
	protected Column column;
	private boolean defaultListener;
	private CellHandler cellHandler;
	public static final String SELECTION="selection";
	public static final String EDIT="edit";
	

	public ColumnListener withColumn(Column column) {
		this.column = column;
		return this;
	}

	public Object getValue(Object entity, SendableEntityCreator creator) {
		String attrName = column.getAttrName();
		if(attrName != null) {
			if(attrName.startsWith("\"")) {
				return attrName.substring(1, attrName.length() - 1);
			}
		}
		if (creator != null && column != null) {
			Object value = creator.getValue(entity, attrName);
			if(column.getNumberFormat()!=null && value instanceof Long) {
				DateTimeEntity item = new DateTimeEntity();
				item.withValue((Long) value);
				return item.toString(column.getNumberFormat()); 
			}
			return value;
		}
		return null;
	}

	public boolean canEdit(Object entity, SendableEntityCreator creator) {
		if(cellHandler != null) {
			return false;
		}
		return column.isEditable();
	}

	public CellEditorElement onSelection(Object entity, SendableEntityCreator creator, double x, double y) {
		if(cellHandler!=null) {
			return cellHandler.onAction(SELECTION, entity, creator, x, y);
		}
		return null;
	}

	public CellEditorElement onEdit(Object entity, SendableEntityCreator creator, double x, double y) {
		if(cellHandler!=null) {
			return cellHandler.onAction(EDIT, entity, creator, x, y);
		}
		return null;
	}

	public boolean isFinish() {
		return false;
	}

	public boolean setValue(Object controll, Object entity,
			SendableEntityCreator creator, Object value) {
		if (creator == null) {
			return false;
		}
		return creator.setValue(entity, column.getAttrName(), value,
				IdMapEncoder.UPDATE);
	}

	public boolean updateWidth(int oldWidth, int newWidth) {
		return true;
	}

	public boolean isDefaultListener() {
		return defaultListener;
	}

	public ColumnListener withDefaultListener(boolean value) {
		this.defaultListener = value;
		return this;
	}

	public ColumnListener withActionListener(CellHandler handler) {
		this.cellHandler = handler;
		return this;
	}
}
