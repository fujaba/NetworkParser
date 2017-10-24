package de.uniks.networkparser.json;

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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;
import de.uniks.networkparser.interfaces.SendableEntityCreatorWrapper;
import de.uniks.networkparser.json.util.JsonObjectCreator;
import de.uniks.networkparser.list.ObjectMapEntry;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class JsonTokener extends Tokener {
	/** The Constant JSON_PROPS. */
	public final static String STOPCHARS = ",]}/\\\"[{;=# ";
	public static final char COMMENT='#';

	public static final char STARTARRAY='[';
	public static final char ENDARRAY=']';
	public static final char STARTENTITY='{';
	public static final char ENDENTITY='}';

	@Override
	public void parseToEntity(EntityList entityList) {
		char c = nextClean(true);
		if (c != STARTARRAY) {
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entityList)) {
				throw new RuntimeException(
						"A JSONArray text must start with '['");
			}
			return;
		}
		if ((nextClean(false)) != ENDARRAY) {
			for (;;) {
				c = getCurrentChar();
				if (c != ',') {
					entityList.add(nextValue(entityList, false, false, (char)0));
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
					if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entityList)) {
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
	public boolean parseToEntity(Entity entity) {
		String key;
		if (nextClean(true) != STARTENTITY) {
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException(
						"A JsonObject text must begin with '{' \n" + buffer);
			}
		}
		skip();
		boolean isQuote = true;
		char stop=(char)0;
		char c;
		do {
			c = nextClean(true);
			switch (c) {
			case 0:
				if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new RuntimeException(
							"A JsonObject text must end with '}'");
				}
				return false;
			case '\\':
				// unquote
				skip();
				isQuote = false;
				continue;
			case COMMENT:
				skip();
				skipTo(BaseItem.CRLF, false, false);
				continue;
			case '/':
				c = nextClean(false);
				if('/' == c) {
					skipTo(BaseItem.CRLF, false, false);
					continue;
				} else if('*' == c) {
					skipTo("*/", true, false);
					continue;
				}
			case ENDENTITY:
				skip();
				return true;
			case ',':
				skip();
				Object keyValue = nextValue(entity, isQuote, false, stop);
				if(keyValue == null) {
					// No Key Found Must eb an empty statement
					return true;
				}
				key = keyValue.toString();
				break;
			default:
				key = nextValue(entity, isQuote, false, stop).toString();
			}
			c = nextClean(true);
			if (c != COLON && c != ENTER) {
				if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new RuntimeException("Expected a ':' after a key ["+ getString(30).toString() + "]");
				}
				return false;
			}
			getChar();
			entity.put(key, nextValue(entity, isQuote, false, stop));
		}while(c!=0);
		return true;
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

			int i;
			for (i = 0; i < xmlEntity.size(); i++) {
				parseEntityProp(props, xmlEntity.getValueByIndex(i), xmlEntity.getKeyByIndex(i));
			}
			for (i = 0; i < xmlEntity.size(); i++) {
				EntityList child = xmlEntity.getChild(i);
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

			int i;
			for (i = 0; i < xmlEntity.size(); i++) {
				parseEntityProp(props, xmlEntity.getValueByIndex(i), xmlEntity.getKeyByIndex(i));
			}

			for (i = 0; i < xmlEntity.size(); i++) {
				EntityList child = xmlEntity.getChild(i);
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
	 * @param map  decoding runtime values
	 * @param kid Is it a Kid Decoding so Target from Map is not the Target
	 * @return the object
	 */
	public Object decoding(JsonObject jsonObject, MapEntity map, boolean kid) {
		if (jsonObject == null) {
			return map.getTarget();
		}
		Grammar grammar = map.getGrammar();
		SendableEntityCreator typeInfo = grammar.getCreator(Grammar.READ, jsonObject, map.getMap(), map.isSearchForSuperClass(), null);
		if (typeInfo != null) {
			Object result = null;
			if(kid == false) {
				result = map.getTarget();
			}
			if (grammar.hasValue(jsonObject, IdMap.ID) && result == null) {
				String jsonId = grammar.getValue(jsonObject, IdMap.ID);
				if (jsonId != null) {
					result = this.map.getObject(jsonId);
				}
			}
			SimpleEvent event =null;
			if (result == null) {
				result = grammar.getNewEntity(typeInfo, grammar.getValue(jsonObject, IdMap.CLASS), false);
				event = new SimpleEvent(SendableEntityCreator.NEW, jsonObject, this.map, null, null, result);
			} else {
				event = new SimpleEvent(SendableEntityCreator.UPDATE, jsonObject, this.map, null, null, result);
			}
			this.map.notify(event);
			result = event.getModelValue();
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
			map.getFilter().isConvertable(event);
			return result;
		} else if (jsonObject.get(IdMap.VALUE) != null) {
			return jsonObject.get(IdMap.VALUE);
		} else if (jsonObject.get(IdMap.ID) != null) {
			return this.map.getObject((String) jsonObject.get(IdMap.ID));
		}
		return null;
	}

	/**
	 * Read json.
	 *
	 * @param target		the target
	 * @param jsonObject	the json object
	 * @param map			the Runtimeinfos
	 * @return the object
	 */
	private Object decoding(Object target, JsonObject jsonObject, MapEntity map) {
		// JSONArray jsonArray;
		Grammar grammar = map.getGrammar();
		Filter filter = map.getFilter();
		boolean isId = filter.isId(target, target.getClass().getName(), map.getMap());
		if (isId) {
			String jsonId = grammar.getValue(jsonObject, IdMap.ID);
			IdMap idMap = this.map;
			if (jsonId == null) {
				return target;
			}
			idMap.put(jsonId, target, true);
//			idMap.getCounter().readId(jsonId);
		}
		JsonObject jsonProp = (JsonObject) grammar.getProperties(jsonObject, map.getMap(), filter, isId, Grammar.READ);
		if (jsonProp != null) {
			SendableEntityCreator prototyp = grammar.getCreator(Grammar.WRITE, target, map.getMap(), map.isSearchForSuperClass(), target.getClass().getName());
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
	 * @param target		the target
	 * @param property		the property
	 * @param value			the value
	 * @param creator		the creator
	 * @param map			the MapEntity Runtime Infos
	 */
	private void parseValue(Object target, String property, Object value, SendableEntityCreator creator,
			MapEntity map) {
		if (value == null) {
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
					creator.setValue(target, property, decoding((JsonObject) kid, map, true), SendableEntityCreator.NEW);
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
					for(SimpleIteratorSet<String, Object> i = new SimpleIteratorSet<String, Object>(json);i.hasNext();) {
						Entry<String, Object> item = i.next();
						String key = item.getKey();
						Object entryValue = item.getValue();
						if (entryValue instanceof JsonObject) {
							creator.setValue(target, property, new ObjectMapEntry().with(key, decoding((JsonObject) entryValue, map, true)), SendableEntityCreator.NEW);
						} else if (entryValue instanceof JsonArray) {
							///FIXME CHANGE DECODE TO DECODING
							throw new RuntimeException();
//								creator.setValue(target, property,
//										new ObjectMapEntry().with(key, decode((JsonArray) entryValue)), SendableEntityCreator.NEW);
						} else {
							creator.setValue(target, property, new ObjectMapEntry().with(key, entryValue), SendableEntityCreator.NEW);
						}
					}
				} else {
					creator.setValue(target, property, decoding(child, map, true), filter.getStrategy());
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
