package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphImage;

public class GraphTest {
	@Test
	public void testDataType() {
		DataType dataType = DataType.create("int");
		Assert.assertEquals(dataType.toString(), "DataType.INT");
	}

	@Test
	public void testGraph() {
		GraphList list = new GraphList();
		Clazz node = new Clazz().with("Item");
		node.with(new GraphImage().with("karli.png"));
		list.with(node);

		GraphConverter converter=new GraphConverter();
		Assert.assertEquals("{\"typ\":\"classdiagram\",\"style\":null,\"nodes\":[{\"typ\":\"clazz\",\"id\":\"Item\",\"head\":{\"src\":\"karli.png\"}}]}", converter.convert(list, false).toString());
	}


	@Test
	public void testSuperClasses() {
		Clazz student = new Clazz().with("Student");
		Clazz person = new Clazz().with("Person");
		student.withSuperClazz(person);

		Assert.assertEquals(student.getSuperClass(), person);
		Assert.assertTrue(person.getKidClazzes(false).contains(student));
	}
}
