package de.uniks.networkparser.gui.test;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.Style;
import de.uniks.networkparser.gui.controller.ModelListenerStringProperty;
import de.uniks.networkparser.gui.table.Column;
import de.uniks.networkparser.gui.table.SearchTableComponent;
import de.uniks.networkparser.gui.table.TableComponent;
import de.uniks.networkparser.gui.table.util.TableListCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.gui.test.model.GroupAccount;
import de.uniks.networkparser.gui.test.model.Person;
import de.uniks.networkparser.gui.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.gui.test.model.util.PersonCreator;

public class GroupAccountGUI extends Application {
	private GroupAccount groupAccount;
	private Person albert;
	private TextField textField;

	private void init(Stage primaryStage) {
		 AnchorPane root = new AnchorPane();

	        primaryStage.setScene(new Scene(root));

	        TableComponent tableView = new SearchTableComponent();
	        
	        JsonIdMap map = new  JsonIdMap();
	        map.withCreator(new TableListCreator());
	        map.withCreator(new PersonCreator());
	        map.withCreator(new GroupAccountCreator());
	        
	        tableView.withMap(map);
	        groupAccount = new GroupAccount();
	        
	        albert = groupAccount.createPersons().withName("Albert");
	        
	        groupAccount.createPersons().withName("Nina");
	        
	        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_NAME).withStyle(new Style().withWidth(100)));
	        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_BALANCE).withStyle(new Style().withWidth(100)));
	        
	        
	        tableView.withSearchProperties(Person.PROPERTY_NAME);
	        tableView.withList(groupAccount,  GroupAccount.PROPERTY_PERSONS);
	        VBox box = new VBox();
	        
	        HBox hbox=new HBox();
	        
	        textField = new TextField();
	        Button addField = new Button();
	        addField.setText("add");
	        addField.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					groupAccount.withPersons(new Person().withName(textField.getText()));
				}
			});        
	        Button update = new Button();
	        update.setText("update");
	        
	        update.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent arg0) {
					groupAccount.createItem().withBuyer(albert).withDescription("Bier").withValue(12.0);
					groupAccount.updateBalances();
				}
			});
	        
	        hbox.getChildren().addAll(textField, addField, update);
	        
	        HBox info=new HBox();
	        Label albertLabel=new Label();
	        albertLabel.setText("Albertsliste:");
	        
	        Label counter = new Label();
	        counter.textProperty().bindBidirectional(new ModelListenerStringProperty(new PersonCreator(), albert, Person.PROPERTY_ITEM));
	        
	        info.getChildren().addAll(albertLabel, counter);
	        
	        box.getChildren().addAll(tableView, hbox, info);
	        root.getChildren().add(box);
//	        groupAccount.updateBalances();
	}
    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}