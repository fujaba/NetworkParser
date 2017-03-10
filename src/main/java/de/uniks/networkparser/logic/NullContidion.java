package de.uniks.networkparser.logic;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.UpdateListener;

public class NullContidion implements UpdateListener, SendableEntityCreator {

	@Override
	public boolean update(Object value) {
		return value == null;
	}

	@Override
	public String[] getProperties() {
		return new String[] {};
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new NullContidion();
	}
}
