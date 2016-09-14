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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.converter.EntityStringConverter;
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
	private SimpleList<EntityList> children;

	public static final String START = "<";

	public static final String END = ">";

	/** The tag. */
	private String tag;

	/** The value. */
	private String valueItem;

	/** Simple Constructor. */
	public XMLEntity() {
		this.withAllowDuplicate(false);
	}

	/**
	 * Instantiates a new xML entity.
	 *
	 * @param value	the tag
	 * @return 		Itself
	 */
	public XMLEntity withValue(String value) {
		XMLTokener tokener = new XMLTokener();
		tokener.withBuffer(value);
		withValue(tokener);
		return this;
	}

	/**
	 * Construct a XMLEntity from a Tokener.
	 *
	 * @param tokener	A Tokener object containing the source string. or a duplicated key.
	 * @return 			Itself
	 */
	public XMLEntity withValue(Tokener tokener) {
		if(tokener!=null) {
			char c = tokener.nextClean(true);
			if(c != XMLTokener.ITEMSTART) {
				this.valueItem = tokener.getString(tokener.length() - tokener.position()).toString();
				return this;
			}
			tokener.parseToEntity((Entity)this);
		}
		return this;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public SimpleList<EntityList> getChildren() {
		if (this.children == null) {
			this.children = new SimpleList<EntityList>();
		}
		return this.children;
	}

	public int getChildrenCount() {
		if (this.children == null) {
			return 0;
		}
		return this.children.size();
	}

	/**
	 * Adds the child.
	 *
	 * @param values
	 *			the child
	 * @return result if the child is added
	 */
	@Override
	public XMLEntity with(Object... values) {
		if(values==null || values.length < 1){
			return this;
		}
		if(values[0] instanceof String) {
			if(values.length == 1) {
				this.withValue((String)values[0]);
			}
		} else if (values.length % 2 == 1) {
			for(Object item : values) {
				if(item instanceof EntityList) {
					getChildren().add((EntityList) item);
				}
			}
			return this;
		}
		super.with(values);
		return this;
	}

	/**
	 * Method to add a new Child to List.
	 *
	 * @param value			the new Child
	 * @return XMLEntity	Instance
	 */
	public XMLEntity withChild(EntityList value) {
		getChildren().add(value);
		return this;
	}

	/**
	 * Method to create a new Child and add it to Children
	 *
	 * @return XMLEntity	new Instance
	 */
	public XMLEntity createChild() {
		XMLEntity xmlEntity = new XMLEntity();
		getChildren().add(xmlEntity);
		return xmlEntity;
	}

	/**
	 * Gets the child.
	 *
	 * @param value the tag to looking for
	 * @param recursiv deep search
	 * @return the child
	 */
	public EntityList getChild(String value, boolean recursiv) {
		if(value==null ) {
			return null;
		}
		if(this.children != null) {
			for (EntityList entity : this.children) {
				if(entity instanceof XMLEntity == false) {
					continue;
				}
				XMLEntity item = (XMLEntity) entity;
				if (value.equals(item.getTag())) {
					return entity;
				}
				if(recursiv) {
					EntityList child = item.getChild(value, recursiv);
					if(child != null) {
						return child;
					}
				}
			}
		}
		if(recursiv) {
			return null;
		}
		XMLEntity item = new XMLEntity().setType(value);
		with(item);
		return item;
	}

	/**
	 * Return all Children with Filter
	 * @param key The key of Filter
	 * @param value The Value of Filter
	 * @return all Children where match the Filter
	 */
	public SimpleList<Entity> getChildren(String key, String value) {
		SimpleList<Entity> children=new SimpleList<Entity>();
		for (EntityList entity : getChildren()) {
			if(entity instanceof Entity) {
				continue;
			}
			Entity item = (Entity) entity;
			if(value.equalsIgnoreCase(item.getString(key))) {
				children.add(item);
			}
		}
		return children;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public String getTag() {
		return this.tag;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.valueItem;
	}

	@Override
	public String toString() {
		return parseItem(new EntityStringConverter());
	}

	@Override
	public String toString(int indentFactor) {
		return parseItem(new EntityStringConverter(indentFactor));
	}

	@Override
	protected String parseItem(EntityStringConverter converter) {
		CharacterBuffer sb = new CharacterBuffer().with(converter.getPrefixFirst());
		if(this.getTag() != null) {
			sb.with(START, this.getTag());
		}

		int size = size();
		for (int i = 0; i < size; i++) {
			Object value = getValueByIndex(i);
			if(value != null) {
				sb.with(" ", get(i), "=", EntityUtil.quote(value.toString()));
			}
		}

		toStringChildren(sb, converter);
		return sb.toString();
	}

	/**
	 * Add The Children to StringBuilder.
	 *
	 * @param sb			The StringBuilder where The Children add
	 * @param converter		The Current Converter
	 */
	protected void toStringChildren(CharacterBuffer sb, EntityStringConverter converter) {
		// parse Children
		if (this.children != null && this.children.size() > 0) {
			if(this.getTag() != null) {
				sb.with(END);
			}
			converter.add();
			for (EntityList child : this.children) {
				sb.with(child.toString(converter));
			}
			converter.minus();
			sb.with(converter.getPrefix());
			if(this.getTag() != null) {
				sb.with("</", getTag(), END);
			}
		} else if (this.valueItem != null) {
			if(this.getTag() != null) {
				sb.with(END);
			}
			sb.with(this.valueItem);
			if(this.getTag() != null) {
				sb.with("</", getTag(), END);
			}
		} else {
			if(this.getTag() != null) {
				sb.with("/>");
			}
		}
	}

	public XMLEntity withCloseTag() {
		if(this.valueItem==null) {
			this.valueItem = "";
		}
		return this;
	}

	/** @return a new Instance of MapEntry */
	public MapEntry getNewEntity() {
		return new MapEntry();
	}

	@Override
	public XMLEntity getNewList(boolean keyValue) {
		return new XMLEntity();
	}

	/**
	 * Static Method to generate XMLEntity.
	 *
	 * @param tag	The name
	 * @return 		a new Instance of XMLEntity
	 */
	public static XMLEntity TAG(String tag) {
		return new XMLEntity().setType(tag);
	}

	@Override
	public XMLEntity withKeyValue(Object key, Object value) {
		super.withKeyValue(key, value);
		return this;
	}

	public XMLEntity without(String key) {
		remove(key);
		return this;
	}

	@Override
	public boolean has(String key) {
		return containsKey(key);
	}

	@Override
	public XMLEntity withValue(Buffer values) {
		Tokener tokener = new XMLTokener().withBuffer(values);
		return withValue(tokener);
	}

	/**
	 * Sets the tag.
	 *
	 * @param value
	 *			the new Tag
	 * @return the instance XMLEntity
	 */
	@Override
	public XMLEntity setType(String value) {
		this.tag = value;
		return this;
	}
}
