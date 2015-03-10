package de.uniks.networkparser.gui.test;

import java.net.URL;
import java.util.Comparator;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.gui.TableList;
import de.uniks.networkparser.gui.table.SearchTableComponent;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.json.JsonIdMap;

public class ExampleController implements Initializable{
	@FXML AnchorPane table;
	private TableList tableList;
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		System.out.println(table);
		
		 tableList = new TableList();
	        tableList.add(new PersonGUI("Jacob",     "Smith",    "jacob.smith@example.com", 1));
	        tableList.add(new PersonGUI("Isabella",  "Johnson",  "isabella.johnson@example.com", 2));
	        tableList.add(new PersonGUI("Ethan",     "Williams", "ethan.williams@example.com", 23));
	        tableList.add(new PersonGUI("Emma",      "Jones",    "emma.jones@example.com", 12));
	        tableList.add(new PersonGUI("Michael",   "Brown",    "michael.brown@example.com", 122));
	        
	        JsonIdMap map = new  JsonIdMap();
	        map.withCreator(new TableList());
	        map.withCreator(new PersonGUICreator());
	        SearchTableComponent tableView = (SearchTableComponent) table.getChildren().get(0);
//	        SearchTableComponent tableView = ((SearchTableController)table).getTable();
	        tableView.withMap(map).withList(tableList);
	        
	        tableView.withSearchProperties(PersonGUI.PROPERTY_FIRSTNAME, PersonGUI.PROPERTY_LASTNAME, PersonGUI.PROPERTY_EMAIL);
	        
	        tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_FIRSTNAME));
	        tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_LASTNAME));
	        tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_EMAIL).withBrowserId(GUIPosition.WEST));
	        
	        tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_DISTANCE).withComparator(new Comparator<TableCellValue>() {
				
				@Override
				public int compare(TableCellValue o1, TableCellValue o2) {
					PersonGUI item1 = (PersonGUI)o1.getItem();
					PersonGUI item2 = (PersonGUI)o2.getItem();
					return item1.getDistance().compareTo(item2.getDistance());
				}
			}));
	}

}
