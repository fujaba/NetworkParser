package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.javafx.ModelListenerFactory;
import de.uniks.networkparser.test.model.GUIEntity;
import de.uniks.networkparser.test.model.util.GUIEntityCreator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ColorPickerGUI extends Application{
		private GUIEntity colorPlayer;
		@Override
		public void start(Stage stage) throws Exception {

			AnchorPane root=new AnchorPane();
			ColorPicker picker=new ColorPicker();

//			ComboBox<String> combo=new ComboBox<String>();

			Scene scene = new Scene(root, 600, 400);

//			ColorPickerList list=new ColorPickerList();

			colorPlayer = new GUIEntity();

//			list.withColor(colorPlayer);

			Label label=new Label();
			ModelListenerFactory.create(picker, new GUIEntityCreator(), colorPlayer, GUIEntity.PROPERTY_COLOR);
//			ModelListenerFactory.create(picker, new GUIEntityCreator(), colorPlayer, GUIEntity.PROPERTY_COLOR);
//FIXME			ModelListenerFactory.create(picker, colorPlayer, GUIEntity.PROPERTY_COLOR);
//			combo.itemsProperty().bind(new ModelListenerListProperty(list, list, ColorPickerList.PROPERTY_COLORS, ColorPlayer.PROPERTY_COLOR, colorPlayer));

			VBox box = new VBox();
//			box.getChildren().addAll(picker, label, combo);
			box.getChildren().addAll(picker, label);
			root.getChildren().add(box);
			stage.setTitle("SimpleGUI");
			stage.setScene(scene);
			stage.show();
		}
		public static void main(String[] args) {
			launch(args);
		}
	}
