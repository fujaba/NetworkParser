package de.uniks.networkparser.gui.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Wallet {
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	public static final String PROPERTY_BALANCE = "balance";
	private double balance;

	public double getBalance() {
		return this.balance;
	}

	public void setBalance(double value) {
		if (this.balance != value) {
			double oldValue = this.balance;
			this.balance = value;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_BALANCE,
					oldValue, value);
		}
	}

	public PropertyChangeSupport getPropertyChangeSupport() {
		return listeners;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		getPropertyChangeSupport().addPropertyChangeListener(listener);
	}

	// ==========================================================================

	public void removeYou() {
		getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
	}
}
