package de.uniks.networkparser.test;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.GraphConverter;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Association;
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

		Clazz abstractArray = model.with(new Clazz().with("AbstractArray"));
		abstractArray.createAttribute("elements", DataType.ref("Object[]"));
		abstractArray.createAttribute("size", DataType.INT);
		abstractArray.createAttribute("index", DataType.INT);
		abstractArray.createAttribute("flag", DataType.BYTE);
		Clazz baseItem = model.with(new Clazz().with("BaseItem"));
		Clazz iterable = model.with(new Clazz().with("Iterable<V>"));
		Clazz abstractList = model.with(new Clazz().with("AbstractList<V>"));
		Clazz simpleList = model.with(new Clazz().with("SimpleList<V>"));
		Clazz simpleSet = model.with(new Clazz().with("SimpleSet<V>"));
		Clazz simpleKeyValueList = model.with(new Clazz().with("SimpleKeyValueList<K, V>"));
		Clazz map = model.with(new Clazz().with("Map<K, V>"));
		Clazz list = model.with(new Clazz().with("List<V>"));
		Clazz set = model.with(new Clazz().with("Set<V>"));
		
		
		
		baseItem.withInterface(true);

		model.with(Association.create(abstractArray, baseItem).withTyp(GraphEdgeTypes.IMPLEMENTS));
		model.with(Association.create(abstractArray, iterable).withTyp(GraphEdgeTypes.IMPLEMENTS));
		model.with(Association.create(abstractList, abstractArray).withTyp(GraphEdgeTypes.GENERALISATION));
		
		model.with(Association.create(simpleKeyValueList, abstractArray).withTyp(GraphEdgeTypes.GENERALISATION));
		model.with(Association.create(simpleList, abstractList).withTyp(GraphEdgeTypes.GENERALISATION));
		model.with(Association.create(simpleSet, abstractList).withTyp(GraphEdgeTypes.GENERALISATION));
		
		
		model.with(Association.create(simpleKeyValueList, map).withTyp(GraphEdgeTypes.IMPLEMENTS));
		model.with(Association.create(simpleList, list).withTyp(GraphEdgeTypes.IMPLEMENTS));
		model.with(Association.create(simpleSet, set).withTyp(GraphEdgeTypes.IMPLEMENTS));
		
		
		
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
		Clazz uni = model.with(new Clazz().with("University"));
		uni.createAttribute("name", DataType.STRING);
		Clazz person = model.with(new Clazz().with("Person"));
		
		uni.withAssoc(person, "has", Cardinality.MANY, "studis", Cardinality.ONE);
		Assert.assertEquals(654, htmlEntity.withGraph(model).toString(2).length());
	}
}
