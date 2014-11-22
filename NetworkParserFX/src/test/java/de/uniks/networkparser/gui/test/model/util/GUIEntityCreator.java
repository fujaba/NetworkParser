package de.uniks.networkparser.gui.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.gui.test.model.GUIEntity;

public class GUIEntityCreator implements SendableEntityCreator{

	@Override
	public String[] getProperties() {
		return new String[]{GUIEntity.PROPERTY_NUMBER, GUIEntity.PROPERTY_COLOR};
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new GUIEntity();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(GUIEntity.PROPERTY_NUMBER.equalsIgnoreCase(attribute)){
			return ((GUIEntity)entity).getNumber();
		}
		if(GUIEntity.PROPERTY_COLOR.equalsIgnoreCase(attribute)){
			return ((GUIEntity)entity).getColor();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		if(GUIEntity.PROPERTY_NUMBER.equalsIgnoreCase(attribute)){
			((GUIEntity)entity).withNumber(Integer.valueOf(""+value));
			return true;
		}
		if(GUIEntity.PROPERTY_COLOR.equalsIgnoreCase(attribute)){
			((GUIEntity)entity).withColor(""+value);
			return true;
		}
		return false;
	}

}
