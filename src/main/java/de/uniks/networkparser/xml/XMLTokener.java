package de.uniks.networkparser.xml;

import java.util.ArrayList;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.list.AbstractList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class XMLTokener extends Tokener {
	public static final String TOKEN=" >//<";

	/** The stack. */
	private ArrayList<Object> stack = new ArrayList<Object>();
	/** Variable of AllowQuote. */
	private boolean isAllowQuote;

	/** The prefix. */
	private String prefix;

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
	 * Get the Prefix from Tokener my be seperated by &amp;.
	 *
	 * @return the Prefix
	 */
	public String getPrefix() {
		return prefix;
	}

	/**
	 * @param value
	 *			set Prefix.
	 * @return XMLTokener Instance
	 */
	public XMLTokener withPrefix(String value) {
		this.prefix = value;
		return this;
	}

	/**
	 * @param value
	 *			add Prefix to Existing.
	 * @return XMLTokener Instance
	 */
	public XMLTokener addPrefix(String value) {
		this.prefix += value;
		return this;
	}

	/**
	 * Add a new Reference Object to Stack.
	 *
	 * @param item
	 *			new Reference Object
	 * @return XMLTokener Instance
	 */
	public XMLTokener withStack(Object item) {
		this.stack.add(item);
		this.prefix = "";
		return this;
	}

	/**
	 * @return The Last Element and remove it
	 */
	public Object popStack() {
		return this.stack.remove(this.stack.size() - 1);
	}

	/** @return The StackSize */
	public int getStackSize() {
		return this.stack.size();
	}

	/**
	 * @param offset
	 *			Offset from Last
	 * @return The Stack Element - offset
	 */
	public Object getStackLast(int offset) {
		return this.stack.get(this.stack.size() - 1 - offset);
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
}
