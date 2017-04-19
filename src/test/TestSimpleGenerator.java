package test;

import org.junit.Test;

import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.template.generator.ModelGenerator;

public class TestSimpleGenerator {

	@Test
	public void testGenerator() {
		GraphList classModel = new GraphList().with("de.uniks.test.model");
		Clazz person = classModel.createClazz("Person");
		Clazz room = classModel.createClazz("Room");
		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);
		ModelGenerator javaModelFactory = new ModelGenerator();
		javaModelFactory.generate("src", classModel);
//		System.out.println(javaModelFactory.create(classModel));
	}
}
