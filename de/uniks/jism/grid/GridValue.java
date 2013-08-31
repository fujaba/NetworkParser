package de.uniks.jism.grid;

import java.util.ArrayList;
/*
 Json Id Serialisierung Map
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:
 1. Redistributions of source code must retain the above copyright
 notice, this list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright
 notice, this list of conditions and the following disclaimer in the
 documentation and/or other materials provided with the distribution.
 3. All advertising materials mentioning features or use of this software
 must display the following acknowledgement:
 This product includes software developed by Stefan Lindel.
 4. Neither the name of contributors may be used to endorse or promote products
 derived from this software without specific prior written permission.

 THE SOFTWARE 'AS IS' IS PROVIDED BY STEFAN LINDEL ''AS IS'' AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL STEFAN LINDEL BE LIABLE FOR ANY
 DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
import java.util.LinkedHashMap;

public class GridValue {
	private int maxRows=0;
	private int maxColumns=0;
	private LinkedHashMap<Object, CellValue> children=new LinkedHashMap<Object, CellValue>();
	private ArrayList<CellValue> childrenLink=new ArrayList<CellValue>();
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
		if(col>maxColumns){
			maxColumns = col;
			refresh=true;
		}
		if(row>maxRows){
			maxRows = row;
			refresh=true;
		}

		CellValue cell=new CellValue().withGrid(this).withCellValue(guiElement.getNewCell(), node, col, row);
		children.put(node, cell);
		childrenLink.add(cell);
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
			 int rowEnd = n.getRowEnd();
			 int colEnd = n.getColumnEnd();
//			 int col = n.getColumn();
			 int row = n.getRow();
			 String rowId="0";
			 if(rowEnd>=maxRows){
				 rowId="1";
			 }
			 String columnId="0";
			 if(colEnd>=maxColumns){
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
	
	public void insertRow(int offset){
		CellValue[] items = children.values().toArray(new CellValue[children.size()]);
		for(CellValue cell : items){
			if(cell.getRow()>=offset){
				cell.withRow(cell.getRow()+1);
				if(guiElement!=null){
					guiElement.move(cell);
				}
				
			}
		}
		maxRows++;
	}
}
