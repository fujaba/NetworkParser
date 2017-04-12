package test;

import org.junit.Test;

import de.uniks.factory.cpp.CppModelFactory;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.Parameter;

public class TestCppMethod {

	@Test
	public void testMethod() {
		GraphList model = new GraphList();
		Clazz person = model.createClazz("Person");
		person.withMethod("testMethod", DataType.STRING, new Parameter(DataType.STRING));
		CppModelFactory modelFactory = new CppModelFactory();
		System.out.println(modelFactory.create(model));
	}
	
}
