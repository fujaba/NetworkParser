package de.uniks.networkparser.xml;

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
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.util.XMLEntityCreator;

public class XMLTokener extends Tokener {
	public static final String TOKEN=" >//<";

	private static final String TOKENSTOPWORDS = " >/<";
	
	public static final String CHILDREN= "<CHILDREN>";

	/** The Constant ENDTAG. */
	public static final char ENDTAG = '/';

	/** The Constant ITEMEND. */
	public static final char ITEMEND = '>';

	/** The Constant ITEMSTART. */
	public static final char ITEMSTART = '<';
	
	private SendableEntityCreator defaultFactory;
	
	/** The stopwords. */
	private ArrayList<String> stopwords = new ArrayList<String>();

	public final EntityStringConverter simpleConverter = new EntityStringConverter();
	
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
		case '<':
//			back();
			BaseItem element = creator.getNewList(false);
//			if (element instanceof EntityList) {
//				parseToEntity((EntityList) element);
			if (element instanceof Entity) {
				parseToEntity((Entity) element);
			}
			return element;
		default:
			break;
		}
		// back();
		if (c == '"') {
			// next();
			skip();
			return "";
		}
		return super.nextValue(creator, allowQuote,  allowDuppleMarks, c);
	}

	@Override
	public void parseToEntity(Entity entity) {
		//FIXME CHECK OR REMOVE
		boolean isAllowQuote = false;
		char c = getCurrentChar();
		if (c != '<') {
			c = nextClean(false);
		}
		if (c != '<') {
			if (logger.error(this, "parseToEntity",
					NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException("A XML text must begin with '<'");
			}
			return;
		}
		if (!(entity instanceof XMLEntity)) {
			if (logger.error(this, "parseToEntity",
					NetworkParserLog.ERROR_TYP_PARSING, entity)) {
				throw new RuntimeException("Parse only XMLEntity");
			}
			return;
		}
		XMLEntity xmlEntity = (XMLEntity) entity;
		xmlEntity.withTag(this.buffer.nextToken(Buffer.STOPCHARSXMLEND).toString());
		XMLEntity child;
		while (true) {
 			c = nextClean(true);
			if (c == 0) {
				break;
			} else if (c == '>') {
				c = nextClean(false);
				if (c == 0) {
					return;
				}
				if (c != '<') {
					xmlEntity.setValueItem(nextString(new CharacterBuffer(), false, false, '<').toString());
					continue;
				}
			}

			if (c == '<') {
				char nextChar = buffer.getChar();
				if (nextChar == '/') {
					skipTo('>', false);
					break;
				} else {
					buffer.withLookAHead(c);
					if (getCurrentChar() == '<') {
						child = (XMLEntity) xmlEntity.getNewList(true);
						parseToEntity((Entity)child);
						xmlEntity.with(child);
						skip();
					} else {
						xmlEntity.setValueItem(nextString(new CharacterBuffer(), false, false, '<').toString());
					}
				}
			} else if (c == '/') {
				skip();
				break;
			} else {
				String key = nextValue(xmlEntity, false, true, c).toString();
				if (key.length() > 0) {
					xmlEntity.put(key,
							nextValue(xmlEntity, isAllowQuote, true, nextClean(false)));
				}
			}
		}
	}

	/**
	 * Skip the Current Entity to &gt;.
	 */
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
			if(tag.equals("<?")) {
				skipEntity();
				skip = true;
			} else if(tag.equals("<!")) {
				skipEntity();
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
	 * @param tokener
	 *			the tokener
	 * @param defaultFactory
	 *			the defaultFactory
	 * @return the object
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
					creator.setValue(entity, key, value, IdMap.NEW);
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
		// Parsing next Element
		if (tokener.skipTo("/>", false, false)) {
			if (tokener.getCurrentChar() == '/') {
				stack.popStack();
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
				while (tokener.isEnd() == false) {
					if (tokener.getCurrentChar() == ITEMSTART) {
						test.with(tokener.getCurrentChar());
						test.with(tokener.getChar());
					}
					if (tokener.getCurrentChar() == ENDTAG) {
						CharacterBuffer endTag = tokener.nextToken(XMLTokener.TOKEN);
						if (stack.getCurrentTag().equals(endTag.toString())) {
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
				creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), IdMap.NEW);
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
							valueItem = tokener.nextToken("" + ITEMEND);
							if (valueItem.equals(stack.getCurrentTag())) {
								stack.popStack();
								// SKip > EndTag
								tokener.skip();
							}
						}
						return entity;
					}
					if (valueItem.isEmpty() == false) {
						creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), IdMap.NEW);
						stack.setValue("" + IdMap.ENTITYSPLITTER, valueItem.toString());
						stack.popStack();
						tokener.skipEntity();
						return entity;
					}
					String childTag = stack.getCurrentTag();
					child = parse(tokener, map);
					if (child != null) {
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
			tag = tokener.nextToken(TOKENSTOPWORDS);
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
		IdMap idMap = map.getMap();
		SendableEntityCreator item = idMap.getCreator(tag.toString(), false);
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
		for(int i=0;i<idMap.getCreators().size();i++) {
			String key = idMap.getCreators().getKeyByIndex(i);
			SendableEntityCreator value = idMap.getCreators().getValueByIndex(i);
			if (key.startsWith(startTag) && value instanceof SendableEntityCreatorTag) {
				filter.put(key, (SendableEntityCreatorTag) value);
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
						creator.setValue(entity, XMLEntity.PROPERTY_VALUE, valueItem.toString(), IdMap.NEW);
					}
				}
				tag = tokener.nextToken(TOKENSTOPWORDS);
				item = idMap.getCreator(tag.toString(), false);
				if(item instanceof SendableEntityCreatorTag) {
					creator = (SendableEntityCreatorTag) item;
				}else{
					creator = (SendableEntityCreatorTag) defaultCreator;
				}
				
				startTag = startTag + IdMap.ENTITYSPLITTER + tag.toString();
				for(int i=filter.size() - 1;i >= 0;i++) {
					String key = filter.getKeyByIndex(i);
					if(key.equals(startTag)) {
						// FOUND THE Item
						creator = filter.getValueByIndex(i);
						addToStack(creator, tokener, tag, valueItem, map);
						return valueItem;
					}
					if(key.startsWith(startTag) == false) {
						filter.removePos(i);
					}
				}
				addToStack(creator, tokener, tag, valueItem, map);
			}
		} 
		return valueItem;
	}
	
	protected Object addToStack(SendableEntityCreatorTag creator, XMLTokener tokener, CharacterBuffer tag, CharacterBuffer value, MapEntity map) {
		Object entity = creator.getSendableInstance(false);
		if(entity instanceof EntityList) {
			creator.setValue(entity, XMLEntity.PROPERTY_VALUE, value.toString(), IdMap.NEW);
			creator.setValue(entity, XMLEntity.PROPERTY_TAG, tag.toString(), IdMap.NEW);
		}
		if(value.isEmpty() == false) {
//FIXME SETVALUE			tokener.setValue(".", value.toString());
		}
		map.getStack().withStack(tag.toString(), entity, creator);
		return entity;
	}
	
	@Override
	public Object transformValue(Object value, BaseItem reference) {
		return EntityUtil.valueToString(value, true, reference, simpleConverter);
	}
	
	/**
	 * @return the defaultFactory
	 */
	public SendableEntityCreator getDefaultFactory() {
		return defaultFactory;
	}

	/**
	 * @param defaultFactory the defaultFactory to set
	 * @return ThisComponent
	 */
	public XMLTokener withDefaultFactory(SendableEntityCreator defaultFactory) {
		this.defaultFactory = defaultFactory;
		return this;
	}
}
