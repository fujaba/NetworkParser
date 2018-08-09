package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.ModelGenerator;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.parser.TemplateResultFragment;

public class testGen {

	@Test
	public void testModel() {
		ClassModel model = new ClassModel("org.networkparser.simple.model");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);

		room.withMethod("init", DataType.VOID);

		person.withBidirectional(room, "room", Cardinality.ONE, "persons", Cardinality.MANY);

		model.withFeature(Feature.DYNAMICVALUES.create());
		model.generate("src/test/java");
	}
	
	@Test
	public void testModelWithPrivate() {
		ClassModel model = new ClassModel("org.networkparser.simple.model");
		Clazz person = model.createClazz("Person");
		Attribute password = person.createAttribute("password", DataType.STRING);
		password.withRole(new ObjectCondition() {
			
			@Override
			public boolean update(Object value) {
				TemplateResultFragment fragment = (TemplateResultFragment) value;
				System.out.println(fragment.getResult());
				return false;
			}
		});
		SendableEntityCreator generateJava = model.getGenerator().generating(null, ModelGenerator.TYPE_JAVA, model);
		System.out.println(generateJava);
	}
}
