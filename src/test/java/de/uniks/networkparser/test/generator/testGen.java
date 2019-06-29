package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.ext.story.StoryStepJUnit;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Feature;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.interfaces.ObjectCondition;

public class testGen {
	public static boolean ENABLE=false;
	@Test
	public void testModel() {
		if(ENABLE == false) {
			return;
		}
		ClassModel model = new ClassModel("de.uniks.networkparser.simple.modelA");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");
		room.createAttribute("name", DataType.STRING);
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);

		room.withMethod("init", DataType.VOID);

		person.withBidirectional(room, "room", Association.ONE, "persons", Association.MANY);

		model.withFeature(Feature.DYNAMICVALUES.create());
		model.generate("src/test/java");
		
		Story story=new Story();
		StoryStepJUnit junit;
		try {
			junit=new StoryStepJUnit();
			junit.withUseCase(story, model);
		}catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		Object mathRoom = junit.createElement(room);

		junit.setValue(mathRoom, "name", "MathRoom");
		
//		Object infRoom = junit.createElement(room, "name", "InfRoom");
		
//		Object albert = junit.createElement(person, "name", "InfRoom", "room", infRoom);
//		Assert.assertNotNull(albert);
	}
	

//	@Test
	public void testModelWithPrivate() {
		if(ENABLE == false) {
			return;
		}
		ClassModel model = new ClassModel("de.uniks.networkparser.simple.modelB");
		Clazz person = model.createClazz("Person");
		Attribute password = person.createAttribute("password", DataType.STRING);
		GraphUtil.setRole(password, new ObjectCondition() {
			
			@Override
			public boolean update(Object value) {
//				TemplateResultFragment fragment = (TemplateResultFragment) value;
				return false;
			}
		});
//		SendableEntityCreator generateJava = model.getGenerator().generating(null, ModelGenerator.TYPE_JAVA, model);
		model.generate("src/test/java");
	}
}
