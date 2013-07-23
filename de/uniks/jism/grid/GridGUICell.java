package de.uniks.jism.grid;

public interface GridGUICell {
	public String getStyle();
	public GridGUICell withParent(CellValue parent);
	public void setStyle(String string);
	public void maximizeSize();
	public void setContentNode(Object node);
	public void select(String string);
	public void deselect();
}
