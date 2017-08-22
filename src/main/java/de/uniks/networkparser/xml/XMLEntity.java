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
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.interfaces.BaseItem;
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
	 * Instantiates a new XMLEntity.
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
				Object item = tokener.getString(tokener.length() - tokener.position());
				if(item  != null) {
					this.valueItem = item .toString();
				}
				return this;
			}
			tokener.parseToEntity((Entity)this);
		}
		return this;
	}

	/**
	 * Gets the children.
	 * @param index the Index of Child
	 * @return the children
	 */
	public EntityList getChild(int index) {
		if (this.children == null || index < 0 || index > this.children.size()) {
			return null;
		}
		return this.children.get(index);
	}
	
	public int sizeChildren() {
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
	public boolean add(Object... values) {
		if(values==null || values.length < 1){
			return false;
		}
		if(values[0] instanceof String) {
			if(values.length == 1) {
				this.withValue((String)values[0]);
			}
		} else if (values.length % 2 == 1) {
			for(Object item : values) {
				if(item instanceof EntityList) {
					this.withChild((EntityList) item);
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
	 * @param value			the new Child
	 * @return XMLEntity	Instance
	 */
	public XMLEntity withChild(EntityList value) {
		if(this.children == null) {
			this.children = new SimpleList<EntityList>();
		}
		this.children.add(value);
		return this;
	}

	/**
	 * Method to create a new Child and add it to Children
	 *@param tag TagName
	 * @return XMLEntity	new Instance
	 */
	public XMLEntity createChild(String tag) {
		XMLEntity xmlEntity = new XMLEntity();
		withChild(xmlEntity);
		xmlEntity.setType(tag);
		return xmlEntity;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public String getTag() {
		if(this.tag == null || this.tag.length() <1) {
			return null;
		}
		return this.tag;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		if(this.valueItem == null && this.sizeChildren()>0 ) {
			// Complex children
			boolean show=false;
			for(int i=0;i<this.children.size();i++) {
				EntityList item = this.children.get(i);
				if(item instanceof XMLEntity) {
					if(((XMLEntity)item).getTag() == null) {
						show = true;
						break;
					}
				}
			}
			if(show) {
				CharacterBuffer buffer=new CharacterBuffer();
				String value = null;
				for(int i=0;i<this.children.size();i++) {
					EntityList item = this.children.get(i);
					if(value != null && value.endsWith(">")) {
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
		if(this.getTag() != null ) {
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
	 * @param value	the new Tag
	 * @return the instance XMLEntity
	 */
	public XMLEntity setType(String value) {
		this.tag = value;
		return this;
	}
	
	/**
	 * Return first Children with Filter
	 * @param key The key of Filter
	 * @param value The Value of Filter
	 * @return first Children where match the Filter
	 */
	public Entity getElementBy(String key, String value) {
		if(value == null) {
			return null;
		}
		if(value.equalsIgnoreCase(getString(key))) {
			return this;
		}
		if(PROPERTY_TAG.equals(key)) {
			if(value.equalsIgnoreCase(this.getTag())) {
				return this;
			}
		}
		if(PROPERTY_VALUE.equals(key)) {
			if(value.equalsIgnoreCase(this.getValue())) {
				return this;
			}
		}
		if(this.children == null) {
			return null;
		}
		for(int i=0;i<this.children.size();i++) {
			EntityList entity = this.children.get(i);
			if(entity instanceof XMLEntity) {
				Entity item = ((XMLEntity) entity).getElementBy(key, value);
				if(item != null) {
					return item;
				}
			}
			if(entity instanceof Entity == false) {
				continue;
			}
			Entity item = (Entity) entity;
			if(value.equalsIgnoreCase(item.getString(key))) {
				return item;
			}
		}
		return null;
	}
	/**
	 * Return first Children with Filter
	 * @param key The key of Filter
	 * @param value The Value of Filter
	 * @return first Children where match the Filter
	 */
	public EntityList getElementsBy(String key, String value) {
		if(value == null) {
			return null;
		}
		EntityList children=getNewList(false);
		if(value.equalsIgnoreCase(getString(key))) {
			children.add(this);
		} else if(PROPERTY_TAG.equals(key)) {
			if(value.equalsIgnoreCase(this.getTag())) {
				children.add(this);
			}
		} else if(PROPERTY_VALUE.equals(key)) {
			if(value.equalsIgnoreCase(this.getValue())) {
				children.add(this);
			}
		} else if(EntityUtil.CLASS.equals(key)) {
			int z=0;
			while(z<value.length() && value.charAt(z)==' ') {
				z++;
			}
			String first;
			int pos = value.indexOf(" ", z);
			if(pos<0) {
				first = value.substring(z);
				pos = value.length();
			}else {
				first = value.substring(z, pos);
			}
			if(first.charAt(0) == '#'){
				if(first.substring(1).equals(this.getValue("id"))) {
					value =" " + value.substring(pos);
				}
			} else if(first.charAt(0) == '.') {
				if(first.substring(1).equals(this.getValue(EntityUtil.CLASS))) {
					value = " " + value.substring(pos);
				}
			} else {
				if(first.equals(this.getTag())) {
					value = " " + value.substring(pos);
				}
			}
			if(value.length()==1) {
				return this;
			}
		}
		if(this.children == null) {
			return children;
		}
		for(int i=0;i<this.children.size();i++) {
			EntityList entity = this.children.get(i);
			if(entity instanceof XMLEntity) {
				EntityList items = ((XMLEntity) entity).getElementsBy(key, value);
				if(entity == items || items.size()>0) {
					children.add(items);
					
				} else if(items.sizeChildren()>0 ) {
					for(int c = 0;c < items.sizeChildren(); c++) {
						children.add(items.getChild(c));
					}
				}
			}
			if(entity instanceof Entity == false) {
				continue;
			}
			Entity item = (Entity) entity;
			if(value.equalsIgnoreCase(item.getString(key))) {
				children.add(item);
			}
		}
		if(children.sizeChildren()==1) {
			// to level the result graph
			BaseItem result = children.getChild(0);
			if(result instanceof EntityList) {
				return (EntityList) result;
			}
		}
		return children;
	}
	
	public XMLEntity withValueItem(String value) {
		this.valueItem = value;
		return this;
	}
}
