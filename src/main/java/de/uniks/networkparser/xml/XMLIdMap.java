package de.uniks.networkparser.xml;

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
import java.util.ArrayList;
import java.util.Collection;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.IdMapDecoder;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.xml.util.XMLEntityCreator;
/**
 * A Simple XMLIdMap for Decoding and Encoding XML Elements.
 *
 * @author Stefan Lindel
 */

public class XMLIdMap extends IdMap implements IdMapDecoder {
	private static final String TOKENSTOPWORDS = " >/<";

	/** The Constant ENDTAG. */
	public static final char ENDTAG = '/';

	/** The Constant ITEMEND. */
	public static final char ITEMEND = '>';

	/** The Constant ITEMSTART. */
	public static final char ITEMSTART = '<';

	public static final char DOUBLEQUOTIONMARK = '"';

	/** The Constant SPACE. */
	public static final char SPACE = ' ';
	/** The Constant EQUALS. */
	public static final char EQUALS = '=';
	
	/** The stopwords. */
	private ArrayList<String> stopwords = new ArrayList<String>();

	public final Filter SimpleFilter = new Filter().withMap(this).withIdFilter(BooleanCondition.value(false));
	
	/**
	 * Instantiates a new XML id map.
	 */
	public XMLIdMap() {
		super();
		init();
	}

	/**
	 * Instantiates a new XML id map.
	 */
	protected void init() {
		with(new XMLEntityCreator());
		this.stopwords.add("?xml");
		this.stopwords.add("!--");
		this.stopwords.add("!DOCTYPE");
	}

	/**
	 * Add new Stopwords to List.
	 *
	 * @param values
	 *			The List for add
	 * @return XMLSimpleIdMap Instance
	 */
	public XMLIdMap withStopwords(String... values) {
		if (values == null) {
			return this;
		}
		for (String value : values) {
			if (value != null) {
				this.stopwords.add(value);
			}
		}
		return this;
	}

	@Override
	public Object decode(BaseItem value) {
		return decode(new XMLTokener().withBuffer(value.toString()), null);
	}

	/**
	 * Decoding Teh XMLTokener with XMLGrammar.
	 *
	 * @param tokener
	 *			The XMLTokener
	 * @param defaultFactory
	 *			The XMLGrammar for Structure
	 * @return teh Model-Instance
	 */
	public Object decode(XMLTokener tokener, SendableEntityCreatorTag defaultFactory) {
		while (!tokener.isEnd()) {
			if (tokener.skipTo(ITEMSTART, false)) {
				parseEntity(defaultFactory, tokener);
				return parse(tokener, defaultFactory);
			}
		}
		return null;
	}

	/**
	 * Read Json Automatic create JsonArray or JsonObject.
	 *
	 * @param value
	 *			Decoding Value
	 * @return the object
	 */
	@Override
	public Object decode(String value) {
//TODO REMOVE		return new XMLEntity().withValue(value);
		XMLTokener tokener = new XMLTokener();
		tokener.withBuffer(value);
		tokener.skipHeader();
		return decode(tokener, null);
	}

	public Object decode(Buffer value) {
		XMLTokener tokener = new XMLTokener();
		tokener.withBuffer(value);
		return decode(tokener, null);
	}

	@Override
	public XMLEntity encode(Object entity) {
//		return encode(value, null);
		return encode(entity, filter.newInstance(null));
	}

	@Override
	public XMLEntity encode(Object entity, Filter filter) {
		XMLEntity xmlEntity = new XMLEntity();
		SendableEntityCreator createrProtoTyp = getCreatorClass(entity);
		if (createrProtoTyp == null) {
			return null;
		}
		if (createrProtoTyp instanceof SendableEntityCreatorTag) {
			SendableEntityCreatorTag xmlCreater = (SendableEntityCreatorTag) createrProtoTyp;
			if (xmlCreater.getTag() != null) {
				xmlEntity.withTag(xmlCreater.getTag());
			} else {
				xmlEntity.withTag(entity.getClass().getName());
			}
		} else {
			xmlEntity.withTag(entity.getClass().getName());
		}
		if (filter.isId(entity, entity.getClass().getName())) {
			xmlEntity.put(ID, getId(entity));
		}
		with(filter, entity);
		String[] properties = createrProtoTyp.getProperties();
		if (properties != null) {
			Object referenceObject = createrProtoTyp.getSendableInstance(true);
			for (String property : properties) {
				Object value = createrProtoTyp.getValue(entity, property);
				if (value != null) {
					Object refValue = createrProtoTyp.getValue(referenceObject,
							property);
					boolean encoding = !value.equals(refValue);
					if (encoding) {
						if (property.charAt(0)==XMLIdMap.ENTITYSPLITTER) {
							parserChild(xmlEntity, property, value);
						} else if (value instanceof Collection<?>) {
							for (Object item : (Collection<?>) value) {
								if(hasObjects(filter, item)) {
									continue;
								}
								xmlEntity.with(encode(item, filter));
							}
						} else {
							SendableEntityCreator valueCreater = getCreatorClass(value);
							if (valueCreater != null) {
								if(hasObjects(filter, value)) {
									continue;
								}
								xmlEntity.with(encode(value));
							} else {
								xmlEntity.put(property, value);
							}
						}
					}
				}
			}
		}
		return xmlEntity;
	}
	
	/**
	 * Parser child.
	 *
	 * @param parent
	 *			the parent
	 * @param property
	 *			the property
	 * @param value
	 *			the value
	 * @return the xML entity
	 */
	private EntityList parserChild(EntityList parent, String property,
			Object value) {
		if (property.charAt(0)== XMLIdMap.ENTITYSPLITTER) {
			if(property.length() == 1) {
				// Its ChildValue
				if(parent instanceof XMLEntity) {
					//FIXME creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), NEW);
					((XMLEntity)parent).withValueItem(EntityUtil.valueToString(value, true, parent));
				}
			} else {
				int pos = property.indexOf(XMLIdMap.ENTITYSPLITTER, 1);
				String label;
				String newProp = "";
				if (pos > 0) {
					label = property.substring(1, pos);
					newProp = property.substring(pos);
				} else {
					label = property.substring(1);
				}
				if (label.length() > 0 && parent instanceof XMLEntity) {
					XMLEntity entry = (XMLEntity) parent;
					EntityList child = entry.getChild(label, false);
					if(newProp.length() == 1 || newProp.lastIndexOf(ENTITYSPLITTER) >= 0) {
						if (child == null) {
							XMLEntity item = new XMLEntity();
							item.withTag(label);
							child = item;
							parserChild(child, newProp, value);
							entry.with(item);
						} else {
							parserChild(child, newProp, value);
						}
						return child;
					}
					if(parent instanceof Entity) {
						((Entity)parent).put(label, EntityUtil.valueToString(value, true, parent));
					}
					return entry;
				}
			}
		}
		return null;
	}


	/**
	 * Find tag.
	 *
	 * @param tokener
	 *			the tokener
	 * @param defaultFactory
	 *			the defaultFactory
	 * @return the object
	 */
	protected Object parse(XMLTokener tokener, SendableEntityCreatorTag defaultFactory) {
		parseAttribute(tokener);
		return parseChildren(tokener, defaultFactory);
	}

	protected void parseAttribute(XMLTokener tokener) {
		Object entity = tokener.getCurrentItem();
		SendableEntityCreatorTag creator = tokener.getCurrentCreator();
		if (entity != null) {
			// Parsing attributes
			CharacterBuffer token = new CharacterBuffer();
			char myChar;
			do{
				if (tokener.getCurrentChar() == SPACE) {
					tokener.getChar();
				}
				tokener.nextString(token, true, false, SPACE, EQUALS, ITEMEND, ENDTAG);
				myChar = tokener.getCurrentChar();
				if (myChar == '=') {
					String key = token.toString();
					token.reset();
					tokener.skip(2);
					tokener.nextString(token, true, false, DOUBLEQUOTIONMARK);
					String value = token.toString();
					token.reset();
					tokener.skip();
					creator.setValue(entity, key, value, NEW);
					tokener.setValue(key, value);
					myChar = tokener.getCurrentChar();
				}
			}while(myChar != ITEMEND && myChar != 0 && myChar != ENDTAG);
		}
	}

	protected Object parseChildren(XMLTokener tokener, SendableEntityCreatorTag defaultFactory) {
		Object entity = tokener.getCurrentItem();
		SendableEntityCreatorTag creator = tokener.getCurrentCreator();
		// Parsing next Element
		if (tokener.skipTo("/>", false, false)) {
			if (tokener.getCurrentChar() == '/') {
				tokener.popStack();
				tokener.getChar();
				tokener.nextToken(TOKENSTOPWORDS);
				return entity;
			}

 			char quote = (char) ITEMSTART;
			// Skip >
			tokener.skip();
			CharacterBuffer valueItem = new CharacterBuffer();
			tokener.nextString(valueItem, false, false, quote);
			if (valueItem.isEmptyCharacter() == false) {
				CharacterBuffer test = new CharacterBuffer();
				while (tokener.isEnd() == false ) {
					if (tokener.getCurrentChar() == ITEMSTART) {
						test.with(tokener.getCurrentChar());
						test.with(tokener.getChar());
					}
					if (tokener.getCurrentChar() == ENDTAG) {
						CharacterBuffer endTag = tokener.nextToken(XMLTokener.TOKEN);
						if(tokener.getCurrentTag().equals(endTag.toString())){
							break;
						}else {
							valueItem.with(test);
							valueItem.with(endTag);
							valueItem.with(tokener.getCurrentChar());
						}
					}else if(test.length() > 0){
						valueItem.with(test);
					}else {
						char currentChar = tokener.getChar();
						if( currentChar  != ITEMSTART) {
							valueItem.with(currentChar);
						}
					}
					test.reset();
				}
				creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), NEW);
				tokener.setValue(""+ENTITYSPLITTER, valueItem.toString());
				tokener.popStack();
				tokener.skipEntity();
				return entity;
			}
			if (tokener.getCurrentChar() == ITEMSTART) {
				// show next Tag
				Object child;
				do {
					valueItem = parseEntity(defaultFactory, tokener);
					if(valueItem == null) {
						if(tokener.getCurrentChar()==ENDTAG) {
							// Show if Item is End
							valueItem = tokener.nextToken(""+ITEMEND);
							if(valueItem.equals(tokener.getCurrentTag())) {
								tokener.popStack();
								// SKip > EndTag
								tokener.skip();
							}
						}
						return entity;
					}
					if (valueItem.isEmpty() == false) {
						creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), NEW);
						tokener.setValue(""+ENTITYSPLITTER, valueItem.toString());
						tokener.popStack();
						tokener.skipEntity();
						return entity;
					}
					String childTag = tokener.getCurrentTag();
					child = parse(tokener, defaultFactory);
					if (child != null ) {
						creator.setValue(entity, childTag, child, CHILDREN);
					}
				} while (child != null);
			}
		}
		return entity;
	}

	/**
	 * Gets the entity.
	 *
	 * @param defaultCreator
	 *			the DefaultCreator
	 * @param tokener
	 *			the tokener
	 * @return the entity
	 */
	protected CharacterBuffer parseEntity(SendableEntityCreatorTag defaultCreator, XMLTokener tokener) {
		CharacterBuffer valueItem = new CharacterBuffer();
		CharacterBuffer tag;
		boolean isEmpty = true;
		do {
			if (tokener.getCurrentChar() != ITEMSTART) {
				tokener.nextString(valueItem, false, false, ITEMSTART);
				if (valueItem.isEmpty() == false) {
					valueItem.trim();
					isEmpty = valueItem.isEmpty();
				}
			}
			tag = tokener.nextToken(TOKENSTOPWORDS);
			if (tag != null) {
				for (String stopword : this.stopwords) {
					if (tag.startsWith(stopword, 0, false)) {
						tokener.skipTo(XMLIdMap.ITEMEND, false);
						tokener.skipTo(XMLIdMap.ITEMSTART, false);
						tag = null;
						break;
					}
				}
			}
		} while (tag == null);
		if(tag.length()<1) {
			return null;
		}
		if (tag.isEmpty() && isEmpty) {
			valueItem.reset();
		}
		SendableEntityCreator item = getCreator(tag.toString(), false);
		if (item != null && item instanceof SendableEntityCreatorTag) {
			addToStack((SendableEntityCreatorTag) item, tokener, tag, valueItem);
			return valueItem;
		}
		String startTag;
		if(tag.lastIndexOf(ENTITYSPLITTER)>=0) {
			startTag = tag.substring(0, tag.lastIndexOf(ENTITYSPLITTER));
		} else {
			startTag = tag.toString();
		}
		SimpleKeyValueList<String, SendableEntityCreatorTag> filter=new SimpleKeyValueList<String, SendableEntityCreatorTag>();
		for(int i=0;i<this.creators.size();i++) {
			String key = this.creators.getKeyByIndex(i);
			SendableEntityCreator value = this.creators.getValueByIndex(i);
			if (key.startsWith(startTag) && value instanceof SendableEntityCreatorTag) {
				filter.put(key, (SendableEntityCreatorTag) value);
			}
		}
		if(defaultCreator == null) { 
			defaultCreator = new XMLEntityCreator();
		}

		SendableEntityCreatorTag creator = defaultCreator;

		if(filter.size() < 1) {
			addToStack(creator, tokener, tag, valueItem);
			return valueItem;
		}
		while(filter.size()>0) {
			addToStack(creator, tokener, tag, valueItem);
			parseAttribute(tokener);
			if(tokener.getCurrentChar()=='/') {
				tokener.popStack();
			}else {
				tokener.skip();
				if (tokener.getCurrentChar() != ITEMSTART) {
					tokener.nextString(valueItem, false, false, ITEMSTART);
					if (valueItem.isEmpty() == false) {
						valueItem.trim();
						Object entity = tokener.getCurrentItem();
						creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), NEW);
					}
				}
				tag = tokener.nextToken(TOKENSTOPWORDS);
				item = getCreator(tag.toString(), false);
				if(item instanceof SendableEntityCreatorTag) {
					creator = (SendableEntityCreatorTag) item;
				}else{
					creator = defaultCreator;
				}
				
				startTag = startTag + ENTITYSPLITTER + tag.toString();
				for(int i=filter.size() - 1;i >= 0;i++) {
					String key = filter.getKeyByIndex(i);
					if(key.equals(startTag)) {
						// FOUND THE Item
						creator = filter.getValueByIndex(i);
						addToStack(creator, tokener, tag, valueItem);
						return valueItem;
					}
					if(key.startsWith(startTag) == false) {
						filter.removePos(i);
					}
				}
				addToStack(creator, tokener, tag, valueItem);
			}
		} 
		return valueItem;
	}
	
	protected Object addToStack(SendableEntityCreatorTag creator, XMLTokener tokener, CharacterBuffer tag, CharacterBuffer value) {
		Object entity = creator.getSendableInstance(false);
		if(entity instanceof EntityList) {
			creator.setValue(entity, XMLEntity.PROPERTY_VALUE, value.toString(), NEW);
			creator.setValue(entity, XMLEntity.PROPERTY_TAG, tag.toString(), NEW);
		}
		if(value.isEmpty() == false) {
			tokener.setValue(".", value.toString());
		}
		tokener.withStack(tag.toString(), entity, creator);
		return entity;
	}
}
