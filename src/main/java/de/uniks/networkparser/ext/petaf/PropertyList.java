package de.uniks.networkparser.ext.petaf;

import de.uniks.networkparser.list.AbstractList;

/**
 * Property List.
 *
 * @author Stefan
 */
public class PropertyList extends AbstractList<String> {
	private String[] cache;

	/**
	 * Adds the.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean add(String value) {
		boolean result = super.add(value);
		this.cache = null;
		return result;
	}

	/**
	 * Adds the all.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	public boolean addAll(String... values) {
		if (values == null) {
			return true;
		}
		for (String value : values) {
			if (!super.add(value)) {
				this.cache = null;
				return false;
			}
		}
		this.cache = null;
		return true;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public PropertyList getNewList(boolean keyValue) {
		return new PropertyList();
	}

	/**
	 * Gets the list.
	 *
	 * @return the list
	 */
	public String[] getList() {
		if (this.cache == null) {
			this.cache = this.toArray(new String[this.size()]);
		}
		return this.cache;
	}

	/**
	 * Creates the.
	 *
	 * @param properties the properties
	 * @return the property list
	 */
	public static PropertyList create(String... properties) {
		PropertyList list = new PropertyList();
		if (properties != null) {
			for (String item : properties) {
				list.add(item);
			}
		}
		return list;
	}
}
