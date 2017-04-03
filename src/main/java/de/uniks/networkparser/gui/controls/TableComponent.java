package de.uniks.networkparser.gui.controls;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.gui.Column;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SortedList;

public class TableComponent extends Control {
	static final String TABLE = "table";

	public static final String PROPERTY_COLUMNS = "columns";

	public static final String PROPERTY_ITEM = "item";

	public static final String PROPERTY_PROPERTY = "property";

	public static final String PROPERTY_SEARCHCOLUMNS = "searchColumns";

	protected IdMap map;

	private SimpleList<Column> columns = new SimpleList<Column>();

	private SimpleList<Column> searchColumn = new SimpleList<Column>();

	protected Object source;

	private String property;

	protected SendableEntityCreator sourceCreator;


	//bridge.load({class:"table", property:"talk", columns:[{id:'room'}, {id:'day'}, {id:'talk'},  {id:'state'}], searchColumns:["day", "talk"]});

	public TableComponent() {
		super();
		this.className = TABLE;
		//addBaseElements(PROPERTY_PROPERTY);
		addBaseElements(PROPERTY_COLUMNS);
		addBaseElements(PROPERTY_ITEM);
		addBaseElements(PROPERTY_SEARCHCOLUMNS);
	}


	public boolean createFromCreator(SendableEntityCreator creator) {
		return false;
	}


	public TableComponent withColumn(Column... columns) {
		if (columns == null) {
			return this;
		}
		for (Column c : columns) {
			if (!this.columns.contains(c)) {
				// Save column
				this.columns.add(c);
			}
		}
		if (this.owner != null) {
			for (Column c : columns) {
				if (this.columns.add(c)) {
					// Add a new Column
					this.owner.fireControlChange(this, PROPERTY_COLUMNS, c);
				}
			}
		}
		return this;
	}
	//	showScrollbar(TableViewFX)
	//	withSearchProperties(String...)
	//	withElement(Node...)
	//	addItem(Object)
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


	public TableComponent withList(SortedList<Object> tableList) {
		return this;
	}


	public TableComponent withSearchProperties(String... elements) {
		return this;
	}


	public TableComponent withList(Object entity, String property) {
		return this;
	}


	public SimpleList<Column> getSearchColumn() {
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
			// TODO: curently no item..
			return null;
		}
		/*else if (PROPERTY_PROPERTY.equals(key)) {
			return this.property;
		}*/
		else if (PROPERTY_SEARCHCOLUMNS.equals(key)) {
			return this.searchColumn;
		}
		return super.getValue(key);
	}


	@Override
	public boolean setValue(String key, Object value) {
		if (PROPERTY_COLUMNS.equals(key)) {
			this.columns = (SimpleList<Column>) value;
		}
		else if (PROPERTY_ITEM.equals(key)) {
			// TODO: curently no item..
		}
		/*else if (PROPERTY_PROPERTY.equals(key)) {
			this.property = (String) value;
		}*/
		else if (PROPERTY_SEARCHCOLUMNS.equals(key)) {
			this.searchColumn = (SimpleList<Column>) value;
		}
		return super.setValue(key, value);
	}

}
