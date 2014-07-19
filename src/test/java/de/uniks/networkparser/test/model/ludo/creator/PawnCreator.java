package de.uniks.networkparser.test.model.ludo.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.ludo.Pawn;

public class PawnCreator implements SendableEntityCreator {
	private final String[] properties = new String[]
		{
			Pawn.PROPERTY_COLOR,
			Pawn.PROPERTY_X,
			Pawn.PROPERTY_Y,
			Pawn.PROPERTY_PLAYER,
			Pawn.PROPERTY_POS
		};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Pawn();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		return ((Pawn) target).get(attrName);
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value,
			String type) {
		if (JsonIdMap.REMOVE.equals(type)) {
			attrName = attrName + type;
		}
		return ((Pawn) target).set(attrName, value);
	}
}
