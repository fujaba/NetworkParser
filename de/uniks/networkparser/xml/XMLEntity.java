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
import java.util.Iterator;

import de.uniks.networkparser.AbstractKeyValueEntry;
import de.uniks.networkparser.AbstractKeyValueList;
import de.uniks.networkparser.AbstractList;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.FactoryEntity;
import de.uniks.networkparser.interfaces.StringItem;
/**
 * The Class XMLEntity.
 */

public class XMLEntity extends AbstractKeyValueList<String, Object> implements StringItem, FactoryEntity{
	public static final String PROPERTY_TAG="tag";
	public static final String PROPERTY_VALUE="value";
	/** The children. */
	protected ArrayList<XMLEntity> children;
	private boolean visible=true;

	/** The tag. */
	protected String tag;

	/** The value. */
	protected String value;

	@Override
	protected boolean initAllowDuplicate() {
		return false;
	}
	
	/**
	 * Instantiates a new xML entity.
	 * 
	 * @param value
	 *            the tag
	 * @return Itself
	 */
	public XMLEntity withValue(String value) {
		withValue(new XMLTokener().withText(value));
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
	public boolean add(XMLEntity child) {
		return getChildren().add(child);
	}

	/**
	 * Gets the child.
	 * 
	 * @param tag
	 *            the tag
	 * @return the child
	 */
	public XMLEntity getChild(String tag) {
		for (XMLEntity entity : getChildren()) {
			if (tag.equals(entity.getTag())) {
				return entity;
			}
		}
		return null;
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
	 * @param tag
	 *            the new tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Gets the value.
	 * 
	 * @return the value
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 * Sets the value.
	 * 
	 * @param value
	 *            the new value
	 */
	public void setValue(String value) {
		this.value = value;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni.kassel.peermessage.Entity#toString()
	 */
	@Override
	public String toString() {
		return toString(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni.kassel.peermessage.Entity#toString(int)
	 */
	@Override
	public String toString(int indentFactor) {
		return toString(indentFactor, 0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.uni.kassel.peermessage.Entity#toString(int, int)
	 */
	@Override
	public String toString(int indentFactor, int intent) {
		StringBuilder sb = new StringBuilder();
		if (indentFactor > 0) {
			sb.append("\n");
		}
		sb.append(EntityUtil.repeat(' ', indentFactor));
		sb.append("<" + this.getTag());

		for (Iterator<AbstractKeyValueEntry<String, Object>> i = iterator(); i.hasNext();) {
			Entry<String, Object> attribute = i.next();
			sb.append(" " + attribute.getKey() + "="
					+ EntityUtil.quote((String) attribute.getValue()));
		}

		boolean hasChild = (this.children != null && this.children.size() > 0);
		if (this.value == null && !hasChild) {
			sb.append("/>");
		} else {
			sb.append(">");
			sb.append(toStringValue(indentFactor));
			sb.append("</" + getTag() + ">");
		}
		return sb.toString();
	}

	protected String toStringValue(int indentFactor) {
		StringBuilder sb = new StringBuilder();

		// parse Children
		if (this.children != null && this.children.size() > 0) {
			for (XMLEntity child : this.children) {
				sb.append(child.toString(indentFactor));
			}
		} else if (this.value != null) {
			sb.append(this.value);
		}
		return sb.toString();
	}

	@Override
	public XMLEntity with(Object... values) {
		for(Object value : values){
			if(value instanceof XMLEntity){
				add((XMLEntity)value);
			}
		}
		return this;
	}

	@Override
	public BaseItem withVisible(boolean value) {
		this.visible = value;
		return this;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	@Override
	public AbstractList<?> getNewArray() {
		return new XMLEntity();
	}

	@Override
	public BaseItem getNewObject() {
		return new XMLEntity();
	}

	@Override
	public AbstractKeyValueEntry<String, Object> getNewEntity() {
		return new MapEntry();
	}

	@Override
	public AbstractList<AbstractKeyValueEntry<String, Object>> getNewInstance() {
		return new XMLEntity();
	}
}
