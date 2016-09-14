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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class CSVExporter extends MenuItem implements EventHandler<ActionEvent>{
	private static final String SEPERATOR = ";";
	private TableComponent tableComponent;

	public CSVExporter(TableComponent value) {
		super("CSV");
		this.tableComponent = value;
		setOnAction(this);
	}

	@Override
	public void handle(ActionEvent event) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new ExtensionFilter("CSV File", "*.csv"));
		File choice = fileChooser.showSaveDialog(tableComponent.getScene().getWindow());
		if(choice!=null) {
			ArrayList<String> attributes=new ArrayList<String>();
			StringBuilder line=new StringBuilder();
			FileOutputStream stream = null;
			OutputStreamWriter writer = null;
			try {
				stream = new FileOutputStream(choice);
				writer = new OutputStreamWriter(stream, "UTF-8");
				for(Iterator<TableColumnFX> i = tableComponent.getColumnIterator();i.hasNext();) {
					TableColumnFX tableColumn = i.next();
					line.append(tableColumn.getColumn().getLabelOrAttrName());
					line.append(SEPERATOR);
					attributes.add(tableColumn.getColumn().getAttrName());
				}
				writer.write(line.toString()+BaseItem.CRLF);

				List<Object> items = tableComponent.getItems();
				for(Object item : items) {
					SendableEntityCreator creator = tableComponent.getCreator(item);
					if(creator != null) {
						line=new StringBuilder();
						for(String attribute : attributes) {
							Object value = creator.getValue(item, attribute);
							if(value!=null) {
								line.append(value);
							}
							line.append(SEPERATOR);
						}
						line.append(BaseItem.CRLF);
						writer.write(line.toString()+BaseItem.CRLF);
					}
				}
			} catch (IOException e) {
			} finally {
				try {
					writer.flush();
					writer.close();
					stream.close();
				} catch (IOException e) {
				}
			}
		}
	}
}
