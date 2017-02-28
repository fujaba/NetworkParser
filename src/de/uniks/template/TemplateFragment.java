package de.uniks.template;

public class TemplateFragment implements Comparable<TemplateFragment> {

	private int key = -1;
	
	private String value = "";
	
	@Override
	public int compareTo(TemplateFragment other) {
		if (other.getKey() == key && other.getValue().equals(value)) {
			return 0;
		}
		if (other.getKey() > key) {
			return -1;
		}
		return 1;
	}

	public int getKey() {
		return key;
	}

	public void setKey(int key) {
		this.key = key;
	}
	
	public TemplateFragment withKey(int key) {
		setKey(key);
		return this;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TemplateFragment withValue(String value) {
		setValue(value);
		return this;
	}
	
	@Override
	public String toString() {
		return "" + key;
	}
	
}
