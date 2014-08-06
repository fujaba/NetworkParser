package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.Game;

public class GameCreator implements SendableEntityCreator{

	@Override
	public String[] getProperties() {
		return null;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Game();
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		return false;
	}

}
