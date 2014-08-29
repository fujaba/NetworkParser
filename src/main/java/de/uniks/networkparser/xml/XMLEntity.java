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
import de.uniks.networkparser.AbstractKeyValueList;
import de.uniks.networkparser.AbstractList;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.event.MapEntry;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.FactoryEntity;
import de.uniks.networkparser.interfaces.StringItem;
/**
 * The Class XMLEntity.
 */

public class XMLEntity extends AbstractKeyValueList<String, Object> implements StringItem, FactoryEntity, Entity{
	public static final String PROPERTY_TAG="tag";
	public static final String PROPERTY_VALUE="value";
	/** The children. */
	protected ArrayList<XMLEntity> children;
	private boolean visible=true;

	/** The tag. */
	protected String tag;

	/** The value. */
	protected String value;

	/**
	 * Simple Constructor
	 */
	public XMLEntity() {
		this.allowDuplicate = false;
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
	public boolean addChild(XMLEntity child) {
		return getChildren().add(child);
	}

	/**
	 * Method to add a new Child to List
	 * @param child the new Child
	 * @return XMLEntity Instance
	 */
	public XMLEntity withChild(XMLEntity child) {
		getChildren().add(child);
		return this;
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
	 * @return the instance XMLEntity
	 */
	public XMLEntity withTag(String tag) {
		this.tag = tag;
		return this;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValueItem() {
		return this.value;
	}

	/**
	 * Sets the value.
	 *
	 * @param value
	 *            the new value
	 * @return the XMLEntity Instance
	 */
	public XMLEntity withValueItem(String value) {
		this.value = value;
		return this;
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
		if (intent > 0) {
			sb.append("\n");
		}
		sb.append(EntityUtil.repeat(' ', intent));
		sb.append("<" + this.getTag());

		int size=size();
		for (int i=0;i<size;i++) {
			sb.append(" " + get(i) + "=" + EntityUtil.quote("" + getValue(i)));
		}

		toStringChildren(sb, indentFactor, intent+indentFactor);
		return sb.toString();
	}

	protected void toStringChildren(StringBuilder sb, int indentFactor, int intent) {
		// parse Children
		if (this.children != null && this.children.size() > 0) {
			sb.append(">");
			for (XMLEntity child : this.children) {
				sb.append(child.toString(indentFactor, intent+indentFactor));
			}
			if (indentFactor > 0) {
				sb.append("\n");
			}
			sb.append(EntityUtil.repeat(' ', intent));
			sb.append("</" + getTag() + ">");
		} else if (this.value != null) {
			sb.append(">" + this.value);
			sb.append("</" + getTag() + ">");
		}else{
			sb.append("/>");
		}
	}

	@Override
	public XMLEntity with(Object... values) {
		for (Object value : values) {
			if (value instanceof XMLEntity) {
				addChild((XMLEntity)value);
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

	public MapEntry getNewEntity() {
		return new MapEntry();
	}

	@Override
	public XMLEntity getNewInstance() {
		return new XMLEntity();
	}

	@Override
	public Object remove(Object key) {
		return removeItemByObject("" +key);
	}

	/**
	 * Static Method to generate XMLEntity
	 * @param tag Tagname
	 * @return a new Instance of XMLEntity
	 */
	public static XMLEntity TAG(String tag) {
		return new XMLEntity().withTag(tag);
	}


	public XMLEntity withValue(Object key, Object value) {
		super.withValue(key, value);
		return this;
	}
}
