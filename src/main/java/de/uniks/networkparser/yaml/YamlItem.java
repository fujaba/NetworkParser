package de.uniks.networkparser.yaml;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;

public class YamlItem implements BaseItem{
	private Object key;
	private Object value;
	private String comment;

	public String getComment() {
		return comment;
	}

	public YamlItem withComment(String comment) {
		this.comment = comment;
		return this;
	}

	public Object getValue() {
		return value;
	}

	public YamlItem withValue(Object value) {
		this.value = value;
		return this;
	}

	public Object getKey() {
		return key;
	}

	public YamlItem withKey(Object key) {
		this.key = key;
		return this;
	}

	@Override
	public String toString(Converter converter) {
		return null;
	}

	@Override
	public boolean add(Object... values) {
		return false;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new YamlEntity();
	}

	@Override
	public int size() {
		return 1;
	}
}
