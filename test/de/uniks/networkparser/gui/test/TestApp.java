package de.uniks.networkparser.gui.test;

/**

 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.

 * All rights reserved. Use is subject to license terms.

 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.SearchTableComponent;
import de.uniks.networkparser.gui.table.TableComponent;
import de.uniks.networkparser.gui.table.TableList;
import de.uniks.networkparser.gui.table.creator.TableListCreator;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.json.JsonIdMap;

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

//        emailCol.setCellValueFactory(new PropertyValueFactory("email"));
//        TableComponent tableView = new TableComponent();
        TableComponent tableView = new SearchTableComponent();
        tableList = new TableList();
        tableList.add(new Person("Jacob",     "Smith",    "jacob.smith@example.com" ));
        tableList.add(new Person("Isabella",  "Johnson",  "isabella.johnson@example.com" ));
        tableList.add(new Person("Ethan",     "Williams", "ethan.williams@example.com" ));
        tableList.add(new Person("Emma",      "Jones",    "emma.jones@example.com" ));
        tableList.add(new Person("Michael",   "Brown",    "michael.brown@example.com" ));

        tableList.add(new Person("Jacob",     "Smith",    "jacob.smith@example.com" ));
        tableList.add(new Person("Isabella",  "Johnson",  "isabella.johnson@example.com" ));
        tableList.add(new Person("Ethan",     "Williams", "ethan.williams@example.com" ));
        tableList.add(new Person("Emma",      "Jones",    "emma.jones@example.com" ));
        tableList.add(new Person("Michael",   "Brown",    "michael.brown@example.com" ));

        
        tableList.add(new Person("Jacob",     "Smith",    "jacob.smith@example.com" ));
        tableList.add(new Person("Isabella",  "Johnson",  "isabella.johnson@example.com" ));
        tableList.add(new Person("Ethan",     "Williams", "ethan.williams@example.com" ));
        tableList.add(new Person("Emma",      "Jones",    "emma.jones@example.com" ));
        tableList.add(new Person("Michael",   "Brown",    "michael.brown@example.com" ));

        
        JsonIdMap map = new JsonIdMap();
        map.withCreator(new TableListCreator());
        map.withCreator(new PersonCreator());
        
        tableView.withMap(map).withList(tableList);
        
        tableView.withSearchProperties(Person.PROPERTY_FIRSTNAME, Person.PROPERTY_LASTNAME, Person.PROPERTY_EMAIL);
        
//        tableView.createFromCreator(null, true);
        
        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_FIRSTNAME));
        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_LASTNAME));
        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_EMAIL).withBrowserId(GUIPosition.EAST));
        
        
        root.getChildren().add(tableView);
        
        primaryStage.show();
//        tableView.test();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
//        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }

}