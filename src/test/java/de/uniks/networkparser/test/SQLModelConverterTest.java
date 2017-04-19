package de.uniks.networkparser.test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.ext.sql.SQLCommand;
import de.uniks.networkparser.ext.sql.SQLStatement;
import de.uniks.networkparser.ext.sql.SQLStatementList;
import de.uniks.networkparser.ext.sql.SQLTable;
import de.uniks.networkparser.ext.sql.SQLTokener;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class SQLModelConverterTest {
	@BeforeClass
	public static void loadSQLDriver() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException, IOException {
		File f = new File("lib/sql/sqlite-jdbc-3.8.11.2.jar");
		URL url = new URL("file:///"+f.getAbsolutePath());
//		DriverManager.setLogStream(System.out);
		URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Class<?> sysclass = URLClassLoader.class;

		try {
			Method method = sysclass.getDeclaredMethod("addURL", new Class[] {URL.class});
			method.setAccessible(true);
			method.invoke(sysloader, new Object[] {url});
		} catch (Throwable t) {
			throw new IOException("Error, could not add URL to system classloader");
		}
		Class<?> c = ClassLoader.getSystemClassLoader().loadClass("org.sqlite.JDBC");
		c.newInstance();
	}

	@Test
	public void testClassToSQL() {
		GraphList model = new GraphList().with("gen.model");

		Clazz city = model.createClazz("City");
		Clazz university = model.createClazz("University");
		Clazz student = model.createClazz("Student");

		city.withAttribute("cityId", DataType.INT);
		city.withAttribute("name", DataType.STRING);

		university.withAttribute("uniId", DataType.INT);
		university.withAttribute("name", DataType.STRING);

		student.withAttribute("studentId", DataType.INT);
		student.withAttribute("firstName", DataType.STRING);
		student.withAttribute("lastName", DataType.STRING);
		student.withAttribute("credits", DataType.INT);

		university.withBidirectional(student, "students", Cardinality.MANY, "university", Cardinality.ONE);
		File file = new File("build/sampleA.db");

		if (file.exists()) {
			file.delete();
		}

		SQLTokener tokener = new SQLTokener(SQLStatement.connect("jdbc", "sqlite", "build/sampleA.db"));
		SQLStatementList statements = tokener.encode(model);

		SQLStatement insertStatement = new SQLStatement(SQLCommand.INSERT, "student");
		insertStatement.with("studentId", 5).with("firstName", "Max").with("lastName", "Mustermann").with("credits", 5);

		SQLStatement insertShort = new SQLStatement(SQLCommand.INSERT, "student");
		insertShort.with("studentId", 6).with("firstName", "Moritz");

		SQLStatement updateStatement = new SQLStatement(SQLCommand.UPDATE, "student");

		updateStatement.with("studentId", 7);
		updateStatement.withCondition("studentId", 5);

		SQLStatement deleteStatement = new SQLStatement(SQLCommand.DELETE, "student");

		deleteStatement.withCondition("studentId", 6);

		statements.add(insertStatement);
		statements.add(updateStatement);
		statements.add(deleteStatement);

		Assert.assertTrue(tokener.executeStatements(statements));

		SQLStatement selectStatement = new SQLStatement(SQLCommand.SELECT, "student").withValues("studentId","firstName","lastName","credits");

		statements = new SQLStatementList();

		statements.add(selectStatement);

		SimpleList<SQLTable> results = new SimpleList<SQLTable>();

		Assert.assertTrue(tokener.executeStatements(statements, results, false));

		////////////////////////////////////////////////////////////////////////////////

//		GraphList receivedModel = new GraphList();
//
//		SimpleKeyValueList<String ,SimpleList<SQLCreator>> creatorList = new SimpleKeyValueList<String, SimpleList<SQLCreator>>();
//
//		SQLCreator sqlCreator = null;
//
//		Clazz currentClazz = null;
//
//		SimpleList<SQLCreator> currentCreators = null;
//
//		for (SQLTable sqlTable : results) {
//			String tableName = sqlTable.getTable().substring(0, 1).toUpperCase() + sqlTable.getTable().substring(1);
//			currentClazz = receivedModel.createClazz(tableName);
//			currentCreators = new SimpleList<SQLCreator>();
//			for (SimpleKeyValueList<String, Object> simpleKeyValueList : sqlTable) {
//				sqlCreator = new SQLCreator();
//				for (Entry<String, Object> entry : simpleKeyValueList.entrySet()) {
//					sqlCreator.withProperties(entry.getKey(), entry.getValue());
//				}
//				sqlCreator.withEntity(currentClazz);
//				currentCreators.add(sqlCreator);
//			}
//			creatorList.add(tableName, currentCreators);
//			for (Entry<String, Object> classAttributes : sqlCreator.getPropertiesWithValues().entrySet()) {
//				currentClazz.createAttribute(classAttributes.getKey(), DataType.create(classAttributes.getValue().getClass()));
//			}
//		}
//
//		SQLTable sqlTable = results.first();
//
//		SQLStatementList encodedStatements = tokener.encode(receivedModel);
//
//		SQLDataStatement sqlDataStatement = null;
//
//		for (Entry<String, SimpleList<SQLCreator>> entries : creatorList.entrySet()) {
//			for (SQLCreator newCreator : entries.getValue()) {
//				sqlDataStatement = new SQLDataStatement(SQLCommand.INSERT, entries.getKey());
//				for (Entry<String, Object> entry : newCreator.getPropertiesWithValues().entrySet()) {
//					sqlDataStatement.with(entry.getKey(), entry.getValue());
//				}
//				encodedStatements.add(sqlDataStatement);
//			}
//		}
//
//		SQLCreator currentCreator = creatorList.entrySet().iterator().next().getValue().first();
//
//		IdMap idMap = new IdMap();
//
//		idMap.with(currentCreator);
//
//		SQLDataStatement newInsertStatement = new SQLDataStatement(SQLCommand.INSERT, currentCreator.getSendableInstance(true).toString());
//
//		for (Entry<String, Object> entry : currentCreator.getPropertiesWithValues().entrySet()) {
//			newInsertStatement.with(entry.getKey(), entry.getValue());
//		}
//
//		encodedStatements.add(newInsertStatement);
//
//		SQLDataStatement newUpdateStatement = new SQLDataStatement(SQLCommand.INSERT, currentCreator.getSendableInstance(true).toString());
//
//		for (Entry<String, Object> entry : currentCreator.getPropertiesWithValues().entrySet()) {
//			newUpdateStatement.with(entry.getKey(), entry.getValue());
//		}
//
//		boolean diff = false;
//
//		SimpleKeyValueList<String, Object> insertEntries = null;
//
//		if (newUpdateStatement.getValues() instanceof SimpleKeyValueList<?,?>) {
//			insertEntries = (SimpleKeyValueList<String, Object>) newUpdateStatement.getValues();
//		}
//
//		for (SimpleKeyValueList<String, Object> instances : sqlTable) {
//			for (Entry<String, Object> entry : instances.entrySet()) {
//				if (insertEntries.getValue(entry.getKey()).equals(entry.getValue()) == false) {
//					diff = true;
//				}
//			}
//		}
//
//		if (diff == false) {
//			newUpdateStatement.withCommand(SQLCommand.UPDATE);
//			for (Entry<String,Object> entry : insertEntries.entrySet()) {
//				newUpdateStatement.withCondition(entry.getKey(), entry.getValue());
//			}
//		}
//
//		encodedStatements.add(newUpdateStatement);
//
//		for (SQLStatement sqlStatement : encodedStatements) {
//			System.out.println(sqlStatement);
//		}

	}

	@Test
	public void generateModel() {
//		ClassModel model = new ClassModel("gen.model");
//
//		Clazz city = model.createClazz("City");
//		Clazz university = model.createClazz("University");
//		Clazz student = model.createClazz("Student");
//
//		city.withAttribute("cityId", DataType.INT);
//		city.withAttribute("name", DataType.STRING);
//
//		university.withAttribute("uniId", DataType.INT);
//		university.withAttribute("name", DataType.STRING);
//
//		student.withAttribute("studentId", DataType.INT);
//		student.withAttribute("firstName", DataType.STRING);
//		student.withAttribute("lastName", DataType.STRING);
//		student.withAttribute("credits", DataType.INT);
//
//		university.withBidirectional(student, "students", Cardinality.MANY, "university", Cardinality.ONE);
//		model.generate("src");
	}

	@Test
	public void createTables() {
		File file = new File("build/sampleB.db");

		if (file.exists()) {
			file.delete();
		}

		SQLTokener tokener = new SQLTokener(SQLStatement.connect("jdbc", "sqlite", "build/sampleB.db"));

		IdMap map = UniversityCreator.createIdMap("1");
		map.withTimeStamp(1);

		University universityModel = new University();
		universityModel.withName("Uni").withUniId(1).withStudents(new Student().withStudentId(1).withFirstName("Max").withLastName("Mustermann").withCredits(5)
				, new Student().withStudentId(2).withFirstName("Michael").withLastName("Mustermann").withCredits(7));

		SQLStatementList list = (SQLStatementList) map.encode(universityModel, tokener);

		Assert.assertEquals("jdbc:sqlite:build/sampleB.db", list.get(0).toString());
		Assert.assertEquals("CREATE TABLE IF NOT EXISTS Student ('_ID' STRING, 'name' STRING, 'studNo' STRING, 'in' STRING, 'university' UNIVERSITY, 'firstName' STRING, 'lastName' STRING, 'credits' INTEGER, 'friends' STRING)", list.get(1).toString());
		Assert.assertEquals("CREATE TABLE IF NOT EXISTS University ('_ID' STRING, 'name' STRING, 'students' INTEGER[], 'rooms' OBJECT[])", list.get(2).toString());
		Assert.assertEquals("INSERT INTO Student (_ID, university, firstName, lastName, credits) values('S2', 'U1', 'Max', 'Mustermann', '5')", list.get(3).toString());
		Assert.assertEquals("INSERT INTO Student (_ID, university, firstName, lastName, credits) values('S3', 'U1', 'Michael', 'Mustermann', '7')", list.get(4).toString());
		Assert.assertEquals("INSERT INTO University (_ID, name, students, rooms) values('U1', 'Uni', '{S2, S3}', '{}')", list.get(5).toString());
	}

	@Test
	public void createTablesWithUpdates() {
		File file = new File("build/sampleC.db");

		if (file.exists()) {
			file.delete();
		}

		SQLTokener tokener = new SQLTokener(SQLStatement.connect("jdbc", "sqlite", "build/sampleC.db"));

		IdMap map = UniversityCreator.createIdMap("1");
		map.withTimeStamp(1);

		University universityModel = new University();
		universityModel.withName("Uni").withUniId(1).withStudents(new Student().withStudentId(1).withFirstName("Max").withLastName("Mustermann").withCredits(5)
				, new Student().withStudentId(2).withFirstName("Michael").withLastName("Mustermann").withCredits(7));

		SQLStatementList list = (SQLStatementList) map.encode(universityModel, tokener);

		Assert.assertEquals("jdbc:sqlite:build/sampleC.db", list.get(0).toString());
		Assert.assertEquals("CREATE TABLE IF NOT EXISTS Student ('_ID' STRING, 'name' STRING, 'studNo' STRING, 'in' STRING, 'university' UNIVERSITY, 'firstName' STRING, 'lastName' STRING, 'credits' INTEGER, 'friends' STRING)", list.get(1).toString());
		Assert.assertEquals("CREATE TABLE IF NOT EXISTS University ('_ID' STRING, 'name' STRING, 'students' INTEGER[], 'rooms' OBJECT[])", list.get(2).toString());

		Assert.assertEquals("INSERT INTO Student (_ID, university, firstName, lastName, credits) values('S2', 'U1', 'Max', 'Mustermann', '5')", list.get(3).toString());
		Assert.assertEquals("INSERT INTO Student (_ID, university, firstName, lastName, credits) values('S3', 'U1', 'Michael', 'Mustermann', '7')", list.get(4).toString());
		Assert.assertEquals("INSERT INTO University (_ID, name, students, rooms) values('U1', 'Uni', '{S2, S3}', '{}')", list.get(5).toString());

		tokener.executeStatements(list);
		tokener.withFlag(SQLTokener.FLAG_NONE);
		list = (SQLStatementList) map.encode(universityModel, tokener);

		Assert.assertEquals("jdbc:sqlite:build/sampleC.db", list.get(0).toString());
		Assert.assertEquals("UPDATE Student SET university='U1', firstName='Max', lastName='Mustermann', credits='5' WHERE _ID='S2'", list.get(1).toString());
		Assert.assertEquals("UPDATE Student SET university='U1', firstName='Michael', lastName='Mustermann', credits='7' WHERE _ID='S3'", list.get(2).toString());
		Assert.assertEquals("UPDATE University SET name='Uni', students='{S2, S3}', rooms='{}' WHERE _ID='U1'", list.get(3).toString());

//		for (SQLStatement statement : list) {
//			System.out.println(statement.toString());
//		}

	}
}
