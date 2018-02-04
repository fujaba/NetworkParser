package de.uniks.networkparser.test.javafx;


import java.io.File;

import de.uniks.networkparser.ext.javafx.DiagramEditor;
import javafx.application.Application;
import javafx.stage.Stage;

public class ShowDiagrammEditor extends Application{
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) throws Exception {
		String url = new File("diagram/diagram.html").toURI().toString();
		DiagramEditor.create(stage, url);
	}
}
