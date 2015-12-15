package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.House;

public class HouseCreator implements SendableEntityCreator {
	public String[] getProperties() {return new String[]{House.PROPERTY_NAME, House.PROPERTY_FLOOR};}
	public Object getSendableInstance(boolean prototyp) {return new House();}
	
	public Object getValue(Object entity, String attribute) {
		if(House.PROPERTY_NAME.equals(attribute)){
			return ((House)entity).getName();
		}
		if(House.PROPERTY_FLOOR.equals(attribute)){
			return ((House)entity).getFloor();
		}
		return null;
	}
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(House.PROPERTY_NAME.equals(attribute)){
			((House)entity).setName(""+value);
			return true;
		}
		if(House.PROPERTY_FLOOR.equals(attribute)){
			((House)entity).setFloor((Integer)value);
			return true;
		}
		return false;
	}
}
