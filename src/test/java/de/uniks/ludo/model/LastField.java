package de.uniks.ludo.model;
import de.uniks.ludo.model.Target;


public class LastField extends Field {


	public static final String PROPERTY_TARGET = "target";

	private Target target = null;

	public Target getTarget() {
		return this.target;
	}

	public boolean setTarget(Target value) {
		if (this.target == value) {
			return false;
		}
		Target oldValue = this.target;
		if (this.target != null) {
			this.target = null;
			oldValue.setLastField(null);
		}
		this.target = value;
		if (value != null) {
			value.withLastField(this);
		}
		firePropertyChange(PROPERTY_TARGET, oldValue, value);
		return true;
	}

	public LastField withTarget(Target value) {
		this.setTarget(value);
		return this;
	}

	public Target createTarget() {
		Target value = new Target();
		withTarget(value);
		return value;
	}
}