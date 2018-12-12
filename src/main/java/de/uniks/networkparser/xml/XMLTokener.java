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

import de.uniks.networkparser.EntityCreator;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleKeyValueList;

public class XMLTokener extends Tokener {
	/** The Constant ENDTAG. */
	public static final char ENDTAG = '/';

	private static final char[] TOKEN = new char[] { ' ', XMLEntity.START, ENDTAG, XMLEntity.END };

	public static final String CHILDREN = "<CHILDREN>";

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
	 * Get the next value. The value can be a Boolean, Double, Integer, BaseEntity,
	 * Long, or String.
	 *
	 * @param creator    The new Creator
	 * @param allowQuote is in Text allow Quote
	 * @param c          The Terminate Char
	 *
	 * @return An object.
	 */
	@Override
	public Object nextValue(Buffer buffer, BaseItem creator, boolean allowQuote, boolean allowDuppleMarks, char c) {
		switch (c) {
		case BufferItem.QUOTES:
		case '\'':
			buffer.skip();
			CharacterBuffer v = nextString(buffer, new CharacterBuffer(), allowQuote, true, c);
			String g = EntityUtil.unQuote(v);
			return g;
		case XMLEntity.START:
			BaseItem element = creator.getNewList(false);
			if (element instanceof Entity) {
				parseToEntity((Entity) element, buffer);
			}
			return element;
		default:
			break;
		}
		return super.nextValue(buffer, creator, allowQuote, allowDuppleMarks, c);
	}

	@Override
	public boolean parseToEntity(Entity entity, Buffer buffer) {
		skipHeader(buffer);
		if(buffer == null) {
			return false;
		}
		char c = buffer.getCurrentChar();
		if (c != XMLEntity.START) {
			c = buffer.nextClean(false);
		}
		if (entity instanceof XMLEntity == false) {
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException("Parse only XMLEntity");
			}
			return false;
		}
		XMLEntity xmlEntity = (XMLEntity) entity;
		if (c != XMLEntity.START) {
//			xmlEntity.withValue(this.buffer);
			if (isError(this, "parseToEntity", NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException("A XML text must begin with '<'");
			}
			return false;
		}
		xmlEntity.withType(buffer.nextToken(false, Buffer.STOPCHARSXMLEND).toString());
		XMLEntity child;
		while (true) {
			c = buffer.nextClean(true);
			if (c == 0) {
				break;
			} else if (c == XMLEntity.END) {
				c = buffer.nextClean(false);
				if (c == 0) {
					return true;
				}
				if (c != XMLEntity.START) {
					CharacterBuffer item = new CharacterBuffer();
					buffer.nextString(item, false, false, '<');
					char currentChar = buffer.getCurrentChar();
					char nextChar = buffer.nextClean(false);
					if (nextChar != '/') {
						// May be another child so it is possible text node text
						XMLEntity newChild = new XMLEntity();
						newChild.withValue(item.toString());
						xmlEntity.withChild(newChild);
					} else {
						xmlEntity.withValue(item.toString());
					}
					buffer.withLookAHead("" + currentChar + nextChar);
					c = currentChar;
				}
			}

			if (c == XMLEntity.START) {
				char nextChar = buffer.getChar();
				if (nextChar == '/') {
					buffer.skipTo(XMLEntity.END, false);
					break;
				} else if (nextChar == '!') {
					nextChar = buffer.getChar();
					if('[' ==nextChar) {
						// MIGHT BE <![CDATA[
						buffer.skipTo("!]]>", true, true);
					} else {
						buffer.skipTo("-->", true, true);
					}
					buffer.skip();
				} else {
					buffer.withLookAHead(c);
					if (buffer.getCurrentChar() == '<') {
						child = (XMLEntity) xmlEntity.getNewList(true);
						if (parseToEntity((Entity) child, buffer)) {
							xmlEntity.with(child);
							buffer.skip();
						}
					} else {
						xmlEntity.withValue(nextString(buffer, new CharacterBuffer(), false, false, '<').toString());
					}
				}
			} else if (c == '/') {
				buffer.skip();
				break;
			} else {
				if (xmlEntity.sizeChildren() < 1) {
					// Normal key Value
					Object value = nextValue(buffer, xmlEntity, false, true, c);
					if (value == null) {
						return false;
					}
					String key = value.toString();
					if (key.length() > 0) {
						xmlEntity.put(key, nextValue(buffer, xmlEntity, isAllowQuote, true, buffer.nextClean(false)));
					}
				} else {
					// Just a Child
					CharacterBuffer item = new CharacterBuffer();
					nextString(buffer, item, false, false, '<');
					// May be another child so it is possible text node text
					XMLEntity newChild = new XMLEntity();
					newChild.withValue(item.toString());
					xmlEntity.withChild(newChild);
				}
			}
		}
		return true;
	}

	/**
	 * Skip the Current Entity to &gt;.
	 * 
	 * @param buffer Buffer for Values
	 */
	protected void skipEntity(Buffer buffer) {
		if(buffer == null) {
			return;
		}
		buffer.skipTo('>', false);
		// Skip >
		buffer.nextClean(false);
	}

	public String skipHeader(Buffer buffer) {
		boolean skip = false;
		CharacterBuffer tag;
		if(buffer == null) {
			return null;
		}
		do {
			tag = buffer.getString(2);
			if (tag == null) {
				tag = new CharacterBuffer();
				break;
			} else if (tag.equals("<?")) {
				skipEntity(buffer);
				skip = true;
			} else if (tag.equals("<!")) {
				buffer.skipTo(">", true, true);
				buffer.nextClean(false);
				skip = true;
			} else {
				skip = false;
			}
		} while (skip);
		String item = tag.toString();
		if (buffer != null) {
			buffer.withLookAHead(item);
		}
		return item;
	}

	@Override
	public String toString() {
		return "XMLTokener";
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
	 * @param tokener the tokener
	 * @param buffer  Buffer for Values
	 * @param map     decoding runtime values
	 * @return the object
	 */
	public Object parse(XMLTokener tokener, Buffer buffer, MapEntity map) {
		parseAttribute(tokener, buffer, map);
		return parseChildren(tokener, buffer, map);
	}

	protected void parseAttribute(XMLTokener tokener, Buffer buffer, MapEntity map) {
		if (map == null) {
			return;
		}
		MapEntityStack stack = map.getStack();
		if (stack == null) {
			return;
		}
		Object entity = stack.getCurrentItem();
		SendableEntityCreator creator = stack.getCurrentCreator();
		if (entity != null) {
			// Parsing attributes
			CharacterBuffer token = new CharacterBuffer();
			char myChar;
			do {
				if (buffer.getCurrentChar() == IdMap.SPACE) {
					buffer.getChar();
				}
				tokener.nextString(buffer, token, true, false, IdMap.SPACE, IdMap.EQUALS, XMLEntity.END, ENDTAG);
				myChar = buffer.getCurrentChar();
				if (myChar == ENTER) {
					String key = token.toString();
					token.clear();
					buffer.skip(2);
					tokener.nextString(buffer, token, true, false, IdMap.DOUBLEQUOTIONMARK);
					String value = token.toString();
					token.clear();
					buffer.skip();
					creator.setValue(entity, key, value, SendableEntityCreator.NEW);
					stack.setValue(key, value);
					myChar = buffer.getCurrentChar();
				}
			} while (myChar != XMLEntity.END && myChar != 0 && myChar != ENDTAG);
		}
	}

	protected Object parseChildren(XMLTokener tokener, Buffer buffer, MapEntity map) {
		if (map == null) {
			return null;
		}
		MapEntityStack stack = map.getStack();
		if (stack == null) {
			return null;
		}
		Object entity = stack.getCurrentItem();
		SendableEntityCreator creator = stack.getCurrentCreator();
		if (creator == null) {
			return null;
		}
		// Parsing next Element
		if (buffer.skipTo("/>", false, false)) {
			if (buffer.getCurrentChar() == '/') {
				stack.popStack();
				buffer.getChar();
				tokener.nextToken(buffer, false, TOKEN);
				return entity;
			}

			char quote = (char) XMLEntity.START;
			// Skip >
			buffer.skip();
			CharacterBuffer valueItem = new CharacterBuffer();
			tokener.nextString(buffer, valueItem, false, false, quote);
			if (valueItem.isEmptyCharacter() == false) {
				CharacterBuffer test = new CharacterBuffer();
				while (buffer.isEnd() == false) {
					if (buffer.getCurrentChar() == XMLEntity.START) {
						test.with(buffer.getCurrentChar());
						test.with(buffer.getChar());
					}
					if (buffer.getCurrentChar() == ENDTAG) {
						CharacterBuffer endTag = tokener.nextToken(buffer, false, XMLTokener.TOKEN);
						String currentTag = stack.getCurrentTag();
						if (currentTag == null || currentTag.equals(endTag.toString())) {
							break;
						} else {
							valueItem.with(test);
							valueItem.with(endTag);
							valueItem.with(buffer.getCurrentChar());
						}
					} else if (test.length() > 0) {
						valueItem.with(test);
					} else {
						char currentChar = buffer.getChar();
						if (currentChar != XMLEntity.START) {
							valueItem.with(currentChar);
						}
					}
					test.clear();
				}
				if (entity != null) {
					creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), SendableEntityCreator.NEW);
				}
				stack.setValue("" + IdMap.ENTITYSPLITTER, valueItem.toString());
				stack.popStack();
				tokener.skipEntity(buffer);
				return entity;
			}
			if (buffer.getCurrentChar() == XMLEntity.START) {
				// show next Tag
				Object child;
				do {
					valueItem = parseEntity(tokener, buffer, map);
					if (valueItem == null) {
						if (buffer.getCurrentChar() == ENDTAG) {
							// Show if Item is End
							valueItem = tokener.nextToken(buffer, false, XMLEntity.END);
							if (valueItem.equals(stack.getCurrentTag())) {
								stack.popStack();
								// SKip > EndTag
								buffer.skip();
							}
						}
						return entity;
					}
					if (valueItem.isEmpty() == false) {
						creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(),
								SendableEntityCreator.NEW);
						stack.setValue("" + IdMap.ENTITYSPLITTER, valueItem.toString());
						stack.popStack();
						tokener.skipEntity(buffer);
						return entity;
					}

					String childTag = stack.getCurrentTag();
					child = parse(tokener, buffer, map);
					if (childTag != null && child != null) {
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
	 * @param tokener the tokener
	 * @param buffer  Buffer for Values
	 * @param map     the decoding runtime values
	 * @return the entity
	 */
	public CharacterBuffer parseEntity(XMLTokener tokener, Buffer buffer, MapEntity map) {
		CharacterBuffer valueItem = new CharacterBuffer();
		if (tokener == null || buffer == null) {
			return valueItem;
		}
		CharacterBuffer tag;
		boolean isEmpty = true;
		do {
			if (buffer.getCurrentChar() != XMLEntity.START) {
				tokener.nextString(buffer, valueItem, false, false, XMLEntity.START);
				if (valueItem.isEmpty() == false) {
					valueItem.trim();
					isEmpty = valueItem.isEmpty();
				}
			}
			tag = tokener.nextToken(buffer, false, TOKEN);
			if (tag != null) {
				for (String stopword : this.stopwords) {
					if (tag.startsWith(stopword, 0, false)) {
						buffer.skipTo(XMLEntity.END, false);
						buffer.skipTo(XMLEntity.START, false);
						tag = null;
						break;
					}
				}
			}
			if (buffer.isEnd()) {
				break;
			}
		} while (tag == null);
		if (tag == null || tag.length() < 1) {
			return null;
		}
		if (tag.isEmpty() && isEmpty) {
			valueItem.clear();
		}
		IdMap idMap = getMap();
		SendableEntityCreator item = null;
		if (idMap != null) {
			item = idMap.getCreator(tag.toString(), false, null);
		}
		if (item != null && item instanceof SendableEntityCreatorTag) {
			addToStack((SendableEntityCreatorTag) item, tokener, tag, valueItem, map);
			return valueItem;
		}
		String startTag;
		if (tag.lastIndexOf(IdMap.ENTITYSPLITTER) >= 0) {
			startTag = tag.substring(0, tag.lastIndexOf(IdMap.ENTITYSPLITTER));
		} else {
			startTag = tag.toString();
		}
		SimpleKeyValueList<String, SendableEntityCreatorTag> filter = new SimpleKeyValueList<String, SendableEntityCreatorTag>();
		if (idMap != null) {
			for (int i = 0; i < idMap.getCreators().size(); i++) {
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
		if (defaultCreator instanceof SendableEntityCreatorTag) {
			creator = (SendableEntityCreatorTag) defaultCreator;
		} else {
			creator = EntityCreator.createXML();
		}
		if (filter.size() < 1) {
			addToStack(creator, tokener, tag, valueItem, map);
			return valueItem;
		}
		StringBuilder sTag = new StringBuilder(startTag);
		while (filter.size() > 0) {
			addToStack(creator, tokener, tag, valueItem, map);
			parseAttribute(tokener, buffer, map);
			if (buffer.getCurrentChar() == '/') {
				stack.popStack();
			} else {
				buffer.skip();
				if (buffer.getCurrentChar() != XMLEntity.START) {
					tokener.nextString(buffer, valueItem, false, false, XMLEntity.START);
					if (valueItem.isEmpty() == false) {
						valueItem.trim();
						Object entity = stack.getCurrentItem();
						creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(),
								SendableEntityCreator.NEW);
					}
				}
				tag = tokener.nextToken(buffer, false, TOKEN);
				item = idMap.getCreator(tag.toString(), false, null);
				if (item instanceof SendableEntityCreatorTag) {
					creator = (SendableEntityCreatorTag) item;
				} else {
					creator = (SendableEntityCreatorTag) defaultCreator;
				}
				sTag.append(IdMap.ENTITYSPLITTER).append(tag.toString());
//				startTag = startTag + IdMap.ENTITYSPLITTER + tag.toString();
				for (int i = filter.size() - 1; i >= 0; i++) {
					String key = filter.getKeyByIndex(i);
					if (key.equals(sTag.toString())) {
						// FOUND THE Item
						creator = filter.getValueByIndex(i);
						addToStack(creator, tokener, tag, valueItem, map);
						return valueItem;
					}
					if (key.startsWith(sTag.toString()) == false) {
						filter.removePos(i);
					}
				}
				addToStack(creator, tokener, tag, valueItem, map);
			}
		}
		return valueItem;
	}

	public Entity createLink(Entity parent, String property, String className, String id) {
		if (parent != null) {
			parent.put(property, id);
		}
		return null;
	}

	protected Object addToStack(SendableEntityCreatorTag creator, XMLTokener tokener, CharacterBuffer tag,
			CharacterBuffer value, MapEntity map) {
		if (creator == null) {
			return null;
		}
		Object entity = creator.getSendableInstance(false);
		if (entity instanceof EntityList) {
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
