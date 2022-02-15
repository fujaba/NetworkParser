package de.uniks.networkparser.interfaces;

// TODO: Auto-generated Javadoc
/**
 * The Interface Entity.
 *
 * @author Stefan
 */
public interface Entity extends BaseItem {
	
	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String getString(String key);
	
	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(Object key);

	/**
	 * Gets the key by index.
	 *
	 * @param pos the pos
	 * @return the key by index
	 */
	public String getKeyByIndex(int pos);

	/**
	 * Gets the value by index.
	 *
	 * @param index the index
	 * @return the value by index
	 */
	public Object getValueByIndex(int index);

	/**
	 * Without.
	 *
	 * @param key the key
	 * @return the entity
	 */
	public Entity without(String key);

	/**
	 * check if Entity has the Key.
	 *
	 * @param key The Key for search
	 * @return success if the item has the Property
	 */
	public boolean has(String key);

	/**
	 * Add Key-Value item to Entity.
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
	 * Activate Allow Empty Value.
	 *
	 * @param allow is Empty Value (NULL) Allow
	 * @return The BaseItem
	 */
	public BaseItem withAllowEmptyValue(boolean allow);

	/**
	 * Gets the element by.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the element by
	 */
	public BaseItem getElementBy(String key, String value);

	/**
	 * With value.
	 *
	 * @param values the values
	 * @return the base item
	 */
	public BaseItem withValue(BufferItem values);

	/**
	 * With type.
	 *
	 * @param type the type
	 * @return the base item
	 */
	public BaseItem withType(String type);
}
