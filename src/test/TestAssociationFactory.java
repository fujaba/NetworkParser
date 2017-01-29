package test;

import org.junit.Test;
import org.sdmlib.models.classes.ClassModel;

import de.uniks.factory.ModelFactory;
import de.uniks.factory.java.JavaModelFactory;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;

public class TestAssociationFactory {

	@Test
	public void testBidirectionalAssociation() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person");
		Clazz room = classModel.createClazz("Room");
		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.println(javaModelFactory.create(classModel));
	}

	@Test
	public void testMethod() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person");
		Clazz student = classModel.createClazz("Student");
		Method method = new Method("personMethod", DataType.VOID, new Parameter(DataType.STRING).with("name"), new Parameter(DataType.INT).with("age"), new Parameter(student, "student"));
		person.with(method);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.println(javaModelFactory.create(classModel));
	}
	
	@Test
	public void testInterfaceMethod() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		Method method = new Method("personMethod", DataType.VOID);
		person.with(method);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.println(javaModelFactory.create(classModel));
	}
	
	@Test
	public void testAbstractMethod() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person").with(Modifier.create("abstract"));
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		Method method = new Method("personMethod", DataType.VOID).with(Modifier.create("abstract"));
		person.with(method);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.println(javaModelFactory.create(classModel));
	}
	
	@Test
	public void testAbstractInterfaceMethod() {
		ClassModel classModel = new ClassModel();
		Clazz human = classModel.createClazz("Human").enableInterface();
		Clazz person = classModel.createClazz("Person").with(Modifier.create("abstract"));
		Clazz student = classModel.createClazz("Student");
		Clazz subHuman = classModel.createClazz("subHuman");
		person.withSuperClazz(human);
		student.withSuperClazz(person);
		Method method = new Method("personMethod", DataType.VOID, new Parameter(subHuman, "subHuman")).with(Modifier.create("abstract"));
		human.with(method);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.println(javaModelFactory.create(classModel));
	}
	
	@Test 
	public void testImplements() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.print(javaModelFactory.create(classModel));
	}

	@Test
	public void testImplementsWithData() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person);
		person.withAttribute("name", DataType.STRING);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.print(javaModelFactory.create(classModel));
	}
	
	@Test
	public void testImplementsWithDataExtended() {
		ClassModel classModel = new ClassModel();
		Clazz human = classModel.createClazz("Human").enableInterface();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz student = classModel.createClazz("Student");
		Clazz animal = classModel.createClazz("Animal");
		student.withSuperClazz(person);
		person.withSuperClazz(human);
		human.withAttribute("age", DataType.INT);
		person.withAttribute("name", DataType.STRING);
		human.withBidirectional(animal, "animal", Cardinality.ONE, "human", Cardinality.ONE);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.print(javaModelFactory.create(classModel));
	}
	
	@Test
	public void testAbstractSuperClazzWithAttribute() {
		ClassModel classModel = new ClassModel();
		Clazz human = classModel.createClazz("Human").with(Modifier.create("abstract"));
		Attribute name = new Attribute("name", DataType.STRING).with(Modifier.create("abstract"));
		human.with(name);
		human.withAttribute("wise", DataType.BOOLEAN);
		Clazz person = classModel.createClazz("Person").with(Modifier.create("abstract"));
		Attribute age = new Attribute("age", DataType.INT).with(Modifier.create("abstract"));
		person.with(age);
		Clazz student = classModel.createClazz("Student");
		person.withSuperClazz(human);
		student.withSuperClazz(person);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.println(javaModelFactory.create(classModel));
	}
	
	@Test
	public void testAbstractSuperClazzWithAssociation() {
		ClassModel classModel = new ClassModel();
		Clazz human = classModel.createClazz("Human").with(Modifier.create("abstract"));
		Clazz subHuman = classModel.createClazz("SubHuman");
		human.withBidirectional(subHuman, "subHuman", Cardinality.ONE, "human", Cardinality.ONE);
		human.getAssociations(a -> ((Association) a).getName().equals("subHuman")).first();
		Clazz person = classModel.createClazz("Person").with(Modifier.create("abstract"));
		Clazz subPerson = classModel.createClazz("SubPerson");
		person.withBidirectional(subPerson, "subPerson", Cardinality.ONE, "person", Cardinality.ONE);
		Clazz student = classModel.createClazz("Student");
		person.withSuperClazz(human);
		student.withSuperClazz(person);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.println(javaModelFactory.create(classModel));
	}
	
	@Test
	public void testImplementAndExtendAssociation() {
		ClassModel classModel = new ClassModel();
		Clazz human = classModel.createClazz("Human").enableInterface();
		Clazz subHuman = classModel.createClazz("SubHuman").withBidirectional(human, "human", Cardinality.ONE, "subHuman", Cardinality.ONE);
		Clazz person = classModel.createClazz("Person").withSuperClazz(human);
		Clazz student = classModel.createClazz("Student").withSuperClazz(person, human);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.println(javaModelFactory.create(classModel));
	}
	
	@Test 
	public void testMultipleSuperClazzes() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz human = classModel.createClazz("Human");
		Clazz student = classModel.createClazz("Student");
		student.withSuperClazz(person, human);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.print(javaModelFactory.create(classModel));
	}
	
	@Test
	public void testSpecialInterfaceCase() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person").enableInterface();
		Clazz human = classModel.createClazz("Human");
		Clazz student = classModel.createClazz("Student");
		Clazz participant = classModel.createClazz("Participant");
		Clazz subPerson = classModel.createClazz("SubPerson");
		human.withSuperClazz(person);
		student.withSuperClazz(human);
		participant.withSuperClazz(student);
		person.withBidirectional(subPerson, "subPerson", Cardinality.ONE, "person", Cardinality.ONE);
		JavaModelFactory javaModelFactory = new JavaModelFactory();
		System.out.print(javaModelFactory.create(classModel));
	}
	
}