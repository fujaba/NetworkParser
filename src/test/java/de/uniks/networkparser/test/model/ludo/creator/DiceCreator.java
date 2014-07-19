package de.uniks.networkparser.test.model.ludo.creator;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.ludo.Dice;

public class DiceCreator implements SendableEntityCreator {
   private final String[] properties = new String[]
		   {
		      Dice.PROPERTY_VALUE,
		      Dice.PROPERTY_GAME,
		      Dice.PROPERTY_PLAYER,
		   };
		   
	@Override
	public String[] getProperties() {
		return properties;
	}

	@Override
	public Object getSendableInstance(boolean reference) {
		return new Dice();
	}

	@Override
	public Object getValue(Object target, String attrName) {
		return ((Dice) target).get(attrName);
	}

	@Override
	public boolean setValue(Object target, String attrName, Object value,
			String type) {
		if (JsonIdMap.REMOVE.equals(type)) {
			attrName = attrName + type;
		}
		return ((Dice) target).set(attrName, value);
	}
}
