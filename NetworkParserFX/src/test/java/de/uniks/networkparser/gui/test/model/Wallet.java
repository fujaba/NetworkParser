package de.uniks.networkparser.gui.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public class Wallet {
	protected PropertyChangeSupport listeners = new PropertyChangeSupport(this);
	public static final String PROPERTY_SUM = "sum";
	private double sum;

	public double getSum() {
		return this.sum;
	}

	public void setSum(double value) {
		if (this.sum != value) {
			double oldValue = this.sum;
			this.sum = value;
			getPropertyChangeSupport().firePropertyChange(PROPERTY_SUM,
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
