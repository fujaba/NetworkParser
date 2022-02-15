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
import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.list.AbstractArray;
import de.uniks.networkparser.list.SimpleIterator;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class SQLStatement.
 *
 * @author Stefan
 */
public class SQLStatement {
	
	/** The Constant ID. */
	public static final String ID = "_ID";
	
	/** The Constant VALUE. */
	public static final String VALUE = "VALUE";
	
	/** The Constant PROP. */
	public static final String PROP = "PROP";

	protected SQLCommand command;
	protected String table;
	private boolean autoStatement;
	private boolean enable = true;
	private AbstractArray<?> values;
	private SimpleKeyValueList<String, Object> conditions;
	
	/** The Constant SPACE. */
	public static final String SPACE = " ";
	
	/** The Constant EMPTY. */
	public static final String EMPTY = "";
	
	/** The Constant QUOTE. */
	public static final String QUOTE = "'";
	private static final String DRIVER = "DRIVER";
	private static final String DATABASE = "DATABASE";

	/**
	 * Instantiates a new SQL statement.
	 *
	 * @param command the command
	 */
	public SQLStatement(SQLCommand command) {
		this.command = command;
	}

	/**
	 * Instantiates a new SQL statement.
	 *
	 * @param command the command
	 * @param table the table
	 */
	public SQLStatement(SQLCommand command, String table) {
		this.command = command;
		this.table = table;
	}

	/**
	 * Instantiates a new SQL statement.
	 *
	 * @param command the command
	 * @param table the table
	 * @param id the id
	 */
	public SQLStatement(SQLCommand command, String table, String id) {
		this.command = command;
		this.table = table;
		this.with(ID, id);
	}

	/**
	 * Gets the command.
	 *
	 * @return the command
	 */
	public SQLCommand getCommand() {
		return command;
	}

	/**
	 * With command.
	 *
	 * @param command the command
	 * @return the SQL statement
	 */
	public SQLStatement withCommand(SQLCommand command) {
		this.command = command;
		return this;
	}

	/**
	 * Gets the table.
	 *
	 * @return the table
	 */
	public String getTable() {
		return table;
	}

	/**
	 * With table.
	 *
	 * @param value the value
	 * @return the SQL statement
	 */
	public SQLStatement withTable(String value) {
		this.table = value;
		return this;
	}

	/**
	 * Checks if is auto statement.
	 *
	 * @return true, if is auto statement
	 */
	public boolean isAutoStatement() {
		return autoStatement;
	}

	/**
	 * With auto statement.
	 *
	 * @param autoStatement the auto statement
	 * @return the SQL statement
	 */
	public SQLStatement withAutoStatement(boolean autoStatement) {
		this.autoStatement = autoStatement;
		return this;
	}

	/**
	 * Checks if is enable.
	 *
	 * @return true, if is enable
	 */
	public boolean isEnable() {
		return enable;
	}

	/**
	 * With enable.
	 *
	 * @param enable the enable
	 * @return the SQL statement
	 */
	public SQLStatement withEnable(boolean enable) {
		this.enable = enable;
		return this;
	}

	/**
	 * Auto disable.
	 *
	 * @return true, if successful
	 */
	public boolean autoDisable() {
		if (isAutoStatement()) {
			this.withEnable(false);
			return true;
		}
		return false;
	}

	/**
	 * Connect.
	 *
	 * @param driver the driver
	 * @param database the database
	 * @param url the url
	 * @return the SQL statement
	 */
	public static SQLStatement connect(String driver, String database, String url) {
		SQLStatement connectStatement = new SQLStatement(SQLCommand.CONNECTION, url);
		connectStatement.with(DRIVER, driver);
		connectStatement.with(DATABASE, database);
		return connectStatement;
	}

	/**
	 * Update.
	 *
	 * @param table the table
	 * @param id the id
	 * @param property the property
	 * @param newValue the new value
	 * @return the SQL statement
	 */
	public static SQLStatement update(String table, String id, String property, Object newValue) {
		SQLStatement updateStatement = new SQLStatement(SQLCommand.UPDATE, table);
		updateStatement.withCondition(ID, id);
		updateStatement.withValues(property, newValue);
		return updateStatement;
	}

	/**
	 * With.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the SQL statement
	 */
	public SQLStatement with(String key, Object value) {
		if (values == null) {
			values = new SimpleKeyValueList<String, Object>();
		}
		if (values instanceof SimpleKeyValueList<?, ?>) {
			((SimpleKeyValueList<?, ?>) values).withKeyValue(key, value);
		}
		return this;
	}

	/**
	 * Without.
	 *
	 * @param key the key
	 * @return the SQL statement
	 */
	public SQLStatement without(String key) {
		if (values != null && values instanceof SimpleKeyValueList<?, ?>) {
			((SimpleKeyValueList<?, ?>) values).without(key);
		}
		return this;
	}

	/**
	 * With values.
	 *
	 * @param values the values
	 * @return the SQL statement
	 */
	public SQLStatement withValues(Object... values) {
		if (values == null) {
			return this;
		}
		if (this.values == null) {
			this.values = new SimpleList<Object>();
		}
		if (this.values instanceof SimpleList<?> == false) {
			return this;
		}
		SimpleList<?> list = (SimpleList<?>) this.values;
		for (Object item : values) {
			list.with(item);
		}
		return this;
	}

	/**
	 * With condition.
	 *
	 * @param key the key
	 * @param value the value
	 * @return the SQL statement
	 */
	public SQLStatement withCondition(String key, Object value) {
		if (conditions == null) {
			conditions = new SimpleKeyValueList<String, Object>();
		}
		conditions.add(key, value);
		return this;
	}

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		/* CONNECTION */
		if (command == SQLCommand.CONNECTION) {
			String driver = "";
			String database = "";
			if (values instanceof SimpleKeyValueList<?, ?>) {
				SimpleKeyValueList<?, ?> items = (SimpleKeyValueList<?, ?>) values;
				driver = (String) items.getValue(DRIVER);
				database = (String) items.getValue(DATABASE);
			}
			return driver + ":" + database + ":" + this.table;
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;

		/* ADD COMMAND */		
		sb.append(command.getValue()).append(SPACE);

		/* SELECT STATEMENT */
		if (command == SQLCommand.SELECT) {
			if (values instanceof SimpleList<?>) {
				SimpleIterator<String> i = new SimpleIterator<String>(values);
				String item = null;
				for (; i.hasNext();) {
					item = i.next();
					if (first == false) {
						sb.append(", ");
					}
					first = false;
					sb.append("\"" + item + "\"");
				}
				sb.append(" FROM ");
				sb.append(this.table);
			}
			addCondition(sb);
			return sb.toString();
		}
		/* All Other Statements */
		sb.append(this.table).append(SPACE);
		if (command == SQLCommand.DROPTABLE) {
			return sb.toString();
		}
		if (command == SQLCommand.DELETE) {
			addCondition(sb);
			return sb.toString();
		}
		if (command == SQLCommand.CREATETABLE) {
			if (values instanceof SimpleKeyValueList<?, ?>) {
				sb.append("(");
				SimpleIteratorSet<String, Object> i = new SimpleIteratorSet<String, Object>(values);
				Entry<String, Object> item = null;
				for (; i.hasNext();) {
					item = i.next();
					if (first == false) {
						sb.append(", ");
					} else {
						first = false;
					}
					sb.append("'" + item.getKey() + "'").append(" ").append(item.getValue());
				}
				sb.append(")");
			}
			return sb.toString();
		}
		if (command == SQLCommand.INSERT) {
			if (values.size() > 0) {
				if (values instanceof SimpleKeyValueList<?, ?>) {
					SimpleIteratorSet<String, Object> i = new SimpleIteratorSet<String, Object>(values);
					Entry<String, Object> item = i.next();
					StringBuilder values = new StringBuilder();
					values.append(QUOTE).append(item.getValue()).append(QUOTE);
					if (EMPTY.equals(item.getKey())) {
						for (; i.hasNext();) {
							item = i.next();
							values.append(", '").append(i.next().getValue()).append(QUOTE);
						}
					} else {
						StringBuilder keys = new StringBuilder();
						keys.append(item.getKey());
						for (; i.hasNext();) {
							item = i.next();
							keys.append(", ").append(item.getKey());
							values.append(", '");
							convertValue(values, item.getValue());
							values.append(QUOTE);
						}
						sb.append("(").append(keys.toString()).append(") ");
					}
					sb.append("values(").append(values.toString()).append(")");
				}
			}
			return sb.toString();
		}
		if (command == SQLCommand.UPDATE) {
			if (values instanceof SimpleKeyValueList<?, ?>) {
				sb.append("SET ");
				SimpleIteratorSet<String, Object> i = new SimpleIteratorSet<String, Object>(values);
				Entry<String, Object> item = null;
				for (; i.hasNext();) {
					item = i.next();
					if (first == false) {
						sb.append(", ");
					}
					first = false;
					sb.append(item.getKey()).append("=").append(QUOTE);
					convertValue(sb, item.getValue());
					sb.append(QUOTE);
				}
				addCondition(sb);
			}
		}
		return sb.toString();
	}

	private void convertValue(StringBuilder sb, Object value) {
		if (value == null) {
			return;
		}
		if (value instanceof SimpleSet<?>) {
			SimpleSet<?> collection = (SimpleSet<?>) value;
			sb.append('{');
			for (Iterator<?> i = collection.iterator(); i.hasNext();) {
				sb.append(i.next().toString());
				if (i.hasNext()) {
					sb.append(", ");
				}
			}
			sb.append('}');
			return;
		}
		sb.append(value.toString());
	}

	private void addCondition(StringBuilder sb) {
		if (conditions != null) {
			boolean first = true;

			sb.append(" WHERE ");

			for (Iterator<Entry<String, Object>> i = conditions.entrySet().iterator(); i.hasNext();) {
				Entry<String, Object> item = i.next();
				if (first == false) {
					sb.append(", ");
				} else {
					first = false;
				}
				if (item.getValue() instanceof SimpleList<?>) {
					SimpleList<?> values = (SimpleList<?>) item.getValue();
					sb.append(item.getKey()).append(SPACE).append("IN").append(SPACE).append("(");
					boolean newFirst = true;
					for (Object object : values) {
						if (newFirst == false) {
							sb.append(", ");
						} else {
							newFirst = false;
						}
						sb.append("'" + object + "'");
					}
					sb.append(")");
				} else {
					sb.append(item.getKey()).append("=").append("'" + item.getValue() + "'");
				}
			}
		}
	}

	/**
	 * Gets the values.
	 *
	 * @return the values
	 */
	public AbstractArray<?> getValues() {
		return values;
	}

	/**
	 * Gets the primary id.
	 *
	 * @return the primary id
	 */
	public String getPrimaryId() {
		SimpleIteratorSet<String, Object> i;
		if (command == SQLCommand.SELECT) {
			i = new SimpleIteratorSet<String, Object>(conditions);
		} else {
			i = new SimpleIteratorSet<String, Object>(values);
		}
		Entry<String, Object> item = null;
		for (; i.hasNext();) {
			item = i.next();
			if (ID.equals(item.getKey())) {
				return (String) item.getValue();
			}
		}
		if (command == SQLCommand.UPDATE) {
			i = new SimpleIteratorSet<String, Object>(conditions);
			for (; i.hasNext();) {
				item = i.next();
				if (ID.equals(item.getKey())) {
					return (String) item.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * Without condition.
	 *
	 * @param key the key
	 * @return the SQL statement
	 */
	public SQLStatement withoutCondition(String key) {
		if (conditions != null) {
			conditions.remove(key);
		}
		return this;
	}
}
