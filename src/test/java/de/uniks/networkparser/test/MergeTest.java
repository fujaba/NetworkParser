package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.MergeFeature;
import de.uniks.networkparser.ext.ModelGenerator;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
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
		
		
		classModel.generate();
		
		
		// ADD
		person.withAttribute("name", DataType.STRING);
	}
	
	// class Person {
		// private String name;
	//}
	

	public void genDiff() {
//		Clazz person = ModelGenerator.createClazz("de.uniks.model.Person");
		
		
		Clazz person = ModelGenerator.findClazz("de.uniks.model.Person");
		person.withAttribute("name", DataType.STRING);
		
		Attribute name = ModelGenerator.findAttribute(person, "name");
		
	}
}
