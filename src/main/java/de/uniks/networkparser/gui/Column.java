package de.uniks.networkparser.gui;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2017 Stefan Lindel https://github.com/fujaba/NetworkParser/

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
import de.uniks.networkparser.EntityValueFactory;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.Style;
import de.uniks.networkparser.interfaces.GUIPosition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorNoIndex;

public class Column implements SendableEntityCreatorNoIndex {
	public static final int AUTOWIDTH = -1;

	public static final String PROPERTY_ID = "id";

	public static final String PROPERTY_STYLE = "style";

	public static final String PROPERTY_ACTIVESTYLE = "activeStyle";

	public static final String PROPERTY_ATTRIBUTE = "attribute";

	public static final String PROPERTY_NUMBERFORMAT = "numberformat";

	public static final String PROPERTY_EDITCOLUMN = "editColumn";

	public static final String PROPERTY_LABEL = "label";

	public static final String PROPERTY_DEFAULTTEXT = "defaulttext";

	public static final String PROPERTY_RESIZE = "resize";

	public static final String PROPERTY_VISIBLE = "visible";

	public static final String PROPERTY_MOVABLE = "movable";

	public static final String PROPERTY_ALTTEXT = "altText";

	public static final String PROPERTY_BROWSERID = "browserid";

	public static final String PROPERTY_FIELDTYP = "fieldTyp";

	public static final String FORMAT_DATE = "HH:MM:SS";

	private static final String[] properties = new String[] {
			Column.PROPERTY_ID,
			Column.PROPERTY_ATTRIBUTE, Column.PROPERTY_NUMBERFORMAT,
			Column.PROPERTY_EDITCOLUMN, Column.PROPERTY_LABEL,
			Column.PROPERTY_DEFAULTTEXT, Column.PROPERTY_RESIZE,
			Column.PROPERTY_VISIBLE, Column.PROPERTY_MOVABLE,
			Column.PROPERTY_ALTTEXT, Column.PROPERTY_BROWSERID,
			Column.PROPERTY_FIELDTYP, Column.PROPERTY_STYLE,
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
	 *
	 * @return the ID
	 */
	public String getId() {
		return id;
	}

	/**
	 *
	 * @param id the new id
	 * @return itself
	 */
	public Column withID(String id){
		this.id = id;
		return this;
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	public String getLabelOrAttrName() {
		if (label == null) {
			return attrName;
		}
		return label;
	}


	/**
	 * @param label the label to set
	 * @return Itself
	 */
	public Column withLabel(String label) {
		this.label = label;
		return this;
	}


	/** @return the Attribute Name for display */
	public String getAttrName() {
		return attrName;
	}


	/**
	 * @param attrName Attribute Name for display
	 * @return Itself
	 */
	public Column withAttribute(String attrName) {
		this.attrName = attrName;
		return this;
	}


	/**
	 * @param attrName Attribute Name for display
	 * @param edit is the Column is editable
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
	 * @return the NumberFormat
	 */
	public String getNumberFormat() {
		return numberFormat;
	}


	/**
	 * @param value the NumberFormat to set
	 * @return Itself
	 */
	public Column withNumberFormat(String value) {
		this.numberFormat = value;
		return this;
	}


	/**
	 * @return the editColumn
	 */
	public boolean isEditable() {
		return isEditable;
	}


	/**
	 * @param value the editColumn to set
	 * @return Itself
	 */
	public Column withEditable(boolean value) {
		this.isEditable = value;
		return this;
	}


	public boolean isResizable() {
		return isResizable;
	}


	public Column withResizable(boolean isResizable) {
		this.isResizable = isResizable;
		return this;
	}


	public EntityValueFactory getCellValueCreator() {
		return new EntityValueFactory();
	}


	public boolean isVisible() {
		return isVisible;
	}


	public Column withVisible(boolean isVisible) {
		this.isVisible = isVisible;
		return this;
	}


	public Column withAltAttribute(String altAttribute) {
		this.altAttribute = altAttribute;
		return this;
	}


	public String getAltAttribute() {
		return altAttribute;
	}


	public boolean isMovable() {
		return isMovable;
	}


	public Column withMovable(boolean isMovable) {
		this.isMovable = isMovable;
		return this;
	}


	public GUIPosition getBrowserId() {
		return browserId;
	}


	public Column withBrowserId(GUIPosition browserId) {
		this.browserId = browserId;
		return this;
	}


	public FieldTyp getFieldTyp() {
		return fieldTyp;
	}


	public Column withFieldTyp(FieldTyp fieldTyp) {
		this.fieldTyp = fieldTyp;
		return this;
	}


	public String getDefaultText() {
		return defaultText;
	}


	public Column withDefaultText(String defaultText) {
		this.defaultText = defaultText;
		return this;
	}


	public Style getStyle() {
		return style;
	}


	public Style getOrCreateStyle() {
		if (style == null) {
			style = new Style();
		}
		return style;
	}


	public Column withStyle(Style value) {
		this.style = value;
		return this;
	}


	public Style getActiveStyle() {
		return activestyle;
	}


	public Column withActiveStyle(Style activestyle) {
		this.activestyle = activestyle;
		return this;
	}


	public Column withActionHandler(CellHandler handler) {
		this.handler = handler;
		return this;
	}


	public CellHandler getListener() {
		if (handler == null) {
			handler = new CellHandler() {
				@Override
				public boolean onAction(Object entity,
						SendableEntityCreator creator, double x, double y) {
					return isEditable;
				}
			};
		}
		return handler;
	}


	public boolean isListener() {
		return handler != null;
	}


	public Comparator<TableCellValue> getComparator() {
		return comparator;
	}


	public Column withComparator(Comparator<TableCellValue> comparator) {
		this.comparator = comparator;
		return this;
	}


	public Column withComboValue(String value) {
		if (this.numberFormat == null || !this.numberFormat.startsWith("[")
			|| numberFormat.length() == 2) {
			this.numberFormat = "[" + value + "]";
			return this;
		}
		this.numberFormat = this.numberFormat.substring(0,
			this.numberFormat.length() - 1)
			+ "," + value + "]";
		return this;
	}


	@Override
	public String[] getProperties() {
		return properties;
	}


	@Override
	public Object getSendableInstance(boolean prototyp) {
		return new Column();
	}


	@Override
	public Object getValue(Object entity, String attribute) {
		String attrName;
		int pos = attribute.indexOf(".");
		if (pos > 0) {
			attrName = attribute.substring(0, pos);
		}
		else {
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


	@Override
	public boolean setValue(Object entity, String attribute, Object value,
			String type) {
		Column that = (Column) entity;

		if (attribute.equalsIgnoreCase(PROPERTY_ATTRIBUTE)) {
			that.withAttribute((String) value);
			return true;
		}
		if (attribute.equalsIgnoreCase(PROPERTY_ID)) {
			that.withID((String)value);
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
					//					for(StyleCrea)
				}
				else {
					that.withStyle((Style) value);
				}
			}
			else {
				System.out.println("FIXME");
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
		}
		else if ("CLAZZ".equals(getNumberFormat())) {
			if (entity != null) {
				return entity.getClass().getSimpleName();
			}
		}
		return null;
	}


	public boolean setValue(Object controll, Object entity,
			SendableEntityCreator creator, Object value) {
		if (creator == null) {
			return false;
		}
		return creator.setValue(entity, getAttrName(), value, SendableEntityCreator.UPDATE);
	}


	public Column withListener(CellHandler eventHandler) {
		this.handler = eventHandler;
		return this;
	}
}
