package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.ModelGenerator;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Method;

public class TypeScriptModelTest {

	@Test
	public void testTypescriptClazz() {
		ClassModel model = new ClassModel("org.sdmlib.simple.typescript.model.test");
		Clazz person = model.createClazz("Person").enableInterface();

		Clazz student = model.createClazz("Student");
		student.withSuperClazz(person);

		person.createAttribute("name", DataType.STRING);
		person.createAttribute("credits", DataType.LONG);

		Method createMethod = person.createMethod("getLong");
		createMethod.with(DataType.LONG);

		Clazz building = model.createClazz("Building");

		Clazz uni = model.createClazz("University");
		uni.withSuperClazz(building);

		uni.withBidirectional(person, "stud", Association.MANY, "owner", Association.ONE);

		model.createClazz("Teacher");

		model.getGenerator().testGeneratedCode(ModelGenerator.TYPE_TYPESCRIPT);
	}

}
