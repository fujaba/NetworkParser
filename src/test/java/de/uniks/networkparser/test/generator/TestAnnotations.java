package de.uniks.networkparser.test.generator;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Clazz;

public class TestAnnotations {

	@Test
	public void testClassWithAnnotation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.annotation_a");
		Clazz person = model.createClazz("Person");

		Annotation annotation = Annotation.create("Deprecated");
		person.with(annotation);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithMultipleAnnotations() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.annotation_b");
		Clazz person = model.createClazz("Person");

		Annotation annotation = Annotation.create("Deprecated");

		person.with(annotation);
		person.with(Annotation.DEPRECATED);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");
	}

}
