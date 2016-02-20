package de.uniks.networkparser.test.model.ludo.creator;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.ludo.Ludo;

public class LudoCreator implements SendableEntityCreator {
	private final String[] properties = new String[]
	{
			Ludo.PROPERTY_DATE,
			Ludo.PROPERTY_PLAYERS,
			Ludo.PROPERTY_DICE,
			Ludo.PROPERTY_FIELDS
	};

	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Ludo();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		return ((Ludo) target).get(attrName);
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value,
			String type) {
		if (IdMap.REMOVE.equals(type)) {
			attrName = attrName + type;
		}
		return ((Ludo) target).set(attrName, value);
	}
}
