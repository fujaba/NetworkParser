package de.uniks.jism.grid;

import java.util.HashMap;

public class GridValue {
	private int maxRows=0;
	private int maxColumns=0;
	private HashMap<Object, CellValue> children=new HashMap<Object, CellValue>();
	private CellValue selectedCell;
	private GridGUITable guiElement;

	public GridValue withGridTable(GridGUITable value){
		this.guiElement = value;
		return this;
	}
	
	public CellValue add(Object node, int col, int row, String width, String height) {
		CellValue cell = add(node, col, row);
		cell.withHeight(height).withWidth(width);
		return cell;
	}
	
	public CellValue add(Object node, int col, int row) {
		boolean refresh=false;
		if(col>=maxColumns){
			maxColumns = col;
			refresh=true;
		}
		if(row>=maxRows){
			maxRows = row;
			refresh=true;
		}

		CellValue cell=new CellValue().withGrid(this).withCellValue(guiElement.getNewCell(), node, col, row);
		children.put(node, cell);
		if(guiElement!=null){
			guiElement.add(cell);
		}

		if(refresh){
			refreshLines();
		}
		return cell;
	}
	
	public void setSpanRow(Object node, int row){
		CellValue cell = children.get(node);
		if(cell!=null){
			cell.withRowSpan(row);
			if(guiElement!=null){
				guiElement.setSpanRow(cell);
			}

			refreshLines();
		}
	}
	
	public void setSpanColumn(Object node, int column){
		CellValue cell = children.get(node);
		if(cell!=null){
			cell.withColumnSpan(column);
			if(guiElement!=null){
				guiElement.setSpanColumn(cell);
			}
			refreshLines();
		}
	}
	
	public CellValue getCell(Object node){
		return children.get(node);
	}
	
	public void refreshLines(){
		 for (CellValue n: children.values()) {
			 int row = n.getRowEnd();
			 int col = n.getColumnEnd();
			 String rowId="0";
			 if(row>=maxRows){
				 rowId="1";
			 }
			 String columnId="0";
			 if(col>=maxColumns){
				 columnId="1";
			 }
			 n.setStyle("-fx-border-color: black; -fx-border-width: 1 "+columnId+" "+rowId+" 1;");
		}
	}

	public boolean selectCell(CellValue cell) {
		if(selectedCell==cell){
			return true;
		}
		if(selectedCell!=null){
			selectedCell.deselect();
			selectedCell=null;
		}
		if(cell!=null){
			cell.select();
			this.selectedCell=cell;
			return true;
		}
		return false;
	}

	public int getCountColumns() {
		return maxColumns;
	}
	public int getCountRows() {
		return maxRows;
	}
}
