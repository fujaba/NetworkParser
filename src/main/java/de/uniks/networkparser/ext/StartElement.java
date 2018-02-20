package de.uniks.networkparser.ext;

public class StartElement {
	private String key;
	private String label;
	private String description;
	private Object defaultValue;
	private Object value;

	/** @return the label */
	public String getLabel() {
		return label;
	}
	/**
	 * @param label the label to set
	 * @return  ThisComponent
	 */
	public StartElement withLabel(String label) {
		this.label = label;
		return this;
	}
	/** @return the description */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 * @return  ThisComponent
	 */
	public StartElement withDescription(String description) {
		this.description = description;
		return this;
	}
	/** @return the values */
	public Object getDefaultValues() {
		return defaultValue;
	}

	/**
	 *  @param values the values to set
	 * @return ThisComponent
	 * */
	public StartElement withDefaultValues(Object values) {
		this.defaultValue = values;
		return this;
	}
	/** @return the key */
	public String getKey() {
		return key;
	}
	/**
	 * @param key the key to set
	 * @return ThisComponent
	 */
	public StartElement withKey(String key) {
		this.key = key;
		return this;
	}
	/** @return the value  */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value the value to set
	 * @return ThisComponent
	 */
	public StartElement withValue(Object value) {
		this.value = value;
		return this;
	}
}
