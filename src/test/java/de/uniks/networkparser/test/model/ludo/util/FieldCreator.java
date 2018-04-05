package de.uniks.networkparser.test.model.ludo.util;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.ludo.Field;

public class FieldCreator implements SendableEntityCreator {
	private final String[] properties = new String[]
	{
	  Field.PROPERTY_COLOR,
	  Field.PROPERTY_KIND,
	  Field.PROPERTY_X,
	  Field.PROPERTY_Y,
	  Field.PROPERTY_GAME,
	  Field.PROPERTY_NEXT,
	  Field.PROPERTY_PREV,
	  Field.PROPERTY_LANDING,
	  Field.PROPERTY_ENTRY,
	  Field.PROPERTY_STARTER,
	  Field.PROPERTY_BASEOWNER,
	  Field.PROPERTY_LANDER,
	  Field.PROPERTY_PAWNS
	};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Field();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		if(target instanceof Field == false) {
			return null;
		}
		Field field = (Field) target;
		if (Field.PROPERTY_COLOR.equalsIgnoreCase(attrName)) {
			return field.getColor();
		}
		if (Field.PROPERTY_KIND.equalsIgnoreCase(attrName)) {
			return field.getKind();
		}
		if (Field.PROPERTY_X.equalsIgnoreCase(attrName)) {
			return field.getX();
		}
		if (Field.PROPERTY_Y.equalsIgnoreCase(attrName)) {
			return field.getY();
		}
		if (Field.PROPERTY_GAME.equalsIgnoreCase(attrName)) {
			return field.getGame();
		}
		if (Field.PROPERTY_NEXT.equalsIgnoreCase(attrName)) {
			return field.getNext();
		}
		if (Field.PROPERTY_PREV.equalsIgnoreCase(attrName)) {
			return field.getPrev();
		}
		if (Field.PROPERTY_LANDING.equalsIgnoreCase(attrName)) {
			return field.getLanding();
		}
		if (Field.PROPERTY_ENTRY.equalsIgnoreCase(attrName)) {
			return field.getEntry();
		}
		if (Field.PROPERTY_STARTER.equalsIgnoreCase(attrName)) {
			return field.getStarter();
		}
		if (Field.PROPERTY_BASEOWNER.equalsIgnoreCase(attrName)) {
			return field.getBaseowner();
		}
		if (Field.PROPERTY_LANDER.equalsIgnoreCase(attrName)) {
			return field.getLander();
		}
		if (Field.PROPERTY_PAWNS.equalsIgnoreCase(attrName)) {
			return field.getPawns();
		}
		if (Field.PROPERTY_LABEL.equalsIgnoreCase(attrName)) {
			return field.getLabel();
		}
		return null;
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value,
			String type) {
		if (SendableEntityCreator.REMOVE.equals(type)) {
			attrName = attrName + type;
		}
		return ((Field) target).set(attrName, value);
	}

	public static IdMap createIdMap(String sessionID) {
		return LudoCreator.createIdMap(sessionID);
	}
}
