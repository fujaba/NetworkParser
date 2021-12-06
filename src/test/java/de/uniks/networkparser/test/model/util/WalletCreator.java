package de.uniks.networkparser.test.model.util;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.test.model.Wallet;

public class WalletCreator implements SendableEntityCreator {
	private static final String[] props = new String[] {Wallet.PROPERTY_SUM};
	@Override
	public String[] getProperties() {
		return props;
	}

	@Override
	public Object getValue(Object entity, String attribute) {
		if(Wallet.PROPERTY_SUM.equals(attribute)) {
			return ((Wallet) entity).getSum();
		}
		return null;
	}

	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if(Wallet.PROPERTY_SUM.equals(attribute)) {
			((Wallet) entity).setSum((double) value);
			return true;
		}		
		return false;
	}

	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Wallet();
	}
}
