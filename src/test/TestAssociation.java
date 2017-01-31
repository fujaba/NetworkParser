package test;

import org.junit.Test;

import de.uniks.factory.ModelFactory;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzImport;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;

public class TestAssociation {

	@Test
	public void testBidirectionalAssociation() {
		GraphList classModel = new GraphList();
		Clazz student = classModel.createClazz("Student");
		Clazz teacher = classModel.createClazz("Teacher");
		student.withBidirectional(teacher, "teacher", Cardinality.ONE, "students", Cardinality.MANY);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testImplementsWithoutMethods() {
		GraphList classModel = new GraphList();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testImplementsWithMethods() {
		GraphList classModel = new GraphList();
		Clazz person = classModel.createClazz("Person");
//		.enableInterface();
		Clazz room = classModel.createClazz("Room");
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		
		student.with(ClazzImport.create("java.util.Date"));
		student.with(ClazzImport.create("java.util.Date"));
		System.out.println(student.getInterfaces(true).size());
		System.out.println(student.getSuperClazzes(true).size());
		System.out.println(student.getImports().size());
//		classModel.fixClassModel()
		
		room.withBidirectional(person, "persons", Cardinality.MANY, "room", Cardinality.ONE);
		ModelFactory graphFactory = new ModelFactory();
//		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testImplementsWithAttribute() {
		GraphList classModel = new GraphList();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		person.withAttribute("name", DataType.STRING);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testExtendsWithoutData() {
		GraphList classModel = new GraphList();
		Clazz person = classModel.createClazz("Person");
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testExtendsWithData() {
		GraphList classModel = new GraphList();
		Clazz person = classModel.createClazz("Person");
		Clazz room = classModel.createClazz("Room");
		Clazz student = classModel.createClazz("Student");
		person.withAttribute("name", DataType.STRING);
		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		student.withSuperClazz(person);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
}