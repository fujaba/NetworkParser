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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.converter.EntityStringConverter;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
/**
 * The Class XMLEntity.
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
	 * @param value
	 *			the tag
	 * @return Itself
	 */
	public XMLEntity withValue(String value) {
		XMLTokener tokener = new XMLTokener();
		tokener.withBuffer(value);
		tokener.skipHeader();
		withValue(tokener);
		return this;
	}

	/**
	 * Construct a XMLEntity from a Tokener.
	 *
	 * @param tokener
	 *			A Tokener object containing the source string. or a duplicated
	 *			key.
	 * @return Itself
	 */
	public XMLEntity withValue(Tokener tokener) {
		if(tokener!=null) {
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
				this.setValueItem((String)values[0]);
			}
		}else if (values.length % 2 == 1) {
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
	 * @param value
	 *			the new Child
	 * @return XMLEntity Instance
	 */
	public XMLEntity withChild(EntityList value) {
		getChildren().add(value);
		return this;
	}

	/**
	 * Gets the child.
	 *
	 * @param value the tag to looking for
	 * @param recursiv deep search
	 * @return the child
	 */
	public EntityList getChild(String value, boolean recursiv) {
		if(value==null || this.children == null) {
			return null;
		}
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
		if(recursiv) {
			return null;
		}
		XMLEntity item = new XMLEntity().withTag(value);
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
	 * Sets the tag.
	 *
	 * @param value
	 *			the new Tag
	 * @return the instance XMLEntity
	 */
	public XMLEntity withTag(String value) {
		this.tag = value;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return this.valueItem;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *			the new value
	 * @return Success
	 */
	public boolean setValueItem(Object value) {
		if(value instanceof String) {
			this.valueItem = (String)value;
		}else {
			this.valueItem = ""+value;
		}
		return true;
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
		converter.add();
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
		converter.minus();
		return sb.toString();
	}

	/**
	 * Add The Children to StringBuilder.
	 *
	 * @param sb
	 *			The StringBuilder where The Children add
	 * @param converter
	 *			The Current Converter
	 */
	protected void toStringChildren(CharacterBuffer sb, EntityStringConverter converter) {
		// parse Children
		if (this.children != null && this.children.size() > 0) {
			if(this.getTag() != null) {
				sb.with(END);
			}
			for (EntityList child : this.children) {
				sb.with(child.toString(converter));
			}
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
	 * @param tag
	 *			Tagname
	 * @return a new Instance of XMLEntity
	 */
	public static XMLEntity TAG(String tag) {
		return new XMLEntity().withTag(tag);
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
	public Object getValue(int index) {
		return getValueByIndex(index);
	}

	public XMLEntity withValueItem(String value) {
		setValueItem(value);
		return this;
	}
}
