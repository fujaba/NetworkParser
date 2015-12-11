package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphImage;

public class GraphTest {

	@Test
	public void testGraph() {
		GraphList list = new GraphList();
		Clazz node = new Clazz().with("Item");
		node.with(new GraphImage().with("karli.png"));
		list.with(node);
		
		GraphConverter converter=new GraphConverter();
		Assert.assertEquals("{\"typ\":\"classdiagram\",\"style\":null,\"nodes\":[{\"typ\":\"clazz\",\"id\":\"Item\",\"head\":{\"src\":\"karli.png\"}}]}", converter.convert(list, false).toString()); 
	}
}
