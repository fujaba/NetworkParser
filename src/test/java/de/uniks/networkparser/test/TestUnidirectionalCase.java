package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;

public class TestUnidirectionalCase {

	@Test
	public void testSelfReferencingUnidirectional() {
		ClassModel model = new ClassModel();

		Clazz testClazz = model.createClazz("TestClazz");

		Association assoc = testClazz.createUniDirectional(testClazz, "target", Association.ONE);

		Assert.assertTrue(testClazz.getAssociations().contains(assoc));
		Assert.assertTrue(testClazz.getAssociations().contains(assoc.getOther()));
	}

}
