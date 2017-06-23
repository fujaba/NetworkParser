package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.generic.ReflectionLoader;
import de.uniks.networkparser.ext.javafx.dialog.DialogBox;
import de.uniks.networkparser.ext.javafx.dialog.DialogPane;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class PopUpTest extends Application {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		ReflectionLoader.logger = System.out;
		
		DialogPane dialogPane = new DialogPane(null, null);
		System.out.println(dialogPane.getPane() instanceof Pane);
//		System.out.println(dialogPane.getProxy() instanceof Pane);
		
		
		stage.setTitle("PopUpTest");
		VBox root = new VBox();
		root.setAlignment(Pos.CENTER);

		Button showPopUp=new Button("show PopUp");
		showPopUp.setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {
//				DialogBox.showInfo(stage, "My little PopUp", "This is a PopUpWindow", true);
				DialogBox.showInfo(stage, "My little PopUp", "This is a PopUpWindow", false);
			};
		});
		root.getChildren().add(showPopUp);
		Scene scene = new Scene(root, 400, 300);
		stage.setScene(scene);
		stage.show();
	}

}