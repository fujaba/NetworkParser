package de.uniks.networkparser.gui.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.form.PropertyComposite;
import de.uniks.networkparser.json.JsonIdMap;

public class FormEditorGUI extends Application {
	private void init(Stage primaryStage) {
		 AnchorPane root = new AnchorPane();

	        primaryStage.setScene(new Scene(root));

	        JsonIdMap map = new  JsonIdMap();
	        map.withCreator(new PersonCreator());
	        PersonGUI albert= new PersonGUI().withName("Albert");
	        
	        PropertyComposite box = new PropertyComposite();
	        box.withDataBinding(map, albert, new Column().withAttrName(PersonGUI.PROPERTY_LASTNAME));
	        root.getChildren().add(box);
	        
//	        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_NAME).withStyle(new Style().withWidth(100)));
//	        tableView.withColumn(new Column().withAttrName(Person.PROPERTY_BALANCE).withStyle(new Style().withWidth(100)));
//	        tableView.withSearchProperties(Person.PROPERTY_NAME);
//	        tableView.withList(groupAccount,  GroupAccount.PROPERTY_PERSONS);
//	        VBox box = new VBox();
//	        
//	        HBox hbox=new HBox();
//	        
//	        textField = new TextField();
//	        Button addField = new Button();
//	        addField.setText("add");
//	        addField.setOnAction(new EventHandler<ActionEvent>() {
//				@Override
//				public void handle(ActionEvent arg0) {
//					groupAccount.addToPersons(new Person().withName(textField.getText()));
//				}
//			});        
//	        Button update = new Button();
//	        update.setText("update");
//	        
//	        update.setOnAction(new EventHandler<ActionEvent>() {
//				@Override
//				public void handle(ActionEvent arg0) {
//					groupAccount.createItems().withBuyer(albert).withDescription("Bier").withValue(12.0);
//					groupAccount.updateBalances();
//				}
//			});
//	        
//	        hbox.getChildren().addAll(textField, addField, update);
//	        
//	        HBox info=new HBox();
//	        Label albertLabel=new Label();
//	        albertLabel.setText("Albertsliste:");
//	        
//	        Label counter = new Label();
//	        counter.textProperty().bindBidirectional(new ModelListenerStringProperty(new PersonCreator(), albert, Person.PROPERTY_ITEMS));
//	        
//	        info.getChildren().addAll(albertLabel, counter);
//	        
//	        box.getChildren().addAll(tableView, hbox, info);
	}

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
