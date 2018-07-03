package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.javafx.dialog.DialogBox;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SimpleMessageBox extends Application{
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		SimpleController controller = SimpleController.create(primaryStage);
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
		controller.show(gridPane);
	}
}
