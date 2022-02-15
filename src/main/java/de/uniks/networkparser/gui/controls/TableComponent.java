package de.uniks.networkparser.gui.controls;

/*
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
import java.util.Collection;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

/**
 * The Class TableComponent.
 *
 * @author Stefan
 */
public class TableComponent extends Control {
	static final String TABLE = "table";

	/** The Constant PROPERTY_COLUMNS. */
	public static final String PROPERTY_COLUMNS = "columns";

	/** The Constant PROPERTY_SEARCHCOLUMNS. */
	public static final String PROPERTY_SEARCHCOLUMNS = "searchColumns";

	protected IdMap map;

	private SimpleList<Column> columns = new SimpleList<Column>();

	private SimpleList<String> searchColumn = new SimpleList<String>();

	private SimpleList<Object> items = new SimpleList<Object>();

	protected SendableEntityCreator sourceCreator;

	/* bridge.load({class:"table", property:"talk", columns:[{id:'room'},
	 {id:'day'}, {id:'talk'}, {id:'state'}], searchColumns:["day", "talk"]}); */

	/**
	 * Instantiates a new table component.
	 */
	public TableComponent() {
		super();
		this.className = TABLE;
		addBaseElements(PROPERTY_COLUMNS);
		addBaseElements(PROPERTY_ELEMENTS);
		addBaseElements(PROPERTY_SEARCHCOLUMNS);
	}

	/**
	 * Creates the from creator.
	 *
	 * @param creator the creator
	 * @return true, if successful
	 */
	public boolean createFromCreator(SendableEntityCreator creator) {
		return false;
	}

	/**
	 * With column.
	 *
	 * @param columns the columns
	 * @return the table component
	 */
	public TableComponent withColumn(Column... columns) {
		addColumn(columns);
		return this;
	}

	/**
	 * Adds the column.
	 *
	 * @param columns the columns
	 * @return true, if successful
	 */
	public boolean addColumn(Column... columns) {
		if (columns == null) {
			return false;
		}
		boolean changed = false;
		for (Column c : columns) {
			if (this.columns.add(c)) {
				changed = true;
				firePropertyChange(PROPERTY_COLUMNS, null, c);
			}
		}
		return changed;
	}

	/**
	 * Adds the search properties.
	 *
	 * @param elements the elements
	 * @return true, if successful
	 */
	public boolean addSearchProperties(String... elements) {
		if (elements == null) {
			return false;
		}
		boolean changed = false;
		for (String c : elements) {
			if (this.searchColumn.add(c)) {
				changed = true;
				firePropertyChange(PROPERTY_SEARCHCOLUMNS, null, c);
			}
		}
		return changed;
	}
	/* showScrollbar(TableViewFX) */
	/* withSearchProperties(String...) */
	/* removeItem(Object) */
	/* withList(Object, String) */
	/* getColumn(Column) */
	/* getProperty() */
	/* propertyChange(PropertyChangeEvent) */
	/* getColumnIterator() */
	/* getCreator(Object) */
	/* getElement(int) */
	/* getItems() */
	/* getColumns() */
	/* changed(ObservableValue<? extends Number>, Number, Number) */
	/* getFieldFactory() */
	/* withCounterColumn(Column) */
	/* getSelection() */
	/* saveColumns() */
	/* loadColumns(JsonArray, boolean) */
	/* addItemsFromPropertyChange(SendableEntity, String, String) */

	/**
	 * With map.
	 *
	 * @param map the map
	 * @return the table component
	 */
	public TableComponent withMap(IdMap map) {
		return this;
	}

	/**
	 * With search properties.
	 *
	 * @param elements the elements
	 * @return the table component
	 */
	public TableComponent withSearchProperties(String... elements) {
		addSearchProperties(elements);
		return this;
	}

	/**
	 * With list.
	 *
	 * @param entity the entity
	 * @param property the property
	 * @return the table component
	 */
	public TableComponent withList(Object entity, String property) {
		return this;
	}

	/**
	 * With list.
	 *
	 * @param tableList the table list
	 * @return the table component
	 */
	public TableComponent withList(Collection<Object> tableList) {
		return this;
	}

	/**
	 * With list.
	 *
	 * @param items the items
	 * @return the table component
	 */
	public TableComponent withList(Object... items) {
		addList(items);
		return this;
	}

	/**
	 * Adds the list.
	 *
	 * @param elements the elements
	 * @return true, if successful
	 */
	public boolean addList(Object... elements) {
		if (elements == null) {
			return false;
		}
		boolean changed = false;
		for (Object c : elements) {
			if (this.items.add(c)) {
				changed = true;
				firePropertyChange(PROPERTY_ELEMENTS, null, c);
			}
		}
		return changed;
	}

	/**
	 * Gets the search column.
	 *
	 * @return the search column
	 */
	public SimpleList<String> getSearchColumn() {
		return searchColumn;
	}

	/**
	 * Gets the property.
	 *
	 * @return the property
	 */
	public String getProperty() {
		return property;
	}

	/**
	 * Gets the value.
	 *
	 * @param key the key
	 * @return the value
	 */
	@Override
	public Object getValue(String key) {
		if (PROPERTY_COLUMNS.equals(key)) {
			return this.columns;
		} else if (PROPERTY_ELEMENTS.equals(key)) {
			return this.items;
		} else if (PROPERTY_SEARCHCOLUMNS.equals(key)) {
			return this.searchColumn;
		}
		return super.getValue(key);
	}

	/**
	 * Sets the value.
	 *
	 * @param key the key
	 * @param value the value
	 * @return true, if successful
	 */
	@Override
	public boolean setValue(String key, Object value) {
		if (PROPERTY_COLUMNS.equals(key)) {
			if (value instanceof Column[]) {
				return this.addColumn((Column[]) value);
			} else if (value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>) value;
				Column[] array = ((Collection<?>) value).toArray(new Column[list.size()]);
				return this.addColumn(array);
			}
			return false;
		}
		if (PROPERTY_ELEMENTS.equals(key)) {
			if (value instanceof Object[]) {
				return this.addList((Object[]) value);
			} else if (value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>) value;
				Object[] array = ((Collection<?>) value).toArray(new Object[list.size()]);
				return this.addList(array);
			}
			return this.addList(value);
		}
		if (PROPERTY_SEARCHCOLUMNS.equals(key)) {
			if (value instanceof String) {
				return this.addSearchProperties((String) value);
			} else if (value instanceof String[]) {
				return this.addSearchProperties((String[]) value);
			} else if (value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>) value;
				String[] array = ((Collection<?>) value).toArray(new String[list.size()]);
				return this.addSearchProperties(array);
			}
			return false;
		}
		return super.setValue(key, value);
	}

	/**
	 * New instance.
	 *
	 * @return the table component
	 */
	@Override
	public TableComponent newInstance() {
		return new TableComponent();
	}
}
