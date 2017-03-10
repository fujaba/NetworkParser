package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.javafx.controller.ModelListenerFactory;
import de.uniks.networkparser.ext.javafx.controller.ModelListenerStringProperty;
import de.uniks.networkparser.test.model.GUIEntity;
import de.uniks.networkparser.test.model.util.GUIEntityCreator;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class ClickCounter extends Application
{
   private GUIEntity data = new GUIEntity();
   private TextField field;

   public static void main(String[] args)
   {
	  launch(args);
   }

   @Override
   public void start(Stage stage) throws Exception
   {
	   VBox root=new VBox();
	   HBox box=new HBox();

	  Label label = new Label("Button has been clicked: ");
	  Label dataLabel = new Label();

	  box.getChildren().addAll(label, dataLabel);

	  dataLabel.setTextAlignment(TextAlignment.RIGHT);

	  dataLabel.textProperty().bind(new ModelListenerStringProperty(new GUIEntityCreator(), data, GUIEntity.PROPERTY_NUMBER));

	  field = new TextField();
//FIRST
//		JavaBeanIntegerProperty beanProperty = JavaBeanIntegerPropertyBuilder.create().bean(data).name(GUIEntity.PROPERTY_NUMBER).build();
//	  field.textProperty().bind(beanProperty.asString());

//SECOND SDMLIB
//	  field.textProperty().bindBidirectional(new ModelListenerStringProperty(new GUIEntityCreator(), data, GUIEntity.PROPERTY_NUMBER));

//SIMPLE
//	  ModelListenerProperty.create(field.textProperty(), new GUIEntityCreator(), data, GUIEntity.PROPERTY_NUMBER);

// TEST SIMPLE
	  ModelListenerFactory.create(field, data, GUIEntity.PROPERTY_NUMBER);

	  Button button = new Button("Clicke Me");

	  root.getChildren().addAll(box, field, button);

	  button.setOnAction(new EventHandler<ActionEvent>()
	  {

		 @Override
		 public void handle(ActionEvent arg0)
		 {
			data.withNumber(data.getNumber() + 1);
			System.out.println("now: " + data.getNumber());
		 }
	  });

	  root.setAlignment(Pos.CENTER);

	  Scene scene = new Scene(root, 400, 300);

	  stage.setTitle("Click Counter");
	  stage.setScene(scene);
	  stage.show();
   }
}
