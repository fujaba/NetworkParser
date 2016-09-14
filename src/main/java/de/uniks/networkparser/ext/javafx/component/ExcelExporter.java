package de.uniks.networkparser.ext.javafx.component;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.uniks.networkparser.ext.io.ExcelBuffer;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.excel.ExcelCell;
import de.uniks.networkparser.parser.excel.ExcelRow;
import de.uniks.networkparser.parser.excel.ExcelSheet;
import de.uniks.networkparser.parser.excel.ExcelWorkBook;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class ExcelExporter extends MenuItem implements EventHandler<ActionEvent>{
	private TableComponent tableComponent;

	public ExcelExporter(TableComponent value) {
		super("XLSX");
		this.tableComponent = value;
		setOnAction(this);
	}

	@Override
	public void handle(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("Excel File", "*.xlsx"));
		File choice = fileChooser.showSaveDialog(tableComponent.getScene().getWindow());
		if(choice!=null) {
			if(choice.exists()){
				if(choice.delete() == false) {
					return;
				}
			}
			ArrayList<String> attributes=new ArrayList<String>();
			ExcelWorkBook workBook = new ExcelWorkBook();
			ExcelSheet sheet = new ExcelSheet();
			workBook.with(sheet);
			ExcelRow row=new ExcelRow();
			sheet.with(row);

			//header
			for(Iterator<TableColumnFX> i = tableComponent.getColumnIterator();i.hasNext();) {
				TableColumnFX tableColumn = i.next();
				row.add(new ExcelCell().withContent(tableColumn.getColumn().getLabelOrAttrName()));
				attributes.add(tableColumn.getColumn().getAttrName());
			}
			List<Object> items = tableComponent.getItems();

			// data
			for(Object item : items) {
				row=new ExcelRow();
				sheet.with(row);
				SendableEntityCreator creator = tableComponent.getCreator(item);
				if(creator != null) {
					for(String attribute : attributes) {
						Object value = creator.getValue(item, attribute);
						ExcelCell cell = new ExcelCell().withContent(value);
						row.with(cell);
					}
				}
			}
			ExcelBuffer buffer=new ExcelBuffer();
			buffer.encode(choice, workBook);
		}
	}
}
