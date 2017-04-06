package de.uniks.networkparser.gui.controls;

public class NumberField extends Input<Integer> {
	/*
	 * Constants
	 */
	protected static final String NUMBER = "number";

	public NumberField() {
		super();
		this.type = NUMBER;
		// set the default value to 0
		this.setValue("defaultValue", 0);
	}
}
