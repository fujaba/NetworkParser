package de.uniks.networkparser.test.javafx;

/**

 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.

 * All rights reserved. Use is subject to license terms.

 */

import java.util.Comparator;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.javafx.TableList;
import de.uniks.networkparser.ext.javafx.component.TableComponent;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.TableCellValue;
import de.uniks.networkparser.interfaces.GUIPosition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**

 * A simple table with a header row.

 *

 * @see javafx.scene.control.TableCell

 * @see javafx.scene.control.TableColumn

 * @see javafx.scene.control.TablePosition

 * @see javafx.scene.control.TableRow

 * @see javafx.scene.control.TableView

 */

public class TestApp extends Application {

	private TableList tableList;

	private void init(Stage primaryStage) {

		AnchorPane root = new AnchorPane();

		primaryStage.setScene(new Scene(root));

//		emailCol.setCellValueFactory(new PropertyValueFactory("email"));
//		TableComponent tableView = new TableComponent();
		TableComponent tableView = new TableComponent();
		tableList = new TableList();
		tableList.add(new PersonGUI("Jacob",	 "Smith",	"jacob.smith@example.com", 1));
		tableList.add(new PersonGUI("Isabella",  "Johnson",  "isabella.johnson@example.com", 2));
		tableList.add(new PersonGUI("Ethan",	 "Williams", "ethan.williams@example.com", 23));
		tableList.add(new PersonGUI("Emma",	  "Jones",	"emma.jones@example.com", 12));
		tableList.add(new PersonGUI("Michael",   "Brown",	"michael.brown@example.com", 122));

		tableList.add(new PersonGUI("Jacob",	 "Smith",	"jacob.smith@example.com", 1));
		tableList.add(new PersonGUI("Isabella",  "Johnson",  "isabella.johnson@example.com", 2));
		tableList.add(new PersonGUI("Ethan",	 "Williams", "ethan.williams@example.com", 23));
		tableList.add(new PersonGUI("Emma",	  "Jones",	"emma.jones@example.com", 12));
		tableList.add(new PersonGUI("Michael",   "Brown",	"michael.brown@example.com", 122));


		tableList.add(new PersonGUI("Jacob",	 "Smith",	"jacob.smith@example.com", 1));
		tableList.add(new PersonGUI("Isabella",  "Johnson",  "isabella.johnson@example.com", 2));
		tableList.add(new PersonGUI("Ethan",	 "Williams", "ethan.williams@example.com", 23));
		tableList.add(new PersonGUI("Emma",	  "Jones",	"emma.jones@example.com", 12));
		tableList.add(new PersonGUI("Michael",   "Brown",	"michael.brown@example.com", 122));


		IdMap map = new  IdMap();
		map.with(tableList);
		map.with(new PersonGUICreator());

		tableView.withMap(map).withList(tableList);

		tableView.withSearchProperties(PersonGUI.PROPERTY_FIRSTNAME, PersonGUI.PROPERTY_LASTNAME, PersonGUI.PROPERTY_EMAIL);

//		tableView.createFromCreator(null, true);

		tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_FIRSTNAME));
		tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_LASTNAME));
		tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_EMAIL).withBrowserId(GUIPosition.WEST));
		tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_DISTANCE));
		tableView.withColumn(new Column().withAttrName(PersonGUI.PROPERTY_DISTANCE).withComparator(new Comparator<TableCellValue>() {

			@Override
			public int compare(TableCellValue o1, TableCellValue o2) {
				PersonGUI item1 = (PersonGUI)o1.getItem();
				PersonGUI item2 = (PersonGUI)o2.getItem();
				return item1.getDistance().compareTo(item2.getDistance());
			}
		}));

		root.getChildren().add(tableView);

		primaryStage.show();


//		System.out.println(map.toJsonObject(tableList).toString(2));
//		tableView.test();

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
//		primaryStage.show();
	}

	public static void main(String[] args) { launch(args); }

}
