package de.uniks.networkparser.gui.controls;

public class ChoiceField extends Input<String> {
	/*
	 * Constants
	 */
	protected static final String CHECKBOX = "checkbox";

	protected static final String RADIO = "radio";

	protected static final String CHECKED = "checked";

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


	public boolean setChecked(boolean checked) {
		boolean oldValue = this.checked;
		this.checked = checked;
		return firePropertyChange(CHECKED, oldValue, checked);
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
