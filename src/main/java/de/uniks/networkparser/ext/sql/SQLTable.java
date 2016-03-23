package de.uniks.networkparser.ext.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.AbstractArray;
import de.uniks.networkparser.list.SimpleIterator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class SQLTable extends SimpleList<Object>{
	private String table;
	private boolean simple;
	

	public static SQLTable create(ResultSet executeQuery, SQLStatement statement, boolean dynamic) {
		AbstractArray<?> values = statement.getValues();
		String[] properties = new String[values.size()];
		String property = null;
		int counter = 0;
		for(SimpleIterator<String> i = new SimpleIterator<String>(values);i.hasNext();) {
			property = i.next();
			properties[counter] = property;
			counter++;
		}
		return create(executeQuery, properties, statement.getTable(), dynamic);
	}
	
	public static SQLTable create(ResultSet executeQuery, SendableEntityCreator creator) {
		String tableName;
		Object prototype = creator.getSendableInstance(true);
		if(prototype instanceof Class<?>) {
			tableName = EntityUtil.shortClassName(((Class<?>)prototype).getName()); 
		}else {
			tableName = EntityUtil.shortClassName(prototype.getClass().getName());
		}
		return create(executeQuery, creator.getProperties(), tableName, false);
	}
	
	public SimpleList<Object> getColumnValue(String column) {
		SimpleList<Object> values=new SimpleList<Object>();
		if(this.simple) {
			return this;
		}
		for(Iterator<Object> i = this.iterator();i.hasNext();) {
			Object item = i.next();
			if(item instanceof SimpleKeyValueList<?,?>) {
				SimpleKeyValueList<?,?> row = (SimpleKeyValueList<?, ?>) item;
				values.add(row.get(column));
			}
		}
		return values;
	}
		
	public static SQLTable create(ResultSet executeQuery, String[] properties, String table, boolean isDynamicResult) {
		SQLTable sqlTable = new SQLTable();
		sqlTable.withTable(table);
		if(isDynamicResult && properties.length != 1) {
			isDynamicResult = false;
		}
		if(properties != null) {
			try {
				if(isDynamicResult) {
					String prop = properties[0];
					while(executeQuery.next())
					{
						sqlTable.add(executeQuery.getObject(prop));
					}
					sqlTable.withSimple(true);
				} else {
					while(executeQuery.next())
					{
						SimpleKeyValueList<String, Object> row = new SimpleKeyValueList<String, Object>();
						for(String prop : properties) {
							row.add(prop, executeQuery.getObject(prop));
						}
						sqlTable.add(row);
					}
				}
			} catch (SQLException e) {
				System.out.println(e);
			}
		}
		return sqlTable;
	}

	private SQLTable withTable(String value) {
		this.table = value;
		return this;
	}
	
	public String getTable() {
		return this.table;
	}
	
	public boolean isSimple() {
		return simple;
	}
	public SQLTable withSimple(boolean value) {
		this.simple = value;
		return this;
	}
}
