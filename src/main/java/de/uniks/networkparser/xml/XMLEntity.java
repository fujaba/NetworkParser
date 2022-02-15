package de.uniks.networkparser.xml;

import de.uniks.networkparser.EntityStringConverter;
import de.uniks.networkparser.StringUtil;
/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.MapEntry;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class XMLEntity.
 *
 * @author Stefan Lindel
 */

public class XMLEntity extends SimpleKeyValueList<String, Object> implements Entity, EntityList {
	/** Constant of TAG. */
	public static final String PROPERTY_TAG = "tag";
	/** Constant of VALUE. */
	public static final String PROPERTY_VALUE = "value";
	/** The children. */
	private SimpleList<BaseItem> children;

	/** The Constant START. */
	public static final char START = '<';

	/** The Constant END. */
	public static final char END = '>';

	/** The tag. */
	protected String tag;

	/** The value. */
	protected String valueItem;

	/** Simple Constructor. */
	public XMLEntity() {
		this.withAllowDuplicate(false);
	}

	/**
	 * Instantiates a new XMLEntity.
	 *
	 * @param value the tag
	 * @return Itself
	 */
	public XMLEntity withValue(String value) {
		XMLTokener tokener = new XMLTokener();
		CharacterBuffer buffer = new CharacterBuffer().with(value);
		return withValue(tokener, buffer);
	}

	/**
	 * Construct a XMLEntity from a Tokener.
	 *
	 * @param tokener A Tokener object containing the source string. or a duplicated
	 *                key.
	 * @param values  Value of Element
	 * @return Itself
	 */
	public XMLEntity withValue(Tokener tokener, Object values) {
		if (tokener != null) {
			Buffer buffer = null;
			if (values instanceof Buffer) {
				buffer = (Buffer) values;
			} else if (values instanceof CharSequence) {
				buffer = new CharacterBuffer().with((CharSequence) values);
			}
			if (buffer == null) {
				return null;
			}
			char c = buffer.nextClean(true);
			if (c != START) {
				Object item = buffer.getString(buffer.length() - buffer.position());
				if (item != null) {
					this.valueItem = item.toString();
				}
				return this;
			}
			tokener.parseToEntity(this, buffer);
		}
		return this;
	}

	/**
	 * Gets the children.
	 * 
	 * @param index the Index of Child
	 * @return the children
	 */
	public BaseItem getChild(int index) {
		if (this.children == null || index < 0 || index > this.children.size()) {
			return null;
		}
		return this.children.get(index);
	}

	/**
	 * Size children.
	 *
	 * @return the int
	 */
	public int sizeChildren() {
		if (this.children == null) {
			return 0;
		}
		return this.children.size();
	}
	
	/**
	 * Adds the child.
	 *
	 * @param values the child
	 * @return result if the child is added
	 */
	@Override
	public boolean add(Object... values) {
		if (values == null || values.length < 1) {
			return false;
		}
		if (values[0] instanceof String) {
			if (values.length == 1) {
				this.withValue((String) values[0]);
			}
		} else if (values.length % 2 == 1) {
			for (Object item : values) {
				if (item instanceof BaseItem) {
					this.withChild((BaseItem) item);
				}
			}
			return true;
		}
		super.add(values);
		return true;
	}

	/**
	 * Method to add a new Child to List.
	 *
	 * @param value the new Child
	 * @return XMLEntity Instance
	 */
	public XMLEntity withChild(BaseItem value) {
		if (this.children == null) {
			this.children = new SimpleList<BaseItem>();
		}
		this.children.add(value);
		return this;
	}

	/**
	 * Method to add a new Child to List.
	 *
	 * @param value the new Child
	 * @return XMLEntity Instance
	 */
	public XMLEntity withoutChild(BaseItem value) {
		if (this.children == null) {
			return this;
		}
		this.children.remove(value);
		return this;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public String getTag() {
		if (this.tag == null || this.tag.length() < 1) {
			return null;
		}
		return this.tag;
	}

	/**
	 * Clear children.
	 *
	 * @return the XML entity
	 */
	public XMLEntity clearChildren() {
		this.children = null;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		if (this.valueItem == null && this.sizeChildren() > 0) {
			/* Complex children */
			boolean show = false;
			for (int i = 0; i < this.children.size(); i++) {
				BaseItem item = this.children.get(i);
				if (item instanceof XMLEntity) {
					if (((XMLEntity) item).getTag() == null) {
						show = true;
						break;
					}
				}
			}
			if (show) {
				CharacterBuffer buffer = new CharacterBuffer();
				String value = null;
				for (int i = 0; i < this.children.size(); i++) {
					BaseItem item = this.children.get(i);
					if (value != null && value.endsWith(">")) {
						buffer.with(' ');
					}
					value = item.toString();
					buffer.with(value);
				}
				return buffer.toString();
			}
		}
		return this.valueItem;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	/**
	 * To string.
	 *
	 * @param indentFactor the indent factor
	 * @return the string
	 */
	@Override
	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	@Override
	protected String parseItem(EntityStringConverter converter) {
		if (converter == null) {
			return null;
		}
		CharacterBuffer sb = new CharacterBuffer().with(converter.getPrefixFirst());
		String tag = this.getTag();
		if (tag != null) {
			sb.with(START);
			sb.with(tag);
		}

		int size = size();
		for (int i = 0; i < size; i++) {
			Object value = getValueByIndex(i);
			if (value != null) {
				sb.with(" ", "" + get(i), "=", StringUtil.quote(value.toString()));
			}
		}

		toStringChildren(sb, converter);
		return sb.toString();
	}

	/**
	 * Add The Children to StringBuilder.
	 *
	 * @param sb        The StringBuilder where The Children add
	 * @param converter The Current Converter
	 */
	protected void toStringChildren(CharacterBuffer sb, EntityStringConverter converter) {
		/* parse Children */
		if (sb == null) {
			return;
		}
		String tag = this.getTag();
		if (this.children != null && this.children.size() > 0 && converter != null) {
			if (tag != null) {
				sb.with(END);
			}
			converter.add();
			for (BaseItem child : this.children) {
				sb.with(child.toString(converter));
			}
			converter.minus();
			sb.with(converter.getPrefix());
			if (tag != null) {
				if ("<!--".equals(tag)) {
					sb.with("--!>");
				} else {
					sb.with("</", tag);
					sb.with(END);
				}
			}
		} else if (this.valueItem != null) {
			if (tag != null) {
				sb.with(END);
			}
			sb.with(this.valueItem);
			if (tag != null) {
				sb.with("</", tag);
				sb.with(END);
			}
		} else {
			if (tag != null) {
				sb.with("/>");
			}
		}
	}

	/**
	 * With close tag.
	 *
	 * @return the XML entity
	 */
	public XMLEntity withCloseTag() {
		if (this.valueItem == null) {
			this.valueItem = "";
		}
		return this;
	}

	/**
	 * Gets the new entity.
	 *
	 * @return a new Instance of MapEntry
	 */
	public MapEntry getNewEntity() {
		return new MapEntry();
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public XMLEntity getNewList(boolean keyValue) {
		return new XMLEntity();
	}

	/**
	 * Static Method to generate XMLEntity.
	 *
	 * @param tag The name
	 * @return a new Instance of XMLEntity
	 */
	public static XMLEntity TAG(String tag) {
		return new XMLEntity().withType(tag);
	}

	/**
	 * With key value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the XML entity
	 */
	@Override
	public XMLEntity withKeyValue(Object key, Object value) {
		super.withKeyValue(key, value);
		return this;
	}

	/**
	 * Without.
	 *
	 * @param key the key
	 * @return the XML entity
	 */
	public XMLEntity without(String key) {
		remove(key);
		return this;
	}

	/**
	 * Checks for.
	 *
	 * @param key the key
	 * @return true, if successful
	 */
	@Override
	public boolean has(String key) {
		return containsKey(key);
	}

	/**
	 * With value.
	 *
	 * @param values the values
	 * @return the XML entity
	 */
	@Override
	public XMLEntity withValue(BufferItem values) {
		Tokener tokener = new XMLTokener();
		return withValue(tokener, values);
	}

	/**
	 * Sets the tag.
	 *
	 * @param value the new Tag
	 * @return the instance XMLEntity
	 */
	public XMLEntity withType(String value) {
		this.tag = value;
		return this;
	}

	/**
	 * Return first Children with Filter.
	 *
	 * @param key   The key of Filter
	 * @param value The Value of Filter
	 * @return first Children where match the Filter
	 */
	public Entity getElementBy(String key, String value) {
		if (value == null) {
			return null;
		}
		if (value.equalsIgnoreCase(getString(key))) {
			return this;
		}
		if (PROPERTY_TAG.equals(key)) {
			if (value.equalsIgnoreCase(this.getTag())) {
				return this;
			}
		}
		if (PROPERTY_VALUE.equals(key)) {
			if (value.equalsIgnoreCase(this.getValue())) {
				return this;
			}
		}
		if (this.children == null) {
			return null;
		}
		for (int i = 0; i < this.children.size(); i++) {
			BaseItem entity = this.children.get(i);
			if (entity instanceof XMLEntity) {
				Entity item = ((XMLEntity) entity).getElementBy(key, value);
				if (item != null) {
					return item;
				}
			}
			if (!(entity instanceof Entity)) {
				continue;
			}
			Entity item = (Entity) entity;
			if (value.equalsIgnoreCase(item.getString(key))) {
				return item;
			}
		}
		return null;
	}

	/**
	 * Return first Children with Filter.
	 *
	 * @param key   The key of Filter
	 * @param value The Value of Filter
	 * @return first Children where match the Filter
	 */
	public EntityList getElementsBy(String key, String value) {
		if (value == null) {
			return null;
		}
		EntityList children = getNewList(false);
		if (value.equalsIgnoreCase(getString(key))) {
			children.add(this);
		} else if (PROPERTY_TAG.equals(key)) {
			if (value.equalsIgnoreCase(this.getTag())) {
				children.add(this);
			}
		} else if (PROPERTY_VALUE.equals(key)) {
			if (value.equalsIgnoreCase(this.getValue())) {
				children.add(this);
			}
		} else if (StringUtil.CLASS.equals(key)) {
			int z = 0;
			while (z < value.length() && value.charAt(z) == ' ') {
				z++;
			}
			String first;
			int pos = value.indexOf(" ", z);
			if (pos < 0) {
				first = value.substring(z);
				pos = value.length();
			} else {
				first = value.substring(z, pos);
			}
			if (first.charAt(0) == '#') {
				if (first.substring(1).equals(this.getValue("id"))) {
					value = " " + value.substring(pos);
				}
			} else if (first.charAt(0) == '.') {
				if (first.substring(1).equals(this.getValue(StringUtil.CLASS))) {
					value = " " + value.substring(pos);
				}
			} else {
				if (first.equals(this.getTag())) {
					value = " " + value.substring(pos);
				}
			}
			if (value.length() == 1) {
				return this;
			}
		}
		if (this.children == null) {
			return children;
		}
		for (int i = 0; i < this.children.size(); i++) {
			BaseItem entity = this.children.get(i);
			if (entity instanceof XMLEntity) {
				EntityList items = ((XMLEntity) entity).getElementsBy(key, value);
				if (entity == items || items.size() > 0) {
					children.add(items);

				} else if (items.sizeChildren() > 0) {
					for (int c = 0; c < items.sizeChildren(); c++) {
						children.add(items.getChild(c));
					}
				}
			}else {
				if (!(entity instanceof Entity)) {
					continue;
				}
				Entity item = (Entity) entity;
				if (value.equalsIgnoreCase(item.getString(key))) {
					children.add(item);
				}
			}
		}
		if (children.sizeChildren() == 1) {
			/* to level the result graph */
			BaseItem result = children.getChild(0);
			if (result instanceof EntityList) {
				return (EntityList) result;
			}
		}
		return children;
	}

	/**
	 * With child.
	 *
	 * @param tag the tag
	 * @param values the values
	 * @return the XML entity
	 */
	public XMLEntity withChild(String tag, String... values) {
		createChild(tag, values);
		return this;
	}

	/**
	 * Method to create a new Child and add it to Children.
	 *
	 * @param tag    TagName
	 * @param values Values of Child
	 * @return XMLEntity new Instance
	 */
	public XMLEntity createChild(String tag, String... values) {
		XMLEntity child = new XMLEntity().withType(tag);
		this.withChild(child);
		if (values == null) {
			return this;
		}
		int i = 0;
		for (; i < values.length; i += 2) {
			if (i + 1 >= values.length) {
				break;
			}
			String key = values[i];
			String value = values[i + 1];
			child.add(key, value);
		}
		if (i < values.length) {
			child.withValueItem(values[i]);
		}
		return child;
	}

	/**
	 * With value item.
	 *
	 * @param value the value
	 * @return the XML entity
	 */
	public XMLEntity withValueItem(String value) {
		this.valueItem = value;
		return this;
	}

	/**
	 * Adds the comment.
	 *
	 * @param comment the comment
	 * @return the XML entity
	 */
	public XMLEntity addComment(String comment) {
		XMLEntity newComment = new XMLEntity();
		newComment.withType("<!--");
		if (comment == null) {
			return this;
		}
		newComment.withValueItem(comment);
		this.add(newComment);
		return this;
	}

	/**
	 * First child.
	 *
	 * @return the XML entity
	 */
	@Override
	public XMLEntity firstChild() {
		BaseItem child = this.getChild(0);
		if(child instanceof XMLEntity) {
			return (XMLEntity) child;
		}
		return null;
	}
	
	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public SimpleList<BaseItem> getChildren() {
		return children;
	}

	
}
