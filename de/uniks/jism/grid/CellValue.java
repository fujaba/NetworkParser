package de.uniks.jism.grid;

import de.uniks.jism.Style;
import de.uniks.jism.calculator.RegCalculator;

public class CellValue{
	public static final String COUNT="%count%";
	public static final String POSITION="%position%";
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
			return (int)result;
			
		}

		return getRow()+getRowSpan()-1;
	}
	public int getColumnEnd(){
		if(widthExpression!=null){
			RegCalculator calculator=new RegCalculator().withStandard();
			calculator.withConstants(COUNT, grid.getCountRows());
			calculator.withConstants(POSITION, column);
			double result = (double)calculator.calculate(widthExpression);
			return (int)result;
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
		this.rowSpan = value;
		return this;
	}
	
	public CellValue withColumnSpan(int value) {
		this.columnSpan = value;
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
}
