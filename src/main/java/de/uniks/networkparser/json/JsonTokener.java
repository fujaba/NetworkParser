package de.uniks.networkparser.json;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.xml.XMLEntity;

public class JsonTokener extends Tokener {
	public final static String STOPCHARS = ",]}/\\\"[{;=# ";
	
	public void parseToEntity(AbstractList<?> entityList) {
		char c = nextClean(true);
		if (c != '[') {
			if (logger.error(this, "parseToEntity",
					NetworkParserLog.ERROR_TYP_PARSING, entityList)) {
				throw new RuntimeException(
						"A JSONArray text must start with '['");
			}
			return;
		}
		if ((nextClean(false)) != ']') {
			for (;;) {
				c = getCurrentChar();
				if (c != ',') {
					entityList.withAll(nextValue(entityList, false, false, (char)0));
				}
				c = nextClean(true);
				switch (c) {
				case ';':
				case ',':
					if (nextClean(false) == ']') {
						return;
					}
					break;
				case ']':
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
		case '{':
			BaseItem element = creator.getNewList(true);
			if (element instanceof SimpleKeyValueList<?, ?>) {
				this.parseToEntity((SimpleKeyValueList<?, ?>) element);
			}
			return element;
		case '[':
			BaseItem item = creator.getNewList(false);
			if (item instanceof AbstractList<?>) {
				this.parseToEntity((AbstractList<?>) item);
			}
			return item;
		default:
			break;
		}
		return super.nextValue(creator, allowQuote, allowDuppleMarks, stopChar);
	}

	public void parseToEntity(SimpleKeyValueList<?, ?> entity) {
		char c;
		String key;
		if (nextClean(true) != '{') {
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
			case '}':
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
			entity.withKeyValue(key, nextValue(entity, isQuote, false, stop));
		}
	}
	
	public JsonObject parseEntity(JsonObject parent,
			SimpleKeyValueList<?, ?> newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			parent.put(JsonIdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValueItem() != null
					&& xmlEntity.getValueItem().length() > 0) {
				parent.put(JsonIdMap.VALUE, xmlEntity.getValueItem());
			}

			for (int i = 0; i < xmlEntity.size(); i++) {
				parseEntityProp(props, xmlEntity.getValueByIndex(i), xmlEntity.getKeyByIndex(i));
			}
			for (XMLEntity children : xmlEntity.getChildren()) {
				parseEntityProp(props, children, children.getTag());
			}
			parent.put(JsonIdMap.JSON_PROPS, props);
		}
		return parent;
	}
	
	public void parseEntityProp(JsonObject props, Object propValue, String prop) {
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
	 * Cross compiling
	 *
	 * @param parent
	 *			the parent Element
	 * @param newValue
	 *			the newValue
	 * @return Itself
	 */
	public JsonObject parseToEntity(JsonObject parent,
			SimpleKeyValueList<?, ?> newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			parent.put(JsonIdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValueItem() != null
					&& xmlEntity.getValueItem().length() > 0) {
				parent.put(JsonIdMap.VALUE, xmlEntity.getValueItem());
			}

			for (int i = 0; i < xmlEntity.size(); i++) {
				parseEntityProp(props, xmlEntity.getValueByIndex(i), xmlEntity.getKeyByIndex(i));
			}
			for (XMLEntity children : xmlEntity.getChildren()) {
				parseEntityProp(props, children, children.getTag());
			}
			parent.put(JsonIdMap.JSON_PROPS, props);
		}
		return parent;
	}

}
