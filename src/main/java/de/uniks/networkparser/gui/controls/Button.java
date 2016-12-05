package de.uniks.networkparser.gui.controls;

public class Button extends Input<String> {
	/* Constants */
	protected static final String BUTTON = "button";
	protected static final String ONCLICK = "onclick";
	
	/* variables */
	protected String onClick;

	public Button() {
		super();
		/* Set variables of parent class */
		this.className = BUTTON;
		this.addBaseElements(ONCLICK);
	}
	
	@Override
	public Object getValue(String key) {
		if (ONCLICK.equals(key)) {
			return this.onClick;
		} 
		return super.getValue(key);
	}
	
	@Override
	public boolean setValue(String key, Object value) {
		if (ONCLICK.equalsIgnoreCase(key)) {
			this.onClick = ""+ value;
			return true;
		}
		return super.setValue(key, value);
	}
	
	/**
	 * @return the onclick
	 */
	public String getOnClick() {
		return onClick;
	}

	/**
	 * @param value the onclick to set
	 * @return boolean if success 
	 */
	public boolean setOnClick(String value) {
		if(this.onClick != value ) {
			this.onClick = value;
			return true;
		}
		return false;
	}
}
