package de.uniks.networkparser.test;


import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;

public class TestUnidirectionalCase {

	@Test
	public void testSelfReferencingUnidirectional() {
		ClassModel model = new ClassModel();

		Clazz testClazz = model.createClazz("TestClazz");

		Association assoc = testClazz.createUniDirectional(testClazz, "target", Association.ONE);

		assertTrue(testClazz.getAssociations().contains(assoc));
		assertTrue(testClazz.getAssociations().contains(assoc.getOther()));
	}

}
