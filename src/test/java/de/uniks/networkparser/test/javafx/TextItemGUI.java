package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.javafx.controller.ModelListenerFactory;
import de.uniks.networkparser.ext.javafx.dialog.DialogBox;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class TextItemGUI extends Application{
	private String name="bla";
	@Override
	public void start(Stage stage) throws Exception {

		AnchorPane root=new AnchorPane();
		Scene scene = new Scene(root, 600, 400);
		TextField label=new TextField();
		ModelListenerFactory.create(label, this, "name");

		HBox box = new HBox();
		Button button=new Button("display");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent arg0) {
				DialogBox.showInfo("Name", "The name is: "+name);
			}
		});
		box.getChildren().addAll( label, button);
		root.getChildren().add(box);
		stage.setTitle("SimpleGUI");
		stage.setScene(scene);
		stage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}
}
