package de.uniks.networkparser.test.generator;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;

public class TestRemoveCode {

//	@Test
	public void testRemoveAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		String rootDir = "src/test/java";
		
		ClassModel model = new ClassModel("org.sdmlib.simple.model.removeCode_a");
		
		Clazz person = model.createClazz("Person");
		
		person.withAttribute("name", DataType.STRING);
		
		model.generate(rootDir);
		
//FIXME		model.getGenerator().getOrCreate(person.getAttributes().first()).removeGeneratedCode(rootDir);
		
	}
	
//	@Test
	public void testRemoveMethod() {
		if(Generator.DISABLE) {
			return;
		}
		String rootDir = "src/test/java";
		
		ClassModel model = new ClassModel("org.sdmlib.simple.model.removeCode_b");
		
		Clazz person = model.createClazz("Person");
		
		person.withMethod("think", DataType.VOID);
		
		model.generate(rootDir);
		
		//FIXME		model.getGenerator().getOrCreate(person.getMethods().first()).removeGeneratedCode(rootDir);
		
	}
	
//	@Test
	public void testRemoveClass() {
		if(Generator.DISABLE) {
			return;
		}
		String rootDir = "src/test/java";
		
		ClassModel model = new ClassModel("org.sdmlib.simple.model.removeCode_c");
		
		Clazz person = model.createClazz("Person");
		Clazz pupil = model.createClazz("Pupil");
		
		model.generate(rootDir);
		
		//FIXME GenClass genPupil = (GenClass) model.getGenerator().getOrCreate(pupil);
		
		//FIXME genPupil.removeGeneratedCode(rootDir);
		
	}
	
//	@Test
	public void testRemoveAssociation() {
		if(Generator.DISABLE) {
			return;
		}
		String rootDir = "src/test/java";
		
		ClassModel model = new ClassModel("org.sdmlib.simple.model.removeCode_d");
		
		Clazz person = model.createClazz("Person");
		Clazz pupil = model.createClazz("Pupil");
		
		person.withBidirectional(pupil, "pupils", Cardinality.MANY, "person", Cardinality.ONE);
		
		model.generate(rootDir);
		
		//FIXME model.getGenerator().getOrCreate(person.getAssociations().first()).removeGeneratedCode(rootDir);
		
		//FIXME model.getGenerator().getOrCreate(pupil.getAssociations().first()).removeGeneratedCode(rootDir);
		
	}
	
}
