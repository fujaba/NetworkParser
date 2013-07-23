package de.uniks.jism.grid;

public interface GridGUITable {
	public void add(CellValue cell);
	public void setSpanRow(CellValue node);
	public void setSpanColumn(CellValue node);
	public GridGUICell getNewCell();
	public void setSpanRow(Object node, int row);
	public void setSpanColumn(Object node, int column);
}
