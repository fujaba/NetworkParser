package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Parameter;

public class TestInterface {

	@Test
	public void testClassAsInterface() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.interface_a");
		Clazz person = model.createClazz("Person");

		person.enableInterface();

		model.getGenerator().removeAndGenerate("java");

	}

	@Test
	public void testClassAsInterfaceWithImplementedClass() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.interface_b");
		Clazz person = model.createClazz("Person");
		Clazz pupil = model.createClazz("Pupil");

		person.enableInterface();

		person.withMethod("think", DataType.VOID);
		person.withMethod("sayName", DataType.VOID, new Parameter(DataType.STRING));
		person.withAttribute("name", DataType.STRING);

		pupil.withSuperClazz(person);

		model.getGenerator().removeAndGenerate("java");

	}

	@Test
	public void testMultipleInterfacesWithMultipleChildren() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.interface_c");
		Clazz person = model.createClazz("Person");
		Clazz pupil = model.createClazz("Pupil");
		Clazz teacher = model.createClazz("Teacher");
		Clazz secretary = model.createClazz("Secretary");

		person.enableInterface();
		pupil.enableInterface();

		teacher.withSuperClazz(person);
		teacher.withSuperClazz(pupil);
		secretary.withSuperClazz(person);
		secretary.withSuperClazz(pupil);

		model.getGenerator().removeAndGenerate("java");

	}

}
