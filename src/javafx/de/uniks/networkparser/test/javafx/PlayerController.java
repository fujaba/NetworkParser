package de.uniks.networkparser.test.javafx;

import de.uniks.networkparser.interfaces.SendableEntityCreator;

public class PlayerController implements SendableEntityCreator {

	@Override
	public String[] getProperties() {
		return null;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(entity instanceof PlayerController == false) {
			return false;
		}
//		PlayerController controller = entity;
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new PlayerController();
	}

}
