package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.graph.GraphClazz;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphNodeImage;

public class GraphTest {

	@Test
	public void testGraph() {
		GraphList list = new GraphList();
		GraphClazz node = new GraphClazz().withClassName("Item");
		node.with(new GraphNodeImage().with("karli.png"));
		list.with(node);
		
		GraphConverter converter=new GraphConverter();
		System.out.println(converter.convert(list, false));
	}
}
