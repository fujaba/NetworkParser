package de.uniks.networkparser.json;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.IdMapCounter;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public class Grammar {
	/**
	 * @param jsonObject
	 *            The Object for read
	 * @param map
	 *            The IdMap
	 * @param filter
	 *            The filter
	 * @param isId
	 *            The isReadId
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
	 *            The Object for read
	 * @param map
	 *            The IdMap
	 * @return the Creator for this JsonObject
	 */
	public SendableEntityCreator getReadCreator(JsonObject jsonObject,
			IdMap map) {
		Object className = jsonObject.get(JsonIdMap.CLASS);
		return map.getCreator((String) className, true);
	}

	/**
	 * @param modelItem
	 *            Item for write
	 * @param className
	 *            String className
	 * @param map
	 *            The IdMap
	 *
	 * @return the Creator for this JsonObject
	 */
	public SendableEntityCreator getWriteCreator(Object modelItem,
			String className, IdMap map) {
		SendableEntityCreator creator = map.getCreator(className, true);
		if(creator != null) {
			return creator;
		}
		return map.getSuperCreator(modelItem);
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

	public boolean hasReadValue(JsonObject json, String property) {
		return json.has(property);
	}

	public String getReadValue(JsonObject json, String property) {
		return json.getString(property);
	}

	public String getWriteId(Object obj, IdMapCounter counter) {
		return null;
	}
}
