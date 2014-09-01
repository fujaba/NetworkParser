package de.uniks.networkparser.logic;

/*
 NetworkParser
 Copyright (c) 2011 - 2013, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
 */

import java.util.Collection;

import de.uniks.networkparser.IdMapEncoder;

/**
 * Logic Clazz for MapValues.
 *
 * @author Stefan Lindel
 */
public class ValuesMap extends ValuesSimple {
	/** Variable for IdMap. */
	public IdMapEncoder map;
	/** Variable for Enitity. */
	public Object entity;
	/** Variable for Property. */
	public String property;
	/** Variable for Value. */
	public Object value;
	/** Variable for Assoc Typ toOne or ToMany. */
	public boolean isMany;
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
	 * @param isMany
	 *            Is Assoc is to 1 or toMany
	 * @param deep
	 *            depp value from root
	 * @return ValuesMap Instance
	 */
	public static ValuesMap with(IdMapEncoder map, Object entity,
			String property, Object value, boolean isMany, int deep) {
		ValuesMap mapCondition = new ValuesMap();
		mapCondition.map = map;
		mapCondition.entity = entity;
		mapCondition.property = property;
		mapCondition.value = value;
		mapCondition.isMany = isMany;
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
	public static ValuesMap with(IdMapEncoder map, Object entity,
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
		mapCondition.isMany = (value instanceof Collection<?>);
		return mapCondition;
	}
}
