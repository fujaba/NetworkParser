package test;

import org.junit.Test;

import de.uniks.factory.cpp.CppModelFactory;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;

public class TestCppAttributeFactory {
	
	@Test
	public void testAttribute() {
		GraphList model = new GraphList();
		Clazz person = model.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		CppModelFactory modelFactory = new CppModelFactory();
		System.out.println(modelFactory.create(model));
	}

}
