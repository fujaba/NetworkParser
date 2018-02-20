package de.uniks.networkparser.gui.controls;

public class ChoiceField extends Input<String> {
	/*
	 * Constants
	 */
	protected static final String CHECKBOX = "checkbox";

	protected static final String RADIO = "radio";

	protected static final String CHECKED = "checked";

	protected static final String VALUE = "value";

	public static final String ON = "on";

	public static final String OFF = "on";

	private boolean checked = false;


	public ChoiceField(boolean multi) {
		super();
		this.addBaseElements(CHECKED);
		if (multi) {
			this.type = RADIO;
		}
		else {
			this.type = CHECKBOX;
		}
	}

	public boolean isChecked() {
		return checked;
	}


	public boolean setChecked(boolean checked) {
		boolean oldValue = this.checked;
		this.checked = checked;
		boolean changed = firePropertyChange(CHECKED, oldValue, checked);
		if (changed) {
			this.setValue((checked) ? "on" : "off");
		}
		return changed;
	}


	@Override
	public boolean setValue(String value) {
		String oldValue = this.value;
		this.value = value;
		boolean changed = firePropertyChange(VALUE, oldValue, value);
		if (changed) {
			this.setChecked(ON.equals(value));
		}
		return changed;
	}


	@Override
	public boolean setValue(String key, Object value) {
		if (CHECKED.equals(key)) {
			return this.setChecked(Boolean.valueOf("" + value));
		}
		return super.setValue(key, value);
	}


	@Override
	public Object getValue(String key) {
		if (CHECKED.equals(key)) {
			return (checked) ? CHECKED : null;
		}
		return super.getValue(key);
	}
}
