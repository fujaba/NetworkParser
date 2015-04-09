package de.uniks.networkparser.gui.javafx.test;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class PersonGUICreator implements SendableEntityCreator{

	@Override
	public String[] getProperties() {
		return new String[]{PersonGUI.PROPERTY_FIRSTNAME, PersonGUI.PROPERTY_LASTNAME, PersonGUI.PROPERTY_EMAIL, PersonGUI.PROPERTY_DISTANCE};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new PersonGUI();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(PersonGUI.PROPERTY_FIRSTNAME.equalsIgnoreCase(attribute)){
			return ((PersonGUI)entity).firstnameProperty().getValue();
		}
		if(PersonGUI.PROPERTY_LASTNAME.equalsIgnoreCase(attribute)){
			return ((PersonGUI)entity).lastnameProperty().getValue();
		}
		if(PersonGUI.PROPERTY_EMAIL.equalsIgnoreCase(attribute)){
			return ((PersonGUI)entity).emailProperty().getValue();
		}
		if(PersonGUI.PROPERTY_DISTANCE.equalsIgnoreCase(attribute)){
			return ((PersonGUI)entity).distanceProperty().getValue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(PersonGUI.PROPERTY_FIRSTNAME.equalsIgnoreCase(attribute)){
			((PersonGUI)entity).firstnameProperty().setValue(""+value);
			return true;
		}
		if(PersonGUI.PROPERTY_LASTNAME.equalsIgnoreCase(attribute)){
			((PersonGUI)entity).lastnameProperty().setValue(""+value);
			return true;
		}
		if(PersonGUI.PROPERTY_EMAIL.equalsIgnoreCase(attribute)){
			((PersonGUI)entity).emailProperty().setValue(""+value);
			return true;
		}
		if(PersonGUI.PROPERTY_DISTANCE.equalsIgnoreCase(attribute)){
			((PersonGUI)entity).distanceProperty().setValue(Integer.valueOf(""+value));
			return true;
		}

		return false;
	}

}
