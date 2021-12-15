package de.uniks.networkparser.test.bridge;

import org.junit.Assert;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.ext.gui.JavaBridgeFX;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.gui.EventTypes;
import de.uniks.networkparser.gui.controls.Button;
import de.uniks.networkparser.gui.controls.ChoiceField;
import de.uniks.networkparser.gui.controls.Form;
import de.uniks.networkparser.gui.controls.NumberField;
import de.uniks.networkparser.gui.controls.TableComponent;
import de.uniks.networkparser.gui.controls.TextField;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class SampleController {
	private JavaBridgeFX javaBridge;
	SimpleObject blub;

	public void initialize() {
		javaBridge = new JavaBridgeFX();

//		blub = SimpleObject.create("number", "value", 0);
//		blub.setId("number");
//		javaBridge.getMap().put("number", blub);
//		javaBridge.executeScript("bridge.load('number');");

		buildNumberField();
		// buildAddButton();
		// buildSubtractButton();
		// buildEventButton();

		// javaBridge.addControl(new PasswordField());
		// javaBridge.addControl(DateTimeField.createDateField());
		// javaBridge.addControl(DateTimeField.createTimeField());
		// buildChoiceField();

		// buildTable();

		// buildForm();

		// javaBridge.executeScript(
		// "if (!document.getElementById('FirebugLite')){E =
		// document['createElement' + 'NS'] &&
		// document.documentElement.namespaceURI;E = E ?
		// document['createElement' + 'NS'](E, 'script') :
		// document['createElement']('script');E['setAttribute']('id',
		// 'FirebugLite');E['setAttribute']('src', 'https://getfirebug.com/' +
		// 'firebug-lite.js' + '#startOpened');E['setAttribute']('FirebugLite',
		// '4');(document['getElementsByTagName']('head')[0] ||
		// document['getElementsByTagName']('body')[0]).appendChild(E);E = new
		// Image;E['setAttribute']('src', 'https://getfirebug.com/' +
		// '#startOpened');}");
	}

	protected void buildForm() {
		Form form = new Form();
		TextField textField = new TextField();
		textField.setValue("Test");
		form.withElement(textField);

		javaBridge.addControl(form);
	}

	protected void buildChoiceField() {
		ChoiceField choiceField = new ChoiceField(false);
		choiceField.setChecked(true);
		javaBridge.addControl(choiceField);
		javaBridge.addControl(new ChoiceField(true));
	}

	protected void buildTable() {
		TableComponent tableComponent = new TableComponent();

		Column testColumn = new Column().withLabel("number");
		testColumn.withID("value");

		tableComponent.withColumn(testColumn);
		tableComponent.setProperty("number");

		javaBridge.addControl(tableComponent);
	}

	protected void buildEventButton() {
		Button button = new Button();
		button.setValue("Fire Event");
		// button.setOnclick((Condition<Number>) value -> {asdsadjkl});

		// button.setOnClick("SampleController.add()");
		button.setId("fireButton");

		javaBridge.addControl(button);

		javaBridge.addEventListener(button, EventTypes.CLICK,
				new ObjectCondition() {
					@Override
					public boolean update(Object value) {
						System.err.println("Blub: " + blub);
						return true;
					}
				});
	}

	private void buildNumberField() {
		NumberField numberField = new NumberField();
		numberField.setId("wurst");
		numberField.setProperty("number.value");
		numberField.setValue(0);

		javaBridge.addControl(numberField);

//		javaBridge.addEventListener(numberField, EventTypes.CHANGE,
//				new InputFieldValueListener<Integer>(numberField));

//		javaBridge.addEventListener(numberField, EventTypes.CHANGE,
//				new ObjectCondition() {
//					@Override
//					public boolean update(Object value) {
//						// blub.setValue("value", numberField.getValue());
//						return true;
//					}
//				});

		/*
		 * new ObjectCondition() {
		 * 
		 * @Override public boolean update(Object value) { if(value instanceof
		 * EventFX){ EventFX e = (EventFX) value; try { JsonObjectLazy obj =
		 * ((JsonObjectLazy) e.getCurrentTarget()); Object newValue =
		 * obj.loadValue("value");
		 * numberField.setValue(Integer.valueOf(newValue+""));
		 * 
		 * blub.setValue("value", Integer.valueOf(newValue+"")); } catch
		 * (Exception e2) { e2.printStackTrace(); } // value.get //
		 * NumberField.this.setValue(key, value); } return false; } });
		 */

//		javafx.scene.control.Button button = new javafx.scene.control.Button();
//		button.setOnAction(evt -> {
//			numberField
//					.setValue(Math.toIntExact(Math.round(Math.random() * 100)));
//		});
//		buttonBar.getButtons().add(button);
	}

	protected void buildAddButton() {
		Button button = new Button();
		button.setValue("add");
		// button.setOnclick((Condition<Number>) value -> {asdsadjkl});

		// button.setOnClick("SampleController.add()");
		// button.setId("addButton");

		javaBridge.addControl(button);

		// javaBridge.addListener(button, EventTypes.CLICK, "add", this);

		javaBridge.addEventListener(button, EventTypes.CLICK,
				new ObjectCondition() {
					@Override
					public boolean update(Object value) {
						blub.setValue("value",
								Integer.valueOf("" + blub.getValue("value"))
										+ 1);
						return false;
					}
				});

	}

	protected void buildSubtractButton() {
		Button button = new Button();
		button.setValue("subtract");

		javaBridge.addControl(button);

		javaBridge.addEventListener(button, EventTypes.CLICK,
				new ObjectCondition() {
					@Override
					public boolean update(Object value) {
						blub.setValue("value",
								Integer.valueOf("" + blub.getValue("value"))
										- 1);
						return false;
					}
				});
	}

	public void showValue(SimpleEvent event) {
		IdMap map = javaBridge.getMap();
		Object object = map.getObject("number");
		Assert.assertNotNull(object);
	}

	public void add(SimpleEvent event) {
		IdMap map = javaBridge.getMap();
		Object object = map.getObject("number");

		if (object instanceof SimpleObject) {
			int value = (int) ((SimpleObject) object).getValue("value");
			((SimpleObject) object).setValue("value", ++value);
		}

	}

	public void subtract(SimpleEvent event) {
		IdMap map = javaBridge.getMap();
		Object object = map.getObject("number");
		// ModelListenerFactory.update(object, "value", Addition.create(1));

		if (object instanceof SimpleObject) {
			Integer value = (Integer) ((SimpleObject) object).getValue("value");
			((SimpleObject) object).setValue("value", --value);
		}

	}
}
