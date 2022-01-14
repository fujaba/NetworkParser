package de.uniks.networkparser.interfaces;

public interface Entity extends BaseItem {
	public String getString(String key);
	
	public Object getValue(Object key);

	public String getKeyByIndex(int pos);

	public Object getValueByIndex(int index);

	public Entity without(String key);

	/**
	 * check if Entity has the Key
	 *
	 * @param key The Key for search
	 * @return success if the item has the Property
	 */
	public boolean has(String key);

	/**
	 * Add Key-Value item to Entity
	 * 
	 * @param key   The key
	 * @param value The new Value
	 * @return The value
	 */
	public Object put(String key, Object value);

	/**
	 * Make a prettyprinted Text of this Entity.
	 * <p>
	 * Warning: This method assumes that the data structure is acyclical.
	 *
	 * @param indentFactor The number of spaces to add to each level of indentation.
	 * @return a printable, displayable, portable, transmittable representation of
	 *         the object, beginning with <code>{</code>&nbsp;<small>(left
	 *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
	 *         brace)</small>.
	 */
	public String toString(int indentFactor);

	/**
	 * Activate Allow Empty Value
	 * 
	 * @param allow is Empty Value (NULL) Allow
	 * @return The BaseItem
	 */
	public BaseItem withAllowEmptyValue(boolean allow);

	public BaseItem getElementBy(String key, String value);

	public BaseItem withValue(BufferItem values);

	public BaseItem withType(String type);
}
