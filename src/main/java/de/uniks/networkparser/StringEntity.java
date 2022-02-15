package de.uniks.networkparser;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Converter;

/**
 * The Class StringEntity.
 *
 * @author Stefan
 */
public class StringEntity implements BaseItem {
	private String value;

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return toString(new EntityStringConverter());
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the string entity
	 */
	public StringEntity with(String value) {
		this.value = value;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
	@Override
	public String toString(Converter converter) {
		if (converter == null) {
			return null;
		}
		if (converter instanceof EntityStringConverter) {
			return value;
		}
		return converter.encode(this);
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		if (values == null) {
			return false;
		}
		if (values.length > 0) {
			this.value = (String) values[0];
			return true;
		}
		return false;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new StringEntity();
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	@Override
	public int size() {
		if (this.value != null) {
			return 1;
		}
		return 0;
	}
}
