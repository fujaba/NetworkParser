package de.uniks.networkparser.test.generator;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;

public class TestAnnotation {

	@Test
	public void testAnnotation() {
		ClassModel model = new ClassModel("de.uniks.networkparser.simple.modelA");
		Clazz a = model.createClazz("A");
		Clazz b = model.createClazz("B");
		Association newAssoc = a.createBidirectional(b, "b", Association.MANY, "parent", Association.ONE);
		Annotation annotation = Annotation.create("XmlElementWrapper", "name", "emails");
		annotation.withImport("javax.xml.bind.annotation.XmlElementWrapper");
		newAssoc.with(annotation);
//		model.generate("src/test/java");
//		@XmlElementWrapper(name = "emails")
//		 @XmlElement(name = "email")
	}
}
