package de.uniks.networkparser.xml;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class XMLTokener extends Tokener {
	public static final String TOKEN=" >//<";

	/** The Stack. */
	private SimpleKeyValueList<Object, SendableEntityCreatorTag> stack = new SimpleKeyValueList<Object, SendableEntityCreatorTag>();
	
	private SimpleList<String> tags = new SimpleList<String>();
	
	/** Variable of AllowQuote. */
	private boolean isAllowQuote;

	private SimpleKeyValueList<String, SimpleSet<String>> childProperties= new SimpleKeyValueList<String, SimpleSet<String>>();

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
			if (element instanceof SimpleKeyValueList<?, ?>) {
				parseToEntity((SimpleKeyValueList<?, ?>) element);
			} else if (element instanceof SimpleList<?>) {
				parseToEntity((SimpleList<?>) element);
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
	public void parseToEntity(SimpleKeyValueList<?, ?> entity) {
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
					xmlEntity.withValueItem(nextString(new CharacterBuffer(), false, false, '<').toString());
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
						parseToEntity(child);
						xmlEntity.with(child);
						skip();
					} else {
						xmlEntity.withValueItem(nextString(new CharacterBuffer(), false, false, '<').toString());
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

	@Override
	public void parseToEntity(AbstractList<?> entityList) {
		// Do Nothing
	}

	/**
	 * Remove The Last Element 
	 */
	public void popStack() {
		this.stack.removePos(this.stack.size() - 1);
		this.tags.remove(this.tags.size() - 1);
	}

	/** @return The StackSize */
	public int getStackSize() {
		return this.stack.size();
	}
	
	/**
	 * @return The Stack Element - offset
	 */
	public Object getCurrentItem() {
		return this.stack.last();
	}
	
	/**
	 * Add a new Reference Object to Stack.
	 * @param tag	The new Tag
	 * @param item 	new Reference Object
	 * @param creator The Creator for the Item
	 * @return XMLTokener Instance
	 */
	public XMLTokener withStack(String tag, Object item, SendableEntityCreatorTag creator) {
		stack.add(item, creator);
		tags.add(tag);
		String[] properties = creator.getProperties();
		for(String property : properties) {
			int lastPos = property.lastIndexOf(IdMap.ENTITYSPLITTER);
			if(lastPos >= 0) {
				String prop;
				if(lastPos == property.length() - 1) {
					// Value of XML Entity like uni.
					prop = ".";
				} else {
					prop = property.substring(lastPos + 1);
				}
				int pos = childProperties.indexOf(prop);
				if(pos>=0) {
					childProperties.getValueByIndex(pos).add(property);
				} else {
					SimpleSet<String> child = new SimpleSet<String>();
					child.add(property);
					childProperties.put(prop, child);
				}
			}
		}
		return this;
	}

	
	/**
	 * @return The Stack Element - offset
	 */
	public SendableEntityCreatorTag getCurrentCreator() {
		return this.stack.getValueByIndex(this.stack.size() - 1);
	}

	/**
	 * @param value
	 *			of AllowQuote
	 * @return XMLTokener Instance
	 */
	public XMLTokener withAllowQuote(boolean value) {
		this.isAllowQuote = value;
		return this;
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

	public void setValue(String key, String value) {
		SimpleSet<String> set = childProperties.get(key);
		if(set != null) {
			for(String ChildKey : set) {
				int pos = getEntityPos(ChildKey);
				if(pos >= 0 ) {
					Object entity = stack.getKeyByIndex(pos);
					SendableEntityCreatorTag creator = stack.getValueByIndex(pos);
					creator.setValue(entity, ChildKey, value, XMLIdMap.NEW);
				}
			}
		}
//		Object entity = getCurrentItem();
		
//		creator.setValue(entity, key, value, NEW);
		
	}
	
	private int getEntityPos(String entity) {
		int start=entity.lastIndexOf(XMLIdMap.ENTITYSPLITTER);
		int pos = this.tags.size() - 1;
		for(int end=start-1;end>=0;end --) {
			if(entity.charAt(end) ==XMLIdMap.ENTITYSPLITTER) {
				String item = entity.substring(end+1, start);
				String tag = tags.get(pos);
				if(tag == null || tag.equals(item) == false) {
					return -1;
				}
				start = end;
				pos--;
			}
		}
		return pos;
	}

	public String getCurrentTag() {
		if(this.tags.size() >0 ){
			return this.tags.get(this.tags.size() - 1);
		}
		return null;
	}
}
