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
import java.util.ArrayList;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.StringItem;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
/**
 * The Class XMLEntity.
 */
public class XMLEntity extends SimpleKeyValueList<String, Object> implements StringItem, Entity {
	/** Constant of TAG. */
	public static final String PROPERTY_TAG = "tag";
	/** Constant of VALUE. */
	public static final String PROPERTY_VALUE = "value";
	/** The children. */
	private ArrayList<XMLEntity> children;

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
	 *            the tag
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
	 *            A Tokener object containing the source string. or a duplicated
	 *            key.
	 * @return Itself
	 */
	public XMLEntity withValue(Tokener tokener) {
		if(tokener!=null)
			tokener.parseToEntity(this);
		return this;
	}

	/**
	 * Gets the children.
	 *
	 * @return the children
	 */
	public ArrayList<XMLEntity> getChildren() {
		if (this.children == null) {
			this.children = new ArrayList<XMLEntity>();
		}
		return this.children;
	}

	/**
	 * Adds the child.
	 *
	 * @param child
	 *            the child
	 * @return result if the child is added
	 */
	public boolean addChild(XMLEntity child) {
		return getChildren().add(child);
	}

	/**
	 * Method to add a new Child to List.
	 *
	 * @param value
	 *            the new Child
	 * @return XMLEntity Instance
	 */
	public XMLEntity withChild(XMLEntity value) {
		getChildren().add(value);
		return this;
	}

	/**
	 * Gets the child.
	 *
	 * @param value
	 *            the tag
	 * @return the child
	 */
	public XMLEntity getChild(String value) {
		if(value==null) {
			return null;
		}
		for (XMLEntity entity : getChildren()) {
			if (value.equals(entity.getTag())) {
				return entity;
			}
		}
		return null;
	}

	/**
	 * Return all Children with Filter
	 * @param key The key of Filter
	 * @param value The Value of Filter
	 * @return all Children where match the Filter
	 */
	public SimpleList<XMLEntity> getChildren(String key, String value) {
		SimpleList<XMLEntity> children=new SimpleList<XMLEntity>();
		for (XMLEntity entity : getChildren()) {
			if(value.equalsIgnoreCase(entity.getString(key))) {
				children.add(entity);
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
	 *            the new Tag
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
	public String getValueItem() {
		return this.valueItem;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 * @return the XMLEntity Instance
	 */
	public XMLEntity withValueItem(String value) {
		this.valueItem = value;
		return this;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	@Override
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	@Override
	public String toString(int indentFactor, int intent) {
		StringBuilder sb = new StringBuilder();
		if (intent > 0) {
			sb.append("\n");
		}
		sb.append(EntityUtil.repeat(' ', intent));
		sb.append("<" + this.getTag());

		int size = size();
		for (int i = 0; i < size; i++) {
			Object value = getValueByIndex(i);
			if(value != null) {
				sb.append(" " + get(i) + "=" + EntityUtil.quote(value.toString()));	
			}
		}

		toStringChildren(sb, indentFactor, intent + indentFactor);
		return sb.toString();
	}

	/**
	 * Add The Children to StringBuilder.
	 *
	 * @param sb
	 *            The StringBuilder where The Children add
	 * @param indentFactor
	 *            IntentFactor for indent
	 * @param intent
	 *            Current Intent
	 */
	protected void toStringChildren(StringBuilder sb, int indentFactor,
			int intent) {
		// parse Children
		if (this.children != null && this.children.size() > 0) {
			sb.append(">");
			for (XMLEntity child : this.children) {
				sb.append(child.toString(indentFactor, intent + indentFactor));
			}
			if (indentFactor > 0) {
				sb.append("\n");
			}
			sb.append(EntityUtil.repeat(' ', intent));
			sb.append("</" + getTag() + ">");
		} else if (this.valueItem != null) {
			sb.append(">" + this.valueItem);
			sb.append("</" + getTag() + ">");
		} else {
			sb.append("/>");
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
	 *            Tagname
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
	
	public Entity without(String key) {
		remove(key);
		return this;
	}
}
