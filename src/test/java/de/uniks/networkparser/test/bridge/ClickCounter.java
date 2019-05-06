package de.uniks.networkparser.test.bridge;

import org.junit.Assert;

import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.ext.SimpleController;
import de.uniks.networkparser.ext.javafx.JavaBridgeFX;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.controls.Button;
import de.uniks.networkparser.gui.controls.NumberField;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class ClickCounter {
//	private JavaBridgeFX javaBridge;
//	SimpleObject blub;

	public static void main(String[] args) {
		JavaBridgeFX javaBridge = new JavaBridgeFX();
		SimpleController controller = SimpleController.create(javaBridge, null, true, false);
		Assert.assertNotNull(controller);
		SimpleObject blub=new SimpleObject();	

		NumberField numberField = new NumberField();
		numberField.withElement(blub);
		javaBridge.addControl(numberField);

		Button button = new Button();
		button.setValue("add");
		javaBridge.addControl(button);
		javaBridge.addEventListener(button, EventTypes.CLICK,
				new ObjectCondition() {
					@Override
					public boolean update(Object value) {
						blub.setValue("value", Integer.valueOf("" + blub.getValue("value")) + 1);
						return false;
					}
		});
	}
}
