package de.uniks.networkparser.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.graph.GraphCardinality;
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
		
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/graph.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/dagre.min.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/drawer.js");
		
		Assert.assertEquals(425, htmlEntity.toString(2).length());

		DocEnvironment docEnvironment = new DocEnvironment();
		GraphList model = new GraphList().withTyp(GraphIdMap.CLASS);

		GraphClazz abstractArray = model.with(new GraphClazz().with("AbstractArray"));
		abstractArray.createAttribute("elements", GraphDataType.ref("Object[]"));
		abstractArray.createAttribute("size", GraphDataType.INT);
		abstractArray.createAttribute("index", GraphDataType.INT);
		abstractArray.createAttribute("flag", GraphDataType.BYTE);
		GraphClazz baseItem = model.with(new GraphClazz().with("BaseItem"));
		GraphClazz iterable = model.with(new GraphClazz().with("Iterable<V>"));
		GraphClazz abstractList = model.with(new GraphClazz().with("AbstractList<V>"));
		GraphClazz simpleList = model.with(new GraphClazz().with("SimpleList<V>"));
		GraphClazz simpleSet = model.with(new GraphClazz().with("SimpleSet<V>"));
		GraphClazz simpleKeyValueList = model.with(new GraphClazz().with("SimpleKeyValueList<K, V>"));
		GraphClazz map = model.with(new GraphClazz().with("Map<K, V>"));
		GraphClazz list = model.with(new GraphClazz().with("List<V>"));
		GraphClazz set = model.with(new GraphClazz().with("Set<V>"));
		
		
		
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
		
		
		
		docEnvironment.writeJson("simpleCollection.html", "../src/main/resources/de/uniks/networkparser/graph/", new GraphConverter().convertToJson(model, true));
	}
	
	@Test
	public void testWriteSimpleHTML() {
		HTMLEntity htmlEntity = new HTMLEntity();
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/graph.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/dagre.min.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/drawer.js");
		
		GraphList model = new GraphList().withTyp(GraphIdMap.CLASS);
		GraphClazz uni = model.with(new GraphClazz().with("University"));
		uni.createAttribute("name", GraphDataType.STRING);
		GraphClazz person = model.with(new GraphClazz().with("Person"));
		
		uni.withAssoc(person, "has", GraphCardinality.MANY, "studis", GraphCardinality.ONE);
		Assert.assertEquals(654, htmlEntity.withGraph(model).toString(2).length());
	}
}
