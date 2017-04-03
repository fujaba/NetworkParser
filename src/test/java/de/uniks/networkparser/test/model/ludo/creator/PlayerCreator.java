package de.uniks.networkparser.test.model.ludo.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.ludo.Player;

public class PlayerCreator implements SendableEntityCreator {
	private final String[] properties = new String[]
		{
			Player.PROPERTY_COLOR,
			Player.PROPERTY_ENUMCOLOR,
			Player.PROPERTY_NAME,
			Player.PROPERTY_X,
			Player.PROPERTY_Y,
			Player.PROPERTY_GAME,
			Player.PROPERTY_NEXT,
			Player.PROPERTY_PREV,
			Player.PROPERTY_DICE,
			Player.PROPERTY_START,
			Player.PROPERTY_BASE,
			Player.PROPERTY_LANDING,
			Player.PROPERTY_PAWNS
			};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Player();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		return ((Player) target).get(attrName);
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value,
			String type) {
		if (SendableEntityCreator.REMOVE.equals(type)) {
			attrName = attrName + type;
		}
		return ((Player) target).set(attrName, value);
	}
}
