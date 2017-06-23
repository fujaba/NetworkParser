package de.uniks.networkparser.gui.controls;

import de.uniks.networkparser.interfaces.ObjectCondition;

public class Button extends Input<String> {
	/* Constants */
	protected static final String BUTTON = "button";
	
	private String actionType;
	
	public static final String MINIMIZE = "minimize";
	
	public static final String CLOSE = "close";
	
	public static final String MAXIMIZE = "maximize";
	
	public Button() {
		super();
		/* Set variables of parent class */
		this.type = BUTTON;
	}
	
	public String getActionType() {
		return actionType;
	}
	public Button withActionType(String graphicType) {
		this.actionType = graphicType;
		return this;
	}
	public Button withActionType(String graphicType, ObjectCondition conditon) {
		this.actionType = graphicType;
		this.addClickListener(conditon);
		return this;
	}

	public Button withValue(String value) {
		super.setValue(value);
		return this;
	}
}
