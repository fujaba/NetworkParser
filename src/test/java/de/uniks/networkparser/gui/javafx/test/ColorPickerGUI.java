package de.uniks.networkparser.gui.javafx.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.javafx.controller.ModelListenerColorProperty;
import de.uniks.networkparser.gui.javafx.controller.ModelListenerStringProperty;
import de.uniks.networkparser.gui.javafx.test.model.GUIEntity;
import de.uniks.networkparser.gui.javafx.test.model.util.GUIEntityCreator;

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
			label.textProperty().bind(new ModelListenerStringProperty(new GUIEntityCreator(), colorPlayer, GUIEntity.PROPERTY_COLOR));
			picker.valueProperty().bindBidirectional(new ModelListenerColorProperty(new GUIEntityCreator(), colorPlayer, GUIEntity.PROPERTY_COLOR));

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
