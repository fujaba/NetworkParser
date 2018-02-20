package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.util.MethodSet;

public class GetMethodsTest {

	@Test
	public void testAbstract() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").with(Modifier.create(Modifier.ABSTRACT));
		Method method = person.createMethod("think").with(DataType.BOOLEAN);
		method.with(Modifier.create(Modifier.ABSTRACT));

		Clazz student = model.createClazz("Student").withSuperClazz(person);

		MethodSet methods = student.getMethods();
		Assert.assertEquals(1, methods.size());
		Assert.assertEquals(method, methods.get(0));
	}

	@Test
	public void testInterface() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").enableInterface();
		Clazz student = model.createClazz("Student").withSuperClazz(person);
		Method method = person.createMethod("think").with(DataType.BOOLEAN);
		MethodSet methods = student.getMethods();
		Assert.assertEquals(1, methods.size());
		Assert.assertEquals(method, methods.get(0));
	}

	@Test
	public void testAbstractToNormal() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").with(Modifier.create(Modifier.ABSTRACT));
		Clazz human = model.createClazz("Human").withSuperClazz(person);
		Clazz student = model.createClazz("Student").withSuperClazz(human);
		Method method = person.createMethod("think").with(DataType.BOOLEAN);
		MethodSet humanMethods = human.getMethods();
		Assert.assertEquals(0, humanMethods.size());

		method.with(Modifier.ABSTRACT);
		humanMethods = human.getMethods();
		Assert.assertEquals(1, humanMethods.size());
		Assert.assertEquals(method, humanMethods.get(0));
		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(0, studentMethods.size());
	}

	@Test
	public void testAbstractToAbstract() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").with(Modifier.create(Modifier.ABSTRACT));
		Method method = person.createMethod("think").with(DataType.BOOLEAN);

		Clazz human = model.createClazz("Human").withSuperClazz(person).with(Modifier.create(Modifier.ABSTRACT));

		Clazz student = model.createClazz("Student").withSuperClazz(human);

		MethodSet humanMethods = human.getMethods();
		Assert.assertEquals(0, humanMethods.size());

		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(0, studentMethods.size());

		method.with(Modifier.ABSTRACT);

		studentMethods = student.getMethods();

		Assert.assertEquals(1, studentMethods.size());
		Assert.assertEquals(method, studentMethods.get(0));
	}

	@Test
	public void testAbstractToNormalToAbstract() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").with(Modifier.create(Modifier.ABSTRACT));
		Clazz human = model.createClazz("Human").withSuperClazz(person);
		Clazz student = model.createClazz("Student").withSuperClazz(human);
		Clazz pupil = model.createClazz("Pupil").withSuperClazz(student).with(Modifier.create(Modifier.ABSTRACT));
		Method method = person.createMethod("think").with(DataType.BOOLEAN).with(Modifier.create(Modifier.ABSTRACT));
		MethodSet humanMethods = human.getMethods();

		Assert.assertEquals(1, humanMethods.size());
		Assert.assertEquals(method, humanMethods.get(0));
		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(0, studentMethods.size());
		MethodSet pupilMethods = pupil.getMethods();
		Assert.assertEquals(0, pupilMethods.size());
	}

	@Test
	public void testAbstractToAbstractToNormal() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").with(Modifier.create(Modifier.ABSTRACT));
		Method method = person.createMethod("think").with(DataType.BOOLEAN).with(Modifier.ABSTRACT);

		Clazz human = model.createClazz("Human").withSuperClazz(person).with(Modifier.create(Modifier.ABSTRACT));
		Clazz student = model.createClazz("Student").withSuperClazz(human);
		Clazz pupil = model.createClazz("Pupil").withSuperClazz(student);
		MethodSet humanMethods = human.getMethods();
		Assert.assertEquals(0, humanMethods.size());

		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(1, studentMethods.size());
		Assert.assertEquals(method, studentMethods.get(0));
		MethodSet pupilMethods = pupil.getMethods();
		Assert.assertEquals(0, pupilMethods.size());
	}

	@Test
	public void testAbstractToAbstractToNormalToAbstract() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").with(Modifier.create(Modifier.ABSTRACT));
		Clazz human = model.createClazz("Human").withSuperClazz(person).with(Modifier.create(Modifier.ABSTRACT));
		Clazz student = model.createClazz("Student").withSuperClazz(human);
		Clazz pupil = model.createClazz("Pupil").withSuperClazz(student);
		Clazz subPupil = model.createClazz("SubPupil").withSuperClazz(pupil).with(Modifier.create(Modifier.ABSTRACT));
		Method method = person.createMethod("think").with(DataType.BOOLEAN).with(Modifier.ABSTRACT);
		MethodSet humanMethods = human.getMethods();
		Assert.assertEquals(0, humanMethods.size());

		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(1, studentMethods.size());
		Assert.assertEquals(method, studentMethods.get(0));
		MethodSet pupilMethods = pupil.getMethods();
		Assert.assertEquals(0, pupilMethods.size());
		MethodSet subPupilMethods = subPupil.getMethods();
		Assert.assertEquals(0, subPupilMethods.size());
	}

	@Test
	public void testAbstractToNormalToAbstractToNormal() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").with(Modifier.create(Modifier.ABSTRACT));
		Clazz human = model.createClazz("Human").withSuperClazz(person);
		Clazz student = model.createClazz("Student").withSuperClazz(human);
		Clazz pupil = model.createClazz("Pupil").withSuperClazz(student).with(Modifier.create(Modifier.ABSTRACT));
		Clazz subPupil = model.createClazz("SubPupil").withSuperClazz(pupil);
		Method method = person.createMethod("think").with(DataType.BOOLEAN).with(Modifier.ABSTRACT);
		MethodSet humanMethods = human.getMethods();
		Assert.assertEquals(1, humanMethods.size());
		Assert.assertEquals(method, humanMethods.get(0));
		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(0, studentMethods.size());
		MethodSet pupilMethods = pupil.getMethods();
		Assert.assertEquals(0, pupilMethods.size());
		MethodSet subPupilMethods = subPupil.getMethods();
		Assert.assertEquals(0, subPupilMethods.size());
	}

	@Test
	public void testInterfaceToAbstractToNormal() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").enableInterface();
		Clazz student = model.createClazz("Student").withSuperClazz(person).with(Modifier.create(Modifier.ABSTRACT));
		Clazz pupil = model.createClazz("Pupil").withSuperClazz(student);
		Method method = person.createMethod("think").with(DataType.BOOLEAN);
		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(0, studentMethods.size());
		MethodSet pupilMethods = pupil.getMethods();
		Assert.assertEquals(1, pupilMethods.size());
		Assert.assertEquals(method, pupilMethods.get(0));
	}

	@Test
	public void testInterfaceToNormalToAbstract() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").enableInterface();
		Clazz student = model.createClazz("Student").withSuperClazz(person);
		Clazz pupil = model.createClazz("Pupil").withSuperClazz(student).withSuperClazz(student).with(Modifier.create(Modifier.ABSTRACT));
		Method method = person.createMethod("think").with(DataType.BOOLEAN);
		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(1, studentMethods.size());
		Assert.assertEquals(method, studentMethods.get(0));
		MethodSet pupilMethods = pupil.getMethods();
		Assert.assertEquals(0, pupilMethods.size());
	}

	@Test
	public void testInterfaceAndAbstract() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").enableInterface();
		Clazz teacher = model.createClazz("Teacher").with(Modifier.create(Modifier.ABSTRACT));
		Clazz student = model.createClazz("Student").withSuperClazz(person, teacher);
		Method method = person.createMethod("think").with(DataType.BOOLEAN);
		Method method2 = teacher.createMethod("walk").with(DataType.INT).with(Modifier.create(Modifier.ABSTRACT));
		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(2, studentMethods.size());
		Assert.assertTrue(studentMethods.contains(method));
		Assert.assertTrue(studentMethods.contains(method2));
	}

	@Test
	public void testInterfaceAndAbstractToNormal() {
		GraphList model = new GraphList().with("de.uniks");
		Clazz person = model.createClazz("Person").enableInterface();
		Clazz teacher = model.createClazz("Teacher").with(Modifier.create(Modifier.ABSTRACT));
		Clazz student = model.createClazz("Student").withSuperClazz(person, teacher);
		Clazz pupil = model.createClazz("Pupil").withSuperClazz(student);
		Method method = person.createMethod("think").with(DataType.BOOLEAN);
		Method method2 = teacher.createMethod("walk").with(DataType.INT).with(Modifier.create(Modifier.ABSTRACT));
		MethodSet studentMethods = student.getMethods();
		Assert.assertEquals(2, studentMethods.size());
		Assert.assertTrue(studentMethods.contains(method));
		Assert.assertTrue(studentMethods.contains(method2));
		MethodSet pupilMethods = pupil.getMethods();
		Assert.assertEquals(0, pupilMethods.size());
	}

}
