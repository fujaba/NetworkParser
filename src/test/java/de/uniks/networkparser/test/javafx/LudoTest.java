package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.javafx.PointPaneController;

public class LudoTest {

	public static void main(String[] args) {
		SimpleController controller = SimpleController.createFX();
		controller.withMap(new PointPaneController(), "dice");
		controller.withFXML("LudoGameScreen.fxml", LudoTest.class);
//		controller.show(null, true, true);
		controller.show();
		
		
		// AND MERGING
		
	}
}
