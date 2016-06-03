package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.event.GUILine;
import de.uniks.networkparser.ext.javafx.StyleFX;
import de.uniks.networkparser.interfaces.GUIPosition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class TestWindow extends Application{

	private void init(Stage primaryStage) {

		AnchorPane root = new AnchorPane();

		Label label = new Label();

		StyleFX style=new StyleFX();

		style.withBorder(GUIPosition.NORTH, new GUILine().withColor("#00FFFF").withWidth("1"));
		style.withBorder(GUIPosition.WEST, new GUILine().withColor("#000000").withWidth("1"));
		style.withBorder(GUIPosition.SOUTH, new GUILine().withColor("#CCCCCC").withWidth("20"));

		label.setText("Hallo Welt");
		System.out.println(style.toString());
		label.setStyle(style.toString());

		root.getChildren().add( label );

		primaryStage.setScene(new Scene(root));
		primaryStage.show();

	}
	@Override
	public void start(Stage primaryStage) throws Exception {
		init(primaryStage);
	}

	public static void main(String[] args) { launch(args); }

}
