package de.uniks.networkparser.gui.test;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import de.uniks.networkparser.gui.ModelListenerColorProperty;
import de.uniks.networkparser.gui.ModelListenerListProperty;
import de.uniks.networkparser.gui.ModelListenerStringProperty;
import de.uniks.networkparser.test.model.ColorPickerList;
import de.uniks.networkparser.test.model.ColorPlayer;

public class ColorPickerGUI extends Application{
		private ColorPlayer colorPlayer;
		@Override
		public void start(Stage stage) throws Exception {

			AnchorPane root=new AnchorPane();
			ColorPicker picker=new ColorPicker();
			
			ComboBox<String> combo=new ComboBox<String>();
			
			Scene scene = new Scene(root, 600, 400);
			
			ColorPickerList list=new ColorPickerList();
			
			colorPlayer = new ColorPlayer();
			
			list.withColor(colorPlayer);
			
			Label label=new Label();
			label.textProperty().bind(new ModelListenerStringProperty(colorPlayer, colorPlayer, ColorPlayer.PROPERTY_COLOR));
			picker.valueProperty().bindBidirectional(new ModelListenerColorProperty(colorPlayer, colorPlayer, ColorPlayer.PROPERTY_COLOR));

			combo.itemsProperty().bind(new ModelListenerListProperty(list, list, ColorPickerList.PROPERTY_COLORS, ColorPlayer.PROPERTY_COLOR, colorPlayer));
			
			Button apply=new Button();
			VBox box = new VBox();
			box.getChildren().addAll(picker, label, apply, combo);
			root.getChildren().add(box);
			stage.setTitle("SimpleGUI");
			stage.setScene(scene);
			stage.show();
		}
		public static void main(String[] args) {
			launch(args);
		}
		

	}
