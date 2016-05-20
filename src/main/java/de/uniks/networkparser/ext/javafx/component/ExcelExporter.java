package de.uniks.networkparser.ext.javafx.component;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
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
