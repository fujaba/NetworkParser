package de.uniks.networkparser;

import de.uniks.networkparser.logic.SimpleValues;

/**
 * Logic Clazz for MapValues.
 *
 * @author Stefan Lindel
 */
public class SimpleValuesMap extends SimpleValues {
	/** Variable for IdMap. */
	private IdMap map;
	/** Variable for Enitity. */
	private Object entity;
	/** Variable for Property. */
	private String property;
	/** Variable for Deep from Root. */
	private int deep;

	/**
	 * Set all Values of Entity
	 * @param entity
	 *			The Entity of Map
	 * @param property
	 *			The Attribute
	 * @param value
	 *			The current Value
	 * @param deep
	 *			depp value from root
	 * @return ValuesMap Instance
	 */
	SimpleValuesMap with(Object entity, String property, Object value, int deep) {
		this.entity = entity;
		this.property = property;
		this.value = value;
		this.deep = deep;
		return this;
	}
	
	/**
	 * Set all Values of Entity
	 * @param entity
	 *			The Entity of Map
	 * @param property
	 *			The Attribute
	 * @param value
	 *			The current Value
	 * @param deep
	 *			depp value from root
	 * @return ValuesMap Instance
	 */
	SimpleValuesMap with(Object entity, String property) {
		this.entity = entity;
		this.property = property;
		return this;
	}
	
	public IdMap getMap() {
		return map;
	}

	public SimpleValuesMap with(IdMap map) {
		this.map = map;
		return this;
	}

	public Object getEntity() {
		return entity;
	}
	
	public SimpleValuesMap withEntity(Object entity) {
		this.entity = entity;
		return this;
	}
	public String getProperty() {
		return property;
	}
	public SimpleValuesMap with(String property) {
		this.property = property;
		return this;
	}
	public int getDeep() {
		return deep;
	}
	public SimpleValuesMap with(int deep) {
		this.deep = deep;
		return this;
	}

	public Object getValue() {
		return value;
	}
	public SimpleValuesMap withValue(Object value) {
		super.withValue(value);
		return this;
	}

}
