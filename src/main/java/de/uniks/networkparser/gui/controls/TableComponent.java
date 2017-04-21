package de.uniks.networkparser.gui.controls;

import java.util.Collection;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;

public class TableComponent extends Control {
	static final String TABLE = "table";

	public static final String PROPERTY_COLUMNS = "columns";

	public static final String PROPERTY_ITEM = "item";

	public static final String PROPERTY_SEARCHCOLUMNS = "searchColumns";

	protected IdMap map;

	private SimpleList<Column> columns = new SimpleList<Column>();

	private SimpleList<String> searchColumn = new SimpleList<String>();

	private SimpleList<Object> items = new SimpleList<Object>();

	protected SendableEntityCreator sourceCreator;


	//bridge.load({class:"table", property:"talk", columns:[{id:'room'}, {id:'day'}, {id:'talk'},  {id:'state'}], searchColumns:["day", "talk"]});

	public TableComponent() {
		super();
		this.className = TABLE;
		addBaseElements(PROPERTY_COLUMNS);
		addBaseElements(PROPERTY_ITEM);
		addBaseElements(PROPERTY_SEARCHCOLUMNS);
	}


	public boolean createFromCreator(SendableEntityCreator creator) {
		return false;
	}

	public TableComponent withColumn(Column... columns) {
		addColumn(columns);
		return this;
	}
	public boolean addColumn(Column... columns) {
		if (columns == null) {
			return false;
		}
		boolean changed = false;
		for (Column c : columns) {
			if(this.columns.add(c)) {
				changed = true;
				firePropertyChange(PROPERTY_COLUMNS, null, c);
			}
		}
		return changed;
	}
	
	public boolean addSearchProperties(String... elements) {
		if (elements == null) {
			return false;
		}
		boolean changed = false;
		for (String c : elements) {
			if(this.searchColumn.add(c)) {
				changed = true;
				firePropertyChange(PROPERTY_SEARCHCOLUMNS, null, c);
			}
		}
		return changed;
	}
	//	showScrollbar(TableViewFX)
	//	withSearchProperties(String...)
	//	removeItem(Object)
	//	withList(Object, String)
	//	getColumn(Column)
	//	getProperty()
	//	propertyChange(PropertyChangeEvent)
	//	getColumnIterator()
	//	getCreator(Object)
	//	getElement(int)
	//	getItems()
	//	getColumns()
	//	changed(ObservableValue<? extends Number>, Number, Number)
	//	getFieldFactory()
	//	withCounterColumn(Column)
	//	getSelection()
	//	saveColumns()
	//	loadColumns(JsonArray, boolean)
	//	addItemsFromPropertyChange(SendableEntity, String, String)


	public TableComponent withMap(IdMap map) {
		return this;
	}



	public TableComponent withSearchProperties(String... elements) {
		addSearchProperties(elements);
		return this;
	}
	
	public TableComponent withList(Object entity, String property) {
		return this;
	}

	public TableComponent withList(Collection<Object> tableList) {
		return this;
	}
	public TableComponent withList(Object... items) {
		addList(items);
		return this;
	}

	public boolean addList(Object... elements) {
		if (elements == null) {
			return false;
		}
		boolean changed = false;
		for (Object c : elements) {
			if(this.items.add(c)) {
				changed = true;
				firePropertyChange(PROPERTY_ITEM, null, c);
			}
		}
		return changed;
	}
	

	public SimpleList<String> getSearchColumn() {
		return searchColumn;
	}

	public String getProperty() {
		return property;
	}


	@Override
	public Object getValue(String key) {
		if (PROPERTY_COLUMNS.equals(key)) {
			return this.columns;
		}
		else if (PROPERTY_ITEM.equals(key)) {
			return this.items;
		}
		else if (PROPERTY_SEARCHCOLUMNS.equals(key)) {
			return this.searchColumn;
		}
		return super.getValue(key);
	}


	@Override
	public boolean setValue(String key, Object value) {
		if (PROPERTY_COLUMNS.equals(key)) {
//			if(value instanceof Control) {
//				return this.addColumn((Control)value);
			if(value instanceof Column[]) {
				return this.addColumn((Column[])value);
			} else if(value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>)value;
				Column[] array = ((Collection<?>) value).toArray(new Column[list.size()]);
				return this.addColumn(array);
			}
			return false;
		}
		if (PROPERTY_ITEM.equals(key)) {
			if(value instanceof Object[]) {
				return this.addList((Object[])value);
			} else if(value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>)value;
				Object[] array = ((Collection<?>) value).toArray(new Object[list.size()]);
				return this.addList(array);
			}
			return this.addList(value);
		}
		if (PROPERTY_SEARCHCOLUMNS.equals(key)) {
			if(value instanceof String) {
				return this.addSearchProperties((String)value);
			} else if(value instanceof String[]) {
				return this.addSearchProperties((String[])value);
			} else if(value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>)value;
				String[] array = ((Collection<?>) value).toArray(new String[list.size()]);
				return this.addSearchProperties(array);
			}
			return false;
		}
		return super.setValue(key, value);
	}

}
