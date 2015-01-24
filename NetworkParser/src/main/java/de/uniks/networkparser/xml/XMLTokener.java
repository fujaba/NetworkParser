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

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.ReferenceObject;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.FactoryEntity;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * Tokener for parsing XML-Files.
 *
 * @author Stefan Lindel
 */
public class XMLTokener extends Tokener {
	/** The stack. */
	private ArrayList<ReferenceObject> stack = new ArrayList<ReferenceObject>();
	/** Variable of AllowQuote. */
	private boolean isAllowQuote = false;

	/** The prefix. */
	private String prefix;

	/**
	 * Get the next value. The value can be a Boolean, Double, Integer,
	 * BaseEntity, Long, or String.
	 *
	 * @param creator
	 *            The new Creator
	 * @param allowQuote
	 *            is in Text allow Quote
	 * @param c
	 *            The Terminate Char
	 *
	 * @return An object.
	 */
	@Override
	public Object nextValue(BaseItem creator, boolean allowQuote, char c) {
		switch (c) {
		case '"':
		case '\'':
			next();
			return nextString(c, false, allowQuote, false, true);
		case '<':
			back();
			if (creator instanceof FactoryEntity) {
				BaseItem element = ((FactoryEntity) creator).getNewObject();
				if (element instanceof SimpleKeyValueList<?, ?>) {
					parseToEntity((SimpleKeyValueList<?, ?>) element);
				} else if (element instanceof SimpleList<?>) {
					parseToEntity((SimpleList<?>) element);
				}
				return element;
			}
		default:
			break;
		}
		// back();
		if (c == '"') {
			// next();
			next();
			return "";
		}
		return super.nextValue(creator, allowQuote, c);
	}

	@Override
	public void parseToEntity(SimpleKeyValueList<?, ?> entity) {
		char c = getCurrentChar();

		if (c != '<') {
			c = nextClean();
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
		if (buffer.isCache()) {
			c = nextClean();
			int pos = position();
			while (c >= ' ' && getStopChars().indexOf(c) < 0 && c != '>') {
				c = next();
			}
			xmlEntity.withTag(buffer.substring(pos, position() - pos));
		} else {
			StringBuilder sb = new StringBuilder();
			c = nextClean();
			while (c >= ' ' && getStopChars().indexOf(c) < 0 && c != '>') {
				sb.append(c);
				c = next();
			}
			xmlEntity.withTag(sb.toString());
		}

		XMLEntity child;
		while (true) {
			c = nextStartClean();
			if (c == 0) {
				break;
			} else if (c == '>') {
				c = nextClean();
				if (c == 0) {
					return;
				}
				if (c != '<') {
					xmlEntity.withValueItem(nextString('<', false, false,
							false, false));
					continue;
				}
			}

			if (c == '<') {
				if (charAt(position() + 1) == '/') {
					stepPos(">", false, false);
					break;
				} else {
					if (getCurrentChar() == '<') {
						child = (XMLEntity) xmlEntity.getNewArray();
						parseToEntity(child);
						xmlEntity.addChild(child);
					} else {
						xmlEntity.withValueItem(nextString('<', false, false,
								false, false));
					}
				}
			} else if (c == '/') {
				next();
				break;
			} else {
				String key = nextValue(xmlEntity, false, c).toString();
				if (key.length() > 0) {
					xmlEntity.put(key,
							nextValue(xmlEntity, isAllowQuote, nextClean()));
				}
			}
		}
	}

	/**
	 * Skip the Current Entity to &gt;.
	 */
	protected void skipEntity() {
		stepPos(">", false, false);
		// Skip >
		next();
	}

	@Override
	public XMLTokener withText(String value) {
		super.withText(value);
		return this;
	}

	@Override
	public void parseToEntity(SimpleList<?> entityList) {
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
	 *            set Prefix.
	 * @return XMLTokener Instance
	 */
	public XMLTokener withPrefix(String value) {
		this.prefix = value;
		return this;
	}

	/**
	 * @param value
	 *            add Prefix to Existing.
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
	 *            new Reference Object
	 * @return XMLTokener Instance
	 */
	public XMLTokener withStack(ReferenceObject item) {
		this.stack.add(item);
		this.prefix = "";
		return this;
	}

	/**
	 * @return The Last Element and remove it
	 */
	public ReferenceObject popStack() {
		return this.stack.remove(this.stack.size() - 1);
	}

	/** @return The StackSize */
	public int getStackSize() {
		return this.stack.size();
	}

	/**
	 * @param offset
	 *            Offset from Last
	 * @return The Stack Element - offset
	 */
	public ReferenceObject getStackLast(int offset) {
		return this.stack.get(this.stack.size() - 1 - offset);
	}

	/**
	 * @param value
	 *            of AllowQuote
	 * @return XMLTokener Instance
	 */
	public XMLTokener withAllowQuote(boolean value) {
		this.isAllowQuote = value;
		return this;
	}
}
