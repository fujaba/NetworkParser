package de.uniks.networkparser.grid;

/*
 NetworkParser
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
import de.uniks.networkparser.Style;
import de.uniks.networkparser.calculator.RegCalculator;

public class CellValue{
	public static final String COUNT="count";
	public static final String POSITION="position";
	private int rowSpan=1;
	private int columnSpan=1;
	private int column;
	private int row;
	private Object child;
	private String style=null;
	private GridValue grid;
	private Style cellStyle;
	private GridGUICell guiElement;
	private String heightExpression;
	private String widthExpression;
	
	public CellValue withGrid(GridValue grid){
		this.grid=grid;
		return this;
	}
	
	
	public CellValue withCellValue(GridGUICell guiElement,  Object node, int col, int row){
		this.child = node;
		this.column = col;
		this.row = row;
		this.guiElement = guiElement;
		this.guiElement.withParent(this);

//		this.guiElement.getProperties().put(GridGUICell.ROW_INDEX_CONSTRAINT, row);
//		this.guiElement.getProperties().put(GridGUICell.COLUMN_INDEX_CONSTRAINT, row);

		if(guiElement!=null){
			guiElement.maximizeSize();
			guiElement.setContentNode(node);
			guiElement.setStyle("-fx-background-color: white;-fx-border-color: black; -fx-border-width: 1 0 0 1;");
		}
		return this;
	}
	
	public void select(){
		select("-fx-background-color: #d8f0f3;");
	}
	
	public void select(String add){
		this.style = this.getStyle();
		this.setStyle(style+add);
	}
	
	public void deselect(){
		this.setStyle(style+"-fx-background-color: white;");
		this.style = null;
	}
	
	public Object getChild(){
		return child;
	}

	public int getColumn() {
		return column;
	}

	public int getRow() {
		return row;
	}
	public int getRowEnd(){
		if(heightExpression!=null){
			RegCalculator calculator=new RegCalculator().withStandard();
			calculator.withConstants(COUNT, grid.getCountRows());
			calculator.withConstants(POSITION, row);
			double result = (double)calculator.calculate(heightExpression);
			int end = (int)result;
			withRowSpan(end-row);
			return end;
		}

		return getRow()+getRowSpan()-1;
	}
	public int getColumnEnd(){
		if(widthExpression!=null){
			RegCalculator calculator=new RegCalculator().withStandard();
			calculator.withConstants(COUNT, grid.getCountRows());
			calculator.withConstants(POSITION, column);
			double result = (double)calculator.calculate(widthExpression);
			int end = (int)result;
			withColumnSpan(end-column);
			return end;
		}
		return getColumn()+getColumnSpan()-1;
	}
	public int getColumnSpan() {
		return columnSpan;
	}

	public int getRowSpan() {
		return rowSpan;
	}
	public CellValue withRowSpan(int value) {
		if(value<1) {
			return this;
		}
		int oldValue = this.rowSpan; 
		this.rowSpan = value;
		if(value != oldValue){
			this.guiElement.getProperties().put(GridGUICell.ROW_SPAN_CONSTRAINT, value);
		}
		
		return this;
	}
	
	public CellValue withColumnSpan(int value) {
		if(value<1) {
			return this;
		}
		int oldValue = this.columnSpan; 
		this.columnSpan = value;
		if(value!=oldValue){
			this.guiElement.getProperties().put(GridGUICell.COLUMN_SPAN_CONSTRAINT, value);
		}
		return this;
	}
	
	public Style getCellStyle() {
		return cellStyle;
	}

	public CellValue withCellStyle(Style cellStyle) {
		this.cellStyle = cellStyle;
		return this;
	}
	
	public void setSpanRow(int row){
		if(this.grid!=null){
			this.grid.setSpanRow(this, row);
		}
	}

	public void setSpanColumn(int column){
		if(this.grid!=null){
			this.grid.setSpanColumn(this, column);
		}
	}
	
	public CellValue withHeight(String value) {
		this.heightExpression = value;
		return this;
	}

	public CellValue withWidth(String value) {
		this.widthExpression = value;
		return this;
	}
	
	public String getSavedStyle(){
		return style;
	}
	
	public GridGUICell getGUIElement(){
		return guiElement;
	}

	public String getStyle() {
		if(guiElement!=null){
			return guiElement.getStyle();
		}
		return null;
	}
	
	public void setStyle(String value) {
		if(guiElement!=null){
			guiElement.setStyle(value);
		}
	}


	public void withRow(int value) {
		this.row = value;
	}
}
