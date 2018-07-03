package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.SimpleController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class PointPaneGUI extends Application {
//	private Dice dice = new Dice();
//FIXME	private PointPaneController pointPaneController;
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		SimpleController controller = SimpleController.create(primaryStage);
		AnchorPane layout = new AnchorPane();
		Pane pane = new Pane();
		pane.setPrefSize(32, 32);
		pane.setStyle("-fx-border-color: #2e8b57;-fx-border-width: 2px;");
		layout.getChildren().add(pane);

		// Dice must be three at first throw
//		PointPaneController.RandomSeed = 42;

//		pointPaneController = new  PointPaneController(pane);
//		dice.addPropertyChangeListener(Dice.PROPERTY_VALUE, pointPaneController);
//		pointPaneController.addW6Listener();
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
//				pointPaneController.throwDice();
			}
		});
		controller.show(layout);
	}

}
