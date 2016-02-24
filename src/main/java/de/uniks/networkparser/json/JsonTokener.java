package de.uniks.networkparser.json;

import java.util.Iterator;
import java.util.Map;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.event.ObjectMapEntry;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;
import de.uniks.networkparser.json.util.JsonObjectCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.logic.SimpleMapEvent;
import de.uniks.networkparser.xml.XMLEntity;

public class JsonTokener extends Tokener {
	/** The Constant JSON_PROPS. */
	public static final String PROPS = "prop";
	
	public final static String STOPCHARS = ",]}/\\\"[{;=# ";
	
	public static final char STARTARRAY='[';
	public static final char ENDARRAY=']';
	public static final char STARTENTITY='{';
	public static final char ENDENTITY='}';

	@Override
	public void parseToEntity(EntityList entityList) {
		char c = nextClean(true);
		if (c != STARTARRAY) {
			if (logger.error(this, "parseToEntity",
					NetworkParserLog.ERROR_TYP_PARSING, entityList)) {
				throw new RuntimeException(
						"A JSONArray text must start with '['");
			}
			return;
		}
		if ((nextClean(false)) != ENDARRAY) {
			for (;;) {
				c = getCurrentChar();
				if (c != ',') {
					entityList.with(nextValue(entityList, false, false, (char)0));
				}
				c = nextClean(true);
				switch (c) {
				case ';':
				case ',':
					if (nextClean(false) == ENDARRAY) {
						return;
					}
					break;
				case ENDARRAY:
					skip();
					return;
				default:
					if (logger.error(this, "parseToEntity",
							NetworkParserLog.ERROR_TYP_PARSING, entityList)) {
						throw new RuntimeException(
								"Expected a ',' or ']' not '"
										+ getCurrentChar() + "'");
					}
					return;
				}
			}
		}
		skip();
	}

	@Override
	public Object nextValue(BaseItem creator, boolean allowQuote, boolean  allowDuppleMarks, char stopChar) {
		stopChar = nextClean(true);

		switch (stopChar) {
		case '"':
			skip();
			return EntityUtil.unQuote(nextString(new CharacterBuffer(), allowQuote, true, stopChar));
		case '\\':
			// Must be unquote
			skip();
			skip();
			return nextString(new CharacterBuffer(), allowQuote, true, '"');
		case STARTENTITY:
			BaseItem element = creator.getNewList(true);
			if (element instanceof Entity ) {
				this.parseToEntity((Entity) element);
			}
			return element;
		case STARTARRAY:
			BaseItem item = creator.getNewList(false);
			if (item instanceof EntityList) {
				this.parseToEntity((EntityList) item);
			}
			return item;
		default:
			break;
		}
		return super.nextValue(creator, allowQuote, allowDuppleMarks, stopChar);
	}

	@Override
	public void parseToEntity(Entity entity) {
		char c;
		String key;
		if (nextClean(true) != STARTENTITY) {
			if (logger.error(this, "parseToEntity",
					NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException(
						"A JsonObject text must begin with '{' \n" + buffer);
			}
		}
		skip();
		boolean isQuote = true;
		char stop=(char)0;
		for (;;) {
			c = nextClean(true);
			switch (c) {
			case 0:
				if (logger.error(this, "parseToEntity",
						NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new RuntimeException(
							"A JsonObject text must end with '}'");
				}
				return;
			case '\\':
				// unquote
				skip();
				isQuote = false;
				continue;
			case ENDENTITY:
				skip();
				return;
			case ',':
				skip();
				key = nextValue(entity, isQuote, false, stop).toString();
				break;
			default:
				key = nextValue(entity, isQuote, false, stop).toString();
			}
			c = nextClean(true);
			if (c != ':' && c != '=') {
				if (logger.error(this, "parseToEntity",
						NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new RuntimeException("Expected a ':' after a key ["+ getString(30).toString() + "]");
				}
				return;
			}
			c = getChar();
			entity.put(key, nextValue(entity, isQuote, false, stop));
		}
	}

	public JsonObject parseEntity(JsonObject parent,
			SimpleKeyValueList<?, ?> newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			parent.put(IdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValue() != null
					&& xmlEntity.getValue().length() > 0) {
				parent.put(IdMap.VALUE, xmlEntity.getValue());
			}

			for (int i = 0; i < xmlEntity.size(); i++) {
				parseEntityProp(props, xmlEntity.getValueByIndex(i), xmlEntity.getKeyByIndex(i));
			}
			for (EntityList child : xmlEntity.getChildren()) {
				if(child  instanceof XMLEntity == false) {
					continue;
				}
				XMLEntity xml = (XMLEntity) child;
				parseEntityProp(props, xml, xml.getTag());
			}
			parent.put(PROPS, props);
		}
		return parent;
	}
	
	/**
	 * Cross compiling
	 * @param parent  the parent Element
	 * @param newValue the newValue
	 * @return Itself
	 */
	public JsonObject parseToEntity(JsonObject parent,
			SimpleKeyValueList<?, ?> newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			parent.put(IdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValue() != null
					&& xmlEntity.getValue().length() > 0) {
				parent.put(IdMap.VALUE, xmlEntity.getValue());
			}

			for (int i = 0; i < xmlEntity.size(); i++) {
				parseEntityProp(props, xmlEntity.getValueByIndex(i), xmlEntity.getKeyByIndex(i));
			}
			
			for (EntityList child : xmlEntity.getChildren()) {
				if(child  instanceof XMLEntity == false) {
					continue;
				}
				XMLEntity xml = (XMLEntity) child;
				parseEntityProp(props, xml, xml.getTag());
			}
			parent.put(PROPS, props);
		}
		return parent;
	}


	//FIXME  NEW PROPERTY
	private void parseEntityProp(JsonObject props, Object propValue, String prop) {
		if (propValue != null) {
			if (propValue instanceof XMLEntity) {
				if (props.containsKey(prop)) {
					Object child = props.get(prop);
					JsonArray propList = null;
					if (child instanceof JsonObject) {
						propList = new JsonArray();
						propList.add(child);
					} else if (child instanceof JsonArray) {
						propList = (JsonArray) child;
					}
					if (propList != null) {
						propList.add(parseEntity(new JsonObject(),
								(XMLEntity) propValue));
						props.put(prop, propList);
					}
				} else {
					props.put(
							prop,
							parseEntity(new JsonObject(), (XMLEntity) propValue));
				}
			} else {
				props.put(prop, propValue);
			}
		}
	}

	/**
	 * Read json.
	 *
	 * @param jsonObject the json object
	 * @param filter  the filter for decoding
	 * @return the object
	 */
	public Object decoding(JsonObject jsonObject, MapEntity map) {
		if (jsonObject == null) {
			return map.getTarget();
		}
		SendableEntityCreator typeInfo = map.getCreator(Grammar.READ, jsonObject, null);
		if (typeInfo != null) {
			Object result = map.getTarget();
			if (map.hasValue(jsonObject, IdMap.ID) && result == null) {
				String jsonId = map.getValue(jsonObject, IdMap.ID);
				if (jsonId != null) {
					result = map.getObject(jsonId);
				}
			}
			if (result == null) {
				result = map.getNewEntity(typeInfo, map.getValue(jsonObject, IdMap.CLASS), false);
				map.notify(new SimpleMapEvent(IdMap.NEW, map.getMap(), jsonObject, result));
			} else {
				map.notify(new SimpleMapEvent(IdMap.UPDATE, map.getMap(), jsonObject, result));
			}
			if (typeInfo instanceof SendableEntityCreatorWrapper) {
				String[] properties = typeInfo.getProperties();
				if (properties != null) {
					JsonObjectCreator jsonCreator = new JsonObjectCreator();
					JsonObject valueMap = new JsonObject();
					for (String property : properties) {
						Object value = jsonObject.get(property);
						parseValue(valueMap, property, value, jsonCreator, map);
					}
					result = ((SendableEntityCreatorWrapper) typeInfo).newInstance(valueMap);
				}
			} else if (typeInfo instanceof SendableEntityCreatorNoIndex) {
				String[] properties = typeInfo.getProperties();
				if (properties != null) {
					for (String property : properties) {
						Object obj = jsonObject.get(property);
						parseValue(result, property, obj, typeInfo, map);
					}
				}
			} else {
				decoding(result, jsonObject, map);
			}
			return result;
		} else if (jsonObject.get(IdMap.VALUE) != null) {
			return jsonObject.get(IdMap.VALUE);
		} else if (jsonObject.get(IdMap.ID) != null) {
			return map.getObject((String) jsonObject.get(IdMap.ID));
		}
		return null;
	}

	/**
	 * Read json.
	 *
	 * @param target  the target
	 * @param jsonObject  the json object
	 * @param filter  the filter for decoding
	 * @return the object
	 */
	private Object decoding(Object target, JsonObject jsonObject, MapEntity map) {
		// JSONArray jsonArray;
		boolean isId = map.isId(target, target.getClass().getName());
		if (isId) {
			String jsonId = map.getValue(jsonObject, IdMap.ID);
			IdMap idMap = map.getMap();
			if (jsonId == null) {
				return target;
			}
			idMap.put(jsonId, target);
			idMap.getCounter().readId(jsonId);
		}
		JsonObject jsonProp = (JsonObject) map.getProperties(jsonObject, isId, Grammar.READ);
		if (jsonProp != null) {
			SendableEntityCreator prototyp = map.getCreator(Grammar.WRITE, target, target.getClass().getName());
			String[] properties = prototyp.getProperties();
			if (properties != null) {
				for (String property : properties) {
					Object obj = jsonProp.get(property);
					parseValue(target, property, obj, prototyp, map);
				}
			}
		}
		return target;
	}

	/**
	 * Parses the value.
	 * @param target  the target
	 * @param property  the property
	 * @param value  the value
	 * @param creator  the creator
	 * @param filter the filter
	 */
	private void parseValue(Object target, String property, Object value, SendableEntityCreator creator,
			MapEntity map) {
		if (value != null) {
			if (value instanceof JsonArray) {
				JsonArray jsonArray = (JsonArray) value;
				for (int i = 0; i < jsonArray.size(); i++) {
					Object kid = jsonArray.get(i);
					if (kid instanceof JsonObject) {
						// got a new kid, create it
						creator.setValue(target, property, decoding((JsonObject) kid, map), IdMap.NEW);
					} else {
						creator.setValue(target, property, kid, IdMap.NEW);
					}
				}
			} else {
				if (value instanceof JsonObject) {
					// // got a new kid, create it
					JsonObject child = (JsonObject) value;
					// CHECK LIST AND MAPS
					String className = target.getClass().getName();
					Object ref_Obj = map.getNewEntity(creator, className, true);
					if (ref_Obj instanceof Class<?>) {
						ref_Obj = map.getNewEntity(creator, className, false);
					}
					Object refValue = creator.getValue(ref_Obj, property);
					if (refValue instanceof Map<?, ?>) {
						JsonObject json = (JsonObject) value;
						Iterator<String> i = json.keySet().iterator();
						while (i.hasNext()) {
							String key = i.next();
							Object entryValue = json.get(key);
							if (entryValue instanceof JsonObject) {
								///FIXME CHANGE DECODE TO DECODING
								creator.setValue(target, property, new ObjectMapEntry().with(key, decoding((JsonObject) entryValue, map)), IdMap.NEW);
							} else if (entryValue instanceof JsonArray) {
								///FIXME CHANGE DECODE TO DECODING
								throw new RuntimeException();
//								creator.setValue(target, property,
//										new ObjectMapEntry().with(key, decode((JsonArray) entryValue)), IdMap.NEW);
							} else {
								creator.setValue(target, property, new ObjectMapEntry().with(key, entryValue), IdMap.NEW);
							}
						}
					} else {
						creator.setValue(target, property, decoding(child, map), map.getStrategy());
					}
				} else {
					creator.setValue(target, property, value, map.getStrategy());
				}
			}
		}
	}

	@Override
	public Entity newInstance() {
		return new JsonObject();
	}
	
	@Override
	public EntityList newInstanceList() {
		return new JsonArray();
	}
	public Entity createLink(Entity parent, String property, String className, String id) {
		Entity child = newInstance();
		child.put(IdMap.CLASS, className);
		child.put(IdMap.ID, id);
		return child;
	}
	
	
	
	
	
	
	
	
	
	
	
	//FIXME OLDS
//	/**
//	 * To Jsonobject.
//	 *
//	 * @param entity
//	 *            the entity to convert
//	 * @param filter
//	 *            the filter
//	 * @param className
//	 *            the className of the entity
//	 * @param deep
//	 *            the deep of model-level
//	 * @return the Jsonobject
//	 */
//	private JsonObject toJsonObject(Object entity, MapEntity map, String className) {
//		String id = null;
//		SendableEntityCreator creator = map.getCreator(Grammar.WRITE, entity, className);
//		if (creator == null) {
//			return null;
//		}
//		if (creator instanceof SendableEntityCreatorNoIndex) {
//		} else {
//			id = map.getId(entity, className);
//		}
//		JsonObject jsonProp = new JsonObject().withAllowEmptyValue(map.isFullSeriation());
//		String[] properties = map.getProperties(creator);
//		if (properties != null) {
//			Object referenceObject = map.getNewEntity(creator, className, true);
//			map.add();
//			for (String property : properties) {
//				if (jsonProp.has(property)) {
//					if (map.error("toJsonObject", NetworkParserLog.ERROR_TYP_DUPPLICATE, entity, className)) {
//						throw new RuntimeException("Property duplicate:" + property + "(" + className + ")");
//					}
//				}
//				//Object subValue = parseProperty(creator, entity, filter, className, property, null, deep + 1);
//				Object subValue = parseProperty(creator, entity, referenceObject, property, map, null);
//				if (subValue != null || map.isFullSeriation()) {
//					jsonProp.put(property, subValue);
//				}
//			}
//			map.minus();
//		}
//		return (JsonObject) map.setProperties(creator, className, id, jsonProp);
//	}
//
//

}
