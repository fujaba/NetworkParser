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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.string.StringContainer;
import de.uniks.networkparser.xml.XMLEntity;

public class JsonTokener extends Tokener {
	public final static String STOPCHARS = ",:]}/\\\"[{;=# ";
	private boolean allowCRLF = false;

	public boolean isAllowCRLF() {
		return allowCRLF;
	}

	public JsonTokener withAllowCRLF(boolean allowCRLF) {
		this.allowCRLF = allowCRLF;
		return this;
	}

	@Override
	public Object nextValue(BaseItem creator, boolean allowQuote) {
		char c = nextClean(true);

		switch (c) {
		case '"':
			next();
			return EntityUtil.unQuote(nextString(new StringContainer(), allowQuote, true, false, true, c));
		case '\\':
			// Must be unquote
			next();
			next();
			return nextString(new StringContainer(), allowQuote, true, '"');
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
		// back();
		return super.nextValue(creator, allowQuote);
	}

	@Override
	protected String getStopChars() {
		return STOPCHARS;
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

	@Override
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
		next();
		boolean isQuote = true;
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
				next();
				isQuote = false;
				continue;
			case '}':
				next();
				return;
			case ',':
				next();
				key = nextValue(entity, isQuote).toString();
				break;
			default:
				key = nextValue(entity, isQuote).toString();
			}
			c = nextClean(true);
			if (c == '=') {
				if (charAt(position() + 1) == '>') {
					next();
				}
			} else if (c != ':') {
				if (logger.error(this, "parseToEntity",
						NetworkParserLog.ERROR_TYP_PARSING, entity)) {
					throw new RuntimeException("Expected a ':' after a key ["
							+ getNextString(30) + "]");
				}
				return;
			}
			next();
			entity.withKeyValue(key, nextValue(entity, isQuote));
		}
	}

	@Override
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
					entityList.withAll(nextValue(entityList, false));
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
					next();
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
		next();
	}
}
