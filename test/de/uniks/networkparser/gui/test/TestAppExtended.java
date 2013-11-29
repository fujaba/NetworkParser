package de.uniks.networkparser.gui.test;

/**

 * Copyright (c) 2008, 2012 Oracle and/or its affiliates.

 * All rights reserved. Use is subject to license terms.

 */

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.SearchTableComponent;
import de.uniks.networkparser.gui.table.TableComponent;
import de.uniks.networkparser.gui.table.TableList;
import de.uniks.networkparser.gui.table.creator.TableListCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.creator.GroupAccountCreator;
import de.uniks.networkparser.test.model.creator.PersonCreator;

/**

 * A simple table with a header row.

 *

 * @see javafx.scene.control.TableCell

 * @see javafx.scene.control.TableColumn

 * @see javafx.scene.control.TablePosition

 * @see javafx.scene.control.TableRow

 * @see javafx.scene.control.TableView

 */

public class TestAppExtended extends Application {

    private TableList tableList;
	private TextField textField;
	private GroupAccount groupAccount;
	private Person albert;

	private void init(Stage primaryStage) {

        AnchorPane root = new AnchorPane();

        primaryStage.setScene(new Scene(root));

        TableComponent tableView = new SearchTableComponent();
        groupAccount = new GroupAccount();
        albert = new Person().withName("Albert");
		groupAccount.addToPersons(albert);
        groupAccount.addToPersons(new Person().withName("Stefan"));
        
        JsonIdMap map = new  JsonIdMap();
        map.withCreator(new TableListCreator());
        map.withCreator(new PersonCreator());
        map.withCreator(new GroupAccountCreator());
        
        tableView.withMap(map).withList(groupAccount, GroupAccount.PROPERTY_PERSONS);
        
        tableView.withSearchProperties(Person.PROPERTY_NAME, Person.PROPERTY_BALANCE);
        
//        tableView.createFromCreator(null, true);
        
        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_NAME));
        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_BALANCE, true));
        
        VBox box=new VBox();
        box.getChildren().add(tableView);
        
        textField = new TextField();
        Button addField = new Button();
        addField.setText("add");
        addField.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				groupAccount.addToPersons(new Person().withName(textField.getText()));
			}
		});        
        Button update = new Button();
        update.setText("update");
        update.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				albert.setBalance(42.0);
			}
		});
        HBox add = new HBox();
        add.getChildren().addAll(textField, addField, update);
        box.getChildren().add(add);
        
        root.getChildren().add(box);
        primaryStage.show();
        
        
//        System.out.println(map.toJsonObject(tableList).toString(2));
//        tableView.test();

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
//        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }

}