package de.uniks.networkparser.parser;

import java.nio.charset.Charset;
import java.util.Comparator;

import de.uniks.networkparser.Pos;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLTokener;

/**
 * The Class ExcelCell.
 *
 * @author Stefan
 */
public class ExcelCell implements SendableEntityCreatorTag, EntityList {
	
	/** The Constant TAG. */
	public static final String TAG = "c";
	
	/** The Constant PROPERTY_STYLE. */
	public static final String PROPERTY_STYLE = "s";
	
	/** The Constant PROPERTY_TYPE. */
	public static final String PROPERTY_TYPE = "t";
	
	/** The Constant PROPERTY_REFERENZ. */
	public static final String PROPERTY_REFERENZ = "r";

	/** The Constant CELLTYPE_EXTLST. */
	public static final String CELLTYPE_EXTLST = "extLst";
	
	/** The Constant CELLTYPE_FORMULAR. */
	public static final String CELLTYPE_FORMULAR = "f";
	
	/** The Constant CELLTYPE_RICHTEXT. */
	public static final String CELLTYPE_RICHTEXT = "is";
	
	/** The Constant CELLTYPE_VALUE. */
	public static final String CELLTYPE_VALUE = "v";

	/** The Constant PROPERTIES. */
	public static final String[] PROPERTIES = { PROPERTY_STYLE, PROPERTY_TYPE, PROPERTY_REFERENZ };
	private Pos pos;
	private Object content;
	private ExcelCell referenceCell;
	private String style;
	private String type;
	private SimpleList<EntityList> children;

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return PROPERTIES;
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @return the value
	 */
	@Override
	public Object getValue(Object entity, String attribute) {
		if (entity instanceof ExcelCell == false) {
			return null;
		}
		if (PROPERTY_STYLE.equals(attribute)) {
			return ((ExcelCell) entity).getStyle();
		}
		if (PROPERTY_TYPE.equals(attribute)) {
			return ((ExcelCell) entity).getType();
		}
		if (PROPERTY_REFERENZ.equals(attribute)) {
			return ((ExcelCell) entity).getReferenz();
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param entity the entity
	 * @param attribute the attribute
	 * @param value the value
	 * @param type the type
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(Object entity, String attribute, Object value, String type) {
		if (entity instanceof ExcelCell == false) {
			return false;
		}
		ExcelCell item = (ExcelCell) entity;
		if (PROPERTY_STYLE.equals(attribute)) {
			item.setStyle("" + value);
			return true;
		}
		if (PROPERTY_TYPE.equals(attribute)) {
			item.setType("" + value);
			return true;
		}
		if (PROPERTY_REFERENZ.equals(attribute)) {
			item.withReferenz(Pos.valueOf("" + value));
			return true;
		}
		if (XMLTokener.CHILDREN.equals(type)) {
			item.add(value);
			return true;
		}
		return false;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new ExcelCell();
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	@Override
	public String getTag() {
		return TAG;
	}

	/**
	 * Gets the referenz.
	 *
	 * @return the referenz
	 */
	public Pos getReferenz() {
		return pos;
	}

	/**
	 * With referenz.
	 *
	 * @param referenz the referenz
	 * @return the excel cell
	 */
	public ExcelCell withReferenz(Pos referenz) {
		this.pos = referenz;
		return this;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public String getStyle() {
		return style;
	}

	/**
	 * Gets the content.
	 *
	 * @return the content
	 */
	public Object getContent() {
		if (referenceCell != null) {
			return referenceCell.getContent();
		}
		return content;
	}

	/**
	 * Gets the content as string.
	 *
	 * @return the content as string
	 */
	public String getContentAsString() {
		if (referenceCell != null) {
			return referenceCell.getContentAsString();
		}
		if (content == null) {
			return "";
		}
		return content.toString();
	}

	/**
	 * Sets the content.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setContent(Object value) {
		if (referenceCell != null) {
			return referenceCell.setContent(value);
		}
		if ((this.content == null && value != null) || (this.content != null && this.content.equals(value) == false)) {
			this.content = value;
			return true;
		}
		return false;
	}

	/**
	 * With content.
	 *
	 * @param value the value
	 * @return the excel cell
	 */
	public ExcelCell withContent(Object value) {
		setContent(value);
		return this;
	}

	/**
	 * Gets the reference cell.
	 *
	 * @return the reference cell
	 */
	public ExcelCell getReferenceCell() {
		return referenceCell;
	}

	/**
	 * Sets the style.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setStyle(String value) {
		if ((this.style == null && value != null) || (this.style != null && this.style.equals(value) == false)) {
			this.style = value;
			return true;
		}
		return false;
	}

	/**
	 * Sets the type.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setType(String value) {
		if ((this.type == null && value != null) || (this.type != null && this.type.equals(value) == false)) {
			this.type = value;
			return true;
		}
		return false;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		String ref = "";
		if (this.pos != null) {
			ref = this.pos.toString();
		}
		Object context = getContent();
		if (context == null) {
			if (this.children != null) {
				CharacterBuffer buffer = new CharacterBuffer();
				for (EntityList item : this.children) {
					buffer.with(item.toString());
				}
				return buffer.toString();
			}
			return "";
		}
		if (context instanceof Number) {
			return "<c r=\"" + ref + "\"><v>" + context + "</v></c>";
		}
		if (context instanceof Boolean) {
			if ((Boolean) context) {
				return "<c r=\"" + ref + "\" t=\"b\"><v>1</v></c>";
			}
			return "<c r=\"" + ref + "\" t=\"b\"><v>0</v></c>";
		}
		return "<c r=\"" + ref + "\" t=\"inlineStr\"><is><t>"
				+ new String(context.toString().getBytes(Charset.forName("UTF-8")), Charset.forName("UTF-8"))
				+ "</t></is></c>";
	}

	/**
	 * To string.
	 *
	 * @param indentFactor the indent factor
	 * @return the string
	 */
	@Override
	public String toString(int indentFactor) {
		return toString();
	}

	protected String toString(int indentFactor, int indent) {
		return toString();
	}

	/**
	 * To string.
	 *
	 * @param converter the converter
	 * @return the string
	 */
	@Override
	public String toString(Converter converter) {
		return toString();
	}

	/**
	 * Adds the.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	@Override
	public boolean add(Object... values) {
		if (values == null) {
			return false;
		}
		if (children == null) {
			this.children = new SimpleList<EntityList>();
		}
		for (Object item : values) {
			if (item instanceof EntityList) {
				this.children.add((EntityList) item);
			}
		}
		return true;
	}

	/**
	 * Gets the new list.
	 *
	 * @param keyValue the key value
	 * @return the new list
	 */
	@Override
	public BaseItem getNewList(boolean keyValue) {
		return new ExcelCell();
	}

	/**
	 * Gets the children.
	 * 
	 * @param index the Index of Child
	 * @return the children
	 */
	public EntityList getChild(int index) {
		if (this.children == null || index < 0 || index > this.children.size()) {
			return null;
		}
		return this.children.get(index);
	}

	/**
	 * Size.
	 *
	 * @return the int
	 */
	public int size() {
		return sizeChildren();
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
	 * Sets the reference cell.
	 *
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setReferenceCell(ExcelCell value) {
		if ((this.referenceCell == null && value != null)
				|| (this.referenceCell != null && this.referenceCell.equals(value) == false)) {
			this.referenceCell = value;
			return true;
		}
		return false;
	}

	/**
	 * Creates the.
	 *
	 * @param content the content
	 * @return the excel cell
	 */
	public static ExcelCell create(Object content) {
		return new ExcelCell().withContent(content);
	}

	/**
	 * Checks if is comparator.
	 *
	 * @return true, if is comparator
	 */
	@Override
	public boolean isComparator() {
		return false;
	}

	/**
	 * Comparator.
	 *
	 * @return the comparator
	 */
	@Override
	public Comparator<Object> comparator() {
		return null;
	}

	/**
	 * With value.
	 *
	 * @param values the values
	 * @return the base item
	 */
	@Override
	public BaseItem withValue(BufferItem values) {
		return this;
	}

	/**
	 * First child.
	 *
	 * @return the base item
	 */
	@Override
	public BaseItem firstChild() {
		return getChild(0);
	}
}
