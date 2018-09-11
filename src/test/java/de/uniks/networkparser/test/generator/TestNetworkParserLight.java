package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModelBuilder;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class TestNetworkParserLight {

	@Test
	public void testBuilder() {
		ClassModelBuilder builder = new ClassModelBuilder("de.uniks.model");
		

		Clazz person = builder.buildClass("Person");
		builder.createAttribute("name", DataType.STRING)
			.createAttribute("matrikelno", DataType.INT);
		
		builder.createClass("University").createAttribute("name", DataType.STRING);
		
		builder.createAssociation("student", Cardinality.MANY, person, "studs", Cardinality.ONE);

		
		builder.build();
	}
}
