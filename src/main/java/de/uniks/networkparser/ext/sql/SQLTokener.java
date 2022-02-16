
package de.uniks.networkparser.ext.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.Grammar;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

/**
 * The Class SQLTokener.
 *
 * @author Stefan
 */
public class SQLTokener extends Tokener {
	private SQLStatement sqlConnection;
	private Connection connection;
	
	/** The Constant TABLE_FLAT. */
	public static final String TABLE_FLAT = "table";
	
	/** The Constant TABLE_PRIVOTISIERUNG. */
	public static final String TABLE_PRIVOTISIERUNG = "pivotisierung";
	
	/** The Constant FLAG_NONE. */
	public static final byte FLAG_NONE = 0x00;
	
	/** The Constant FLAG_CREATE. */
	public static final byte FLAG_CREATE = 0x01;
	
	/** The Constant FLAG_DROP. */
	public static final byte FLAG_DROP = 0x02;
	private static final String TYPE_INTEGER = "INTEGER";
	private static final String TYPE_STRING = "STRING";
	private static final String TYPE_OBJECT = "OBJECT";
	private byte flag = FLAG_CREATE;
	private String stragety = TABLE_FLAT;

	/**
	 * Instantiates a new SQL tokener.
	 *
	 * @param connection the connection
	 */
	public SQLTokener(SQLStatement connection) {
		this.sqlConnection = connection;
	}

	/**
	 * Instantiates a new SQL tokener.
	 *
	 * @param connection the connection
	 * @param stragety the stragety
	 */
	public SQLTokener(SQLStatement connection, String stragety) {
		this.sqlConnection = connection;
		this.stragety = stragety;
	}

	/**
	 * Encode.
	 *
	 * @param model the model
	 * @return the SQL statement list
	 */
	public SQLStatementList encode(GraphList model) {
		SQLStatementList result = new SQLStatementList();
		result.add(sqlConnection);
		for (Clazz clazz : model.getClazzes()) {
			result.add(new SQLStatement(SQLCommand.DROPTABLE, clazz.getName()));

			SQLStatement createClass = new SQLStatement(SQLCommand.CREATETABLE, clazz.getName(), TYPE_STRING);
			if (TABLE_FLAT.equalsIgnoreCase(stragety)) {
				parseAssociations(clazz, createClass);
				parseAttributes(clazz, createClass);
			} else {
				createClass.with(SQLStatement.PROP, TYPE_STRING);
				createClass.with(SQLStatement.VALUE, TYPE_OBJECT);
			}
			result.add(createClass);
		}
		return result;
	}

	private void parseAttributes(Clazz clazz, SQLStatement sqlClass) {
		for (Attribute attribute : clazz.getAttributes()) {
			sqlClass.with(attribute.getName(),
					StringUtil.convertPrimitiveToObjectType(attribute.getType().getName(true)).toUpperCase());
		}
	}

	private void parseAssociations(Clazz clazz, SQLStatement sqlClass) {
		String type = "";
		for (Association association : clazz.getAssociations()) {
			if (association.getCardinality() == Association.MANY) {
				type = TYPE_INTEGER;
			} else {
				type = "INTEGER[]";
			}
			sqlClass.with(association.getOther().getName(), type);
		}
	}

	/**
	 * Execute statements.
	 *
	 * @param statements the statements
	 * @return true, if successful
	 */
	public boolean executeStatements(SQLStatementList statements) {
		return executeStatements(statements, null, false);
	}

	/**
	 * Execute statements.
	 *
	 * @param statements the statements
	 * @param results the results
	 * @param dynamicTable the dynamic table
	 * @return true, if successful
	 */
	public boolean executeStatements(SQLStatementList statements, SimpleList<SQLTable> results, boolean dynamicTable) {
		boolean result = true;
		Connection connection = null;
		for (SQLStatement statement : statements) {
			if (!statement.isEnable()) {
				continue;
			}
			Statement query = null;
			try {
				if (statement.getCommand() == SQLCommand.CONNECTION) {
					if (connection != null) {
						disconnect(connection);
					}
					connection = connect(statement);
				} else {
					if (connection == null) {
						connection = connect(this.sqlConnection);
					}
					query = connection.createStatement();
					if (statement.getCommand() == SQLCommand.SELECT) {
						ResultSet executeQuery = query.executeQuery(statement.toString());
						if (results != null) {
							results.add(SQLTable.create(executeQuery, statement, dynamicTable));
						}
						result = true;
					} else {
						/* Check for Insert if really insert or Update */
						if (statement.isAutoStatement()) {
							if (statement.getCommand() == SQLCommand.INSERT
									|| statement.getCommand() == SQLCommand.UPDATE) {

							}
						}
						query.execute(statement.toString());
					}
				}
			} catch (Exception e) {
				result = false;
			} finally {
				if (query != null) {
					try {
						query.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		if (connection != null) {
			if (this.connection == null || this.connection != connection) {
				result = result & disconnect(connection);
			}
		}
		return result;
	}

	/**
	 * Disconnect.
	 *
	 * @param connection the connection
	 * @return true, if successful
	 */
	public boolean disconnect(Connection connection) {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			return false;
		}
		return true;
	}

	/**
	 * Close.
	 *
	 * @return true, if successful
	 */
	public boolean close() {
		if (this.connection == null) {
			return true;
		}
		Connection con = connection;
		this.connection = null;
		return this.disconnect(con);
	}

	/**
	 * Connect.
	 *
	 * @param connect the connect
	 * @return the connection
	 */
	public Connection connect(SQLStatement connect) {
		if (connect == null) {
			return null;
		}
		if (this.connection != null) {
			return connection;
		}
		String string = connect.toString();
		try {
			return DriverManager.getConnection(string);
		} catch (SQLException e) {

		}
		return null;
	}

	/**
	 * With connection.
	 *
	 * @param connection the connection
	 * @return the SQL tokener
	 */
	public SQLTokener withConnection(Connection connection) {
		this.connection = connection;
		return this;
	}

	/**
	 * Gets the connection.
	 *
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * Encode.
	 *
	 * @param entity the entity
	 * @param map the map
	 * @return the SQL statement list
	 */
	@Override
	public SQLStatementList encode(Object entity, MapEntity map) {
		SQLStatementList statements = new SQLStatementList();
		statements.add(sqlConnection);
		map.withTokenerFlag(flag);
		parseModel(map, entity, statements);
		SimpleList<SQLTable> results = new SimpleList<SQLTable>();
		validateStatements(statements, results);
		return statements;
	}

	/**
	 * Update.
	 *
	 * @param entity the entity
	 * @param id the id
	 * @param property the property
	 * @param newValue the new value
	 * @return the SQL statement list
	 */
	public SQLStatementList update(Object entity, String id, String property, Object newValue) {
		SQLStatementList statements = new SQLStatementList();
		statements.add(sqlConnection);
		SQLStatement command = SQLStatement.update(entity.getClass().getSimpleName(), id, property, newValue);
		statements.add(command);
		return statements;
	}

	private void addTableCreate(String tableName, Object item, SendableEntityCreator creator,
			SQLStatementList statements, MapEntity map) {
		if (!map.contains(tableName)) {
			if (map.isTokenerFlag(FLAG_DROP)) {
				statements.add(new SQLStatement(SQLCommand.DROPTABLE, tableName));
			}
			if (map.isTokenerFlag(FLAG_CREATE)) {
				SQLStatement dataStatement = new SQLStatement(SQLCommand.CREATETABLE, tableName, TYPE_STRING);
				/* SWITCH FOR PRIVOTISIERUNG */
				String[] properties = null;
				if (TABLE_PRIVOTISIERUNG.equalsIgnoreCase(this.stragety)) {
					dataStatement.with(SQLStatement.PROP, TYPE_STRING);
					dataStatement.with(SQLStatement.VALUE, TYPE_OBJECT);
				} else {
					properties = creator.getProperties();
				}
				if (properties != null) {
					for (int i = 0; i < properties.length; i++) {
						if (properties[i].indexOf('.') >= 0 || properties[i] == SendableEntityCreator.DYNAMIC) {
							continue;
						}
						String type = null;
						Object value = creator.getValue(item, properties[i]);
						if (value instanceof Collection<?>) {
							Collection<?> collection = (Collection<?>) value;
							if (collection.size() > 0) {
								Object child = collection.iterator().next();
								String simpleName = child.getClass().getName();
								SendableEntityCreator currentCreator = this.map.getCreator(simpleName, true, true,
										null);
								if (currentCreator != null) {
									type = "INTEGER[]";
								}
							}
							if (type == null) {
								type = "OBJECT[]";
							}
						} else {
							if (value == null) {
								type = TYPE_STRING;
							} else {
								String simpleName = value.getClass().getSimpleName();
								SendableEntityCreator currentCreator = this.map.getCreator(simpleName, true, true,
										null);
								if (currentCreator != null) {
									type = TYPE_INTEGER;
								} else {
									type = simpleName;
								}
							}
						}
						dataStatement.with(properties[i], StringUtil.convertPrimitiveToObjectType(type).toUpperCase());
					}
				}
				statements.add(dataStatement);
			}
			map.add(tableName);
		}
	}

	private String parseModel(MapEntity map, Object item, SQLStatementList statements) {
		if (map.contains(item)) {
			return this.map.getKey(item);
		}
		String id = this.map.getId(item, true);
		map.with(item);
		String className = item.getClass().getName();
		Grammar grammar = map.getGrammar();
		SendableEntityCreator creator = grammar.getCreator(SendableEntityCreator.NEW, item, map, className);
		if (creator == null) {
			return item.toString();
		}
		String tableName = StringUtil.shortClassName(className);
		/* Add TableCreate */
		addTableCreate(tableName, item, creator, statements, map);

		if (TABLE_PRIVOTISIERUNG.equalsIgnoreCase(this.stragety)) {
			parseModelPrivotisierung(map, tableName, id, creator, item, statements);
		} else {
			parseModelFlat(map, tableName, id, creator, item, statements);
		}
		return id;
	}

	private void parseModelPrivotisierung(MapEntity map, String tableName, String id, SendableEntityCreator creator,
			Object item, SQLStatementList statements) {
		String[] properties = creator.getProperties();
		Object prototype = creator.getSendableInstance(true);
		SQLStatement insertStatement;

		for (String property : properties) {
			Object value = creator.getValue(item, property);
			/* Null Value */
			if (value == null || property == SendableEntityCreator.DYNAMIC) {
				continue;
			}
			/* DefaultValue */
			if (value.equals(creator.getValue(prototype, property))) {
				continue;
			}

			/* SWITCH FOR TO N-ASSOC */
			if (value instanceof Collection<?>) {
				Collection<?> children = (Collection<?>) value;
				for (Iterator<?> i = children.iterator(); i.hasNext();) {
					String neighbourId = parseModel(map, i.next(), statements);

					insertStatement = new SQLStatement(SQLCommand.INSERT, tableName, id);
					insertStatement.with(SQLStatement.PROP, property);
					insertStatement.with(SQLStatement.VALUE, neighbourId);
					statements.add(insertStatement);
				}
			} else {
				insertStatement = new SQLStatement(SQLCommand.INSERT, tableName, id);
				insertStatement.with(SQLStatement.PROP, property);
				SendableEntityCreator childCreator = this.map.getCreator(value.getClass().getName(), true, true, null);
				if (childCreator != null) {
					insertStatement.with(SQLStatement.VALUE, parseModel(map, value, statements));
				} else {
					insertStatement.with(SQLStatement.VALUE, value);
				}
				statements.add(insertStatement);
			}
		}
	}

	private void parseModelFlat(MapEntity map, String tableName, String id, SendableEntityCreator creator, Object item,
			SQLStatementList statements) {
		SQLStatement insertStatement = new SQLStatement(SQLCommand.INSERT, tableName);
		insertStatement.with(SQLStatement.ID, id);

		for (String property : creator.getProperties()) {
			Object value = creator.getValue(item, property);
			if (value == null || property == SendableEntityCreator.DYNAMIC) {
				continue;
			}
			if (value instanceof Collection<?>) {
				Collection<?> children = (Collection<?>) value;
				SimpleSet<String> values = new SimpleSet<String>();
				for (Iterator<?> i = children.iterator(); i.hasNext();) {
					values.add(parseModel(map, i.next(), statements));
				}
				insertStatement.with(property, values);
			} else {
				SendableEntityCreator childCreator = this.map.getCreator(value.getClass().getName(), true, true, null);
				if (childCreator != null) {
					insertStatement.with(property, parseModel(map, value, statements));
				} else {
					insertStatement.with(property, value);
				}
			}
		}
		statements.add(insertStatement);
	}

	final static class SelectSearcher {
		private SimpleList<String> ids = new SimpleList<String>();
		private SimpleList<String> deleteIds = new SimpleList<String>();
		private boolean drop;
		private boolean create;
		private SimpleList<SQLStatement> mayBeStatements = new SimpleList<SQLStatement>();

		public void clear() {
			ids.clear();
		}

		public boolean addId(String primaryId) {
			return ids.add(primaryId);
		}

		public SimpleList<String> getIds() {
			return ids;
		}

		public void addDeletedId(String primaryId) {
			deleteIds.add(primaryId);
		}

		public SimpleList<String> getDeletedIds() {
			return deleteIds;
		}
	}

	/**
	 * Validate statements.
	 *
	 * @param result the result
	 * @param results the results
	 * @return true, if successful
	 */
	public boolean validateStatements(SQLStatementList result, SimpleList<SQLTable> results) {
		SimpleKeyValueList<String, SelectSearcher> foundKeys = new SimpleKeyValueList<String, SelectSearcher>();

		for (SQLStatement statement : result) {
			String table = statement.getTable();
			SelectSearcher values = foundKeys.get(table);
			if (values == null) {
				values = new SelectSearcher();
				foundKeys.put(table, values);
			}
			if (statement.getCommand() == SQLCommand.DROPTABLE) {
				values.clear();
				values.drop = true;
				continue;
			}
			if (statement.getCommand() == SQLCommand.CREATETABLE) {
				if (values.drop) {
					values.create = true;
				}
				continue;
			}
			String primaryKey = statement.getPrimaryId();
			if (statement.getCommand() == SQLCommand.INSERT) {
				if (values.create) {
					/* Its ok */
					continue;
				}
				if (values.drop) {
					if (!statement.autoDisable()) {
						values.addId(primaryKey);
					}
				} else {
					/* Add Id */
					if (values.addId(primaryKey)) {
						values.mayBeStatements.add(statement);
					} else {
						/* Already found insert must be Update */
						if (statement.isAutoStatement()) {
							statement.withCommand(SQLCommand.UPDATE);
							statement.withCondition(SQLStatement.ID, primaryKey);
							statement.without(SQLStatement.ID);
						}
					}
				}
				continue;
			}
			if (statement.getCommand() == SQLCommand.UPDATE) {
				if (values.create) {
					if (!values.getIds().contains(primaryKey)) {
						if (statement.isAutoStatement()) {
							statement.withCommand(SQLCommand.INSERT);
							statement.withoutCondition(SQLStatement.ID);
							statement.with(SQLStatement.ID, primaryKey);
							values.addId(primaryKey);
						}
					}
				} else if (values.drop) {
					if (statement.isAutoStatement()) {
						statement.withEnable(false);
					} else {
						/* Add Id */
						values.addId(primaryKey);
					}
				} else {
					values.mayBeStatements.add(statement);
					if (statement.isAutoStatement() && !values.getIds().contains(statement.getPrimaryId())) {
						if (primaryKey != null) {
							statement.withCommand(SQLCommand.INSERT);
							statement.withoutCondition(SQLStatement.ID);
							statement.with(SQLStatement.ID, primaryKey);
							values.addId(primaryKey);
						}
					}
				}
				continue;
			}
			if (statement.getCommand() == SQLCommand.DELETE) {
				if (values.create) {
					if (!values.getIds().contains(primaryKey)) {
						if (statement.isAutoStatement()) {
							statement.withEnable(false);
						} else if (primaryKey != null) {
							values.addId(primaryKey);
							values.addDeletedId(primaryKey);
						}
					}
				} else if (primaryKey != null) {
					values.addId(primaryKey);
					values.addDeletedId(primaryKey);
				}
			} else if (statement.getCommand() == SQLCommand.SELECT) {
				if (!values.create && values.drop) {
					statement.autoDisable();
				} else {
					if (values.getDeletedIds().contains(primaryKey)) {
						statement.autoDisable();
					} else if (values.getIds().contains(primaryKey)) {
					} else {
						/* MAY BE */
						values.mayBeStatements.add(statement);
					}
				}
			}
		}
		SQLStatementList selectList = new SQLStatementList();
		for (SimpleIteratorSet<String, SelectSearcher> i = new SimpleIteratorSet<String, SelectSearcher>(foundKeys); i
				.hasNext();) {
			Entry<String, SelectSearcher> entity = i.next();
			SelectSearcher values = entity.getValue();
			if (values.mayBeStatements.size() < 1) {
				continue;
			}
			String tableName = entity.getKey();
			SQLStatement selectStatement = new SQLStatement(SQLCommand.SELECT, tableName).withValues(SQLStatement.ID)
					.withCondition(SQLStatement.ID, values.getIds());
			selectList.add(selectStatement);
		}
		if (selectList.size() < 1) {
			return false;
		}

		/* Find Ids in DataBase */
		executeStatements(selectList, results, true);

		/* Try to find all Matches to remove some MayBe's */
		for (SQLTable sqlTable : results) {
			SelectSearcher selectSearcher = foundKeys.get(sqlTable.getTable());
			SimpleList<Object> idValues = sqlTable.getColumnValue(SQLStatement.ID);
			for (Object id : idValues) {
				for (int i = selectSearcher.mayBeStatements.size() - 1; i >= 0; i--) {
					SQLStatement statement = selectSearcher.mayBeStatements.get(i);
					String primaryId = statement.getPrimaryId();
					if (primaryId != null && primaryId.equals(id)) {
						if (statement.getCommand().equals(SQLCommand.INSERT)) {
							statement.withCommand(SQLCommand.UPDATE);
							statement.withCondition(SQLStatement.ID, primaryId);
							statement.without(SQLStatement.ID);
							selectSearcher.mayBeStatements.remove(i);
						} else {
							/* May be Update or Select statement. */
						}
					}
				}
			}
		}
		return true;
	}

	/**
	 * Gets the flag.
	 *
	 * @return the flag
	 */
	public byte getFlag() {
		return flag;
	}

	/**
	 * With flag.
	 *
	 * @param flag the flag to set
	 * @return thisComponent
	 */
	public SQLTokener withFlag(byte flag) {
		this.flag = flag;
		return this;
	}
}
