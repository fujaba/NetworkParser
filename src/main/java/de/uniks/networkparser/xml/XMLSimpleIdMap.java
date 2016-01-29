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

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.IdMapDecoder;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.xml.util.XMLGrammar;
import de.uniks.networkparser.xml.util.XSDEntityCreator;
/**
 * A Simple XMLIdMap for Decoding and Encoding XML Elements.
 *
 * @author Stefan Lindel
 */

public class XMLSimpleIdMap extends IdMap implements IdMapDecoder {
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
	public static final char EQUALS = '=';

	/** The stopwords. */
	private ArrayList<String> stopwords = new ArrayList<String>();

	/**
	 * Instantiates a new XML id map.
	 */
	public XMLSimpleIdMap() {
		super();
		init();
	}

	/**
	 * Instantiates a new XML id map.
	 */
	protected void init() {
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
	public XMLSimpleIdMap withStopwords(String... values) {
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
	 * @param factory
	 *			The XMLGrammar for Structure
	 * @return teh Model-Instance
	 */
	public Object decode(XMLTokener tokener, XMLGrammar factory) {
		if (factory == null) {
			factory = new XSDEntityCreator();
		}
		while (!tokener.isEnd()) {
			if (tokener.skipTo(ITEMSTART, false)) {
				XMLEntity item = getEntity(factory, tokener);
				if (item != null) {
					return parse(item, tokener, factory);
				}
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
		return new XMLEntity().withValue(value);
	}

	@Override
	public XMLEntity encode(Object value) {
		return encode(value, null);
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
						if (value instanceof Collection<?>) {
							for (Object item : (Collection<?>) value) {
								xmlEntity.addChild(encode(item));
							}
						} else {
							SendableEntityCreator valueCreater = getCreatorClass(value);
							if (valueCreater != null) {
								xmlEntity.addChild(encode(value));
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
	 * Find tag.
	 *
	 * @param entity
	 *			The Entity
	 *
	 * @param tokener
	 *			the tokener
	 * @param grammar
	 *			the grammar
	 * @return the object
	 */
	protected Object parse(XMLEntity entity, XMLTokener tokener,
			XMLGrammar grammar) {
		if (entity != null) {
			// Parsing attributes
			CharacterBuffer token = new CharacterBuffer();
			char myChar;
			do{
				if (tokener.getCurrentChar() == SPACE) {
					tokener.getChar();
				}
				tokener.nextString(token, true, false, SPACE, EQUALS, ITEMEND);
				myChar = tokener.getCurrentChar();
				if (myChar == '=') {
					String key = token.toString();
					token.reset();
					tokener.skip(2);
					tokener.nextString(token, true, false, DOUBLEQUOTIONMARK);
					String value = token.toString();
					token.reset();
					tokener.skip();
					grammar.setValue(entity, key, value, IdMap.NEW);
				}
				myChar = tokener.getCurrentChar();
			}while(myChar != ITEMEND && myChar != 0 && myChar != ENDTAG);

			// Add to StackTrace
			tokener.withStack(entity);

			// Parsing next Element
			if (tokener.skipTo("/>", false, false)) {
				if (tokener.getCurrentChar() == '/') {
					tokener.popStack();
					tokener.getChar();
					CharacterBuffer tag = tokener.nextToken(TOKENSTOPWORDS);
					grammar.endChild(tag.toString());
					// skipEntity();
					return entity;
				}

				char quote = (char) ITEMSTART;
				// Skip >
				tokener.skip();
				CharacterBuffer sc=new CharacterBuffer();
				tokener.nextString(sc, false, false, quote);
				sc.trim();
				XMLEntity newTag;
				if (tokener.getCurrentChar() == ITEMSTART) {
					// show next Tag
					Object child;
					do {
						boolean saveValue = true;
						do {
							newTag = getEntity(grammar, tokener);
							if (newTag == null) {
								entity.withValueItem(sc.toString());
								tokener.popStack();
								tokener.skipEntity();
								return entity;
							}
							if (newTag.getTag().isEmpty()) {
								if (saveValue) {
									entity.withValueItem(newTag.getValueItem());
								}
								tokener.skipEntity();
								newTag = getEntity(grammar, tokener);
								if (newTag == null) {
									tokener.popStack();
									tokener.skipEntity();
								}
								return entity;
							}
							if (grammar.parseChild(entity, newTag, tokener)) {
								// Skip >
								saveValue = false;
								tokener.skip();
							} else {
								break;
							}
						} while (true);
						child = parse(newTag, tokener.withPrefix(""), grammar);
						if (child != null && child instanceof XMLEntity) {
							grammar.addChildren(this, entity, (XMLEntity) child);
						}
					} while (child != null);
				}
			}
		}
		return entity;
	}

	/**
	 * Gets the entity.
	 *
	 * @param factory
	 *			the grammar
	 * @param tokener
	 *			the tokener
	 * @return the entity
	 */
	protected XMLEntity getEntity(XMLGrammar factory, XMLTokener tokener) {
		XMLEntity entity;
		if (factory != null) {
			Object newObj = factory.getSendableInstance(false);
			if (newObj instanceof XMLEntity) {
				entity = (XMLEntity) newObj;
			} else {
				entity = new XMLEntity();
			}
		} else {
			entity = new XMLEntity();
		}

		CharacterBuffer tag = null;
		boolean isEmpty = true;
		do {
			if (tokener.getCurrentChar() != ITEMSTART) {
				CharacterBuffer sc = new CharacterBuffer();
				tokener.nextString(sc, false, false, ITEMSTART);
				if (sc.isEmpty() == false) {
					sc.trim();
					isEmpty = sc.isEmpty();
				}
				entity.withValueItem(sc.toString());
			}
			tag = tokener.nextToken(TOKENSTOPWORDS);
			if (tag != null) {
				for (String stopword : this.stopwords) {
					if (tag.startsWith(stopword, 0, false)) {
						tokener.skipTo(XMLSimpleIdMap.ITEMEND, false);
						tokener.skipTo(XMLSimpleIdMap.ITEMSTART, false);
						tag = null;
						break;
					}
				}
			}
		} while (tag == null);
		if (tag.isEmpty() && isEmpty) {
			return null;
		}
		entity.withTag(tag.toString());
		return entity;
	}
}
