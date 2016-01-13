package de.uniks.networkparser.json;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
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
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public class Grammar {
	/**
	 * @param jsonObject
	 *			The Object for read
	 * @param map
	 *			The IdMap
	 * @param filter
	 *			The filter
	 * @param isId
	 *			The isReadId
	 *
	 * @return the props of theJsonObject
	 */
	public JsonObject getReadProperties(JsonObject jsonObject,
			IdMap map, Filter filter, boolean isId) {
		if (isId) {
			if (jsonObject.has(JsonIdMap.JSON_PROPS)) {
				return jsonObject.getJsonObject(JsonIdMap.JSON_PROPS);
			}
		} else {
			JsonObject props = new JsonObject();
			for (int i = 0; i < jsonObject.size(); i++) {
				if (!JsonIdMap.CLASS.equalsIgnoreCase(jsonObject.getKeyByIndex(i))) {
					props.put(jsonObject.getKeyByIndex(i), jsonObject.getValueByIndex(i));
				}

			}
			return props;
		}
		return null;
	}

	/**
	 * @param jsonObject
	 *			The Object for read
	 * @param map
	 *			The IdMap
	 * @param searchForSuperCreator
	 *			search for Creator in superclasses
	 * @return the Creator for this JsonObject
	 */
	public SendableEntityCreator getReadCreator(JsonObject jsonObject,
			IdMap map, boolean searchForSuperCreator) {
		Object className = jsonObject.get(JsonIdMap.CLASS);
		if(className == null) {
			return null;
		}
		SendableEntityCreator creator = map.getCreator((String) className, true);
		if(creator != null) {
			return creator;
		}
		Class<?> clazzName = getClassForName((String) className);
		return getSuperCreator(map, searchForSuperCreator, clazzName);
	}
	
	protected Class<?> getClassForName(String name) {
		return null;
	}

	/**
	 * @param modelItem
	 *			Item for write
	 * @param className
	 *			String className
	 * @param map
	 *			The IdMap
	 * @param searchForSuperCreator
	 *			search for Creator in superclasses
	 * @return the Creator for this JsonObject
	 */
	public SendableEntityCreator getWriteCreator(Object modelItem,
			String className, IdMap map, boolean searchForSuperCreator) {
		SendableEntityCreator creator = map.getCreator(className, true);
		if(creator != null) {
			return creator;
		}
		return getSuperCreator(map, searchForSuperCreator, modelItem);
	}

	public JsonObject getWriteObject(IdMap map,
			SendableEntityCreator prototyp, String className, String id,
			JsonObject jsonProp, Filter filter) {
		JsonObject json = new JsonObject();
		json.put(JsonIdMap.CLASS, className);
		if (prototyp instanceof SendableEntityCreatorNoIndex
				|| !filter.isId(jsonProp, className)) {
			for (int i = 0; i < jsonProp.size(); i++) {
				json.put(jsonProp.getKeyByIndex(i), jsonProp.getValueByIndex(i));
			}
			return json;
		}
		json.put(IdMap.ID, id);
		if (jsonProp.size() > 0) {
			json.put(JsonIdMap.JSON_PROPS, jsonProp);
		}
		return json;
	}

	/**
	 * Get a new Instance of Element from the Creator
	 * @param creator The EntityCreator
	 * @return The new Instance
	 */
	public Object getNewPrototyp(SendableEntityCreator creator) {
		return creator.getSendableInstance(true);
	}
	
	/**
	 * Get a new Instance of Element from the Creator
	 * @param creator The EntityCreator
<<<<<<< HEAD
=======
	 * @param className Alternative Name of Class
>>>>>>> adapt_SDMLib_datamodel
	 * @return The new Instance
	 */
	public Object getNewEntity(SendableEntityCreator creator, String className) {
		return creator.getSendableInstance(false);
	}
	
	public boolean hasReadValue(JsonObject json, String property) {
		return json.has(property);
	}

	public String getReadValue(JsonObject json, String property) {
		return json.getString(property);
	}

	public String getWriteId(Object obj, IdMapCounter counter) {
		return null;
	}
	public SendableEntityCreator getSuperCreator(IdMap map, boolean searchForSuperCreator, Object modelItem) {
		return null;
	}
}
