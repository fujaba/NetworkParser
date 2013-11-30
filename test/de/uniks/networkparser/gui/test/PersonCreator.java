package de.uniks.networkparser.gui.test;

import org.sdmlib.serialization.interfaces.SendableEntityCreator;

public class PersonCreator implements SendableEntityCreator{

	@Override
	public String[] getProperties() {
		return new String[]{Person.PROPERTY_FIRSTNAME, Person.PROPERTY_LASTNAME, Person.PROPERTY_EMAIL, Person.PROPERTY_DISTANCE};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Person();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(Person.PROPERTY_FIRSTNAME.equalsIgnoreCase(attribute)){
			return ((Person)entity).firstnameProperty().getValue();
		}
		if(Person.PROPERTY_LASTNAME.equalsIgnoreCase(attribute)){
			return ((Person)entity).lastnameProperty().getValue();
		}
		if(Person.PROPERTY_EMAIL.equalsIgnoreCase(attribute)){
			return ((Person)entity).emailProperty().getValue();
		}
		if(Person.PROPERTY_DISTANCE.equalsIgnoreCase(attribute)){
			return ((Person)entity).distanceProperty().getValue();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(Person.PROPERTY_FIRSTNAME.equalsIgnoreCase(attribute)){
			((Person)entity).firstnameProperty().setValue(""+value);
			return true;
		}
		if(Person.PROPERTY_LASTNAME.equalsIgnoreCase(attribute)){
			((Person)entity).lastnameProperty().setValue(""+value);
			return true;
		}
		if(Person.PROPERTY_EMAIL.equalsIgnoreCase(attribute)){
			((Person)entity).emailProperty().setValue(""+value);
			return true;
		}
		if(Person.PROPERTY_DISTANCE.equalsIgnoreCase(attribute)){
			((Person)entity).distanceProperty().setValue(""+value);
			return true;
		}

		return false;
	}

}
