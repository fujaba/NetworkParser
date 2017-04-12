package de.uniks.networkparser.xml;

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
import java.util.ArrayList;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class XMLTokener extends Tokener {
	/** The Constant ENDTAG. */
	public static final char ENDTAG = '/';

	/** The Constant ITEMEND. */
	public static final char ITEMEND = '>';

	/** The Constant ITEMSTART. */
	public static final char ITEMSTART = '<';

	private static final char[] TOKEN = new char[]{' ', ITEMSTART, ENDTAG, ITEMEND};

	public static final String CHILDREN= "<CHILDREN>";


	private SendableEntityCreator defaultFactory;

	/** The stopwords. */
	private ArrayList<String> stopwords = new ArrayList<String>();

	public static final EntityStringConverter SIMPLECONVERTER = new EntityStringConverter();

	private boolean isAllowQuote;

	/** Instantiates a new XML id map. */
	public XMLTokener() {
		this.stopwords.add("?xml");
		this.stopwords.add("!--");
		this.stopwords.add("!DOCTYPE");
	}

	/**
	 * Get the next value. The value can be a Boolean, Double, Integer,
	 * BaseEntity, Long, or String.
	 *
	 * @param creator
	 *			The new Creator
	 * @param allowQuote
	 *			is in Text allow Quote
	 * @param c
	 *			The Terminate Char
	 *
	 * @return An object.
	 */
	@Override
	public Object nextValue(BaseItem creator, boolean allowQuote, boolean allowDuppleMarks, char c) {
		switch (c) {
		case '"':
		case '\'':
			skip();
			CharacterBuffer v = nextString(new CharacterBuffer(), allowQuote, true, c);
			String g = EntityUtil.unQuote(v);
			return g;
		case ITEMSTART:
			BaseItem element = creator.getNewList(false);
			if (element instanceof Entity) {
				parseToEntity((Entity) element);
			}
			return element;
		default:
			break;
		}
		return super.nextValue(creator, allowQuote, allowDuppleMarks, c);
	}

	public void parseToEntity(Entity entity) {
		skipHeader();
		char c = getCurrentChar();
		if (c != ITEMSTART) {
			c = nextClean(false);
		}
		if (entity instanceof XMLEntity == false) {
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException("Parse only XMLEntity");
			}
			return;
		}
		XMLEntity xmlEntity = (XMLEntity) entity;
		if (c != ITEMSTART) {
//			xmlEntity.withValue(this.buffer);
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException("A XML text must begin with '<'");
			}
			return;
		}
		xmlEntity.setType(this.buffer.nextToken(false, Buffer.STOPCHARSXMLEND).toString());
		XMLEntity child;
		while (true) {
 			c = nextClean(true);
			if (c == 0) {
				break;
			} else if (c == ITEMEND) {
				c = nextClean(false);
				if (c == 0) {
					return;
				}
				if (c != ITEMSTART) {
					CharacterBuffer item = new CharacterBuffer();
					nextString(item, false, false, '<');
					char currentChar = getCurrentChar();
					char nextChar = nextClean(false);
					if(nextChar !='/' ) {
						// May be another child so it is possible text node text
						XMLEntity newChild=new XMLEntity();
						newChild.withValue(item.toString());
						xmlEntity.withChild(newChild);
					}else {
						xmlEntity.withValue(item.toString());
					}
					buffer.withLookAHead(""+currentChar+nextChar);
					c = currentChar;
				}
			}

			if (c == ITEMSTART) {
				char nextChar = buffer.getChar();
				if (nextChar == '/') {
					skipTo(ITEMEND, false);
					break;
				} else if(nextChar == '!') {
					skipTo("-->", true, true);
					skip();
				} else {
					buffer.withLookAHead(c);
					if (getCurrentChar() == '<') {
						child = (XMLEntity) xmlEntity.getNewList(true);
						parseToEntity((Entity)child);
						xmlEntity.with(child);
						skip();
					} else {
						xmlEntity.withValue(nextString(new CharacterBuffer(), false, false, '<').toString());
					}
				}
			} else if (c == '/') {
				skip();
				break;
			} else {
				if(xmlEntity.sizeChildren()<1) {
					// Normal key Value
					String key = nextValue(xmlEntity, false, true, c).toString();
					if (key.length() > 0) {
						xmlEntity.put(key,
								nextValue(xmlEntity, isAllowQuote, true, nextClean(false)));
					}
				} else {
					// Just a Child
					CharacterBuffer item = new CharacterBuffer();
					nextString(item, false, false, '<');
					// May be another child so it is possible text node text
					XMLEntity newChild=new XMLEntity();
					newChild.withValue(item.toString());
					xmlEntity.withChild(newChild);
				}
			}
		}
	}

	/**	Skip the Current Entity to &gt;. */
	protected void skipEntity() {
		skipTo('>', false);
		// Skip >
		nextClean(false);
	}

	public String skipHeader() {
		boolean skip=false;
		CharacterBuffer tag;
		do {
			tag = this.getString(2);
			if(tag == null) {
				tag = new CharacterBuffer();
				break;
			} else if(tag.equals("<?")) {
				skipEntity();
				skip = true;
			} else if(tag.equals("<!")) {
				skipTo("-->", true, true);
				skip();
				skip = true;
			} else {
				skip = false;
			}
		}while(skip);
		String item = tag.toString();
		this.buffer.withLookAHead(item);
		return item;
	}

	public XMLTokener withBuffer(String value) {
		super.withBuffer(value);
		return this;
	}
	@Override
	public String toString() {
		if(buffer instanceof BufferedBuffer) {
			return "XMLTokener: "+((BufferedBuffer)buffer).substring(-1);
		}
		return super.toString();
	}

	@Override
	public Entity newInstance() {
		return new XMLEntity();
	}

	@Override
	public EntityList newInstanceList() {
		return new XMLEntity();
	}

	/**
	 * Find tag.
	 *
	 * @param tokener 	the tokener
	 * @param map 		decoding runtime values
	 * @return 			the object
	 */
	public Object parse(XMLTokener tokener, MapEntity map) {
		parseAttribute(tokener, map);
		return parseChildren(tokener, map);
	}

	protected void parseAttribute(XMLTokener tokener, MapEntity map) {
		MapEntityStack stack = map.getStack();
		Object entity = stack.getCurrentItem();
		SendableEntityCreator creator = stack.getCurrentCreator();
		if (entity != null) {
			// Parsing attributes
			CharacterBuffer token = new CharacterBuffer();
			char myChar;
			do {
				if (tokener.getCurrentChar() == IdMap.SPACE) {
					tokener.getChar();
				}
				tokener.nextString(token, true, false, IdMap.SPACE, IdMap.EQUALS, ITEMEND, ENDTAG);
				myChar = tokener.getCurrentChar();
				if (myChar == '=') {
					String key = token.toString();
					token.reset();
					tokener.skip(2);
					tokener.nextString(token, true, false, IdMap.DOUBLEQUOTIONMARK);
					String value = token.toString();
					token.reset();
					tokener.skip();
					creator.setValue(entity, key, value, SendableEntityCreator.NEW);
					stack.setValue(key, value);
					myChar = tokener.getCurrentChar();
				}
			} while (myChar != ITEMEND && myChar != 0 && myChar != ENDTAG);
		}
	}

	protected Object parseChildren(XMLTokener tokener, MapEntity map) {
		MapEntityStack stack = map.getStack();
		Object entity = stack.getCurrentItem();
		SendableEntityCreator creator = stack.getCurrentCreator();
		if(creator == null) {
			return null;
		}
		// Parsing next Element
		if (tokener.skipTo("/>", false, false)) {
			if (tokener.getCurrentChar() == '/') {
				stack.popStack();
				tokener.getChar();
				tokener.nextToken(false, TOKEN);
				return entity;
			}

			char quote = (char) ITEMSTART;
			// Skip >
			tokener.skip();
			CharacterBuffer valueItem = new CharacterBuffer();
			tokener.nextString(valueItem, false, false, quote);
			if (valueItem.isEmptyCharacter() == false) {
				CharacterBuffer test = new CharacterBuffer();
				while (tokener.isEnd() == false) {
					if (tokener.getCurrentChar() == ITEMSTART) {
						test.with(tokener.getCurrentChar());
						test.with(tokener.getChar());
					}
					if (tokener.getCurrentChar() == ENDTAG) {
						CharacterBuffer endTag = tokener.nextToken(false, XMLTokener.TOKEN);
						String currentTag = stack.getCurrentTag();
						if (currentTag == null || currentTag.equals(endTag.toString())) {
							break;
						} else {
							valueItem.with(test);
							valueItem.with(endTag);
							valueItem.with(tokener.getCurrentChar());
						}
					} else if (test.length() > 0) {
						valueItem.with(test);
					} else {
						char currentChar = tokener.getChar();
						if (currentChar != ITEMSTART) {
							valueItem.with(currentChar);
						}
					}
					test.reset();
				}
				if(entity!=null) {
					creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), SendableEntityCreator.NEW);
				}
				stack.setValue("" + IdMap.ENTITYSPLITTER, valueItem.toString());
				stack.popStack();
				tokener.skipEntity();
				return entity;
			}
			if (tokener.getCurrentChar() == ITEMSTART) {
				// show next Tag
				Object child;
				do {
					valueItem = parseEntity(tokener, map);
					if (valueItem == null) {
						if (tokener.getCurrentChar() == ENDTAG) {
							// Show if Item is End
							valueItem = tokener.nextToken(false, ITEMEND);
							if (valueItem.equals(stack.getCurrentTag())) {
								stack.popStack();
								// SKip > EndTag
								tokener.skip();
							}
						}
						return entity;
					}
					if (valueItem.isEmpty() == false) {
						creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), SendableEntityCreator.NEW);
						stack.setValue("" + IdMap.ENTITYSPLITTER, valueItem.toString());
						stack.popStack();
						tokener.skipEntity();
						return entity;
					}
					
					String childTag = stack.getCurrentTag();
					child = parse(tokener, map);
					if(childTag != null && child != null) {
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
	 * @param tokener	the tokener
	 * @param map	the decoding runtime values
	 * @return the entity
	 */
	public CharacterBuffer parseEntity(XMLTokener tokener, MapEntity map) {
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
			tag = tokener.nextToken(false, TOKEN);
			if (tag != null) {
				for (String stopword : this.stopwords) {
					if (tag.startsWith(stopword, 0, false)) {
						tokener.skipTo(ITEMEND, false);
						tokener.skipTo(ITEMSTART, false);
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
		IdMap idMap = getMap();
		SendableEntityCreator item = null;
		if(idMap != null) {
			item = idMap.getCreator(tag.toString(), false);
		}
		if (item != null && item instanceof SendableEntityCreatorTag) {
			addToStack((SendableEntityCreatorTag) item, tokener, tag, valueItem, map);
			return valueItem;
		}
		String startTag;
		if(tag.lastIndexOf(IdMap.ENTITYSPLITTER)>=0) {
			startTag = tag.substring(0, tag.lastIndexOf(IdMap.ENTITYSPLITTER));
		} else {
			startTag = tag.toString();
		}
		SimpleKeyValueList<String, SendableEntityCreatorTag> filter=new SimpleKeyValueList<String, SendableEntityCreatorTag>();
		if(idMap != null) {
			for(int i=0;i<idMap.getCreators().size();i++) {
				String key = idMap.getCreators().getKeyByIndex(i);
				SendableEntityCreator value = idMap.getCreators().getValueByIndex(i);
				if (key.startsWith(startTag) && value instanceof SendableEntityCreatorTag) {
					filter.put(key, (SendableEntityCreatorTag) value);
				}
			}
		}
		MapEntityStack stack = map.getStack();
		SendableEntityCreator defaultCreator = getDefaultFactory();
		SendableEntityCreatorTag creator;
		if(defaultCreator instanceof SendableEntityCreatorTag) {
			creator = (SendableEntityCreatorTag) defaultCreator;
		}else {
			creator = new XMLEntityCreator();
		}
		if(filter.size() < 1) {
			addToStack(creator, tokener, tag, valueItem, map);
			return valueItem;
		}
		StringBuilder sTag=new StringBuilder(startTag);
		while(filter.size()>0) {
			addToStack(creator, tokener, tag, valueItem, map);
			parseAttribute(tokener, map);
			if(tokener.getCurrentChar()=='/') {
				stack.popStack();
			}else {
				tokener.skip();
				if (tokener.getCurrentChar() != ITEMSTART) {
					tokener.nextString(valueItem, false, false, ITEMSTART);
					if (valueItem.isEmpty() == false) {
						valueItem.trim();
						Object entity = stack.getCurrentItem();
						creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), SendableEntityCreator.NEW);
					}
				}
				tag = tokener.nextToken(false, TOKEN);
				item = idMap.getCreator(tag.toString(), false);
				if(item instanceof SendableEntityCreatorTag) {
					creator = (SendableEntityCreatorTag) item;
				}else{
					creator = (SendableEntityCreatorTag) defaultCreator;
				}
				sTag.append(IdMap.ENTITYSPLITTER).append(tag.toString());
//				startTag = startTag + IdMap.ENTITYSPLITTER + tag.toString();
				for(int i=filter.size() - 1;i >= 0;i++) {
					String key = filter.getKeyByIndex(i);
					if(key.equals(sTag.toString())) {
						// FOUND THE Item
						creator = filter.getValueByIndex(i);
						addToStack(creator, tokener, tag, valueItem, map);
						return valueItem;
					}
					if(key.startsWith(sTag.toString()) == false) {
						filter.removePos(i);
					}
				}
				addToStack(creator, tokener, tag, valueItem, map);
			}
		}
		return valueItem;
	}

	public Entity createLink(Entity parent, String property, String className, String id) {
		parent.put(property, id);
		return null;
	}

	protected Object addToStack(SendableEntityCreatorTag creator, XMLTokener tokener, CharacterBuffer tag, CharacterBuffer value, MapEntity map) {
		Object entity = creator.getSendableInstance(false);
		if(entity instanceof EntityList) {
			creator.setValue(entity, XMLEntity.PROPERTY_VALUE, value.toString(), SendableEntityCreator.NEW);
			creator.setValue(entity, XMLEntity.PROPERTY_TAG, tag.toString(), SendableEntityCreator.NEW);
		}
		map.getStack().withStack(tag.toString(), entity, creator);
		return entity;
	}
	
	@Override
	public Object transformValue(Object value, BaseItem reference) {
		return EntityUtil.valueToString(value, true, reference, SIMPLECONVERTER);
	}

	@Override
	public BaseItem encode(Object entity, MapEntity map) {
		return null;
	}

	/**
	 * Get the DefaultFactory for Creating Element for Serialization
	 *
	 * @return the get The DefaultFactory
	 */
	public SendableEntityCreator getDefaultFactory() {
		return defaultFactory;
	}

	/**
	 * Add a DefaultFactoriy for creating Elements for Serialization
	 *
	 * @param defaultFactory the defaultFactory to set
	 * @return ThisComponent
	 */
	public XMLTokener withDefaultFactory(SendableEntityCreator defaultFactory) {
		this.defaultFactory = defaultFactory;
		return this;
	}
	public boolean isChild(Object writeValue) {
		return writeValue instanceof BaseItem;
	}

	public XMLTokener withAllowQuote(boolean value) {
		this.isAllowQuote = value;
		return this;
	}

	@Override
	public XMLTokener withMap(IdMap map) {
		super.withMap(map);
		return this;
	}
}
