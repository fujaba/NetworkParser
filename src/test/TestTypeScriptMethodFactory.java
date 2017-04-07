package test;

import org.junit.Test;

import de.uniks.factory.typescript.TypeScriptModelFactory;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;

public class TestTypeScriptMethodFactory {

	@Test
	public void testMethod() {
		GraphList model = new GraphList().with("i.love.sdmlib");
		Clazz room = model.createClazz("Room");
		Clazz student = model.createClazz("Student");
		room.withMethod("determineStudent", DataType.create(student));
		TypeScriptModelFactory modelFactory = new TypeScriptModelFactory();
		System.out.println(modelFactory.create(model));
	}
	
}
