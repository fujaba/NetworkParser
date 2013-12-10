package de.uniks.networkparser.gui.grid;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.
 
 Licensed under the EUPL, Version 1.1 or – as soon they
 will be approved by the European Commission - subsequent
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
import java.beans.PropertyChangeListener;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import de.uniks.networkparser.gui.GUILine;
import de.uniks.networkparser.interfaces.GUIPosition;

public class TableGridPane extends GridPane implements GridGUITable{
	protected ValueGrid value=new ValueGrid().withGridTable(this);
	
	@Override
	public void add(Object cell) {
		Node node = (Node) value.getCell(cell);
		GridStyle style = value.getGridStyle(cell);
		super.add(node, style.getColumn(), style.getRow());
	}
	
	public void move(Object cell) {
		Node node = (Node) value.getCell(cell);
		super.getChildren().remove(node);
		
		
		GridStyle style = value.getGridStyle(cell);
		super.add(node, style.getColumn(), style.getRow());
	}
	public GridStyle addNode(Node cell, int col, int row) {
		return value.add(cell, col, row);
	}
	
	@Override
	public void add(Node cell, int col, int row) {
		value.add(cell, col, row);
//		super.add(arg0, arg1, arg2);
	}
	
	@Override
	public void add(Node cell,  int col, int row, int columnSpan, int rowSpan) {
		GridStyle item = value.add(cell, col, row);
		item.withColumnSpan(columnSpan);
		item.withRowSpan(rowSpan);
	}
	
	public void insetRow(int offset){
		value.insertRow(offset);
	}

	public void setSpanRow(Object cell, int row) {
		value.setSpanRow(cell, row);
		GridStyle gridStyle = value.getGridStyle(cell);
		Node node = (Node) value.getCell(cell);
		if(gridStyle!=null){
			TableGridPane.setRowSpan(node, gridStyle.getRowSpan());
		}
	}

	public void setSpanColumn(Object cell, int column) {
		value.setSpanColumn(cell, column);
		GridStyle gridStyle = value.getGridStyle(cell);
		Node node = (Node) value.getCell(cell);
		if(gridStyle!=null){
			TableGridPane.setColumnSpan(node, gridStyle.getColumnSpan());
		}
	}

	public ValueGrid getGridValue(){
		return value;
	}


	@Override
	public GridStyle getNewStyle() {
		StyleFX gridStyle = new StyleFX();
		gridStyle.withBackground("transparent");
		gridStyle.getBorders().put(GUIPosition.NORTH, new GUILine().withColor("#000000").withWidth("1"));
		gridStyle.getBorders().put(GUIPosition.WEST, new GUILine().withColor("#000000").withWidth("1"));
		
//		gridStyle.withDefaultAlignment(Pos.TOP_LEFT.toString());
//		gridStyle.withDefaultFontSize("12");
//		gridStyle.withDefaultFontFamily("Arial");
//		gridStyle.withDefaultBold(false);
//		gridStyle.withDefaultItalic(false);
//		gridStyle.withDefaultUnderline(false);
//		gridStyle.withDefaultForground("BLACK");
		return gridStyle;
	}

	@Override
	public PropertyChangeListener getNewCell(Object node) {
//		TableRCell tableRCell = new TableRCell();
//		tableRCell.getChildren().add((Node) node);
		return null;
	}
}
