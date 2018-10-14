package de.uniks.networkparser.json;

import java.util.Collection;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
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
	public final static String STOPCHARS = ",]}/\\\"[{;=# ";
	public static final char COMMENT = '#';

	@Override
	public EntityList parseToEntity(EntityList entityList, Buffer buffer) {
		char c = buffer.nextClean(true);
		if (c != JsonArray.START) {
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entityList)) {
				throw new RuntimeException(
						"A JSONArray text must start with '['. It is " + c + "(" + buffer.getString(20) + ")");
			}
			return entityList;
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
						throw new RuntimeException("Expected a ',' or ']' not '" + buffer.getCurrentChar() + "'");
					}
					return entityList;
				}
			}
		}
		buffer.skip();
		return entityList;
	}

	@Override
	public Object nextValue(Buffer buffer, BaseItem creator, boolean allowQuote, boolean allowDuppleMarks,
			char stopChar) {
		stopChar = buffer.nextClean(true);

		switch (stopChar) {
		case BufferItem.QUOTES:
			buffer.skip();
			return EntityUtil.unQuote(nextString(buffer, new CharacterBuffer(), true, true, stopChar));
		case '\\':
			// Must be unquote
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

	@Override
	public boolean parseToEntity(Entity entity, Buffer buffer) {
		String key;
		if (buffer.nextClean(true) != JsonObject.START) {
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException("A JsonObject text must begin with '{' \n" + buffer);
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
					throw new RuntimeException("A JsonObject text must end with '}'");
				}
				return false;
			case '\\':
				// unquote
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
				return true;
			case ',':
				buffer.skip();
				Object keyValue = nextValue(buffer, entity, isQuote, false, stop);
				if (keyValue == null) {
					// No Key Found Must be an empty statement
					return true;
				}
				key = keyValue.toString();
				break;
			default:
				key = nextValue(buffer, entity, isQuote, false, stop).toString();
			}
			c = buffer.nextClean(true);
			if (c != COLON && c != ENTER) {
				if(entity.size()>0) {
					// HJSON OLD ADD TO VALUE
					String oldKey = entity.getKeyByIndex(entity.size() - 1);
					String valueOld = entity.getValueByIndex(entity.size() - 1).toString();
					valueOld +=" "+ key+" "+nextValue(buffer, entity, isQuote, false, stop);

					entity.put(oldKey, valueOld);
					continue;
				}
				if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new RuntimeException("Expected a ':' after a key [" + buffer.getString(30).toString() + "]");
				}
				return false;
			}
			buffer.getChar();
			entity.put(key, nextValue(buffer, entity, isQuote, false, stop));
		} while (c != 0);
		return true;
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

	/**
	 * Cross compiling
	 * 
	 * @param parent   the parent Element
	 * @param newValue the newValue
	 * @return Itself
	 */
	public JsonObject parseToEntity(JsonObject parent, SimpleKeyValueList<?, ?> newValue) {
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

	/**
	 * Read json.
	 *
	 * @param jsonObject the json object
	 * @param map        decoding runtime values
	 * @param kid        Is it a Kid Decoding so Target from Map is not the Target
	 * @return the object
	 */
	public Object decoding(JsonObject jsonObject, MapEntity map, boolean kid) {
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
				result = decoding(result, jsonObject, map);
			}
			map.getFilter().isConvertable(event);
			return result;
//		} else if (jsonObject.get(IdMap.VALUE) != null) {
//			return jsonObject.get(IdMap.VALUE);
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
		// JSONArray jsonArray;
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
//					if(jsonProp.has(property)) {
					Object obj = jsonProp.get(property);
					parseValue(target, property, obj, creator, map);
//					}
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
		// FIXME IF STATGEGY IS UPDATE SET NEW VALUE
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
					// got a new kid, create it
					creator.setValue(target, property, decoding((JsonObject) kid, map, true),
							SendableEntityCreator.NEW);
				} else {
					creator.setValue(target, property, kid, SendableEntityCreator.NEW);
				}
			}
		} else {
			if (value instanceof JsonObject) {
				// // got a new kid, create it
				JsonObject child = (JsonObject) value;
				// CHECK LIST AND MAPS
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
}
