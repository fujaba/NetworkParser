package de.uniks.networkparser.logic;

import de.uniks.networkparser.IdMap;

/**
 * Logic Clazz for MapValues.
 *
 * @author Stefan Lindel
 */
public class ValuesMap extends ValuesSimple {
	/** Variable for IdMap. */
	public IdMap map;
	/** Variable for Enitity. */
	public Object entity;
	/** Variable for Property. */
	public String property;
	/** Variable for Value. */
	public Object value;
	/** Variable for Deep from Root. */
	public int deep;

	/**
	 * @param map
	 *            The Encoder
	 * @param entity
	 *            The Entity of Map
	 * @param property
	 *            The Attribute
	 * @param value
	 *            The current Value
	 * @param deep
	 *            depp value from root
	 * @return ValuesMap Instance
	 */
	public static ValuesMap with(IdMap map, Object entity,
			String property, Object value, int deep) {
		ValuesMap mapCondition = new ValuesMap();
		mapCondition.map = map;
		mapCondition.entity = entity;
		mapCondition.property = property;
		mapCondition.value = value;
		mapCondition.deep = deep;
		return mapCondition;
	}

	/**
	 * @param map
	 *            The Encoder
	 * @param entity
	 *            The Entity of Map
	 * @param property
	 *            The Attribute
	 * @return ValuesMap Instance
	 */
	public static ValuesMap with(IdMap map, Object entity,
			String property) {
		ValuesMap mapCondition = new ValuesMap();
		mapCondition.map = map;
		mapCondition.entity = entity;
		mapCondition.property = property;
		return mapCondition;
	}

	/**
	 * @param entity
	 *            The Entity of Map
	 * @param property
	 *            The Attribute
	 * @param value
	 *            The current Value
	 * @return ValuesMap Instance
	 */
	public static ValuesMap with(Object entity, String property, Object value) {
		ValuesMap mapCondition = new ValuesMap();
		mapCondition.entity = entity;
		mapCondition.property = property;
		mapCondition.value = value;
		return mapCondition;
	}
	
	/**
	 * Set all Values of Entity
	 * @param entity
	 *            The Entity of Map
	 * @param property
	 *            The Attribute
	 * @param value
	 *            The current Value
	 * @return ValuesMap Instance
	 */
	public ValuesMap withValues(Object entity, String property, Object value, int deep) {
		this.entity = entity;
		this.property = property;
		this.value = value;
		this.deep = deep;
		return this;
	}
	
	public static ValuesMap withMap(IdMap value) {
		ValuesMap mapCondition = new ValuesMap();
		mapCondition.map = value;
		return mapCondition;
	}
}
