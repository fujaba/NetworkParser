package de.uniks.networkparser.gui.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.form.ModelForm;
import de.uniks.networkparser.json.JsonIdMap;

public class FormGUI extends Application {
	private void init(Stage primaryStage) {
		 AnchorPane root = new AnchorPane();

	        primaryStage.setScene(new Scene(root));

	        JsonIdMap map = new  JsonIdMap();
	        map.withCreator(new PersonCreator());
	        PersonGUI albert= new PersonGUI().withName("Albert");
	        
	        ModelForm box = new ModelForm();
	        box.withDataBinding(map, albert, true);
	        root.getChildren().add(box);
	}

    @Override
    public void start(Stage primaryStage) throws Exception {
        init(primaryStage);
        primaryStage.show();
    }

    public static void main(String[] args) { launch(args); }
}
