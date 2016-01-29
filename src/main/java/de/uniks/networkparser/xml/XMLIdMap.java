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
import java.util.HashMap;
import java.util.Iterator;
import de.uniks.networkparser.AbstractMap;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.xml.util.XMLGrammar;
/**
 * The Class XMLIdMap.
 */

public class XMLIdMap extends XMLSimpleIdMap {
	/** The Constant ENTITYSPLITTER. */
	public static final String ENTITYSPLITTER = "&";

	/** The Constant ATTRIBUTEVALUE. */
	public static final String ATTRIBUTEVALUE = "?";

	/** The decoder map. */
	private HashMap<String, SendableEntityCreatorTag> decoderMap;

	/**
	 * Inits the.
	 */
	@Override
	protected void init() {
		super.init();
		getCounter();
		this.filter.withIdFilter(BooleanCondition.value(false));
	}

	/**
	 * @param createrClass
	 *			new Creator
	 * @return Boolean for add the new Creator
	 */
	public boolean addCreator(SendableEntityCreator createrClass) {
		if (createrClass instanceof SendableEntityCreatorTag) {
			if (this.decoderMap != null) {
				if (this.decoderMap
						.containsKey(((SendableEntityCreatorTag) createrClass)
								.getTag())) {
					return false;
				}
			}
		} else {
			return false;
		}
		super.with(createrClass);
		return true;
	}

	@Override
	public AbstractMap with(String className,
			SendableEntityCreator createrClass) {
		super.with(className, createrClass);

		if (createrClass instanceof SendableEntityCreatorTag) {
			SendableEntityCreatorTag xmlCreator = (SendableEntityCreatorTag) createrClass;
			if (this.decoderMap == null) {
				this.decoderMap = new HashMap<String, SendableEntityCreatorTag>();
			}
			this.decoderMap.put(xmlCreator.getTag(), xmlCreator);
		}
		return this;
	}

	/**
	 * Gets the creator decode class.
	 *
	 * @param tag
	 *			the tag
	 * @return the creator decode class
	 */
	public SendableEntityCreatorTag getCreatorDecodeClass(String tag) {
		if (this.decoderMap == null) {
			return null;
		}
		return this.decoderMap.get(tag);
	}

	/**
	 * Encode.
	 *
	 * @param entity
	 *			the entity
	 * @return the xML entity
	 */
	@Override
	public XMLEntity encode(Object entity) {
		return encode(entity, filter.newInstance(null));
	}

	@Override
	public XMLEntity encode(Object entity, Filter filter) {
		SendableEntityCreator createrProtoTyp = getCreatorClass(entity);
		if (createrProtoTyp == null) {
			return null;
		}
		XMLEntity xmlEntity = new XMLEntity();

//		SendableEntityCreatorHTML
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
						if (property.startsWith(XMLIdMap.ENTITYSPLITTER)) {
							parserChild(xmlEntity, property, value);
						} else {
							if (value instanceof Collection<?>) {
								for (Object item : (Collection<?>) value) {
									if(hasObjects(filter, item)) {
										continue;
									}
									xmlEntity.addChild(encode(item, filter));
								}

							} else {
								SendableEntityCreator valueCreater = getCreatorClass(value);
								if (valueCreater != null) {
									if(hasObjects(filter, value)) {
										continue;
									}
									xmlEntity.addChild(encode(value, filter));
								} else {
									xmlEntity.put(property, value);
								}
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
	private XMLEntity parserChild(XMLEntity parent, String property,
			Object value) {

		if (property.startsWith(XMLIdMap.ENTITYSPLITTER)) {
			int pos = property.indexOf(XMLIdMap.ENTITYSPLITTER, 1);
			if (pos < 0) {
				pos = property.indexOf(XMLIdMap.ATTRIBUTEVALUE, 1);
			}
			String label;
			String newProp = "";
			if (pos > 0) {
				label = property.substring(1, pos);
				newProp = property.substring(pos);
			} else {
				label = property.substring(1);
			}
			if (label.length() > 0) {
				XMLEntity child = parent.getChild(label);
				if (child == null) {
					child = new XMLEntity();
					child.withTag(label);
					parserChild(child, newProp, value);
					parent.addChild(child);
				} else {
					parserChild(child, newProp, value);
				}
				return child;
			}
		} else if (property.startsWith(XMLIdMap.ATTRIBUTEVALUE)) {
			parent.put(property.substring(1),
					EntityUtil.valueToString(value, true, parent));
		} else if ("".equals(property)) {
			parent.withValueItem(EntityUtil.valueToString(value, true, parent));
		}
		return null;
	}

	@Override
	public Object decode(String value) {
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

	/**
	 * Step empty pos.
	 *
	 * @param newPrefix
	 *			the new prefix
	 * @param entity
	 *			the entity
	 * @param tag
	 *			the tag
	 * @param tokener
	 *			the tokener
	 * @return true, if successful
	 */
	public boolean stepEmptyPos(String newPrefix, Object entity, String tag,
			XMLTokener tokener) {
		boolean exit = false;
		boolean empty = true;

		if (!newPrefix.endsWith("&")) {
			return tokener.skipTo(ITEMSTART, false);
		}
		CharacterBuffer buffer = new CharacterBuffer();
		if (tokener.getCurrentChar() != ITEMSTART) {
			tokener.skip();
		}
		buffer.with(tokener.getCurrentChar());
		ArrayList<String> stack = new ArrayList<String>();
		while (tokener.isEnd() == false && exit == false) {
			if (tokener.checkValues('\t', '\r', '\n', ' ', ITEMSTART) == false) {
				empty = false;
			}
			if (tokener.getCurrentChar() == ITEMSTART) {
				if (empty) {
					exit = true;
					break;
				}
				CharacterBuffer nextTag = tokener.nextToken(XMLTokener.TOKEN);
				boolean addAndRemove=false;
				if (nextTag.length() > 0) {
					String newTag = nextTag.toString();
					stack.add(newTag);
					buffer.with(newTag);
					if(tokener.getCurrentChar() != ITEMSTART) {
						buffer.with(tokener.getCurrentChar());
					}
					if(tokener.getCurrentChar() != ENDTAG) {
						continue;
					}
					if(tokener.getCurrentChar() != ITEMEND) {
						addAndRemove = true;
					}
				}
				if (tokener.getCurrentChar() == ENDTAG) {
					if (stack.size() > 0) {
						CharacterBuffer endTag = tokener.nextToken(XMLTokener.TOKEN);
						if (endTag.equals(stack.get(stack.size() - 1))) {
							stack.remove(stack.size() - 1);
							buffer.with(ENDTAG);
							buffer.with(endTag);
							buffer.with(tokener.getCurrentChar());
						} else {
							stack.remove(stack.size() - 1);
							if(tokener.getCurrentChar()==ITEMEND && stack.size() < 1 && addAndRemove == false){
								exit=true;
								break;
							}
							buffer.with(tokener.getCurrentChar());
							continue;
						}

					} else {
						exit = true;
						buffer.withLen(buffer.length() - 1);
						break;
					}
				}
			}
			if (!exit) {
				buffer.with(tokener.getChar());
			}
		}
		if (!empty && exit) {
			String value = buffer.toString();
			Object refObject = null;
			if (newPrefix.endsWith("&")) {
				refObject = tokener.popStack();
			}
			if (refObject != null) {
				if (newPrefix.length() > 1) {
					newPrefix = newPrefix.substring(0, newPrefix.length() - 1);
				}
				SendableEntityCreator parentCreator = this.getCreatorClass(refObject);
				parentCreator.setValue(refObject, newPrefix, value,
						IdMap.NEW);
			}
		}
		return exit;
	}

	/**
	 * Find tag.
	 *
	 * @param entity
	 *			The Entity
	 * @param tokener
	 *			the Tokener
	 * @param grammar
	 *			the grammar of XML.
	 * @return the object
	 */
	@Override
	protected Object parse(XMLEntity entity, XMLTokener tokener,
			XMLGrammar grammar) {
		String tag = entity.getTag();
		if (tag.length() < 1) {
			return null;
		}
		SendableEntityCreatorTag entityCreater = getCreatorDecodeClass(tag);
		if (entityCreater != null || tokener.getStackSize() > 0) {
			return parseIdEntity(entity, grammar, tokener, entityCreater);
		}
		// Must be a Child of Root
		ArrayList<SendableEntityCreatorTag> filter = new ArrayList<SendableEntityCreatorTag>();
		for (Iterator<SendableEntityCreator> i = iterator(); i.hasNext();) {
			SendableEntityCreator creator = i.next();
			if (creator instanceof SendableEntityCreatorTag) {
				SendableEntityCreatorTag xmlCreator = (SendableEntityCreatorTag) creator;
				if (xmlCreator.getTag().startsWith(tag)) {
					filter.add(xmlCreator);
				}
			}
		}
		while (filter.size() > 1) {
			while (!tokener.isEnd()) {
				if (tokener.skipTo(ITEMSTART, false)) {
					XMLEntity item = getEntity(grammar, tokener);
					if (item != null) {
						tag += XMLIdMap.ENTITYSPLITTER + item.getTag();
						for (Iterator<SendableEntityCreatorTag> i = filter
								.iterator(); i.hasNext();) {
							if (!i.next().getTag().startsWith(tag)) {
								i.remove();
							}
						}
					}
				}
			}
		}
		if (filter.size() == 1) {
			return parseIdEntity(entity, grammar, tokener.withPrefix(""),
					filter.get(0));
		}
		return null;
	}

	/**
	 * Parse a Element with IdCreater.
	 *
	 * @param entity
	 *			The Entity
	 * @param grammar
	 *			The Grammar of XML
	 * @param tokener
	 *			The XML-Tokener
	 * @param creator
	 *			The Entity-Factory
	 * @return the Object
	 */
	protected Object parseIdEntity(XMLEntity entity, XMLGrammar grammar,
			XMLTokener tokener, SendableEntityCreatorTag creator) {
		boolean plainvalue = false;
		Object item = null;
		String newPrefix = "";
		String tag = entity.getTag();
		if (creator == null) {
			if (tokener.getStackSize() == 0) {
				return null;
			}
			// Not found child creater
			Object refObject = tokener.getStackLast(0);
			creator = (SendableEntityCreatorTag) this.getCreatorClass(refObject);
			String[] properties = creator.getProperties();
			tokener.addPrefix(tag);
			if (isCaseSensitive()) {
				for (String prop : properties) {
					if (prop.equalsIgnoreCase(tokener.getPrefix())) {
						// It is a Attribute
						item = refObject;
						plainvalue = true;
						break;
					} else if (prop.startsWith(tokener.getPrefix())) {
						// it is a Child
						item = refObject;
						break;
					}
				}
			} else {
				for (String prop : properties) {
					if (prop.equalsIgnoreCase(tokener.getPrefix())) {
						// It is a Attribute
						item = refObject;
						plainvalue = true;
						break;
					} else if (prop.toLowerCase().startsWith(
							tokener.getPrefix().toLowerCase())) {
						// it is a Child
						item = refObject;
						break;
					}
				}
			}

			if (item != null && !plainvalue) {
				newPrefix = tokener.getPrefix() + XMLIdMap.ENTITYSPLITTER;
				tokener.addPrefix(XMLIdMap.ATTRIBUTEVALUE);
			}
		} else {
			item = creator.getSendableInstance(false);
			tokener.withStack(item);
			newPrefix = XMLIdMap.ENTITYSPLITTER;
		}
		CharacterBuffer plainAttribute = new CharacterBuffer();
		if (item == null) {
			// First Skip not valid entry
			ArrayList<String> myStack = new ArrayList<String>();
			myStack.add(tag);

			while (!tokener.isEnd() && myStack.size() > 0) {
				if (tokener.getCurrentChar() == ENDTAG) {
					CharacterBuffer nextTag = tokener.nextToken(XMLTokener.TOKEN);
					if (nextTag.length() < 1
							|| myStack.get(myStack.size() - 1)
									.equalsIgnoreCase(nextTag.toString())) {
						myStack.remove(myStack.size() - 1);
						continue;
					}
				}
				if (tokener.getCurrentChar() == ITEMSTART) {
					XMLEntity nextTag = getEntity(null, tokener);
					if (nextTag != null && nextTag.getTag().length() > 0) {
						myStack.add(nextTag.getTag());
					}
					continue;
				}
				tokener.skip();
			}
		} else {
			if (!plainvalue) {
				plainAttribute.reset();
				// Parse Attributes
				while (!tokener.isEnd() && tokener.getCurrentChar() != ITEMEND) {
					if (tokener.getCurrentChar() == ENDTAG) {
						break;
					}
//					plainAttribute.with(tokener.getChar());
					if (tokener.getCurrentChar() != ENDTAG) {
						int len = plainAttribute.length();
						tokener.nextString(plainAttribute, false, false, '=');
						if(len < plainAttribute.length()) {
							String key = plainAttribute.trim().toString();
							tokener.skip(2);
							plainAttribute.reset();
							tokener.nextString(plainAttribute, true, true, '"');
							creator.setValue(item, tokener.getPrefix() + key, plainAttribute.toString(), IdMap.NEW);
							plainAttribute.reset();
						}
					}
				}

				if (tokener.getCurrentChar() != ENDTAG) {
					// Children
					while (!tokener.isEnd()) {
						if (stepEmptyPos(newPrefix, item, tag, tokener)) {
							XMLEntity nextTag = getEntity(null, tokener);

							if (nextTag != null) {
								Object result = parse(nextTag,
										tokener.withPrefix(newPrefix), grammar);

								if (result != null) {
									Object refObject = null;
									if (result != item) {
										if ("&".equals(newPrefix)) {
											refObject = tokener.getStackLast(1);
										} else {
											refObject = tokener.getStackLast(0);
										}
										if (refObject != null) {
											
											SendableEntityCreator parentCreator = this.getCreatorClass(refObject);
											parentCreator.setValue(
													refObject,
													nextTag.getTag(), result,
													IdMap.NEW);
											if (tokener.getStackSize() > 0) {
												tokener.popStack();
											}
										}
									}
								}
							}
							if (tokener.isEnd()) {
								if (tokener.getStackSize() > 0) {
									tokener.popStack();
								}
							} else if (tokener.getCurrentChar() == ENDTAG) {
								tokener.skipTo(ITEMEND, false);
								break;
							}else if (tokener.getCurrentChar() != ITEMSTART) {
								tokener.skip();
							}
						}
					}
				} else {
					tokener.skip();
				}
				return item;
			}
			if (tokener.getCurrentChar() == ENDTAG) {
				tokener.skip();
			} else {
				// Add Value
				plainAttribute.reset();
				tokener.skip();
				nextString(tokener, plainAttribute, ITEMSTART + "/" + tag);
//				tokener.nextString(plainAttribute, true, true, '/');
				creator.setValue(item, tokener.getPrefix(), plainAttribute.toString(),
						IdMap.NEW);
				tokener.skipTo(ITEMEND, false);
			}
			return null;
		}
		return item;
	}
	
	public CharacterBuffer nextString(XMLTokener tokener, CharacterBuffer buffer, String search) {
		char[] character = search.toCharArray();
		int z = 0;
		int strLen = character.length;
		

		while(tokener.isEnd() == false) {
			if(z==0) {
				tokener.nextString(buffer, true, false, character[z++]);
				if(tokener.getCurrentChar() == character[0]) {
					buffer.with(tokener.getCurrentChar());
				}
			} else {
				char currentChar = tokener.getChar();
				buffer.with(currentChar);
				if (currentChar == character[z]) {
					z++;
					if (z >= strLen) {
						buffer.withLen(buffer.length() - strLen);
						return buffer;
					}
				} else {
					z = 0;
					tokener.skip();
				}
			}
		}
		return buffer;
	}
}
