package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.javafx.dialog.DialogBox;
import de.uniks.networkparser.ext.javafx.window.FXStageController;
import de.uniks.networkparser.ext.javafx.window.SimpleShell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class SimpleMessageBox extends SimpleShell{
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected Parent createContents(FXStageController value, Parameters args) {
		GridPane gridPane = new GridPane();
		Button button=new Button();
		button.setText("Click Me");
		button.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				DialogBox.showInfo("Test", "info");

			}
		});
		gridPane.getChildren().add(button);
		return gridPane;
	}
}
