package de.uniks.networkparser.test.model.ludo.util;

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
		if (SendableEntityCreator.REMOVE.equals(type)) {
			attrName = attrName + type;
		}
		return ((Ludo) target).set(attrName, value);
	}
	
	public static IdMap createIdMap(String sessionID) {
		IdMap map = new IdMap().withSession(sessionID);
		map.with(new LudoCreator());
		map.with(new DiceCreator());
		map.with(new FieldCreator());
		map.with(new LabelCreator());
		map.with(new PawnCreator());
		map.with(new PlayerCreator());
		map.withTimeStamp(1);
		return map;
	}
}
