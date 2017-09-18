package de.uniks.networkparser.test.generator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Method;

public class TestSDMLib {
	@Test
	public void testClassWithAnnotation() {
		ClassModel model = new ClassModel("org.sdmlib.simple.model.sdmLib");
		Clazz person = model.createClazz("Person");
		Clazz nameAttribute = person.withAttribute("name", DataType.STRING);
		Method eatMethod = person.createMethod("eat");
		
		model.getGenerator().testGeneratedCode();

		
		person.createAttribute("age", DataType.INT);
		person.createMethod("go");
		person.remove(nameAttribute);
		person.remove(eatMethod);
		model.generate("src/test/java");
		
		
		// Create a Person with name and age Attribute
		// and eat and go Method
		Assert.assertEquals(2, person.getAttributes().size());
		Assert.assertEquals(2, person.getMethods().size());
		
	}
}
