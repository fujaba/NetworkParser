package de.uniks.networkparser.xml;

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
import java.util.ArrayList;
import java.util.Collection;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.ReferenceObject;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.xml.util.XMLGrammar;
import de.uniks.networkparser.xml.util.XSDEntityCreator;

/**
 * A Simple XMLIdMap for Decoding and Encoding XML Elements.
 * @author Stefan Lindel
 */
public class XMLSimpleIdMap extends IdMap {
	/** The Constant ENDTAG. */
	public static final char ENDTAG = '/';

	/** The Constant ITEMEND. */
	public static final char ITEMEND = '>';

	/** The Constant ITEMSTART. */
	public static final char ITEMSTART = '<';

	/** The Constant SPACE. */
	public static final char SPACE = ' ';

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
	 * @param values The List for add
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
		return decode((XMLTokener) new XMLTokener().withText(value.toString()), null);
	}

	/**
	 * Decoding Teh XMLTokener with XMLGrammar.
	 * @param tokener The XMLTokener
	 * @param factory The XMLGrammar for Structure
	 * @return teh Model-Instance
	 */
	public Object decode(XMLTokener tokener, XMLGrammar factory) {
		if (factory == null) {
			factory = new XSDEntityCreator();
		}
		while (!tokener.isEnd()) {
			if (tokener.stepPos("" + ITEMSTART, false, false)) {
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
	 * @param value Decoding Value
	 * @return the object
	 */
	@Override
	public Object decode(String value) {
		return decode(getPrototyp().withValue(value));
	}

	@Override
	public XMLEntity encode(Object value) {
		return encode(value,  filter.cloneObj());
	}

	@Override
	public XMLEntity encode(Object entity, Filter filter) {
		XMLEntity xmlEntity = new XMLEntity();
		SendableEntityCreator createrProtoTyp = getCreatorClass(entity);
		if (createrProtoTyp == null) {
			return null;
		}
		if (createrProtoTyp instanceof SendableEntityCreatorXML) {
			SendableEntityCreatorXML xmlCreater = (SendableEntityCreatorXML) createrProtoTyp;
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
						Object refValue = createrProtoTyp.getValue(
								referenceObject, property);
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
	 * @param entity The Entity
	 *
	 * @param tokener
	 *            the tokener
	 * @param grammar
	 *            the grammar
	 * @return the object
	 */
	protected Object parse(XMLEntity entity, XMLTokener tokener, XMLGrammar grammar) {
		if (entity != null) {
			// Parsing attributes
			char myChar = tokener.getCurrentChar();
			while (myChar != ITEMEND) {
				if (myChar == SPACE) {
					tokener.next();
				}
				int start = tokener.position();
				if (tokener.stepPos("=>", false, false)) {
					myChar = tokener.getCurrentChar();
					if (myChar == '=') {
						String key = tokener.substring(start, -1);
						tokener.skip(2);
						start = tokener.position();
						if (tokener.stepPos("\"", false, true)) {
							String value = tokener.substring(start, -1);
							tokener.next();
							grammar.setValue(entity, key, value,
									IdMapEncoder.NEW);
						}
					}
				} else {
					break;
				}
			}

			// Add to StackTrace
			tokener.withStack(new ReferenceObject().withProperty(entity.getTag()).withEntity(entity));

			// Parsing next Element
			if (tokener.stepPos("/>", false, false)) {
				if (tokener.getCurrentChar() == '/') {
					tokener.popStack();
					tokener.next();
					String tag = tokener.getNextTag();
					grammar.endChild(tag);
					// skipEntity();
					return entity;
				}

				char quote = (char) ITEMSTART;
				// Skip >
				tokener.next();
				String strvalue = tokener.nextString(quote, true, false, false, false);
				strvalue = strvalue.trim();
				XMLEntity newTag;
				if (tokener.getCurrentChar() == ITEMSTART) {
					// show next Tag
					Object child;
					do {
						boolean saveValue = true;
						do {
							newTag = getEntity(grammar, tokener);
							if (newTag == null) {
								entity.withValueItem(strvalue);
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
							if (grammar.parseChild(entity, newTag,
									tokener)) {
								// Skip >
								saveValue = false;
								tokener.next();
							} else {
								break;
							}
						} while (true);
						child = parse(newTag, tokener.withPrefix(""), grammar);
						if (child != null && child instanceof XMLEntity) {
							grammar.addChildren(entity, (XMLEntity) child);
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
	 *            the grammar
	 * @param tokener
	 *            the tokener
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
		String tag = null;
		boolean isEmpty = true;
		do {
			if (tokener.getCurrentChar() != ITEMSTART) {
				String strValue = tokener.nextString(ITEMSTART, true, false, false, false);
				if (strValue != null) {
					strValue = strValue.trim();
					isEmpty = strValue.isEmpty();
				}
				entity.withValueItem(strValue);
			}
			tag = tokener.getNextTag();
			if (tag != null) {
				for (String stopword : this.stopwords) {
					if (tag.startsWith(stopword)) {
						tokener.stepPos(">", false, false);
						tokener.stepPos("<", false, false);
						tag = null;
						break;
					}
				}
			}
		} while (tag == null);
		if (tag.isEmpty() && isEmpty) {
			return null;
		}
		entity.withTag(tag);
		return entity;
	}

	@Override
	public XMLEntity getPrototyp() {
		return new XMLEntity();
	}
}
