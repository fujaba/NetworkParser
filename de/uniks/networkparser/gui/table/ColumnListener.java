package de.uniks.networkparser.gui.table;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public interface ColumnListener {
	public boolean canEdit(Object entity, SendableEntityCreator creator);
	public void onSelection(Object entity, SendableEntityCreator creator, int x, int y);
	public CellEditorElement onEdit(Object entity, SendableEntityCreator creator);
	public Object getValue(Object entity, SendableEntityCreator creator);
	public boolean setValue(Object entity, SendableEntityCreator creator, Object value);
	public void dispose();
	public boolean updateWidth(int oldWidth, int newWidth);
	public void update(Object cell);
	public ColumnListener withColumn(Column column);
}
