package de.uniks.networkparser.gui.javafx.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StartSimpleGUI extends Application{
	@Override
	public void start(Stage stage) throws Exception {
//		Parent root = FXMLLoader.load(getClass().getResource("fxml/example.fxml"));
		Parent root = FXMLLoader.load(getClass().getResource("fxml/simple.fxml"));
		
		Scene scene = new Scene(root, 600, 400);
		
		stage.setTitle("SimpleGUI");
		stage.setScene(scene);
		stage.show();
	}
	public static void main(String[] args) {
		launch(args);
	}

}
