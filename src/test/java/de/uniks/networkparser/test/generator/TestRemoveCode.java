package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class TestRemoveCode {
	@Test
	public void testRemoveAttribute() {
		if(Os.isGenerator() == false) {
			return;
		}
		String rootDir = "src/test/java";

		ClassModel model = new ClassModel("org.sdmlib.simple.model.removeCode_a");

		Clazz person = model.createClazz("Person");

		person.withAttribute("name", DataType.STRING);

		model.generate(rootDir);

//FIXME		model.getGenerator().getOrCreate(person.getAttributes().first()).removeGeneratedCode(rootDir);

	}

	@Test
	public void testRemoveMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		String rootDir = "src/test/java";

		ClassModel model = new ClassModel("org.sdmlib.simple.model.removeCode_b");

		Clazz person = model.createClazz("Person");

		person.withMethod("think", DataType.VOID);

		model.generate(rootDir);

		//FIXME		model.getGenerator().getOrCreate(person.getMethods().first()).removeGeneratedCode(rootDir);

	}

	@Test
	public void testRemoveClass() {
		if(Os.isGenerator() == false) {
			return;
		}
		String rootDir = "src/test/java";

		ClassModel model = new ClassModel("org.sdmlib.simple.model.removeCode_c");

		model.createClazz("Person");
		model.createClazz("Pupil");

		model.generate(rootDir);

		//FIXME GenClass genPupil = (GenClass) model.getGenerator().getOrCreate(pupil);

		//FIXME genPupil.removeGeneratedCode(rootDir);

	}

	@Test
	public void testRemoveAssociation() {
		if(Os.isGenerator() == false) {
			return;
		}
		String rootDir = "src/test/java";

		ClassModel model = new ClassModel("org.sdmlib.simple.model.removeCode_d");

		Clazz person = model.createClazz("Person");
		Clazz pupil = model.createClazz("Pupil");

		person.withBidirectional(pupil, "pupils", Association.MANY, "person", Association.ONE);

		model.generate(rootDir);

		//FIXME model.getGenerator().getOrCreate(person.getAssociations().first()).removeGeneratedCode(rootDir);

		//FIXME model.getGenerator().getOrCreate(pupil.getAssociations().first()).removeGeneratedCode(rootDir);

	}

}
