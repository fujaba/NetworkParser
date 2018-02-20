package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Clazz;

public class ParserEndErrorTest {

	@Test
	public void testParserEndError() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.test");
		Clazz person = model.createClazz("Person");
		person.enableInterface();

		model.getGenerator().testGeneratedCode("java");

		model.getGenerator().testGeneratedCode("java");
	}

}
