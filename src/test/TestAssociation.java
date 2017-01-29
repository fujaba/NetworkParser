package test;

import org.junit.Test;
import org.sdmlib.models.classes.ClassModel;

import de.uniks.factory.ModelFactory;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzImport;
import de.uniks.networkparser.graph.DataType;

public class TestAssociation {

	@Test
	public void testBidirectionalAssociation() {
		ClassModel classModel = new ClassModel();
		Clazz student = classModel.createClazz("Student");
		Clazz teacher = classModel.createClazz("Teacher");
		student.withBidirectional(teacher, "teacher", Cardinality.ONE, "students", Cardinality.MANY);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testImplementsWithoutMethods() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testImplementsWithMethods() {
		ClassModel classModel = new ClassModel();
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
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		person.withAttribute("name", DataType.STRING);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testExtendsWithoutData() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person");
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		ModelFactory graphFactory = new ModelFactory();
		System.out.println(graphFactory.create(classModel));
	}
	
	@Test
	public void testExtendsWithData() {
		ClassModel classModel = new ClassModel();
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