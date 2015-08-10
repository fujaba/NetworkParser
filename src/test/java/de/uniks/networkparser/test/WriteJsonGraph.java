package de.uniks.networkparser.test;

import java.io.IOException;

import org.junit.Test;

import de.uniks.networkparser.graph.GraphClazz;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.GraphDataType;
import de.uniks.networkparser.graph.GraphEdge;
import de.uniks.networkparser.graph.GraphEdgeTypes;
import de.uniks.networkparser.graph.GraphIdMap;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.xml.HTMLEntity;

public class WriteJsonGraph {

	@Test
	public void testHTMLEntity() throws IOException {
		HTMLEntity htmlEntity = new HTMLEntity();
		
		htmlEntity.withHeader("includes/diagramstyle.css");
		htmlEntity.withHeader("includes/graph.js");
		htmlEntity.withHeader("includes/dagre.min.js");
		htmlEntity.withHeader("includes/drawer.js");
		
		System.out.println(htmlEntity.toString(2));

		DocEnvironment docEnvironment = new DocEnvironment();
		GraphList model = new GraphList().withTyp(GraphIdMap.CLASS);

		GraphClazz abstractArray = model.with(new GraphClazz().withClassName("AbstractArray"));
		abstractArray.withAttribute("elements", GraphDataType.ref("Object[]"));
		abstractArray.withAttribute("size", GraphDataType.INT);
		abstractArray.withAttribute("index", GraphDataType.INT);
		abstractArray.withAttribute("flag", GraphDataType.BYTE);
		GraphClazz baseItem = model.with(new GraphClazz().withClassName("BaseItem"));
		GraphClazz iterable = model.with(new GraphClazz().withClassName("Iterable<V>"));
		GraphClazz abstractList = model.with(new GraphClazz().withClassName("AbstractList<V>"));
		GraphClazz simpleList = model.with(new GraphClazz().withClassName("SimpleList<V>"));
		GraphClazz simpleSet = model.with(new GraphClazz().withClassName("SimpleSet<V>"));
		GraphClazz simpleKeyValueList = model.with(new GraphClazz().withClassName("SimpleKeyValueList<K, V>"));
		GraphClazz map = model.with(new GraphClazz().withClassName("Map<K, V>"));
		GraphClazz list = model.with(new GraphClazz().withClassName("List<V>"));
		GraphClazz set = model.with(new GraphClazz().withClassName("Set<V>"));
		
		
		
		baseItem.withInterface(true);

		model.with(GraphEdge.create(abstractArray, baseItem).withTyp(GraphEdgeTypes.IMPLEMENTS));
		model.with(GraphEdge.create(abstractArray, iterable).withTyp(GraphEdgeTypes.IMPLEMENTS));
		model.with(GraphEdge.create(abstractList, abstractArray).withTyp(GraphEdgeTypes.GENERALISATION));
		
		model.with(GraphEdge.create(simpleKeyValueList, abstractArray).withTyp(GraphEdgeTypes.GENERALISATION));
		model.with(GraphEdge.create(simpleList, abstractList).withTyp(GraphEdgeTypes.GENERALISATION));
		model.with(GraphEdge.create(simpleSet, abstractList).withTyp(GraphEdgeTypes.GENERALISATION));
		
		
		model.with(GraphEdge.create(simpleKeyValueList, map).withTyp(GraphEdgeTypes.IMPLEMENTS));
		model.with(GraphEdge.create(simpleList, list).withTyp(GraphEdgeTypes.IMPLEMENTS));
		model.with(GraphEdge.create(simpleSet, set).withTyp(GraphEdgeTypes.IMPLEMENTS));
		
		
		
		docEnvironment.writeJson("simpleCollection.html", new GraphConverter().convertToJson(model, true));
	}
}
