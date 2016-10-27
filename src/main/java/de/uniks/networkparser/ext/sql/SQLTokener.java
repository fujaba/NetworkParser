
package de.uniks.networkparser.ext.sql;
/*
NetworkParser
Copyright (c) 2011 - 2016, Stefan Lindel
All rights reserved.

Licensed under the EUPL, Version 1.1 or (as soon they
will be approved by the European Commission) subsequent
versions of the EUPL (the "Licence");
You may not use this work except in compliance with the Licence.
You may obtain a copy of the Licence at:

http://ec.europa.eu/idabc/eupl5

Unless required by applicable law or agreed to in
writing, software distributed under the Licence is
distributed on an "AS IS" basis,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
express or implied.
See the Licence for the specific language governing
permissions and limitations under the Licence.
*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map.Entry;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleIteratorSet;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class SQLTokener extends Tokener {
	private SQLStatement sqlConnection;
	public static final String ID = "_ID";
	public static final byte FLAG_NONE = 0x00;
	public static final byte FLAG_CREATE = 0x01;
	public static final byte FLAG_DROP = 0x02;
	private byte flag = FLAG_CREATE;

	public SQLTokener(SQLStatement connection) {
		this.sqlConnection = connection;
	}
	public SQLStatementList encode(GraphList model) {
		SQLStatementList result = new SQLStatementList();
		result.add(sqlConnection);
		for (Clazz clazz : model.getClazzes()) {
			result.add(new SQLStatement(SQLCommand.DROPTABLE, clazz.getName()));

			SQLStatement createClass = new SQLStatement(SQLCommand.CREATETABLE, clazz.getName());
			createClass.with(ID, "INTEGER");
			parseAssociations(clazz, createClass);
			parseAttributes(clazz, createClass);
			result.add(createClass);
		}
		return result;
	}
	private void parseAttributes(Clazz clazz, SQLStatement sqlClass) {
		for (Attribute attribute : clazz.getAttributes()) {
			sqlClass.with(attribute.getName(), EntityUtil.convertPrimitiveToObjectType(attribute.getType().getName(true)).toUpperCase());
		}
	}

	private void parseAssociations(Clazz clazz, SQLStatement sqlClass) {
		String type = "";
		for (Association association : clazz.getAssociations()) {
			if (association.getCardinality() == Cardinality.MANY) {
				type = "INTEGER";
			} else {
				type = "INTEGER[]";
			}
			sqlClass.with(association.getOther().getName(), type);
		}
	}
	public boolean executeStatements(SQLStatementList statements) {
		return executeStatements(statements, null, false);
	}

	public boolean executeStatements(SQLStatementList statements, SimpleList<SQLTable> results, boolean dynamicTable) {
		boolean result=true;
		Connection connection = null;
		for(SQLStatement statement : statements ) {
			if (statement.isEnable() == false) {
				continue;
			}
			Statement query = null;
			try {
				if(statement.getCommand()==SQLCommand.CONNECTION) {
					if(connection != null) {
						disconnect(connection);
					}
					connection = connect(statement);
				}else {
					if(connection == null) {
						connection = connect(this.sqlConnection);
					}
					query = connection.createStatement();
					if (statement.getCommand() == SQLCommand.SELECT) {
						ResultSet executeQuery = query.executeQuery(statement.toString());
						if(results != null) {
							results.add(SQLTable.create(executeQuery, statement, dynamicTable));
						}
						result = true;
					} else {
						//TODO VODOO
						// Check for Insert if really insert or Update
						if(statement.isAutoStatement()) {
							if(statement.getCommand() == SQLCommand.INSERT || statement.getCommand() == SQLCommand.UPDATE) {

							}
						}
						query.execute(statement.toString());
					}
				}
			} catch(Exception e) {
				result = false;
			} finally {
				if(query != null) {
					try {
						query.close();
					} catch (SQLException e) {
					}
				}
			}
		}
		if(connection != null) {
			result = result & disconnect(connection);
		}
		return result;
	}

	public boolean disconnect(Connection connection) {
		 try {
			if(connection != null ) {
				connection.close();
			}
		}catch (SQLException e) {
			return false;
		}
		return true;
	}

	public Connection connect(SQLStatement connect) {
		try {
			return DriverManager.getConnection(connect.toString());
		} catch (SQLException e) {
		}
		return null;
	}

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
	private void addTableCreate(String tableName, Object item, SendableEntityCreator creator, SQLStatementList statements, MapEntity map) {
		if (map.contains(tableName) == false) {
			if( map.isTokenerFlag(FLAG_DROP)) {
				statements.add(new SQLStatement(SQLCommand.DROPTABLE, tableName));
			}
			if( map.isTokenerFlag(FLAG_CREATE)) {
				SQLStatement dataStatement = new SQLStatement(SQLCommand.CREATETABLE, tableName);
				dataStatement.with(ID, "STRING");
				String[] properties = creator.getProperties();
				for(int i = 0; i < properties.length; i++) {
					String type = null;
					if(properties[i].indexOf('.')>=0) {
						continue;
					}
					Object value = creator.getValue(item, properties[i]);
					if(value instanceof Collection<?>) {
						Collection<?> collection = (Collection<?>) value;
						if(collection.size()>0) {
							Object child = collection.iterator().next();
							String simpleName = child.getClass().getName();
							SendableEntityCreator currentCreator = this.map.getCreator(simpleName, true);
							if(currentCreator != null) {
								type = "INTEGER[]";
							}
						}
						if(type == null) {
							type = "OBJECT[]";
						}
					} else {
						if(value == null) {
							type = "String";
						} else {
							String simpleName = value.getClass().getSimpleName();
							SendableEntityCreator currentCreator = this.map.getCreator(simpleName, true);
							if (currentCreator != null) {
								type = "INTEGER";
							} else {
								type = simpleName;
							}
						}
					}
					dataStatement.with(properties[i], EntityUtil.convertPrimitiveToObjectType(type).toUpperCase());
				}
				statements.add(dataStatement);
			}
			map.add(tableName);
		}
	}

	private String parseModel(MapEntity map, Object item, SQLStatementList statements) {
		if(map.contains(item)) {
			return this.map.getKey(item);
		}
		String id = this.map.getId(item);
		map.with(item);
		String className = item.getClass().getName();
		SendableEntityCreator creator = map.getCreator(IdMap.NEW, this.map, item, className);
		if(creator == null) {
			return item.toString();
		}
		String tableName = EntityUtil.shortClassName(className);
		// Add TableCreate
		addTableCreate(tableName, item, creator, statements, map);
		SQLStatement insertStatement = new SQLStatement(SQLCommand.INSERT, tableName);
		insertStatement.with(ID, id);
		for (String property : creator.getProperties()) {
			Object value = creator.getValue(item, property);
			if(value == null) {
				continue;
			}
			if (value instanceof Collection<?>) {
				Collection<?> children = (Collection<?>) value;
				SimpleSet<String> values = new SimpleSet<String>();
				for(Iterator<?> i = children.iterator();i.hasNext();) {
					values.add(parseModel(map, i.next(), statements));
				}
				insertStatement.with(property, values);
			} else {
				SendableEntityCreator childCreator = this.map.getCreator(value.getClass().getName(), true);
				if(childCreator != null) {
					insertStatement.with(property, parseModel(map, value, statements));
				}else {
					insertStatement.with(property, value);
				}
			}
		}
		statements.add(insertStatement);
		return id;
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
		public SimpleList<String> getDeletedIds () {
			return deleteIds;
		}
	}

	public boolean validateStatements(SQLStatementList result, SimpleList<SQLTable> results) {
		SimpleKeyValueList<String, SelectSearcher> foundKeys=new SimpleKeyValueList<String, SelectSearcher>();

		for(SQLStatement statement : result) {
			String table = statement.getTable();
			SelectSearcher values = foundKeys.get(table);
			if(values == null) {
				values = new SelectSearcher();
				foundKeys.put(table, values);
			}
			if(statement.getCommand()==SQLCommand.DROPTABLE) {
				values.clear();
				values.drop = true;
				continue;
			}
			if(statement.getCommand()==SQLCommand.CREATETABLE) {
				if(values.drop) {
					values.create = true;
				}
				continue;
			}
			String primaryKey = statement.getPrimaryId();
			if(statement.getCommand()==SQLCommand.INSERT) {
				if(values.create) {
					// Its ok
					continue;
				}
				if(values.drop) {
					if(statement.autoDisable() == false) {
						values.addId(primaryKey);
					}
				} else {
					// 	Add Id
					if (values.addId(primaryKey) ) {
						values.mayBeStatements.add(statement);
					} else {
						// Already found insert must be Update
						if(statement.isAutoStatement()) {
							statement.withCommand(SQLCommand.UPDATE);
							statement.withCondition(ID, primaryKey);
							statement.without(ID);
						}
					}
				}
				continue;
			}
			if(statement.getCommand()==SQLCommand.UPDATE) {
				if (values.create) {
					if (values.getIds().contains(primaryKey) == false) {
						if(statement.isAutoStatement()) {
							statement.withCommand(SQLCommand.INSERT);
							statement.withoutCondition(ID);
							statement.with(ID, primaryKey);
							values.addId(primaryKey);
						}
					}
				} else if (values.drop) {
					if(statement.isAutoStatement()) {
						statement.withEnable(false);
					} else {
//						if(statement != null) {
						// 	Add Id
						values.addId(primaryKey);
					}
				} else {
					values.mayBeStatements.add(statement);
					if (statement.isAutoStatement() && values.getIds().contains(statement.getPrimaryId()) == false) {
						statement.withCommand(SQLCommand.INSERT);
						statement.withoutCondition(ID);
						statement.with(ID, primaryKey);
						values.addId(primaryKey);
					}
				}
				continue;
			}
			if(statement.getCommand()==SQLCommand.DELETE) {
				if (values.create) {
					if (values.getIds().contains(primaryKey) == false) {
						if (statement.isAutoStatement()) {
							statement.withEnable(false);
						} else if(primaryKey != null) {
							values.addId(primaryKey);
							values.addDeletedId(primaryKey);
						}
					}
				} else if(primaryKey != null) {
					values.addId(primaryKey);
					values.addDeletedId(primaryKey);
				}
			} else if(statement.getCommand()==SQLCommand.SELECT) {
				if (values.create == false && values.drop) {
					statement.autoDisable();
				}else {
					if(values.getDeletedIds().contains(primaryKey)) {
						statement.autoDisable();
					} else if (values.getIds().contains(primaryKey)) {
					} else {
						// MAY BE
						values.mayBeStatements.add(statement);
					}
				}
			}
		}
		SQLStatementList selectList = new SQLStatementList();
		for(SimpleIteratorSet<String, SelectSearcher> i = new SimpleIteratorSet<String, SelectSearcher>(foundKeys);i.hasNext();) {
			Entry<String, SelectSearcher> entity = i.next();
			SelectSearcher values = entity.getValue();
			if(values.mayBeStatements.size() < 1 ) {
				continue;
			}
			String tableName = entity.getKey();
			SQLStatement selectStatement = new SQLStatement(SQLCommand.SELECT, tableName).withValues(ID).withCondition(ID, values.getIds());
			selectList.add(selectStatement);
		}
		if(selectList.size() < 1) {
			return false;
		}

		// Find Ids in DataBase
		executeStatements(selectList, results, true);

		// Try to find all Matches to remove some MayBe's
		for(SQLTable sqlTable : results) {
			SelectSearcher selectSearcher = foundKeys.get(sqlTable.getTable());
			SimpleList<Object> idValues = sqlTable.getColumnValue(ID);
			for(Object id : idValues) {
				for(int i=selectSearcher.mayBeStatements.size()-1;i>=0;i--) {
					SQLStatement statement = selectSearcher.mayBeStatements.get(i);
					String primaryId = statement.getPrimaryId();
					if (primaryId != null && primaryId.equals(id)) {
						if (statement.getCommand().equals(SQLCommand.INSERT)) {
							statement.withCommand(SQLCommand.UPDATE);
							statement.withCondition(ID, primaryId);
							statement.without(ID);
							selectSearcher.mayBeStatements.remove(i);
						} else {
							// May be Update or Select
	//						statement.
						}
					}
				}
			}
		}
		return true;
	}
	/**
	 * @return the flag
	 */
	public byte getFlag() {
		return flag;
	}
	/**
	 * @param flag the flag to set
	 * @return thisComponent
	 */
	public SQLTokener withFlag(byte flag) {
		this.flag = flag;
		return this;
	}
}
