package de.uniks.networkparser.gui.controls;

public class ChoiceField extends Input<Boolean> {
	/*
	 * Constants
	 */
	protected static final String CHECKBOX = "checkbox";
	protected static final String RADIO = "radio";

	public ChoiceField(boolean multi) {
		super();
		if(multi) {
			this.type = RADIO;
		}else {
			this.type = CHECKBOX;
		}
	}
}
