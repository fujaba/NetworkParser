package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.javafx.SimpleController;
import de.uniks.networkparser.ext.javafx.controller.ModelListenerFactory;
import de.uniks.networkparser.test.model.GUIEntity;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;


public class ApplicationTest extends Application {

	private static GUIEntity data = new GUIEntity();
	public static void main(String[] args) throws Exception {
		launch(args);
	}
	private TextField field;
	@Override
	public void start(Stage primaryStage) throws Exception {
		SimpleController controller = SimpleController.create(primaryStage);
		
		System.out.println(primaryStage);
		VBox root=new VBox();
		   HBox box=new HBox();
		Label label = new Label("Button has been clicked: ");
		  Label dataLabel = new Label();
		  dataLabel.setId(GUIEntity.PROPERTY_NUMBER);

		  box.getChildren().addAll(label, dataLabel);

		  dataLabel.setTextAlignment(TextAlignment.RIGHT);

//		  ModelListenerFactory.create(dataLabel, data, GUIEntity.PROPERTY_NUMBER);
		  ModelListenerFactory.create(dataLabel, data);

		  field = new TextField();

	// TEST SIMPLE
		  ModelListenerFactory.create(field, data, GUIEntity.PROPERTY_NUMBER);

		  
		  Button button = new Button("Clicke Me");

		  root.getChildren().addAll(box, field, button);	
		  
//		  controller.withIcon("de.uniks.networkparser.ext.javafx.dialog.JavaCup32.png");
		  controller.withIcon("dialog/JavaCup32.png", SimpleController.class);
		  
		  
		  controller.showTrayIcon();
		  
		  controller.show(root);
//		  primaryStage.show();
	}
	
}
