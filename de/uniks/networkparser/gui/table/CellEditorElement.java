package de.uniks.networkparser.gui.table;


public interface CellEditorElement {
	public CellEditorElement withColumn(Column column);
	public void cancel();
	public boolean setFocus(boolean value);
	public boolean onActive(boolean value);
	public boolean nextFocus();
	public void apply();
	public Object getValue(boolean convert);
	public void setValue(Object value);
	public FieldTyp getControllForTyp(Object value);
	public void dispose();
}
