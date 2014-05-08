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
import de.uniks.networkparser.TextParsingException;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.BaseKeyValueEntity;
import de.uniks.networkparser.interfaces.BaseListEntity;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.FactoryEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class JsonTokener extends Tokener {
	public final static String STOPCHARS = ",:]}/\\\"[{;=# ";

	@Override
	public Object nextValue(BaseItem creator, boolean allowQuote) {
		char c = nextStartClean();

		switch (c) {
		case '"':
			next();
			return nextString(c, false, allowQuote, false, true);
		case '\\':
			// Must be unquote
			next();
			next();
			return nextString('"', false, allowQuote, true, true);
		case '{':
			if (creator instanceof FactoryEntity) {
				BaseItem element = ((FactoryEntity)creator).getNewObject();
				this.parseToEntity((BaseKeyValueEntity) element);
				return element;
			}
		case '[':
			if (creator instanceof FactoryEntity) {
				BaseListEntity elementList = ((FactoryEntity)creator).getNewArray();
				this.parseToEntity(elementList);
				return elementList;
			}
		default:
			break;
		}
//		back();
		return super.nextValue(creator, allowQuote);
	}

	@Override
	protected String getStopChars() {
		return STOPCHARS;
	}

	public BaseKeyValueEntity parseEntity(JsonObject parent, BaseKeyValueEntity newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			String[] names = BaseKeyValueEntity.getNames(xmlEntity);
			parent.put(JsonIdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValue() != null
					&& xmlEntity.getValue().length() > 0) {
				parent.put(JsonIdMap.VALUE, xmlEntity.getValue());
			}
			for (String prop : names) {
				Object propValue = xmlEntity.get(prop);
				parseEntityProp(props, propValue, prop);
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
				if (props.has(prop)) {
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
	 * @param newValue
	 * @return
	 */
	public BaseKeyValueEntity parseToEntity(JsonObject parent, BaseKeyValueEntity newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			String[] names = BaseKeyValueEntity.getNames(xmlEntity);
			parent.put(JsonIdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValue() != null
					&& xmlEntity.getValue().length() > 0) {
				parent.put(JsonIdMap.VALUE, xmlEntity.getValue());
			}
			for (String prop : names) {
				Object propValue = xmlEntity.get(prop);
				parseEntityProp(props, propValue, prop);
			}
			for (XMLEntity children : xmlEntity.getChildren()) {
				parseEntityProp(props, children, children.getTag());
			}
			parent.put(JsonIdMap.JSON_PROPS, props);
		}
		return parent;
	}

	@Override
	public void parseToEntity(BaseKeyValueEntity entity) throws TextParsingException{
		char c;
		String key;
		if (nextStartClean() != '{') {
			throw new TextParsingException(
					"A JsonObject text must begin with '{'", this);
		}
		next();
		boolean isQuote=true;
		for (;;) {
			c = nextStartClean();
			switch (c) {
			case 0:
				throw new TextParsingException(
						"A JsonObject text must end with '}'", this);
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
			c = nextStartClean();
			if (c == '=') {
				if (charAt(position()+1) == '>') {
					next();
				}
			} else if (c != ':') {
				throw new TextParsingException("Expected a ':' after a key ["
						+ getNextString(30) + "]", this);
			}
			next();
			entity.put(key, nextValue(entity,isQuote));
		}
	}

	@Override
	public void parseToEntity(BaseListEntity entityList) throws TextParsingException{
		char c=nextStartClean();
		if (c != '[') {
			throw new TextParsingException(
					"A JSONArray text must start with '['", this);
		}
		if ((nextClean()) != ']') {
			for (;;) {
				c=getCurrentChar();
				if (c == ',') {
					entityList.with(null);
				} else {
					entityList.with(nextValue(entityList, false));
				}
				c = nextStartClean();
				switch (c) {
				case ';':
				case ',':
					if (nextClean() == ']') {
						return;
					}
					break;
				case ']':
					next();
					return;
				default:
					throw new TextParsingException("Expected a ',' or ']' not '"+getCurrentChar()+"'",
							this);
				}
			}
		}
		next();
	}
}
