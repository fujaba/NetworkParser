package de.uniks.networkparser.test.generator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.ClassModelBuilder;
import de.uniks.networkparser.ext.story.Story;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class TestModelGenForReadMe {

	@Test
	public void testModelGen() {
		Story story = new Story();
		Assert.assertNotNull(story);
		ClassModelBuilder mb = new ClassModelBuilder("de.uniks.studyright");
		Clazz uni = mb.buildClass("University").withAttribute("name", DataType.STRING);
		Clazz student = mb.buildClass("Student").withAttribute("matNo", DataType.INT);
		
		
		uni.withAssoc(student, "students", Association.MANY, "uni", Association.ONE);
		Clazz room = mb.buildClass("Room")
		        .withAttribute("roomNo", DataType.STRING);
		uni.withAssoc(room, "rooms", Association.MANY, "uni", Association.ONE);
		ClassModel model = mb.build(ClassModelBuilder.NOGEN);
		
		System.out.println(model.toString());
		
//		HTMLEntity entity=new HTMLEntity();
//		entity.withText("CLassBuilder");
//		entity.withGraph(model);
		
//		GraphConverter converter = new GraphConverter();
//		converter.
//		model
	}
	@Test
	public void testOldSchool() {
		ClassModel model = new ClassModel("de.uniks.studyright");
		Clazz uni = model.createClazz("University").withAttribute("name", DataType.STRING);
		Clazz student = model.createClazz("Student").withAttribute("matNo", DataType.INT);
		
		uni.withAssoc(student, "students", Association.MANY, "uni", Association.ONE);
		Clazz room = model.createClazz("Room").withAttribute("roomNo", DataType.STRING);
		uni.withAssoc(room, "rooms", Association.MANY, "uni", Association.ONE);
		
		
	}
}
