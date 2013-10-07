package de.uniks.jism.gui.test;

/**

 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.

 * All rights reserved. Use is subject to license terms.

 */

import java.util.Comparator;

import de.uniks.networkparser.gui.TableList;
import de.uniks.networkparser.gui.table.TableComponent;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventType;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
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

        Group root = new Group();

        primaryStage.setScene(new Scene(root));

        final ObservableList<Person> data = FXCollections.observableArrayList(

            new Person("Jacob",     "Smith",    "jacob.smith@example.com" ),

            new Person("Isabella",  "Johnson",  "isabella.johnson@example.com" ),

            new Person("Ethan",     "Williams", "ethan.williams@example.com" ),

            new Person("Emma",      "Jones",    "emma.jones@example.com" ),

            new Person("Michael",   "Brown",    "michael.brown@example.com" )

        );

        TableColumn firstNameCol = new TableColumn();

        firstNameCol.setText("First");

        firstNameCol.setCellValueFactory(new PropertyValueFactory("firstName"));

        TableColumn lastNameCol = new TableColumn();

        lastNameCol.setText("Last");

        lastNameCol.setCellValueFactory(new PropertyValueFactory("lastName"));

        TableColumn emailCol = new TableColumn();

        emailCol.setText("Email");

        emailCol.setMinWidth(200);
        emailCol.setComparator(new Comparator<Object>() {

			@Override
			public int compare(Object o1, Object o2) {
				
				return 0;
			}
		});
//        emailCol.addEventHandler(EventType., eventHandler);

        emailCol.setCellValueFactory(new PropertyValueFactory("email"));

        TableComponent tableView = new TableComponent();
//        TableView tableView = new TableView();

        tableList = new TableList();

        tableList.add(new Person("Jacob",     "Smith",    "jacob.smith@example.com" ));
        tableList.add(new Person("Isabella",  "Johnson",  "isabella.johnson@example.com" ));
        tableList.add(new Person("Ethan",     "Williams", "ethan.williams@example.com" ));
        tableList.add(new Person("Emma",      "Jones",    "emma.jones@example.com" ));
        tableList.add(new Person("Michael",   "Brown",    "michael.brown@example.com" ));
        
        tableView.finishDataBinding(tableList);
//        tableView.setItems(data);

        tableView.getColumns().addAll(firstNameCol, lastNameCol, emailCol);

        root.getChildren().add(tableView.getTableViewer());

    }

 

    public static class Person {

        private StringProperty firstName;

        private StringProperty lastName;

        private StringProperty email;

 

        private Person(String fName, String lName, String email) {

            this.firstName = new SimpleStringProperty(fName);

            this.lastName = new SimpleStringProperty(lName);

            this.email = new SimpleStringProperty(email);

        }

         

        public StringProperty firstNameProperty() { return firstName; }

        public StringProperty lastNameProperty() { return lastName; }

        public StringProperty emailProperty() { return email; }

    }

 

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }

}