package test;

import org.junit.Test;
import org.sdmlib.models.classes.ClassModel;

import de.uniks.factory.ModelFactory;
import de.uniks.factory.java.JavaModelFactory;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class TestAttributeFactory {

//	@Test
//	public void testAttributeFactory() {
//		AttributeFactory factory = new AttributeFactory();
//		Attribute attribute = new Attribute("age", DataType.INT);
//		System.out.println(factory.create(attribute, false));
//	}
	
	@Test
	public void testGraphModelAttributes() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		JavaModelFactory modelFactory = new JavaModelFactory();
//		for(int i = 0; i < 1000; i++) {
//			modelFactory.create(classModel);
//		}
		System.out.println(modelFactory.create(classModel));
	}

	@Test
	public void testCodestyleAttributes() {
		ClassModel classModel = new ClassModel();
		Clazz person = classModel.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		JavaModelFactory modelFactory = new JavaModelFactory();
		modelFactory.setCodeStyle(ModelFactory.CODESTYLE_DIVIDED);
		System.out.println(modelFactory.create(classModel));
	}
	
}
