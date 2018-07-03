package de.uniks.networkparser.test.bridge;

import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.javafx.JavaBridgeFX;
import de.uniks.networkparser.gui.controls.Button;

public class TestBridge {
	public static void main(String[] args) {
		SimpleController controller = SimpleController.create(new JavaBridgeFX(), null, true, false);
		controller.getBridge().enableFirebug();
		controller.getBridge().addControl(new Button().withValue("Click Me"));
		
		
	}
}
