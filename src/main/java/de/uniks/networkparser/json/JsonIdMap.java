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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.event.ObjectMapEntry;
import de.uniks.networkparser.event.util.DateCreator;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.IdMapDecoder;
import de.uniks.networkparser.interfaces.SendableEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.util.JsonArrayCreator;
import de.uniks.networkparser.json.util.JsonObjectCreator;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.logic.SimpleMapEvent;
import de.uniks.networkparser.sort.EntityComparator;
/**
 * The Class JsonIdMap.
 */

public class JsonIdMap extends IdMap implements IdMapDecoder{
	/** The Constant CLASS. */
	public static final String CLASS = "class";

	/** The Constant VALUE. */
	public static final String VALUE = "value";

	/** The Constant JSON_PROPS. */
	public static final String JSON_PROPS = "prop";

	/** The Constant MAINITEM. */
	public static final String MAINITEM = "main";

	protected Grammar grammar = new JsonGrammar();

	/** If this is true the IdMap save the Typ of primary datatypes. */
	protected boolean typSave;

	/** The update listener. */
	protected UpdateListenerJson updateListenerJson;

	/** The updatelistener for Notification changes. */
	protected Object listener;

	/**
	 * Instantiates a new json id map.
	 */
	public JsonIdMap() {
		super();
		this.with(new DateCreator());
		this.with(new JsonObjectCreator());
		this.with(new JsonArrayCreator());
		this.with(new ObjectMapEntry());
	}

	/**
	 * To json object.
	 *
	 * @param object
	 *			the object
	 * @return the json object
	 */
	public JsonObject toJsonObject(Object object) {
		return toJsonObject(object, null);
	}

	/**
	 * To Jsonobject.
	 *
	 * @param entity
	 *			the entity
	 * @param filter
	 *			the filter
	 * @return the Jsonobject
	 */
	public JsonObject toJsonObject(Object entity, Filter filter) {
		if (entity == null) {
			return null;
		}

		filter = this.filter.newInstance(filter);
		return toJsonObject(entity, filter, entity.getClass().getName(), 0);
	}

	/**
	 * To Jsonobject.
	 *
	 * @param entity
	 *			the entity to convert
	 * @param filter
	 *			the filter
	 * @param className
	 *			the className of the entity
	 * @param deep
	 *			the deep of model-level
	 * @return the Jsonobject
	 */
	protected JsonObject toJsonObject(Object entity, Filter filter,
			String className, int deep) {
		String id = null;
		SendableEntityCreator creator = grammar.getCreator(Grammar.WRITE, entity, this, this.searchForSuperCreator, className);
		if (creator == null) {
			return null;
		}
		if (creator instanceof SendableEntityCreatorNoIndex) {
		} else if (!filter.isId(entity, className)) {
			with(filter, entity);
		} else {
			id = getId(entity);
			with(filter, id);
		}

		JsonObject jsonProp = new JsonObject().withAllowEmptyValue(filter.isFullSeriation());
		String[] properties = filter.getProperties(creator);
		if (properties != null) {
			for (String property : properties) {
				if (jsonProp.has(property)) {
					if (logger.error(this, "toJsonObject",
							NetworkParserLog.ERROR_TYP_DUPPLICATE, entity,
							filter, className, deep)) {
						throw new RuntimeException("Property duplicate:"
								+ property + "(" + className + ")");
					}
				}
				Object subValue = parseProperty(creator, entity, filter,
						className, property, null, deep + 1);
				if (subValue != null || filter.isFullSeriation()) {
					jsonProp.put(property, subValue);
				}
			}
		}
		return (JsonObject) grammar.setProperties(this, creator, className, id, jsonProp,
				filter);
	}

	@Override
	public String getId(Object obj) {
		String key = grammar.getId(obj, getCounter());
		if (key != null) {
			put(key, obj);
			return key;
		}
		return super.getId(obj);
	}

	public UpdateListenerJson getUpdateExecuter() {
		if (this.updateListenerJson == null) {
			this.updateListenerJson = new UpdateListenerJson(this);
		}
		return this.updateListenerJson;
	}

	public IdMap with(UpdateListenerJson updateListener) {
		this.updateListenerJson = updateListener;
		return this;
	}

	/**
	 * Garbage collection.
	 *
	 * @param root
	 *			the root
	 */
	public void garbageCollection(Object root) {
		if (this.updateListenerJson == null) {
			this.updateListenerJson = new UpdateListenerJson(this);
		}
		this.updateListenerJson.garbageCollection(root);
	}

	/**
	 * @param object
	 *			for add Listener to object
	 * @return success of adding
	 */
	@Override
	protected boolean addListener(Object object) {
		if (object instanceof SendableEntity) {
			return ((SendableEntity) object)
					.addPropertyChangeListener(getUpdateExecuter());
		}
		return false;
	}

	protected Object parseProperty(SendableEntityCreator prototyp,
			Object entity, Filter filter, String className, String property,
			JsonArray jsonArray, int deep) {
		Object referenceObject = grammar.getNewEntity(prototyp, className, true);

		Object value = prototyp.getValue(entity, property);
		if (value != null) {
			boolean encoding = filter.isFullSeriation();
			if(referenceObject instanceof Class<?>) {
				encoding = true;
			}
			if (!encoding) {
				Object refValue = prototyp.getValue(referenceObject, property);
				encoding = !value.equals(refValue);
			}

			if (encoding) {
				SendableEntityCreator referenceCreator = getCreatorClass(value);
				if (value instanceof Collection<?> && referenceCreator == null) {
					// Simple List or Assocs
					JsonArray subValues = new JsonArray();
					// jsonArray.getNewArray();
					for (Object containee : ((Collection<?>) value)) {
						Object item = parseItem(entity, filter, containee,
								property, jsonArray, null, deep);
						if (item != null) {
							subValues.with(item);
						}
					}
					if (subValues.size() > 0) {
						return subValues;
					}
				} else if (value instanceof Map<?, ?>
						&& referenceCreator == null) {
					// Maps
					JsonArray subValues = new JsonArray();
					Map<?, ?> map = (Map<?, ?>) value;
					String packageName = ObjectMapEntry.class.getName();
					for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
						Entry<?, ?> mapEntry = (Entry<?, ?>) i.next();
						Object item = parseItem(entity, filter, mapEntry,
								property, jsonArray, packageName, deep);
						if (item != null) {
							subValues.with(item);
						}
					}
					if (subValues.size() > 0) {
						return subValues;
					}
				} else {
					return parseItem(entity, filter, value, property,
							jsonArray, null, deep);
				}
			}
		}
		return null;
	}

	protected Object parseItem(Object item, Filter filter, Object entity,
			String property, JsonArray jsonArray, String className, int deep) {
		if (className == null) {
			className = entity.getClass().getName();
		}
		SendableEntityCreator valueCreater = grammar.getCreator(Grammar.WRITE, entity, this, this.searchForSuperCreator, className);
		if (item == null ) {
			return null;
		}else if(!isPropertyRegard(filter, item, property, entity, deep)) {
			return null;
		}
		boolean isId = filter.isId(entity, className);
		if (valueCreater != null) {
			if (isConvertable(filter, entity, property, item, deep)) {
				String subId = this.getKey(entity);
				if (valueCreater instanceof SendableEntityCreatorNoIndex
						|| (isId && !hasObjects(filter, subId))
						|| (!isId && !hasObjects(filter, entity))) {
					if (jsonArray == null) {
						JsonObject result = toJsonObject(entity, filter,
								className, deep + 1);
						return result;
					}
					this.toJsonArray(entity, jsonArray, filter, deep + 1);
				}
			}
			return new JsonObject().withValue(ID, getId(entity), CLASS, entity.getClass().getName());
		}
		if (typSave) {
			JsonObject returnValue = new JsonObject().withValue(CLASS, className);
			returnValue.put(VALUE, entity);
			return returnValue;
		}
		return entity;
	}

	/**
	 * Encode.
	 *
	 * @param entity
	 *			the entity
	 * @return the byte entity message
	 */
	@Override
	public JsonObject encode(Object entity) {
		return toJsonObject(entity);
	}

	/**
	 * Encode.
	 *
	 * @param entity
	 *			the entity
	 * @return the byte entity message
	 */
	@Override
	public JsonObject encode(Object entity, Filter filter) {
		return toJsonObject(entity, filter);
	}

	/**
	 * Read Json Automatic create JsonArray or JsonObject
	 *
	 * @return the object
	 */
	@Override
	public Object decode(String value) {
		if (value.startsWith("[")) {
			return decode(new JsonArray().with(value));
		}
		return decode(new JsonObject().withValue(value));
	}

	/**
	 * Read Json Automatic create JsonArray or JsonObject
	 *
	 * @return the object
	 */
	@Override
	public Object decode(BaseItem value) {
		if (value instanceof JsonArray) {
			return decode((JsonArray) value);
		}
		return decode((JsonObject) value);
	}

	/**
	 * Read json.
	 *
	 * @param jsonArray
	 *			the json array
	 * @return the object
	 */
	public Object decode(JsonArray jsonArray) {
		Object result = null;
		int len = jsonArray.size() - 1;
		// Add all Objects
		for (int i = 0; i <= len; i++) {
			JsonObject kidObject = jsonArray.getJSONObject(i);
			Object tmp = decoding(kidObject, filter);
			if (kidObject.has(MAINITEM)) {
				result = tmp;
			} else if (i == 0) {
				result = tmp;
			}
		}
		return result;
	}

	/**
	 * Read json.
	 *
	 * @param jsonObject
	 *			the json object
	 * @return the object
	 */
	public Object decode(JsonObject jsonObject) {
		return decoding(jsonObject, null);
	}

	/**
	 * Read json.
	 *
	 * @param target
	 *			the target
	 * @param jsonObject
	 *			the json object
	 * @return the object
	 */
	public Object decode(Object target, JsonObject jsonObject) {
		return decode(target, jsonObject, null);
	}

	/**
	 * Read json.
	 *
	 * @param target
	 *			the target
	 * @param jsonObject
	 *			the json object
	 * @param filter
	 *			the filter for decoding
	 * @return the object
	 */
	public Object decode(Object target, JsonObject jsonObject, Filter filter) {
		filter = this.filter.newInstance(filter);
		Object mainItem = decoding(target, jsonObject, filter);
		return mainItem;
	}

	/**
	 * Read json.
	 *
	 * @param jsonObject
	 *			the json object
	 * @param filter
	 *			the filter for decoding
	 * @return the object
	 */
	private Object decoding(JsonObject jsonObject, Filter filter) {
		if (jsonObject == null ){
			return null;
		}
		if (this.updateListenerJson == null) {
			this.updateListenerJson = new UpdateListenerJson(this);
		}
		Object result = this.updateListenerJson.execute(jsonObject, filter);
		if(result != null) {
			return result;
		}
		SendableEntityCreator typeInfo = grammar.getCreator(Grammar.READ, jsonObject, this, this.searchForSuperCreator, null);

		if (typeInfo != null) {
			if (grammar.hasValue(jsonObject, ID)) {
				String jsonId = grammar.getValue(jsonObject, ID);
				if (jsonId != null) {
					result = getObject(jsonId);
				}
			}
			if (result == null) {
				result = grammar.getNewEntity(typeInfo, grammar.getValue(jsonObject, CLASS), false);
				readMessages(NEW, new SimpleMapEvent(this, jsonObject, result));
			} else {
				readMessages(UPDATE, new SimpleMapEvent(this,jsonObject, result));
			}
			filter = this.filter.newInstance(filter);
			if (typeInfo instanceof SendableEntityCreatorWrapper) {
				String[] properties = typeInfo.getProperties();
				if (properties != null) {
					JsonObjectCreator jsonCreator = new JsonObjectCreator();
					JsonObject valueMap = new JsonObject();
					for (String property : properties) {
						Object value = jsonObject.get(property);
						parseValue(valueMap, property, value, jsonCreator, filter);
					}
					result = ((SendableEntityCreatorWrapper)typeInfo).newInstance(valueMap);
				}
			} else if (typeInfo instanceof SendableEntityCreatorNoIndex) {
				String[] properties = typeInfo.getProperties();
				if (properties != null) {
					for (String property : properties) {
						Object obj = jsonObject.get(property);
						parseValue(result, property, obj, typeInfo, filter);
					}
				}
			} else {
				decode(result, jsonObject, filter);
			}
		} else if (jsonObject.get(VALUE) != null) {
			return jsonObject.get(VALUE);
		} else if (jsonObject.get(ID) != null) {
			result = getObject((String) jsonObject.get(ID));
		}
		return result;
	}

	/**
	 * Read json.
	 *
	 * @param target
	 *			the target
	 * @param jsonObject
	 *			the json object
	 * @param filter
	 *			the filter for decoding
	 * @return the object
	 */
	protected Object decoding(Object target, JsonObject jsonObject,
			Filter filter) {
		// JSONArray jsonArray;
		boolean isId = filter.isId(target, target.getClass().getName());
		if (isId) {
			String jsonId = grammar.getValue(jsonObject, ID);
			if (jsonId == null) {
				return target;
			}
			put(jsonId, target);
			getCounter().readId(jsonId);
		}
		JsonObject jsonProp = (JsonObject) grammar.getProperties(jsonObject, this,
				filter, isId, Grammar.READ);
		if (jsonProp != null) {
			SendableEntityCreator prototyp = grammar.getCreator(Grammar.WRITE, target,
					this, this.searchForSuperCreator, target.getClass().getName());
			String[] properties = prototyp.getProperties();
			if (properties != null) {
				for (String property : properties) {
					Object obj = jsonProp.get(property);
					parseValue(target, property, obj, prototyp, filter);
				}
			}
		}
		return target;
	}

	/**
	 * Parses the value.
	 *
	 * @param target
	 *			the target
	 * @param property
	 *			the property
	 * @param value
	 *			the value
	 * @param creator
	 *			the creator
	 * @param filter
	 *			the filter
	 */
	protected void parseValue(Object target, String property, Object value,
			SendableEntityCreator creator, Filter filter) {
		if (value != null) {
			if (value instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray) value;
				for (int i = 0; i < jsonArray.size(); i++) {
					Object kid = jsonArray.get(i);
					if (kid instanceof JsonObject) {
						// got a new kid, create it
						creator.setValue(target, property,
								decoding((JsonObject) kid, filter), NEW);
					} else {
						creator.setValue(target, property, kid, NEW);
					}
				}
			} else {
				if (value instanceof JsonObject) {
					// // got a new kid, create it
					JsonObject child = (JsonObject) value;
					// CHECK LIST AND MAPS
					String className = target.getClass().getName();
					Object ref_Obj = grammar.getNewEntity(creator, null, true);
					if(ref_Obj instanceof Class<?>) {
						ref_Obj = grammar.getNewEntity(creator, className, false);
					}
					Object refValue = creator.getValue(ref_Obj, property);
					if (refValue instanceof Map<?, ?>) {
						JsonObject json = (JsonObject) value;
						Iterator<String> i = json.keySet().iterator();
						while (i.hasNext()) {
							String key = i.next();
							Object entryValue = json.get(key);
							if (entryValue instanceof JsonObject) {
								creator.setValue(target, property, new ObjectMapEntry().with(key, decode((JsonObject) entryValue)), NEW);
							} else if (entryValue instanceof JsonArray) {
								creator.setValue(target, property, new ObjectMapEntry().with(key, decode((JsonArray) entryValue)), NEW);
							} else {
								creator.setValue(target, property, new ObjectMapEntry().with(key, entryValue), NEW);
							}
						}
					} else {
						creator.setValue(target, property, decoding(child, filter), filter.getStrategy());
					}
				} else {
					creator.setValue(target, property, value, filter.getStrategy());
				}
			}
		}
	}

	/**
	 * To json array.
	 *
	 * @param object
	 *			the object
	 * @return the json array
	 */
	public JsonArray toJsonArray(Object object) {
		return toJsonArray(object, null);
	}

	/**
	 * Convert to JsonArray in the resource
	 *
	 * @param object
	 *			the object
	 * @param filter
	 *			the filter
	 * @return the json array
	 */
	public JsonArray toJsonArray(Object object, Filter filter) {
		JsonArray jsonArray = new JsonArray();
		filter = this.filter.newInstance(filter);
		if (object instanceof Collection<?>) {
			Collection<?> list = (Collection<?>) object;
			for (Iterator<?> i = list.iterator(); i.hasNext();) {
				Object item = i.next();
				toJsonArray(item, jsonArray, filter, 0);
			}
			return jsonArray;
		}
		if (object.getClass().isArray()) {
			for (Object item : ((Object[]) object)) {
				toJsonArray(item, jsonArray, filter, 0);
			}
			return jsonArray;
		}
		toJsonArray(object, jsonArray, filter);
		return jsonArray;
	}

	/**
	 * To json sorted array.
	 *
	 * @param object
	 *			the object
	 * @param jsonArray
	 *			the list
	 * @param filter
	 *			the Filter for split serialisation
	 * @return the JsonArray
	 */
	public JsonArray toJsonArray(Object object, JsonArray jsonArray,
			Filter filter) {
		if (jsonArray.isComparator()
				&& jsonArray.comparator() instanceof EntityComparator) {
			((EntityComparator<?>) jsonArray.comparator()).withMap(this);
		}
		filter = this.filter.newInstance(filter);
		return toJsonArray(object, jsonArray, filter, 0);
	}

	protected JsonArray toJsonArray(Object entity, JsonArray jsonArray,
			Filter filter, int deep) {
		String className = entity.getClass().getName();
		String id = getId(entity);

		JsonObject jsonObject = (JsonObject) jsonArray.getNewList(true);
		boolean sortedArray = jsonArray.isComparator();
		boolean isId = filter.isId(entity, className);
		if (isId) {
			if (!hasObjects(filter, id)) {
				jsonObject.put(ID, id);
				jsonObject.put(CLASS, className);
				if (!sortedArray) {
					jsonArray.add(jsonObject);
				}
			}
		} else if (!hasObjects(filter, entity)) {
			jsonObject.put(CLASS, className);
			if (!sortedArray) {
				jsonArray.add(jsonObject);
			}
		}

		SendableEntityCreator creator = getCreator(className, true);
		if (creator == null) {
			if (logger.error(this, "toJsonArray",
					NetworkParserLog.ERROR_TYP_NOCREATOR, entity, jsonArray,
					filter, deep)) {
				throw new RuntimeException("No Creator exist for " + className);
			}
			return null;
		}
		String[] properties = creator.getProperties();
		if (isId) {
			with(filter, id);
		} else {
			with(filter, entity);
		}

		if (properties != null) {
			JsonObject jsonProps = new JsonObject();
			jsonProps.withAllowEmptyValue(filter.isFullSeriation());
			for (String property : properties) {
				if (jsonProps.has(property)) {
					if (logger.error(this, "toJsonArray",
							NetworkParserLog.ERROR_TYP_DUPPLICATE, entity,
							jsonArray, filter, deep)) {
						throw new RuntimeException("Property duplicate:"
								+ property + "(" + className + ")");
					}
				}
				Object subValue = parseProperty(creator, entity, filter,
						className, property, jsonArray, deep + 1);
				if (subValue != null || filter.isFullSeriation()) {
					jsonProps.put(property, subValue);
				}
			}
			if (jsonProps.size() > 0) {
				jsonObject.put(JSON_PROPS, jsonProps);
			}
		}
		if (sortedArray && jsonObject.has(CLASS)) {
			jsonArray.add(jsonObject);
		}

		return jsonArray;
	}

	/**
	 * Send update msg from PropertyChange MapUpdater
	 *
	 * @param evt
	 *			the Change
	 * @param jsonObject
	 *			the json object
	 * @return true, if successful
	 */
	boolean sendUpdateMsg(PropertyChangeEvent evt) {
		if(evt == null) {
			return true;
		}
		return notify(SENDUPDATE, evt);
	}

	boolean readMessages(String typ, PropertyChangeEvent event) {
		return notify(typ, event);
	}

	boolean notify(String typ, PropertyChangeEvent event) {
    	if (this.listener != null ) {
    		if(this.listener instanceof PropertyChangeListener) {
    			((PropertyChangeListener)this.listener).propertyChange(event);
    		}
    		if (this.listener != null && this.listener instanceof UpdateListener) {
    			return ((UpdateListener)this.listener).update(typ, event);
    		}
    	}
		return true;
	}

	/**
	 * To json array by ids.
	 *
	 * @param ids
	 *			the suspend id list
	 * @return success all Items to baseItem
	 */
	public JsonArray getJsonByIds(List<String> ids) {
		if(ids == null) {
			return null;
		}
		JsonArray items=new JsonArray();
		for (String childId : ids) {
			JsonObject jsonObject = toJsonObject(getObject(childId),
					new Filter().withConvertable(new Deep().withDeep(0)));
			if(jsonObject != null) {
				items.add(jsonObject);
			}
		}
		return items;
	}

	@Override
	public String toString() {
		return this.getClass().getName() + " (" + this.size() + ")";
	}

	/**
	 * @param value
	 *			Gammar value
	 * @return Itself
	 */
	public JsonIdMap with(Grammar value) {
		this.grammar = value;
		return this;
	}

	/**
	 * Sets the typ save.
	 *
	 * @param typSave
	 *			the new typ save
	 * @return Itself
	 */
	public JsonIdMap withTypSave(boolean typSave) {
		this.typSave = typSave;
		return this;
	}

	public JsonIdMap with(PropertyChangeListener listener) {
		this.listener = listener;
		return this;
	}

	/**
	 * Set the new Listener
	 *
	 * @param listener the new Listener
	 * @return This Component
	 *
	 * @see JsonIdMap#with(PropertyChangeListener)
	 * @see de.uniks.networkparser.ChainUpdateListener
	 */
	public JsonIdMap with(UpdateListener listener) {
		this.listener = listener;
		return this;
	}

	public JsonIdMap with(SendableEntityCreator... createrClass) {
		super.with(createrClass);
		return this;
	}

	//Redirect
	@Override
	public JsonIdMap withSessionId(String value) {
		super.withSessionId(value);
		return this;
	}
}
