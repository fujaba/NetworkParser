package test;

import org.junit.Test;

import de.uniks.factory.typescript.TypeScriptModelFactory;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;

public class TestTypeScriptAssociationFactory {

	@Test
	public void testBidirectional() {
		GraphList classModel = new GraphList().with("i.love.sdmlib");
		Clazz room = classModel.createClazz("Room");
		Clazz student = classModel.createClazz("Student");
		student.withBidirectional(room, "room", Cardinality.ONE, "students", Cardinality.MANY);
		TypeScriptModelFactory modelFactory = new TypeScriptModelFactory();
		System.out.println(modelFactory.create(classModel));
	}
	
	@Test
	public void testBidirectionalInterface() {
		GraphList classModel = new GraphList().with("i.love.sdmlib");
		Clazz room = classModel.createClazz("Room").enableInterface();
		Clazz subRoom = classModel.createClazz("SubRoom").withSuperClazz(room);
		Clazz student = classModel.createClazz("Student");
		student.withBidirectional(room, "room", Cardinality.ONE, "students", Cardinality.MANY);
		TypeScriptModelFactory modelFactory = new TypeScriptModelFactory();
		System.out.println(modelFactory.create(classModel));
	
	}
	
	@Test
	public void testInterface() {
		GraphList classModel = new GraphList().with("i.love.sdmlib");
		Clazz person = classModel.createClazz("Person").enableInterface();
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		TypeScriptModelFactory modelFactory = new TypeScriptModelFactory();
		System.out.println(modelFactory.create(classModel));
	}
	
	@Test
	public void testSuperClass() {
		GraphList classModel = new GraphList().with("i.love.sdmlib");
		Clazz person = classModel.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		TypeScriptModelFactory modelFactory = new TypeScriptModelFactory();
		System.out.println(modelFactory.create(classModel));
	}
	
	@Test
	public void testMultipleClasses() {
		GraphList classModel = new GraphList().with("i.love.sdmlib");
		Clazz person = classModel.createClazz("Person").enableInterface();
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		Clazz teacher = classModel.createClazz("Teacher").enableInterface();
		Clazz human = classModel.createClazz("Human");
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person, teacher, human);
		TypeScriptModelFactory modelFactory = new TypeScriptModelFactory();
		System.out.println(modelFactory.create(classModel));
	}
	
}
