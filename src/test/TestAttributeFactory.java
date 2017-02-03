package test;

import org.junit.Test;

import de.uniks.factory.ModelFactory;
import de.uniks.factory.java.JavaModelFactory;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphList;

public class TestAttributeFactory {

//	@Test
//	public void testAttributeFactory() {
//		AttributeFactory factory = new AttributeFactory();
//		Attribute attribute = new Attribute("age", DataType.INT);
//		System.out.println(factory.create(attribute, false));
//	}
	
	@Test
	public void testGraphModelAttributes() {
//		long startTime = System.nanoTime();
		GraphList classModel = new GraphList().with("i.love.sdmlib");
		Clazz person = classModel.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		JavaModelFactory modelFactory = new JavaModelFactory();
//		for(int i = 0; i < 1000; i++) {
//			modelFactory.create(classModel);
//		}
//		long run = (System.nanoTime() - startTime);
//		System.out.println("Duration:" +run );
//		return run;
		System.out.println(modelFactory.create(classModel));
	}
	
//	@Test
//	public void testRunTen() {
//		long result =0;
//		for(int i=0;i<30;i++) {
//			result += testGraphModelAttributes(); 
//		}
//		System.out.println("Duration:" +result/30 );
//	}

	@Test
	public void testCodestyleAttributes() {
		GraphList classModel = new GraphList();
		Clazz person = classModel.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.createAttribute("age", DataType.INT);
		person.createAttribute("wise", DataType.BOOLEAN);
		JavaModelFactory modelFactory = new JavaModelFactory();
		modelFactory.setCodeStyle(ModelFactory.CODESTYLE_DIVIDED);
		System.out.println(modelFactory.create(classModel));
	}
	
}
