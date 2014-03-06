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
import java.util.HashSet;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.IdMapEncoder;
import de.uniks.networkparser.ReferenceObject;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.BaseEntity;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorXML;
import de.uniks.networkparser.xml.creator.XMLGrammar;
import de.uniks.networkparser.xml.creator.XSDEntityCreator;

public class XMLSimpleIdMap extends IdMap {
	/** The Constant ENDTAG. */
	public static final char ENDTAG = '/';

	/** The Constant ITEMEND. */
	public static final char ITEMEND = '>';

	/** The Constant ITEMSTART. */
	public static final char ITEMSTART = '<';

	/** The Constant SPACE. */
	public static final char SPACE = ' ';

	/** The stack. */
	protected ArrayList<ReferenceObject> stack = new ArrayList<ReferenceObject>();

	/** The stopwords. */
	protected HashSet<String> stopwords = new HashSet<String>();

	/** The value. */
	protected Tokener value;

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

	
	@Override
	public Object decode(BaseEntity value) {
		return decode((XMLTokener) new XMLTokener().withText(value.toString()), null);
	}
	
	public Object decode(XMLTokener entity, XMLGrammar factory) {
		this.value = entity;
		if(factory==null){
			factory = new XSDEntityCreator();
		}

		this.stack.clear();
		while (!this.value.isEnd()) {
			if (this.value.stepPos("" + ITEMSTART, false, false)) {
				XMLEntity item = getEntity(factory);
				if (item != null) {
					return parse(item, factory, "");
				}
			}
		}
		return null;
	}
	
	/**
	 * Read Json Automatic create JsonArray or JsonObject
	 * @return the object
	 */
	public Object decode(String value){
		if(value.startsWith("<")){
			return decode(getPrototyp().getNewArray().withValue(value));
		}
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
				xmlEntity.setTag(xmlCreater.getTag());
			} else {
				xmlEntity.setTag(entity.getClass().getName());
			}
		} else {
			xmlEntity.setTag(entity.getClass().getName());
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
								xmlEntity.addChild( encode(item) );
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
	 * @param prefix
	 *            the prefix
	 * @param tag
	 *            the tag
	 * @param styleFormat
	 * @param styleFormatCreator
	 * @return the object
	 */
	protected Object parse(XMLEntity entity, XMLGrammar styleFormatCreator, String prefix) {
		if (entity != null) {
			// Parsing attributes
			char myChar = this.value.getCurrentChar();
			while (myChar != ITEMEND) {
				if (myChar == SPACE) {
					value.next();
				}
				int start = this.value.position();
				if (this.value.stepPos("=>", false, false)) {
					myChar = this.value.getCurrentChar();
					if (myChar == '=') {
						String key = this.value.substring(start, -1);
						this.value.skip(2);
						start = this.value.position();
						if (this.value.stepPos("\"", false, true)) {
							String value = this.value.substring(start, -1);
							this.value.next();
							styleFormatCreator.setValue(entity, key, value,
									IdMapEncoder.NEW);
						}
					}
				} else {
					break;
				}
			}

			// Add to StackTrace
			this.stack.add(new ReferenceObject().withProperty(entity.getTag()).withEntity(entity));

			// Parsing next Element
			if (value.stepPos("/>", false, false)) {
				if (value.getCurrentChar() == '/') {
					stack.remove(stack.size() - 1);
					value.next();
					String tag = value.getNextTag();
					styleFormatCreator.endChild(tag);
					// skipEntity();
					return entity;
				}

				char quote = (char) ITEMSTART;
				// Skip >
				value.next();
				String strvalue = value.nextString(quote, true);

				// BACK TO <
				value.back();
				strvalue = strvalue.trim();
				XMLEntity newTag;
				if (this.value.getCurrentChar() == ITEMSTART) {
					// show next Tag
					Object child;
					do {
						boolean saveValue = true;
						do {
							newTag = getEntity(styleFormatCreator);
							if (newTag == null) {
								entity.setValue(strvalue);
								stack.remove(stack.size() - 1);
								skipEntity();
								return entity;
							}
							if (newTag.getTag().isEmpty()) {
								if (saveValue) {
									entity.setValue(newTag.getValue());
								}
								skipEntity();
								newTag = getEntity(styleFormatCreator);
								if (newTag == null) {
									stack.remove(stack.size() - 1);
									skipEntity();
								}
								return entity;
							}
							if (styleFormatCreator.parseChild(entity, newTag,
									value)) {
								// Skip >
								saveValue = false;
								value.next();
							} else {
								break;
							}
						} while (newTag != null);
						child = parse(newTag, styleFormatCreator, "");
						if (child != null && child instanceof XMLEntity) {
							styleFormatCreator.addChildren(entity, (XMLEntity)child);
						}
					} while (child != null);
				}
			}
		}
		return entity;
	}

	protected void skipEntity() {
		value.stepPos(">", false, false);
		// Skip >
		value.next();
	}

	/**
	 * Gets the entity.
	 * 
	 * @param start
	 *            the start
	 * @return the entity
	 */
	protected XMLEntity getEntity(XMLGrammar factory) {
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
			if (value.getCurrentChar() != ITEMSTART) {
				String strValue = value.nextString(ITEMSTART, true);
				if (strValue != null) {
					value.back();
					strValue = strValue.trim();
					isEmpty = strValue.isEmpty();
				}
				entity.setValue(strValue);
			}
			tag = this.value.getNextTag();
			if (tag != null) {
				for (String stopword : this.stopwords) {
					if (tag.startsWith(stopword)) {
						this.value.stepPos(">", false, false);
						this.value.stepPos("<", false, false);
						tag = null;
						break;
					}
				}
			}
		} while (tag == null);
		if (tag.isEmpty() && isEmpty) {
			return null;
		}
		entity.setTag(tag);
		return entity;
	}
	
	@Override
	public XMLEntity getPrototyp() {
		return new XMLEntity();
	}
}
