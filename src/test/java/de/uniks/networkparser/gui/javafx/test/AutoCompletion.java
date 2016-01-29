package de.uniks.networkparser.gui.javafx.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.javafx.controls.AutoCompletionList;
import de.uniks.networkparser.gui.javafx.controls.TextEditorControl;

public class AutoCompletion extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		GridPane gridPane = new GridPane();
		Label label = new Label("Name");
		label.setFont(Font.font("Arial", 50));

		TextEditorControl control = new TextEditorControl();
		control.withAutoCompleting(new AutoCompletionList().with("Hey", "Hello", "Hello World", "Apple", "Cool", "Costa", "Cola", "Coca Cola"));

		gridPane.add(label, 0, 0);
		gridPane.add(control.getControl(), 1, 0);

		//gridPane.setGridLinesVisible(true);
		Scene scene = new Scene(gridPane);
		stage.setScene(scene);
		stage.setMinHeight(100);
		stage.show();
	}
}
