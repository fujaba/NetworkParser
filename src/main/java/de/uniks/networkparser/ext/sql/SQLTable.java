package de.uniks.networkparser.ext.sql;

/*
NetworkParser
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.AbstractArray;
import de.uniks.networkparser.list.SimpleIterator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class SQLTable extends SimpleList<Object> {
	private String table;
	private boolean simple;
	private static NetworkParserLog logger;

	public static SQLTable create(ResultSet executeQuery, SQLStatement statement, boolean dynamic) {
		AbstractArray<?> values = statement.getValues();
		String[] properties = new String[values.size()];
		String property = null;
		int counter = 0;
		for (SimpleIterator<String> i = new SimpleIterator<String>(values); i.hasNext();) {
			property = i.next();
			properties[counter] = property;
			counter++;
		}
		return create(executeQuery, properties, statement.getTable(), dynamic);
	}

	public static SQLTable create(ResultSet executeQuery, SendableEntityCreator creator) {
		String tableName;
		Object prototype = creator.getSendableInstance(true);
		if (prototype instanceof Class<?>) {
			tableName = StringUtil.shortClassName(((Class<?>) prototype).getName());
		} else {
			tableName = StringUtil.shortClassName(prototype.getClass().getName());
		}
		return create(executeQuery, creator.getProperties(), tableName, false);
	}

	public SimpleList<Object> getColumnValue(String column) {
		SimpleList<Object> values = new SimpleList<Object>();
		if (this.simple) {
			return this;
		}
		for (Iterator<Object> i = this.iterator(); i.hasNext();) {
			Object item = i.next();
			if (item instanceof SimpleKeyValueList<?, ?>) {
				SimpleKeyValueList<?, ?> row = (SimpleKeyValueList<?, ?>) item;
				values.add(row.get(column));
			}
		}
		return values;
	}

	public static SQLTable create(ResultSet executeQuery, String[] properties, String table, boolean isDynamicResult) {
		SQLTable sqlTable = new SQLTable();
		sqlTable.withTable(table);
		if (isDynamicResult && properties.length != 1) {
			isDynamicResult = false;
		}
		if (properties != null) {
			try {
				if (isDynamicResult) {
					String prop = properties[0];
					while (executeQuery.next()) {
						sqlTable.add(executeQuery.getObject(prop));
					}
					sqlTable.withSimple(true);
				} else {
					while (executeQuery.next()) {
						SimpleKeyValueList<String, Object> row = new SimpleKeyValueList<String, Object>();
						for (String prop : properties) {
							row.add(prop, executeQuery.getObject(prop));
						}
						sqlTable.add(row);
					}
				}
			} catch (SQLException e) {
			    if(logger != null) {
			        logger.error(executeQuery, "create", e);
			    }
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
