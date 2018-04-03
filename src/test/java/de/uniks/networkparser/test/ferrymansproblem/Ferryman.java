package de.uniks.networkparser.test.ferrymansproblem;

import de.uniks.networkparser.ext.petaf.SendableItem;
import de.uniks.networkparser.test.ferrymansproblem.util.BankSet;

public class Ferryman extends SendableItem {

	private static final String PROPERTY_BANKS = "banks";

	private BankSet banks;

	public Ferryman withBanks(Bank... value) {
		if (value == null) {
			return this;
		}
		for (Bank item : value) {
			if (item != null) {
				if (this.banks == null) {
					this.banks = new BankSet();
				}

				boolean changed = this.banks.add(item);

				if (changed) {
					item.withFerryman(this);
					getPropertyChangeSupport().firePropertyChange(PROPERTY_BANKS, null, item);
				}
			}
		}
		return this;
	}

	public Ferryman withoutBanks(Bank... value) {
		for (Bank item : value) {
			if ((this.banks != null) && (item != null)) {
				if (this.banks.remove(item)) {
					item.setRiver(null);
					getPropertyChangeSupport().firePropertyChange(PROPERTY_BANKS, item, null);
				}
			}
		}
		return this;
	}
}
