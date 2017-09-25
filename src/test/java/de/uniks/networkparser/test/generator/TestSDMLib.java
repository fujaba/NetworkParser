package de.uniks.networkparser.test.generator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.util.MethodSet;

public class TestSDMLib {
	@Test
	public void testSDMLibModification() {
//		if(Generator.DISABLE) {
//			return;
//		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.sdmLib");
		Clazz person = model.createClazz("Person");
		Attribute nameAttribute = new Attribute("name", DataType.STRING);
		person.with(nameAttribute);
		Method eatMethod = person.createMethod("eat");
		
		model.getGenerator().testGeneratedCode();

		Assert.assertEquals(1, person.getAttributes().size());
		Assert.assertEquals(1, person.getMethods().size());
		
		
		person.createAttribute("age", DataType.INT);
		person.createMethod("go");
		
		Assert.assertEquals(2, person.getAttributes().size());
		Assert.assertEquals(2, person.getMethods().size());
		
		person.remove(nameAttribute);
		person.remove(eatMethod);
		
		Assert.assertEquals(1, person.getAttributes().size());
		Assert.assertEquals(1, person.getMethods().size());
		
		
		//model.generate("src/test/java");
		model.getGenerator().generateJava("build/gen/java", model, null);
		
		// Create a Person with name and age Attribute
		// and eat and go Method
		Assert.assertEquals(2, person.getAttributes().size());
		MethodSet methods = person.getMethods();
		Assert.assertEquals(2, methods.size());
		
	}
}
