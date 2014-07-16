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
import de.uniks.networkparser.AbstractKeyValueList;
import de.uniks.networkparser.AbstractList;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.FactoryEntity;
import de.uniks.networkparser.xml.XMLEntity;

public class JsonTokener extends Tokener {
	public final static String STOPCHARS = ",:]}/\\\"[{;=# ";
	private boolean allowCRLF=false;

	public boolean isAllowCRLF() {
		return allowCRLF;
	}

	public JsonTokener withAllowCRLF(boolean allowCRLF) {
		this.allowCRLF = allowCRLF;
		return this;
	}
	
	@Override
	public Object nextValue(BaseItem creator, boolean allowQuote) {
		char c = nextStartClean();

		switch (c) {
		case '"':
			next();
			return EntityUtil.unQuote(nextString(c, isAllowCRLF(), allowQuote, false, true));
		case '\\':
			// Must be unquote
			next();
			next();
			return nextString('"', isAllowCRLF(), allowQuote, true, true);
		case '{':
			if (creator instanceof FactoryEntity) {
				BaseItem element = ((FactoryEntity)creator).getNewObject();
				if(element instanceof AbstractKeyValueList<?,?>){
					this.parseToEntity((AbstractKeyValueList<?,?>)element);
				}
				
				return element;
			}
		case '[':
			if (creator instanceof FactoryEntity) {
				BaseItem element = ((FactoryEntity)creator).getNewArray();
				if(element instanceof AbstractList<?>){
					this.parseToEntity((AbstractList<?>)element);
				}
				return element;
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

	public JsonObject parseEntity(JsonObject parent, AbstractKeyValueList<?, ?> newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			parent.put(JsonIdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValue() != null
					&& xmlEntity.getValue().length() > 0) {
				parent.put(JsonIdMap.VALUE, xmlEntity.getValue());
			}
			
			for(int i=0;i<xmlEntity.size();i++){
				parseEntityProp(props, xmlEntity.getValue(i), xmlEntity.get(i));
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
	 * @param parent the parent Element
	 * @param newValue the newValue
	 * @return Itself
	 */
	public JsonObject parseToEntity(JsonObject parent, AbstractKeyValueList<?, ?> newValue) {
		if (newValue instanceof XMLEntity) {
			XMLEntity xmlEntity = (XMLEntity) newValue;
			parent.put(JsonIdMap.CLASS, xmlEntity.getTag());
			JsonObject props = new JsonObject();
			if (xmlEntity.getValue() != null
					&& xmlEntity.getValue().length() > 0) {
				parent.put(JsonIdMap.VALUE, xmlEntity.getValue());
			}
			
			for(int i=0;i<xmlEntity.size();i++){
				parseEntityProp(props, xmlEntity.getValue(i), xmlEntity.get(i));
			}
			for (XMLEntity children : xmlEntity.getChildren()) {
				parseEntityProp(props, children, children.getTag());
			}
			parent.put(JsonIdMap.JSON_PROPS, props);
		}
		return parent;
	}

	@Override
	public void parseToEntity(AbstractKeyValueList<?,?> entity) {
		char c;
		String key;
		if (nextStartClean() != '{') {
			if(logger.error(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)){
				throw new RuntimeException("A JsonObject text must begin with '{'");
			}
		}
		next();
		boolean isQuote=true;
		for (;;) {
			c = nextStartClean();
			switch (c) {
			case 0:
				if(logger.error(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)){
					throw new RuntimeException("A JsonObject text must end with '}'");
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
			c = nextStartClean();
			if (c == '=') {
				if (charAt(position()+1) == '>') {
					next();
				}
			} else if (c != ':') {
				if(logger.error(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)){
					throw new RuntimeException("Expected a ':' after a key ["+ getNextString(30) + "]");
				}
				return;
			}
			next();
			entity.withValue(key, nextValue(entity,isQuote));
		}
	}

	@Override
	public void parseToEntity(AbstractList<?> entityList) {
		char c=nextStartClean();
		if (c != '[') {
			if(logger.error(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entityList)){
				throw new RuntimeException("A JSONArray text must start with '['");
			}
			return;
		}
		if ((nextClean()) != ']') {
			for (;;) {
				c=getCurrentChar();
				if (c != ',') {
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
					if(logger.error(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entityList)){
						throw new RuntimeException("Expected a ',' or ']' not '"+getCurrentChar()+"'");
					}
					return;
				}
			}
		}
		next();
	}
}
