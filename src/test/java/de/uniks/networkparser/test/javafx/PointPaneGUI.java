package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.javafx.controller.PointPaneController;
import de.uniks.networkparser.ext.javafx.window.FXStageController;
import de.uniks.networkparser.ext.javafx.window.SimpleShell;
import de.uniks.networkparser.test.model.ludo.Dice;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

public class PointPaneGUI extends SimpleShell {
	private Dice dice = new Dice();
	private PointPaneController pointPaneController;
	@Override
	protected Parent createContents(FXStageController value, Parameters args) {
		AnchorPane layout = new AnchorPane();
		Pane pane = new Pane();
		pane.setPrefSize(32, 32);
		pane.setStyle("-fx-border-color: #2e8b57;-fx-border-width: 2px;");
		layout.getChildren().add(pane);
		
		pointPaneController = new  PointPaneController(pane);
		dice.addPropertyChangeListener(Dice.PROPERTY_VALUE, pointPaneController);
		pointPaneController.addW6Listener();
		
		return layout;
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
