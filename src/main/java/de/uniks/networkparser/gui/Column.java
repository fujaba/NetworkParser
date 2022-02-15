package de.uniks.networkparser.gui;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2017 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

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
import java.util.Comparator;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

/**
 * The Class Column.
 *
 * @author Stefan
 */
public class Column implements SendableEntityCreatorNoIndex, CellHandler {
	
	/** The Constant AUTOWIDTH. */
	public static final int AUTOWIDTH = -1;

	/** The Constant PROPERTY_ID. */
	public static final String PROPERTY_ID = "id";

	/** The Constant PROPERTY_STYLE. */
	public static final String PROPERTY_STYLE = "style";

	/** The Constant PROPERTY_ACTIVESTYLE. */
	public static final String PROPERTY_ACTIVESTYLE = "activeStyle";

	/** The Constant PROPERTY_ATTRIBUTE. */
	public static final String PROPERTY_ATTRIBUTE = "attribute";

	/** The Constant PROPERTY_NUMBERFORMAT. */
	public static final String PROPERTY_NUMBERFORMAT = "numberformat";

	/** The Constant PROPERTY_EDITCOLUMN. */
	public static final String PROPERTY_EDITCOLUMN = "editColumn";

	/** The Constant PROPERTY_LABEL. */
	public static final String PROPERTY_LABEL = "label";

	/** The Constant PROPERTY_DEFAULTTEXT. */
	public static final String PROPERTY_DEFAULTTEXT = "defaulttext";

	/** The Constant PROPERTY_RESIZE. */
	public static final String PROPERTY_RESIZE = "resize";

	/** The Constant PROPERTY_VISIBLE. */
	public static final String PROPERTY_VISIBLE = "visible";

	/** The Constant PROPERTY_MOVABLE. */
	public static final String PROPERTY_MOVABLE = "movable";

	/** The Constant PROPERTY_ALTTEXT. */
	public static final String PROPERTY_ALTTEXT = "altText";

	/** The Constant PROPERTY_BROWSERID. */
	public static final String PROPERTY_BROWSERID = "browserid";

	/** The Constant PROPERTY_FIELDTYP. */
	public static final String PROPERTY_FIELDTYP = "fieldTyp";

	/** The Constant FORMAT_DATE. */
	public static final String FORMAT_DATE = "HH:MM:SS";

	private static final String[] properties = new String[] { Column.PROPERTY_ID, Column.PROPERTY_ATTRIBUTE,
			Column.PROPERTY_NUMBERFORMAT, Column.PROPERTY_EDITCOLUMN, Column.PROPERTY_LABEL,
			Column.PROPERTY_DEFAULTTEXT, Column.PROPERTY_RESIZE, Column.PROPERTY_VISIBLE, Column.PROPERTY_MOVABLE,
			Column.PROPERTY_ALTTEXT, Column.PROPERTY_BROWSERID, Column.PROPERTY_FIELDTYP, Column.PROPERTY_STYLE,
			Column.PROPERTY_ACTIVESTYLE };

	private Style style;

	private String id;

	private Style activestyle;

	private String attrName;

	private String numberFormat;

	private boolean isEditable = false;

	private String label;

	private String defaultText;

	private boolean isResizable = true;

	private boolean isVisible = true;

	private boolean isMovable = true;

	private String altAttribute;

	private FieldTyp fieldTyp;

	private GUIPosition browserId = GUIPosition.CENTER;

	protected CellHandler handler;

	private Comparator<TableCellValue> comparator;

	/**
	 * Gets the id.
	 *
	 * @return the ID
	 */
	public String getId() {
		return id;
	}

	/**
	 * With ID.
	 *
	 * @param id the new id
	 * @return itself
	 */
	public Column withID(String id) {
		this.id = id;
		return this;
	}

	/**
	 * Gets the label.
	 *
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Gets the label or attr name.
	 *
	 * @return the label or attr name
	 */
	public String getLabelOrAttrName() {
		if (label == null) {
			return attrName;
		}
		return label;
	}

	/**
	 * With label.
	 *
	 * @param label the label to set
	 * @return Itself
	 */
	public Column withLabel(String label) {
		this.label = label;
		return this;
	}

	/**
	 * Gets the attr name.
	 *
	 * @return the Attribute Name for display
	 */
	public String getAttrName() {
		return attrName;
	}

	/**
	 * With attribute.
	 *
	 * @param attrName Attribute Name for display
	 * @return Itself
	 */
	public Column withAttribute(String attrName) {
		this.attrName = attrName;
		return this;
	}

	/**
	 * With attribute.
	 *
	 * @param attrName Attribute Name for display
	 * @param edit     is the Column is editable
	 * @return Itself
	 */
	public Column withAttribute(String attrName, boolean edit) {
		this.attrName = attrName;
		if (label == null) {
			label = attrName;
		}
		withEditable(edit);
		return this;
	}

	/**
	 * Gets the number format.
	 *
	 * @return the NumberFormat
	 */
	public String getNumberFormat() {
		return numberFormat;
	}

	/**
	 * With number format.
	 *
	 * @param value the NumberFormat to set
	 * @return Itself
	 */
	public Column withNumberFormat(String value) {
		this.numberFormat = value;
		return this;
	}

	/**
	 * Checks if is editable.
	 *
	 * @return the editColumn
	 */
	public boolean isEditable() {
		return isEditable;
	}

	/**
	 * With editable.
	 *
	 * @param value the editColumn to set
	 * @return Itself
	 */
	public Column withEditable(boolean value) {
		this.isEditable = value;
		return this;
	}

	/**
	 * Checks if is resizable.
	 *
	 * @return true, if is resizable
	 */
	public boolean isResizable() {
		return isResizable;
	}

	/**
	 * With resizable.
	 *
	 * @param isResizable the is resizable
	 * @return the column
	 */
	public Column withResizable(boolean isResizable) {
		this.isResizable = isResizable;
		return this;
	}

	/**
	 * Checks if is visible.
	 *
	 * @return true, if is visible
	 */
	public boolean isVisible() {
		return isVisible;
	}

	/**
	 * With visible.
	 *
	 * @param isVisible the is visible
	 * @return the column
	 */
	public Column withVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}

	/**
	 * With alt attribute.
	 *
	 * @param altAttribute the alt attribute
	 * @return the column
	 */
	public Column withAltAttribute(String altAttribute) {
		this.altAttribute = altAttribute;
		return this;
	}

	/**
	 * Gets the alt attribute.
	 *
	 * @return the alt attribute
	 */
	public String getAltAttribute() {
		return altAttribute;
	}

	/**
	 * Checks if is movable.
	 *
	 * @return true, if is movable
	 */
	public boolean isMovable() {
		return isMovable;
	}

	/**
	 * With movable.
	 *
	 * @param isMovable the is movable
	 * @return the column
	 */
	public Column withMovable(boolean isMovable) {
		this.isMovable = isMovable;
		return this;
	}

	/**
	 * Gets the browser id.
	 *
	 * @return the browser id
	 */
	public GUIPosition getBrowserId() {
		return browserId;
	}

	/**
	 * With browser id.
	 *
	 * @param browserId the browser id
	 * @return the column
	 */
	public Column withBrowserId(GUIPosition browserId) {
		this.browserId = browserId;
		return this;
	}

	/**
	 * Gets the field typ.
	 *
	 * @return the field typ
	 */
	public FieldTyp getFieldTyp() {
		return fieldTyp;
	}

	/**
	 * With field typ.
	 *
	 * @param fieldTyp the field typ
	 * @return the column
	 */
	public Column withFieldTyp(FieldTyp fieldTyp) {
		this.fieldTyp = fieldTyp;
		return this;
	}

	/**
	 * Gets the default text.
	 *
	 * @return the default text
	 */
	public String getDefaultText() {
		return defaultText;
	}

	/**
	 * With default text.
	 *
	 * @param defaultText the default text
	 * @return the column
	 */
	public Column withDefaultText(String defaultText) {
		this.defaultText = defaultText;
		return this;
	}

	/**
	 * Gets the style.
	 *
	 * @return the style
	 */
	public Style getStyle() {
		return style;
	}

	/**
	 * Gets the or create style.
	 *
	 * @return the or create style
	 */
	public Style getOrCreateStyle() {
		if (style == null) {
			style = new Style();
		}
		return style;
	}

	/**
	 * With style.
	 *
	 * @param value the value
	 * @return the column
	 */
	public Column withStyle(Style value) {
		this.style = value;
		return this;
	}

	/**
	 * Gets the active style.
	 *
	 * @return the active style
	 */
	public Style getActiveStyle() {
		return activestyle;
	}

	/**
	 * With active style.
	 *
	 * @param activestyle the activestyle
	 * @return the column
	 */
	public Column withActiveStyle(Style activestyle) {
		this.activestyle = activestyle;
		return this;
	}

	/**
	 * With action handler.
	 *
	 * @param handler the handler
	 * @return the column
	 */
	public Column withActionHandler(CellHandler handler) {
		this.handler = handler;
		return this;
	}

	/**
	 * Gets the listener.
	 *
	 * @return the listener
	 */
	public CellHandler getListener() {
		if (handler == null) {
			handler = this;
		}
		return handler;
	}

	/**
	 * Checks if is listener.
	 *
	 * @return true, if is listener
	 */
	public boolean isListener() {
		return handler != null;
	}

	/**
	 * Gets the comparator.
	 *
	 * @return the comparator
	 */
	public Comparator<TableCellValue> getComparator() {
		return comparator;
	}

	/**
	 * With comparator.
	 *
	 * @param comparator the comparator
	 * @return the column
	 */
	public Column withComparator(Comparator<TableCellValue> comparator) {
		this.comparator = comparator;
		return this;
	}

	/**
	 * With combo value.
	 *
	 * @param value the value
	 * @return the column
	 */
	public Column withComboValue(String value) {
		if (this.numberFormat == null || !this.numberFormat.startsWith("[") || numberFormat.length() == 2) {
			this.numberFormat = "[" + value + "]";
			return this;
		}
		this.numberFormat = this.numberFormat.substring(0, this.numberFormat.length() - 1) + "," + value + "]";
		return this;
	}

	/**
	 * Gets the properties.
	 *
	 * @return the properties
	 */
	@Override
	public String[] getProperties() {
		return properties;
	}

	/**
	 * Gets the sendable instance.
	 *
	 * @param prototyp the prototyp
	 * @return the sendable instance
	 */
	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Column();
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
		if (!(entity instanceof Column) || attribute == null) {
			return null;
		}
		String attrName;
		int pos = attribute.indexOf(".");
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		} else {
			attrName = attribute;
		}
		Column that = (Column) entity;
		if (attrName.equalsIgnoreCase(PROPERTY_ATTRIBUTE))
			return that.getAttrName();
		if (attrName.equalsIgnoreCase(PROPERTY_ID))
			return that.getId();
		if (attrName.equalsIgnoreCase(PROPERTY_NUMBERFORMAT))
			return that.getNumberFormat();
		if (attrName.equalsIgnoreCase(PROPERTY_EDITCOLUMN))
			return that.isEditable();
		if (attrName.equalsIgnoreCase(PROPERTY_LABEL))
			return that.getLabel();
		if (attrName.equalsIgnoreCase(PROPERTY_DEFAULTTEXT))
			return that.getDefaultText();
		if (attrName.equalsIgnoreCase(PROPERTY_STYLE))
			return that.getStyle();
		if (attrName.equalsIgnoreCase(PROPERTY_ACTIVESTYLE))
			return that.getActiveStyle();
		if (attrName.equalsIgnoreCase(PROPERTY_RESIZE))
			return that.isResizable();
		if (attrName.equalsIgnoreCase(PROPERTY_VISIBLE))
			return that.isVisible();
		if (attrName.equalsIgnoreCase(PROPERTY_MOVABLE))
			return that.isMovable();
		if (attrName.equalsIgnoreCase(PROPERTY_ALTTEXT))
			return that.getAltAttribute();
		if (attrName.equalsIgnoreCase(PROPERTY_BROWSERID))
			return that.getBrowserId();
		if (attrName.equalsIgnoreCase(PROPERTY_FIELDTYP))
			return that.getFieldTyp();
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
		if (!(entity instanceof Column) || attribute == null) {
			return false;
		}
		Column that = (Column) entity;

		if (attribute.equalsIgnoreCase(PROPERTY_ATTRIBUTE)) {
			that.withAttribute((String) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			that.withID((String) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_NUMBERFORMAT)) {
			that.withNumberFormat((String) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_EDITCOLUMN)) {
			that.withEditable((Boolean) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_LABEL)) {
			that.withLabel((String) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_DEFAULTTEXT)) {
			that.withDefaultText((String) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_STYLE)) {
			if (value instanceof Style) {
				Style style = (Style) value;
				Style oldStyle = that.getStyle();
				if (Filter.MERGE.equals(type) && oldStyle != null) {
					for (String prop : style.getProperties()) {
						if (oldStyle.getValue(oldStyle, prop) == null) {
							oldStyle.setValue(oldStyle, prop, style.getValue(style, prop), SendableEntityCreator.NEW);
						}
					}
				} else {
					that.withStyle((Style) value);
				}
			}
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_ACTIVESTYLE)) {
			that.withActiveStyle((Style) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_RESIZE)) {
			that.withResizable((Boolean) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_VISIBLE)) {
			that.withVisible((Boolean) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_MOVABLE)) {
			that.withMovable((Boolean) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_ALTTEXT)) {
			that.withAltAttribute((String) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_BROWSERID)) {
			that.withBrowserId(GUIPosition.valueOf((String) value));
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_FIELDTYP)) {
			that.withFieldTyp(FieldTyp.valueOf("" + value));
			return true;
		}
		return false;
	}

	/**
	 * Gets the value.
	 *
	 * @param entity the entity
	 * @param creator the creator
	 * @return the value
	 */
	public Object getValue(Object entity, SendableEntityCreator creator) {
		String attrName = getAttrName();
		if (attrName != null) {
			if (attrName.startsWith("\"")) {
				return attrName.substring(1, attrName.length() - 1);
			}
			if (creator != null) {
				Object value = creator.getValue(entity, attrName);
				if (getNumberFormat() != null && value instanceof Long) {
					DateTimeEntity item = new DateTimeEntity();
					item.withValue((Long) value);
					return item.toString(getNumberFormat());
				}
				return value;
			}
		} else if ("CLAZZ".equals(getNumberFormat())) {
			if (entity != null) {
				return entity.getClass().getSimpleName();
			}
		}
		return null;
	}

	/**
	 * Sets the value.
	 *
	 * @param controll the controll
	 * @param entity the entity
	 * @param creator the creator
	 * @param value the value
	 * @return true, if successful
	 */
	public boolean setValue(Object controll, Object entity, SendableEntityCreator creator, Object value) {
		if (creator == null) {
			return false;
		}
		return creator.setValue(entity, getAttrName(), value, SendableEntityCreator.UPDATE);
	}

	/**
	 * With listener.
	 *
	 * @param eventHandler the event handler
	 * @return the column
	 */
	public Column withListener(CellHandler eventHandler) {
		this.handler = eventHandler;
		return this;
	}

	/**
	 * On action.
	 *
	 * @param entity the entity
	 * @param creator the creator
	 * @param x the x
	 * @param y the y
	 * @return true, if successful
	 */
	@Override
	public boolean onAction(Object entity, SendableEntityCreator creator, double x, double y) {
		return isEditable;
	}
}
