package de.uniks.networkparser.test.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Objects;

public class Wallet{
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
	
	public Wallet withSum(double value) {
		this.setSum(value);
		return this;
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
	
	@Override
	public String toString() {
		return "Wallet ("+Objects.hashCode(this)+") :"+this.getSum();
	}

}
