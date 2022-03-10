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
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleException;
import de.uniks.networkparser.SimpleMap;
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

/**
 * The Class JsonTokener.
 *
 * @author Stefan
 */
public class JsonTokener extends Tokener {
    /** The Constant STOPCHARSJSON. */
	public static final char[] STOPCHARS = new char[] {'"', ',', ':', ']', '}', '/', '\\', '[','{', ';', '=', '#', ' '};
	
	/** The Constant COMMENT. */
	public static final char COMMENT = '#';

	/**
	 * Cross compiling.
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
		    return parsingEntity((JsonObject) parent, (SimpleKeyValueList<?, ?>) newValue);
		}
		if (!(newValue instanceof Buffer)) {
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

	public EntityList parsingEntity(EntityList entityList, Buffer buffer) {
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
					entityList.add(nextValue(buffer));
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

	public Entity parsingEntity(Entity entity, Buffer buffer) {
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
				Object keyValue = nextValue(buffer);
				if (keyValue == null) {
					/* No Key Found Must be an empty statement */
					return entity;
				}
				key = keyValue.toString();
				break;
			default:
				Object parse = nextValue(buffer);
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
					valueOld += " " + key + " " + nextValue(buffer);

					entity.put(oldKey, valueOld);
					continue;
				}
				if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new SimpleException("Expected a ':' after a key [" + buffer.getString(30).toString() + "]",
							this);
				}
				return null;
			}
			buffer.skip();
			entity.put(key, nextValue(buffer));
		} while (c != 0);
		return entity;
	}
	
	/**
	 * Next value.
	 *
	 * @param buffer the buffer
	 * @return the object
	 */
	public Object nextValue(Buffer buffer) {
		if (buffer == null) {
			return null;
		}
		char stopChar = buffer.nextClean(true);

		switch (stopChar) {
		case BufferItem.QUOTES:
            buffer.skip();
            CharacterBuffer text = new CharacterBuffer();
            buffer.parseString(text, true, BufferItem.QUOTES);
            buffer.skip();
            return text.toString();
		case '\\':
			/* Must be unquote */
            buffer.skip();
            buffer.skip();
            CharacterBuffer textResult = new CharacterBuffer();
            buffer.parseString(textResult, true, BufferItem.QUOTES);
            buffer.skip();
            return textResult.toString();
		case JsonObject.START:
			return this.parsingEntity(newInstance(), buffer);
		case JsonArray.START:
			return this.parsingEntity(newInstanceList(), buffer);
		default:
			break;
		}
		return super.nextValue(buffer, STOPCHARS);
	}

	/**
	 * Decoding simple.
	 *
	 * @param jsonObject the json object
	 * @param target the target
	 * @param creator the creator
	 * @return the object
	 */
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
						creator.setValue(target, property, subTarget, SimpleMap.NEW);
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
								creator.setValue(target, property, subTarget, SimpleMap.NEW);
							}
						}
					}
				}
			} else if (value != null) {
				/* Simple Value */
				creator.setValue(target, property, value, SimpleMap.NEW);
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
			if (!kid) {
				map.withStrategy(jsonObject.getString(SimpleMap.TYPE));
				result = map.getTarget();
			}
			if (grammar.hasValue(jsonObject, SimpleMap.ID) && result == null) {
				String jsonId = grammar.getValue(jsonObject, SimpleMap.ID);
				if (jsonId != null) {
					result = this.map.getObject(jsonId);
				}
			}
			SimpleEvent event = null;
			if (result == null) {
				result = grammar.getNewEntity(typeInfo, grammar.getValue(jsonObject, SimpleMap.CLASS), false);
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
		} else if (jsonObject.get(SimpleMap.ID) != null) {
			return this.map.getObject((String) jsonObject.get(SimpleMap.ID));
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
			String jsonId = grammar.getValue(jsonObject, SimpleMap.ID);
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
		if (value == null && !map.isStrategyNew()) {
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

	/**
	 * With map.
	 *
	 * @param map the map
	 * @return the json tokener
	 */
	@Override
	public JsonTokener withMap(SimpleMap map) {
		super.withMap(map);
		return this;
	}

	/**
	 * New instance.
	 *
	 * @return the entity
	 */
	@Override
	public Entity newInstance() {
		return new JsonObject();
	}

	/**
	 * New instance list.
	 *
	 * @return the entity list
	 */
	@Override
	public EntityList newInstanceList() {
		return new JsonArray();
	}

	/**
	 * Creates the link.
	 *
	 * @param parent the parent
	 * @param property the property
	 * @param className the class name
	 * @param id the id
	 * @return the entity
	 */
	public Entity createLink(Entity parent, String property, String className, String id) {
		Entity child = newInstance();
		child.put(SimpleMap.CLASS, className);
		child.put(SimpleMap.ID, id);
		return child;
	}
}
