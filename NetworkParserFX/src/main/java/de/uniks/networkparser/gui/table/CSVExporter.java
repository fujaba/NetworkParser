package de.uniks.networkparser.gui.table;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class CSVExporter extends MenuItem implements EventHandler<ActionEvent>{
	private static final String SEPERATOR = ";";
	private static final String CRLF = "\n\r";
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
			try {
				FileWriter writer=new FileWriter(choice);
				for(Iterator<TableColumnFX> i = tableComponent.getColumnIterator();i.hasNext();) {
					TableColumnFX tableColumn = i.next();
					line.append(tableColumn.getColumn().getLabelOrAttrName());
					line.append(SEPERATOR);
					attributes.add(tableColumn.getColumn().getAttrName());
				}
				writer.write(line.toString()+CRLF);
				
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
						line.append(CRLF);
						writer.write(line.toString()+CRLF);
					}
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
