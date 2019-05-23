package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Literal;

public class TestEnumeration {
	@Test
	public void testEnumerationWithoutEntries() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.enums_a");
		Clazz testEnum = model.createClazz("TestEnum");

		testEnum.enableEnumeration();

		model.getGenerator().removeAndGenerate("java");

	}

	@Test
	public void testEnumerationWithEntry() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.enums_b");
		Clazz testEnum = model.createClazz("TestEnum");

		testEnum.enableEnumeration("PERSON");

		model.getGenerator().removeAndGenerate("java");

	}

	// FIXME bei mehreren Eintraegen ein , statt einem ; generiern ausser bei letztem Eintrag

	@Test
	public void testEnumerationWithMultipleEntries() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.enums_c");
		Clazz testEnum = model.createClazz("TestEnum");

		testEnum.enableEnumeration("PERSON","ROOM");
		GraphUtil.setLiteral(testEnum, new Literal("TEACHER"));

		model.getGenerator().removeAndGenerate("java");
	}
	@Test
	public void testEnumerationWithMultipleEntriesKeyValue() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.enums_d");
		Clazz testEnum = model.createClazz("TestEnum");
		testEnum.withAttribute("value", DataType.INT);
		testEnum.enableEnumeration(new Literal("TEACHER").withValue(42));
		model.generate("src/test/java");
		model.getGenerator().removeAndGenerate("java");
	}

}
