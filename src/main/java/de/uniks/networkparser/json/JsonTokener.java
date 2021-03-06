package de.uniks.networkparser.json;

import java.util.Collection;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
import java.util.Map;
import java.util.Map.Entry;

import de.uniks.networkparser.EntityCreator;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;
import de.uniks.networkparser.list.ObjectMapEntry;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class JsonTokener extends Tokener {
	/** The Constant JSON_PROPS. */
	public static final String STOPCHARS = ",]}/\\\"[{;=# ";
	public static final char COMMENT = '#';
	private boolean simpleFormat;

	/**
	 * Cross compiling
	 * 
	 * @param parent   the parent Element
	 * @param newValue the newValue
	 * @return Itself
	 */
	@Override
	public BaseItem parseToEntity(BaseItem parent, Object newValue) {
		if (newValue == null) {
			return null;
		}
		if (newValue instanceof SimpleKeyValueList<?, ?>) {
			return parsingEntityXML((JsonObject) parent, (SimpleKeyValueList<?, ?>) newValue);
		}
		if (newValue instanceof Buffer == false) {
			return null;
		}
		Buffer buffer = (Buffer) newValue;
		if (parent instanceof Entity) {
			return parsingEntity((Entity) parent, buffer);
		} else if (parent instanceof EntityList) {
			return parsingEntity((EntityList) parent, buffer);
		}
		return null;
	}

	private EntityList parsingEntity(EntityList entityList, Buffer buffer) {
		/* FIXME REMOVE */
		if (buffer == null) {
			return null;
		}
		char c = buffer.nextClean(true);
		if (c != JsonArray.START) {
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entityList)) {
				throw new SimpleException(
						"A JSONArray text must start with '['. It is " + c + "(" + buffer.getString(20) + ")", this);
			}
			return null;
		}
		if ((buffer.nextClean(false)) != JsonArray.END) {
			for (;;) {
				c = buffer.getCurrentChar();
				if (c != ',') {
					entityList.add(nextValue(buffer, entityList, false, false, (char) 0));
				}
				c = buffer.nextClean(true);
				switch (c) {
				case ';':
				case ',':
					if (buffer.nextClean(false) == JsonArray.END) {
						return entityList;
					}
					break;
				case JsonArray.END:
					buffer.skip();
					return entityList;
				default:
					if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entityList)) {
						throw new SimpleException("Expected a ',' or ']' not '" + buffer.getCurrentChar() + "'", this);
					}
					return null;
				}
			}
		}
		buffer.skip();
		return entityList;
	}

	private Entity parsingEntity(Entity entity, Buffer buffer) {
		if (buffer == null || entity == null) {
			return entity;
		}
		String key;
		if (buffer.nextClean(true) != JsonObject.START) {
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new SimpleException("A JsonObject text must begin with '{' \n" + buffer, this);
			}
		}
		buffer.skip();
		boolean isQuote = true;
		char stop = (char) 0;
		char c;
		do {
			c = buffer.nextClean(true);
			switch (c) {
			case 0:
				if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new SimpleException("A JsonObject text must end with '}'", this);
				}
				return null;
			case '\\':
				/* unquote */
				buffer.skip();
				isQuote = false;
				continue;
			case COMMENT:
				buffer.skip();
				buffer.skipTo(BaseItem.CRLF, false, false);
				continue;
			case '/':
				c = buffer.nextClean(false);
				if ('/' == c) {
					buffer.skipTo(BaseItem.CRLF, false, false);
					continue;
				} else if ('*' == c) {
					buffer.skipTo("*/", true, false);
					continue;
				}
			case JsonObject.END:
				buffer.skip();
				return entity;
			case ',':
				buffer.skip();
				Object keyValue = nextValue(buffer, entity, isQuote, false, stop);
				if (keyValue == null) {
					/* No Key Found Must be an empty statement */
					return entity;
				}
				key = keyValue.toString();
				break;
			default:
				Object parse = nextValue(buffer, entity, isQuote, false, stop);
				if (parse == null) {
					return entity;
				}
				key = parse.toString();
			}
			c = buffer.nextClean(true);
			if (c != COLON && c != ENTER) {
				if (entity.size() > 0) {
					/* HJSON OLD ADD TO VALUE */
					String oldKey = entity.getKeyByIndex(entity.size() - 1);
					String valueOld = entity.getValueByIndex(entity.size() - 1).toString();
					valueOld += " " + key + " " + nextValue(buffer, entity, isQuote, false, stop);

					entity.put(oldKey, valueOld);
					continue;
				}
				if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new SimpleException("Expected a ':' after a key [" + buffer.getString(30).toString() + "]",
							this);
				}
				return null;
			}
			buffer.getChar();
			entity.put(key, nextValue(buffer, entity, isQuote, false, stop));
		} while (c != 0);
		return entity;
	}

	private BaseItem parsingSimpleEntityXML(JsonObject parent, XMLEntity newValue) {
		/* <TAG PARAM>CHILDREN</TAG> */
		if (newValue == null) {
			return null;
		}

		/* Parsing all Parameter */
		int i = 0;
		for (; i < newValue.size(); i++) {
			String key = newValue.getKeyByIndex(i);
			Object value = newValue.getValueByIndex(i);
			parent.put(key, value);
		}

		if (i == 0 && newValue.sizeChildren() == 1) {
			XMLEntity child = (XMLEntity) newValue.getChild(0);
			if (child.sizeChildren() > 0) {
				/* PARSING */
				JsonObject childItem = (JsonObject) newInstance();
				parsingSimpleEntityXML(childItem, child);
				parent.put(child.getTag(), childItem);

			} else {
				parent.put(child.getTag(), child.getValue());
			}
			return parent;
		}
		/* Parsing children */
		SimpleKeyValueList<String, Integer> childrenCount = new SimpleKeyValueList<String, Integer>();
		for (i = 0; i < newValue.sizeChildren(); i++) {
			XMLEntity child = (XMLEntity) newValue.getChild(i);
			childrenCount.increment(child.getTag());
		}
		SimpleKeyValueList<String, JsonArray> childrenArray = new SimpleKeyValueList<String, JsonArray>();
		for (i = 0; i < newValue.sizeChildren(); i++) {
			JsonObject item = (JsonObject) newInstance();
			BaseItem child = newValue.getChild(i);
			if (child instanceof XMLEntity) {
				XMLEntity xml = (XMLEntity) child;
				String tag = xml.getTag();
				if (childrenCount.get(tag) == 1) {
					parsingSimpleEntityXML(item, xml);
					if (item.size() > 0) {
						parent.put(tag, item);
					} else if (xml.getValue() != null) {
						/* Ifgnore check for ValueItem */
						String value = xml.getValue().trim();
						if (value.length() > 0) {
							parent.put(tag, value);
						}
					}
				} else {
					JsonArray children = childrenArray.get(tag);
					if (children == null) {
						children = (JsonArray) newInstanceList();
						childrenArray.put(tag, children);
					}
					parsingSimpleEntityXML(item, xml);
					children.add(item);
				}
			}
		}
		for (i = 0; i < childrenArray.size(); i++) {
			parent.put(childrenArray.getKeyByIndex(i), childrenArray.getValueByIndex(i));
		}
		return parent;
	}

	private BaseItem parsingEntityXML(JsonObject parent, SimpleKeyValueList<?, ?> newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;

			if (simpleFormat) {
				return parsingSimpleEntityXML(parent, xmlEntity);
			}

			parent.put(IdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValue() != null && xmlEntity.getValue().length() > 0) {
				parent.put(IdMap.VALUE, xmlEntity.getValue());
			}

			int i;
			for (i = 0; i < xmlEntity.size(); i++) {
				parseEntityProp(props, xmlEntity.getValueByIndex(i), xmlEntity.getKeyByIndex(i));
			}

			for (i = 0; i < xmlEntity.size(); i++) {
				BaseItem child = xmlEntity.getChild(i);
				if (child instanceof XMLEntity == false) {
					continue;
				}
				XMLEntity xml = (XMLEntity) child;
				parseEntityProp(props, xml, xml.getTag());
			}
			parent.put(PROPS, props);
			return parent;
		}
		return null;
	}

	@Override
	public Object nextValue(Buffer buffer, BaseItem creator, boolean allowQuote, boolean allowDuppleMarks,
			char stopChar) {
		if (buffer == null) {
			return null;
		}
		stopChar = buffer.nextClean(true);

		switch (stopChar) {
		case BufferItem.QUOTES:
			buffer.skip();
			return EntityUtil.unQuote(nextString(buffer, new CharacterBuffer(), true, true, stopChar));
		case '\\':
			/* Must be unquote */
			buffer.skip();
			buffer.skip();
			return nextString(buffer, new CharacterBuffer(), allowQuote, true, BufferItem.QUOTES);
		case JsonObject.START:
			BaseItem element = creator.getNewList(true);
			if (element instanceof Entity) {
				this.parseToEntity((Entity) element, buffer);
			}
			return element;
		case JsonArray.START:
			BaseItem item = creator.getNewList(false);
			if (item instanceof EntityList) {
				this.parseToEntity((EntityList) item, buffer);
			}
			return item;
		default:
			break;
		}
		return super.nextValue(buffer, creator, allowQuote, allowDuppleMarks, stopChar);
	}

	public JsonObject parseEntity(JsonObject parent, SimpleKeyValueList<?, ?> newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			parent.put(IdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValue() != null && xmlEntity.getValue().length() > 0) {
				parent.put(IdMap.VALUE, xmlEntity.getValue());
			}

			int i;
			for (i = 0; i < xmlEntity.size(); i++) {
				parseEntityProp(props, xmlEntity.getValueByIndex(i), xmlEntity.getKeyByIndex(i));
			}
			for (i = 0; i < xmlEntity.size(); i++) {
				BaseItem child = xmlEntity.getChild(i);
				if (child instanceof XMLEntity == false) {
					continue;
				}
				XMLEntity xml = (XMLEntity) child;
				parseEntityProp(props, xml, xml.getTag());
			}
			parent.put(PROPS, props);
		}
		return parent;
	}

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
						propList.add(parseEntity(new JsonObject(), (XMLEntity) propValue));
						props.put(prop, propList);
					}
				} else {
					props.put(prop, parseEntity(new JsonObject(), (XMLEntity) propValue));
				}
			} else {
				props.put(prop, propValue);
			}
		}
	}

	public Object decodingSimple(JsonObject jsonObject, Object target, SendableEntityCreator creator) {
		if (jsonObject == null || target == null || creator == null) {
			return null;
		}
		String[] properties = creator.getProperties();
		jsonObject.withCaseSensitive(false);
		for (String property : properties) {
			Object value = jsonObject.get(property);
			/* Switch for SimpleSet */
			if (value instanceof JsonObject) {
				/* To 1 Assoc */
				SendableEntityCreator subCreator = map.getCreator(property, false, false, null);
				if (subCreator != null) {
					Object subTarget = subCreator.getSendableInstance(false);
					if (decodingSimple((JsonObject) value, subTarget, subCreator) != null) {
						creator.setValue(target, property, subTarget, IdMap.NEW);
					}
				}
			} else if (value instanceof JsonArray) {
				/* To Many */
				SendableEntityCreator subCreator = map.getCreator(property, false, false, null);
				if (subCreator != null) {
					JsonArray jsonArray = (JsonArray) value;
					for (Object item : jsonArray) {
						if (item instanceof JsonObject) {
							Object subTarget = subCreator.getSendableInstance(false);
							if (decodingSimple((JsonObject) item, subTarget, subCreator) != null) {
								creator.setValue(target, property, subTarget, IdMap.NEW);
							}
						}
					}
				}
			} else if (value != null) {
				/* Simple Value */
				creator.setValue(target, property, value, IdMap.NEW);
			}
		}
		return target;
	}

	/**
	 * Read json.
	 *
	 * @param jsonObject the json object
	 * @param map        decoding runtime values
	 * @param kid        Is it a Kid Decoding so Target from Map is not the Target
	 * @return the object
	 */
	public Object decoding(JsonObject jsonObject, MapEntity map, boolean kid) {
		if (map == null) {
			return null;
		}
		if (jsonObject == null) {
			return map.getTarget();
		}
		Grammar grammar = map.getGrammar();
		SendableEntityCreator typeInfo = grammar.getCreator(Grammar.READ, jsonObject, map, null);
		if (typeInfo != null) {
			Object result = null;
			if (kid == false) {
				map.withStrategy(jsonObject.getString(IdMap.TYPE));
				result = map.getTarget();
			}
			if (grammar.hasValue(jsonObject, IdMap.ID) && result == null) {
				String jsonId = grammar.getValue(jsonObject, IdMap.ID);
				if (jsonId != null) {
					result = this.map.getObject(jsonId);
				}
			}
			SimpleEvent event = null;
			if (result == null) {
				result = grammar.getNewEntity(typeInfo, grammar.getValue(jsonObject, IdMap.CLASS), false);
				event = new SimpleEvent(SendableEntityCreator.NEW, jsonObject, this.map, null, null, result);
			} else {
				event = new SimpleEvent(SendableEntityCreator.UPDATE, jsonObject, this.map, null, null, result);
			}
			this.map.notify(event);
			if (typeInfo instanceof SendableEntityCreatorWrapper) {
				String[] properties = typeInfo.getProperties();
				if (properties != null) {
					EntityCreator jsonCreator = EntityCreator.createJson(true);
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
				result = decoding(result, jsonObject, map);
			}
			map.getFilter().isConvertable(event);
			return result;
		} else if (jsonObject.get(IdMap.ID) != null) {
			return this.map.getObject((String) jsonObject.get(IdMap.ID));
		}
		return null;
	}

	/**
	 * Read json.
	 *
	 * @param target     the target
	 * @param jsonObject the json object
	 * @param map        the Runtimeinfos
	 * @return the object
	 */
	private Object decoding(Object target, JsonObject jsonObject, MapEntity map) {
		/* JSONArray jsonArray; */
		if (map == null) {
			return null;
		}
		Grammar grammar = map.getGrammar();
		boolean isId = map.isId(target);
		if (isId) {
			String jsonId = grammar.getValue(jsonObject, IdMap.ID);
			if (jsonId == null) {
				return target;
			}
			this.map.put(jsonId, target, true);
		}
		JsonObject jsonProp = (JsonObject) grammar.getProperties(jsonObject, map, isId);
		if (jsonProp != null) {
			SendableEntityCreator creator = grammar.getCreator(Grammar.WRITE, target, map, target.getClass().getName());
			if (creator == null) {
				return null;
			}
			if (map.isStrategyNew()) {
				Object prototype = creator.getSendableInstance(true);
				String[] properties = creator.getProperties();
				if (properties != null) {
					for (String property : properties) {
						if (jsonProp.has(property)) {
							Object obj = jsonProp.get(property);
							parseValue(target, property, obj, creator, map);
						} else {
							Object defaultValue = creator.getValue(prototype, property);
							if (defaultValue instanceof Collection<?>) {
								defaultValue = creator.getValue(target, property);
								if (defaultValue instanceof Collection<?>) {
									Object[] elements = ((Collection<?>) defaultValue).toArray();
									for (int i = 0; i < elements.length; i++) {
										creator.setValue(target, property, elements[i], SendableEntityCreator.REMOVE);
									}
								} else {
									creator.setValue(target, property, null, SendableEntityCreator.NEW);
								}
							} else {
								parseValue(target, property, defaultValue, creator, map);
							}
						}
					}
				}
			} else {
				for (int p = 0; p < jsonProp.size(); p++) {
					String property = jsonProp.getKeyByIndex(p);
					Object obj = jsonProp.get(property);
					parseValue(target, property, obj, creator, map);
				}
			}
		}
		return target;
	}

	/**
	 * Parses the value.
	 * 
	 * @param target   the target
	 * @param property the property
	 * @param value    the value
	 * @param creator  the creator
	 * @param map      the MapEntity Runtime Infos
	 */
	private void parseValue(Object target, String property, Object value, SendableEntityCreator creator,
			MapEntity map) {
		/* FIXME IF STATGEGY IS UPDATE SET NEW VALUE */
		if (map == null) {
			return;
		}
		if (value == null && map.isStrategyNew() == false) {
			return;
		}
		Filter filter = map.getFilter();
		Grammar grammar = map.getGrammar();
		if (value instanceof JsonArray) {
			JsonArray jsonArray = (JsonArray) value;
			for (int i = 0; i < jsonArray.size(); i++) {
				Object kid = jsonArray.get(i);
				if (kid instanceof JsonObject) {
					/* got a new kid, create it */
					creator.setValue(target, property, decoding((JsonObject) kid, map, true),
							SendableEntityCreator.NEW);
				} else {
					creator.setValue(target, property, kid, SendableEntityCreator.NEW);
				}
			}
		} else {
			if (value instanceof JsonObject) {
				/* got a new kid, create it */
				JsonObject child = (JsonObject) value;
				/* CHECK LIST AND MAPS */
				String className = target.getClass().getName();
				Object ref_Obj = grammar.getNewEntity(creator, className, true);
				if (ref_Obj instanceof Class<?>) {
					ref_Obj = grammar.getNewEntity(creator, className, false);
				}
				Object refValue = creator.getValue(ref_Obj, property);
				if (refValue instanceof Map<?, ?>) {
					JsonObject json = (JsonObject) value;
					for (SimpleIteratorSet<String, Object> i = new SimpleIteratorSet<String, Object>(json); i
							.hasNext();) {
						Entry<String, Object> item = i.next();
						String key = item.getKey();
						Object entryValue = item.getValue();
						if (entryValue instanceof JsonObject) {
							creator.setValue(target, property,
									new ObjectMapEntry().with(key, decoding((JsonObject) entryValue, map, true)),
									SendableEntityCreator.NEW);
						} else {
							creator.setValue(target, property, new ObjectMapEntry().with(key, entryValue),
									SendableEntityCreator.NEW);
						}
					}
				} else {
					Object decoding = decoding(child, map, true);
					if (decoding != null) {
						creator.setValue(target, property, decoding, filter.getStrategy());
					} else {
						creator.setValue(target, property, child, filter.getStrategy());
					}
				}
			} else {
				creator.setValue(target, property, value, filter.getStrategy());
			}
		}
	}

	@Override
	public JsonTokener withMap(IdMap map) {
		super.withMap(map);
		return this;
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

	public JsonTokener withSimpleFormat(boolean value) {
		this.simpleFormat = value;
		return this;
	}
}
