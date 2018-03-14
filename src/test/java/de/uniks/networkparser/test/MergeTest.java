package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.MergeFeature;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
//import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.interfaces.SimpleEventCondition;

public class MergeTest {

	@Test
	public void testMerge() {
		ClassModel model=new ClassModel();
		model.withFeature(MergeFeature.createCustom().withCondition(new SimpleEventCondition() {
			public boolean update(SimpleEvent value) {
//				value.get
				return false;
			};
		}));


		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person");


		classModel.generate("build/src");


		// ADD
		person.withAttribute("name", DataType.STRING);
	}

	// class Person {
		// private String name;
	//}

	@Test
	public void testMergeDiffV1() {
		// V1
		ClassModel model=new ClassModel();

//		<!--
		//V2
		//model.withFeature() // OVERRIDE EVERYTHING
//		model.getGenerator().findClazz("de.uniks.model.Person").createAttribute("first", DataType.STRING);
//	    -->
		//GENERATE
		model.generate();
	}

	@Test
	public void testMergeDiffV2() {
		de.uniks.networkparser.ext.ModelGenerator generator = new ClassModel().getGenerator("src/test/java");
		generator.findClazz("de.uniks.model.Person").createAttribute("first", de.uniks.networkparser.graph.DataType.STRING);

		generator.applyChange();
	}

	@Test
	public void testMergeDiffV3() {
		// NEW ONE
		ClassModel model = new ClassModel(); //.withFeature(feature);
		model.createClazz("de.uniks.model.Person").createAttribute("first", de.uniks.networkparser.graph.DataType.STRING);
//		model.createClazz("de.uniks.model.Person").remove()
		model.generate("build/src/test/java");
	}
}
