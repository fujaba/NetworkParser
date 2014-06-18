package de.uniks.networkparser.json;

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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.AbstractKeyValueEntry;
import de.uniks.networkparser.AbstractList;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.ObjectMapEntry;
import de.uniks.networkparser.ReferenceObject;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.event.creator.DateCreator;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.MapUpdateListener;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.json.creator.JsonArrayCreator;
import de.uniks.networkparser.json.creator.JsonObjectCreator;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.sort.EntityComparator;
/**
 * The Class JsonIdMap.
 */

public class JsonIdMap extends IdMap {
	/** The Constant CLASS. */
	public static final String CLASS = "class";

	/** The Constant VALUE. */
	public static final String VALUE = "value";

	/** The Constant JSON_PROPS. */
	public static final String JSON_PROPS = "prop";

	/** The Constant MAINITEM. */
	public static final String MAINITEM = "main";

	protected Grammar grammar = new Grammar();

	/** The updatelistener. */
	private MapUpdateListener updatelistener;

	/** If this is true the IdMap save the Typ of primary datatypes. */
	protected boolean typSave;
	
	/**
	 * Instantiates a new json id map.
	 */
	public JsonIdMap() {
		super();
		this.withCreator(new DateCreator());
		this.withCreator(new JsonObjectCreator());
		this.withCreator(new JsonArrayCreator());
		this.withCreator(new MapEntry());
	}
	
	/**
	 * @return the Prototyp forModel
	 */
	@Override
	public JsonObject getPrototyp(){
		return new JsonObject();
	}

	/**
	 * To json object.
	 * 
	 * @param object
	 *            the object
	 * @return the json object
	 */
	public JsonObject toJsonObject(Object object) {
		return toJsonObject(object, filter.cloneObj());
	}

	/**
	 * To Jsonobject.
	 * 
	 * @param entity
	 *            the entity
	 * @param filter
	 *            the filter
	 * @return the Jsonobject
	 */
	public JsonObject toJsonObject(Object entity, Filter filter) {
		if(filter==null){
			filter = new Filter();
		}
		return toJsonObject(entity, filter.withStandard(this.filter), entity.getClass().getName(), 0);
	}

	/**
	 * To Jsonobject.
	 * 
	 * @param entity
	 *            the entity to convert
	 * @param filter
	 *            the filter
	 * @param className
	 *            the className of the entity
	 * @param deep
	 *            the deep of model-level
	 * @return the Jsonobject
	 */
	protected JsonObject toJsonObject(Object entity, Filter filter, String className, int deep) {
		String id = null;
		SendableEntityCreator prototyp = grammar.getWriteCreator(entity,
				className, this);
		if (prototyp == null) {
			return null;
		}
		if (prototyp instanceof SendableEntityCreatorNoIndex){
		}else if(!filter.isId(this, entity, className)) {
			filter.addToVisitedObjects(entity);
		}else{
			id = getId(entity);
			filter.addToVisitedObjects(id);
		}
		
		JsonObject jsonProp = getPrototyp();

		String[] properties = prototyp.getProperties();
		if (properties != null) {
			for (String property : properties) {
				if (jsonProp.has(property) ) {
					logger.error(this, "Property duplicate:" + property
							+ "(" + className + ")");
				}
				Object subValue = parseProperty(prototyp, entity, filter,
						className, property, null, deep+1);
				if (subValue != null) {
					jsonProp.put(property, subValue);
				}
			}
		}

		return grammar.getWriteObject(this, prototyp, className, id, jsonProp,
				filter);
	}
	
	@Override
	public String getId(Object obj) {
		String key = grammar.getWriteId(obj, getCounter());
		if(key!=null){
			put(key, obj);
			return key;
		}
		return super.getId(obj);
	}

	protected Object parseProperty(SendableEntityCreator prototyp,
			Object entity, Filter filter, String className,
			String property, JsonArray jsonArray, int deep) {
		Object referenceObject = prototyp.getSendableInstance(true);

		Object value = prototyp.getValue(entity, property);
		if (value != null) {
			boolean encoding = filter.isFullSeriation();
			if(!encoding){
				Object refValue = prototyp.getValue(referenceObject, property);
				encoding = !value.equals(refValue);
			}

			if (encoding) {
				SendableEntityCreator referenceCreator = getCreatorClass(value);
				if (value instanceof Collection<?> && referenceCreator == null) {
					// Simple List or Assocs
					AbstractList<Object> subValues = getPrototyp().getNewArray();
//					jsonArray.getNewArray();
					for (Object containee : ((Collection<?>) value)) {
						Object item = parseItem(entity, filter, containee,
								property, jsonArray, null, deep);
						if (item != null) {
							subValues.add(item);
						}
					}
					if (subValues.size() > 0) {
						return subValues;
					}
				} else if (value instanceof Map<?, ?>
						&& referenceCreator == null) {
					// Maps
					AbstractList<Object> subValues = getPrototyp().getNewArray();
					Map<?, ?> map = (Map<?, ?>) value;
 					String packageName = ObjectMapEntry.class.getName();
					for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
						Entry<?, ?> mapEntry = (Entry<?, ?>) i.next();
						Object item = parseItem(entity, filter, mapEntry,
								property, jsonArray, packageName, deep);
						if (item != null) {
							subValues.add(item);
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
		}else if(filter.isFullSeriation()){
			return "";
		}
		return null;
	}

	protected Object parseItem(Object item, Filter filter, Object entity, 
			String property, JsonArray jsonArray, String className, int deep) {
		if (item == null || !filter.isPropertyRegard(this, item, property, entity, true, deep)) {
			return null;
		}
		if (className == null) {
			className = entity.getClass().getName();
		}
		SendableEntityCreator valueCreater = getCreator(className, true);
		boolean isId = filter.isId(this, entity, className);
		if (valueCreater != null) {
			if (filter.isConvertable(this, entity, property, item, true, deep) ) {
				String subId = this.getKey(entity);
				if (valueCreater instanceof SendableEntityCreatorNoIndex
						|| (isId &&!filter.hasVisitedObjects(subId))
						|| (!isId && !filter.hasVisitedObjects(entity))){ 
					if (jsonArray == null) {
						JsonObject result = toJsonObject(entity, filter,
								className, deep+1);
						return result;
					}
					this.toJsonArray(entity, jsonArray, filter, deep+1);
				}
			}
			return getPrototyp().withValue(ID, getId(entity));
		}
		if (typSave) {
			JsonObject returnValue = getPrototyp().withValue(CLASS, className);
			returnValue.put(VALUE, entity);
			return returnValue;
		}
		return entity;
	}
	
	/**
	 * Encode.
	 * 
	 * @param entity
	 *            the entity
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
	 *            the entity
	 * @return the byte entity message
	 */
	@Override
	public JsonObject encode(Object entity, Filter filter) {
		return toJsonObject(entity, filter);
	}

	/**
	 * Read Json Automatic create JsonArray or JsonObject
	 * @return the object
	 */
	@Override
	public Object decode(String value){
		if(value.startsWith("[")){
			return decode(getPrototyp().getNewArray().withValue(value));
		}
		return decode(getPrototyp().withValue(value));
	}

	/**
	 * Read Json Automatic create JsonArray or JsonObject
	 * @return the object
	 */
	@Override
	public Object decode(BaseItem value) {
		if(value instanceof JsonArray){
			return decode((JsonArray) value);
		}
		return decode((JsonObject) value);
	}
	
	/**
	 * Read json.
	 * 
	 * @param jsonArray
	 *            the json array
	 * @return the object
	 */
	public Object decode(JsonArray jsonArray) {
		Object result = null;
		int len = jsonArray.size() - 1;
		// Add all Objects
		Filter filter = this.filter.cloneObj();
		for (int i = 0; i <= len; i++) {
			JsonObject kidObject = jsonArray.getJSONObject(i);
			Object tmp = decoding(kidObject, filter);
			if (kidObject.has(MAINITEM)) {
				result = tmp;
			} else if (i == 0) {
				result = tmp;
			}
		}
		for (ReferenceObject ref : filter.getRefs()) {
			ref.execute(this);
		}
		return result;
	}

	/**
	 * Read json.
	 * 
	 * @param jsonObject
	 *            the json object
	 * @return the object
	 */
	public Object decode(JsonObject jsonObject) {
		ArrayList<ReferenceObject> refs = new ArrayList<ReferenceObject>();
		if (jsonObject.has(UPDATE) || jsonObject.has(REMOVE)) {
			// Must be an update
			if (executeUpdateMsg(jsonObject)) {
				String id = jsonObject.getString(JsonIdMap.ID);
				return getObject(id);
			}
			return null;
		}
		Object mainItem = decoding(jsonObject, null);
		for (ReferenceObject ref : refs) {
			ref.execute(this);
		}
		return mainItem;
	}
	
	/**
	 * Read json.
	 * 
	 * @param target
	 *            the target
	 * @param jsonObject
	 *            the json object
	 * @return the object
	 */
	public Object decode(Object target, JsonObject jsonObject) {
		return decode(target, jsonObject, null);
	}
	/**
	 * Read json.
	 * 
	 * @param target
	 *            the target
	 * @param jsonObject
	 *            the json object
	 * @param filter
	 *            the filter for decoding
	 * @return the object
	 */
	public Object decode(Object target, JsonObject jsonObject, Filter filter) {
		if(filter==null){
			filter=this.filter.cloneObj();
		}
		Object mainItem = decoding(target, jsonObject, filter.withStandard(this.filter));
		for (ReferenceObject ref : filter.getRefs()) {
			ref.execute(this);
		}
		return mainItem;
	}

	/**
	 * Read json.
	 * 
	 * @param jsonObject
	 *            the json object
	 * @param filter
	 *            the filter for decoding
	 * @return the object
	 */
	private Object decoding(JsonObject jsonObject, Filter filter) {
		Object result = null;
		SendableEntityCreator typeInfo = grammar.getReadCreator(
				jsonObject, this);
		
		if(filter==null){
			filter=this.filter.cloneObj();
		}

		if (typeInfo != null) {
			if(grammar.hasReadValue(jsonObject, ID)){
				String jsonId = grammar.getReadValue(jsonObject, ID);
				if (jsonId != null) {
					result = getObject(jsonId);
				}
			}
			if (result == null) {
				result = typeInfo.getSendableInstance(false);
				readMessages(null, null, result, jsonObject, NEW);
			} else {
				readMessages(null, null, result, jsonObject, UPDATE);
			}
			filter.withStandard(this.filter);
			if (typeInfo instanceof SendableEntityCreatorNoIndex) {
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
	 *            the target
	 * @param jsonObject
	 *            the json object
	 * @param filter
	 *            the filter for decoding
	 * @return the object
	 */
	protected Object decoding(Object target, JsonObject jsonObject,	Filter filter) {
		// JSONArray jsonArray;
		boolean isId = filter.isId(this, target, target.getClass().getName());
		if (isId) {
			String jsonId =  grammar.getReadValue(jsonObject, ID);
			if (jsonId == null) {
				return target;
			}
			put(jsonId, target);
			getCounter().readId(jsonId);
		}
		JsonObject jsonProp = grammar.getReadProperties(jsonObject, this, filter, isId);
		if (jsonProp != null) {
			SendableEntityCreator prototyp = grammar.getWriteCreator(target,
					target.getClass().getName(), this);
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
	 *            the target
	 * @param property
	 *            the property
	 * @param value
	 *            the value
	 * @param creator
	 *            the creator
	 * @param filter 
	 * 			  the filter
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
						JsonObject child = (JsonObject) kid;
						String className = (String) child.get(CLASS);
						String jsonId = (String) child.get(ID);
						if (className == null && jsonId != null) {
							// It is a Ref
							filter.add(new ReferenceObject()
								.withId(jsonId)
								.withCreator(creator)
								.withProperty(property)
								.withEntity(target));
						} else {
							creator.setValue(target, property,
									decoding((JsonObject) kid, filter), NEW);
						}
					} else {
						creator.setValue(target, property, kid, NEW);
					}
				}
			} else {
				if (value instanceof JsonObject) {
					// // got a new kid, create it
					JsonObject child = (JsonObject) value;
					String className = (String) child.get(CLASS);
					String jsonId = (String) child.get(ID);
					// CHECK LIST AND MAPS
					Object ref_Obj = creator.getSendableInstance(true);
					Object refValue = creator.getValue(ref_Obj, property);
					if (refValue instanceof Map<?, ?>) {
						JsonObject json = (JsonObject) value;
						Iterator<String> i = json.keys();
						while (i.hasNext()) {
							String key = i.next();
							Object entryValue = json.get(key);
							if (entryValue instanceof JsonObject) {
								creator.setValue(
										target,
										property,
										new ObjectMapEntry().with(key, decode((JsonObject) entryValue)),
										NEW);
							} else if (entryValue instanceof JsonArray) {
								creator.setValue(
										target,
										property,
										new ObjectMapEntry().with(key, decode((JsonArray) entryValue)),
										NEW);
							} else {
								creator.setValue(target, property,
										new ObjectMapEntry().with(key, entryValue), NEW);
							}
						}
					} else if (className == null && jsonId != null) {
						// It is a Ref
						filter.add(new ReferenceObject()
									.withId(jsonId)
									.withCreator(creator)
									.withProperty(property)
									.withEntity(target));
					} else {
						creator.setValue(target, property, decoding(child, filter), NEW);
					}
				} else {
					creator.setValue(target, property, value, NEW);
				}
			}
		}
	}

	/**
	 * To json array.
	 * 
	 * @param object
	 *            the object
	 * @return the json array
	 */
	public JsonArray toJsonArray(Object object) {
		return toJsonArray(object, null);
	}

	/**
	 * Convert to JsonArray in the resource
	 * 
	 * @param object
	 *            the object
	 * @param filter
	 *            the filter
	 * @return the json array
	 */
	public JsonArray toJsonArray(Object object, Filter filter) {
		JsonArray jsonArray = getPrototyp().getNewArray();
		if (filter == null) {
			filter = this.filter.cloneObj();
		}
		
		if(object instanceof Collection<?>){
		   Collection<?> list = (Collection<?>) object;
			Filter newFilter = filter.withStandard(this.filter);
			for(Iterator<?> i = list.iterator();i.hasNext();){
				Object item = i.next();
				toJsonArray(item, jsonArray, newFilter, 0);
			}
			return jsonArray;
		}
	   if(object.getClass().isArray()){
	      Filter newFilter = filter.withStandard(this.filter);
	      for(Object item : ((Object[])object)){
            toJsonArray(item, jsonArray, newFilter, 0);
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
	 *            the object
	 * @param jsonArray
	 *            the list
	 * @param filter
	 *            the Filter for split serialisation
	 * @return the JsonArray
	 */
	public JsonArray toJsonArray(Object object, JsonArray jsonArray,
			Filter filter) {
		if(filter==null){
			filter = this.filter;
		}
		if(jsonArray.isComparator() && jsonArray.comparator() instanceof EntityComparator){
			((EntityComparator<?>)jsonArray.comparator()).withMap(this);
		}
		return toJsonArray(object, jsonArray, filter.withStandard(this.filter), 0);
	}

	protected JsonArray toJsonArray(Object entity, JsonArray jsonArray,
			Filter filter, int deep) {
		String className = entity.getClass().getName();
		String id = getId(entity);

		JsonObject jsonObject = jsonArray.getNewObject();
		boolean sortedArray = jsonArray.isComparator();
		boolean isId = filter.isId(this, entity, className);
		if (isId) {
			if (!filter.hasVisitedObjects(id) ) {
				jsonObject.put(ID, id);
				jsonObject.put(CLASS, className);
				if(!sortedArray){
					jsonArray.add(jsonObject);
				}
			}
		}else if (!filter.hasVisitedObjects(entity) ) {
			jsonObject.put(CLASS, className);
			if(!sortedArray){
				jsonArray.add(jsonObject);
			}
		}

		SendableEntityCreator creator = getCreator(className, true);
		if (creator == null ) {
			logger.error(this, "No Creator exist for " + className);
			return null;
		}
		String[] properties = creator.getProperties();
		if (isId) {
			filter.addToVisitedObjects(id);
		}else{
			filter.addToVisitedObjects(entity);
		}

		if (properties != null) {
			JsonObject jsonProps = getPrototyp();
			for (String property : properties) {
				if (jsonProps.has(property) ) {
					logger.error(this, "Property duplicate:" + property + "(" + className + ")");
				}
				Object subValue = parseProperty(creator, entity, filter,
						className, property, jsonArray, deep+1);
				if (subValue != null) {
					jsonProps.put(property, subValue);
				}
			}
			if (jsonProps.size() > 0) {
				jsonObject.put(JSON_PROPS, jsonProps);
			}
		}
		if(sortedArray && jsonObject.has(CLASS)){
			jsonArray.add(jsonObject);
		}
		
		return jsonArray;
	}

	/**
	 * Sets the update msg listener.
	 * 
	 * @param listener
	 *            the new update msg listener
	 * @return JsonIdMap
	 */
	public JsonIdMap withUpdateMsgListener(MapUpdateListener listener) {
		this.updatelistener = listener;
		if (listener instanceof PropertyChangeListener) {
			super.withUpdateMsgListener((PropertyChangeListener) listener);
		}
		return this;
	}

	@Override
	public IdMapEncoder withUpdateMsgListener(PropertyChangeListener listener) {
		super.withUpdateMsgListener(listener);
		if (listener instanceof MapUpdateListener) {

			this.updatelistener = (MapUpdateListener) listener;
		}
		return this;
	}

	/**
	 * Send update msg from PropertyChange MapUpdater
	 * 
	 * @param evt
	 *            the Change
	 * @param jsonObject
	 *            the json object
	 * @return true, if successful
	 */
	public boolean sendUpdateMsg(PropertyChangeEvent evt, JsonObject jsonObject) {
		if (updatePropertylistener != null && evt != null) {
			updatePropertylistener.propertyChange(evt);
		}

		if (this.updatelistener != null && evt != null) {
			return this.updatelistener.sendUpdateMsg(evt.getSource(), evt.getPropertyName(), evt.getOldValue(),
					evt.getNewValue(), jsonObject);
		}
		return true;
	}
	
	public boolean readMessages(String key, Object element, Object value, JsonObject props, String typ){
		if (this.updatelistener != null) {
			return this.updatelistener.readMessages(key, element, value, props, typ);
		}
		return true;
	}

	public boolean isReadMessages(String key, Object element, JsonObject props, String typ){
		if (this.updatelistener != null) {
			return this.updatelistener.isReadMessages(key, element, props, typ);
		}
		return true;
	}
	
	/**
	 * To json object by id.
	 * 
	 * @param id
	 *            the id
	 * @return the json object
	 */
	public JsonObject toJsonObjectById(String id) {
		return toJsonObject(super.getObject(id), new Filter().withConvertable(new Deep().withDeep(0)));
	}

	/**
	 * To json array by ids.
	 * 
	 * @param suspendIdList
	 *            the suspend id list
	 */
	public void toJsonArrayByIds(ArrayList<String> suspendIdList) {
		JsonObject sendObj = getPrototyp();
		JsonArray children = sendObj.getNewArray();
		for (String childId : suspendIdList) {
			children.add(toJsonObjectById(childId));
		}
		sendObj.put(IdMapEncoder.UPDATE, children);
		sendUpdateMsg(null, sendObj);
	}

	/**
	 * Execute update msg.
	 * 
	 * @param element
	 *            the element
	 * @return true, if successful
	 */
	public boolean executeUpdateMsg(JsonObject element) {
		if (this.updateListener == null) {
			this.updateListener = new UpdateListener(this);
		}
		return this.updateListener.execute(element);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni.kassel.peermessage.IdMap#garbageCollection(java.util.Set)
	 */
	public void garbageCollection(List<String> classCounts) {
		for(Iterator<AbstractKeyValueEntry<String, Object>> i = keyValue.iterator();i.hasNext();){
			String id = i.next().getKeyString();
			if (!classCounts.contains(id)) {
				i.remove();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClass().getName() + " (" + this.size() + ")";
	}

	public boolean skipCollision(Object masterObj, String key, Object value,
			JsonObject removeJson, JsonObject updateJson) {
		if (this.updatelistener != null) {
			return this.updatelistener.skipCollision(masterObj, key, value,
					removeJson, updateJson);
		}
		return true;
	}

	/**
	 * @param value Gammar value
	 * @return Itself
	 */
	public JsonIdMap withGrammar(Grammar value) {
		this.grammar = value;
		return this;
	}
	
	/**
	 * Sets the typ save.
	 * 
	 * @param typSave
	 *            the new typ save
	 * @return Itself
	 */
	public JsonIdMap withTypSave(boolean typSave) {
		this.typSave = typSave;
		return this;
	}
}
