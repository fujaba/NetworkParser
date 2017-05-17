package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.DataTypeMap;
import de.uniks.networkparser.graph.DataTypeSet;
import de.uniks.networkparser.graph.Modifier;

public class TestAttributes {

	@Test
	public void testClassWithoutAttributes() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_a");
		Clazz person = model.createClazz("Person");
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
	}
	
	@Test
	public void testClassWithAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_b");
		Clazz person = model.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
		
	}
	
	@Test
	public void testClassWithMultipleAttributes() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_c");
		Clazz person = model.createClazz("Person");
		person.createAttribute("name", DataType.STRING);
		person.with(new Attribute("age", DataType.INT));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
		
	}
	
	@Test
	public void testClassWithSetAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_d");
		Clazz person = model.createClazz("Person");
		person.with(new Attribute("names", DataTypeSet.create(DataType.STRING)));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
		
	}
	
	@Test
	public void testClassWithSingleSetAttributes() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_e");
		Clazz person = model.createClazz("Person");
		person.with(new Attribute("ages", DataTypeSet.create(DataType.INT)));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
	}
	
	@Test
	public void testClassWithMultipleSetAttributes() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_f");
		Clazz person = model.createClazz("Person");
		person.createAttribute("names", DataTypeSet.create(DataType.STRING));
		person.with(new Attribute("ages", DataTypeSet.create(DataType.INT)));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
	}
	
	@Test
	public void testClassWithSetSetAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_g");
		Clazz person = model.createClazz("Person");
		person.createAttribute("namesSet", DataTypeSet.create(DataTypeSet.create(DataType.STRING)));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
	}
	// FIXME Parser generiert withCreator(...) anstelle von with(...)
	// (temporaer behoben)
	
	@Test
	public void testClassWithMapAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_h");
		Clazz person = model.createClazz("Person");
		person.createAttribute("names", DataTypeMap.create(DataType.STRING, DataType.STRING));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
		
	}
	
	@Test
	public void testClassWithMapMapAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_i");
		Clazz person = model.createClazz("Person");
		person.createAttribute("namesMap", DataTypeMap.create(DataType.STRING, DataTypeMap.create(DataType.STRING, DataType.STRING)));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
		
	}
	
	@Test
	public void testClassWithSetMapAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_j");
		Clazz person = model.createClazz("Person");
		Attribute createAttribute = person.createAttribute("namesList", DataTypeSet.create(DataTypeMap.create(DataType.STRING, DataType.STRING)));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
		
	}
	
	@Test
	public void testClassWithMapSetAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_k");
		Clazz person = model.createClazz("Person");
		Attribute createAttribute = person.createAttribute("names", DataTypeMap.create(DataType.STRING, DataTypeSet.create(DataType.STRING)));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
		
	}
	
	// FIXME withCreator(...) anstelle von with(...) in CreatorCreator
	// (temporaer behoben)
	
	@Test
	public void testClassWithPrivateModifiedAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_l");
		Clazz person = model.createClazz("Person");
		person.with(new Attribute("personalName", DataType.STRING).with(Modifier.PRIVATE));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
		
	}
	
	// FIXME public Modifier verhindert die Generierung von gettern und settern
	
	@Test
	public void testClassWithPublicModifiedAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_m");
		Clazz person = model.createClazz("Person");
		person.with(new Attribute("personalName", DataType.STRING).with(Modifier.PUBLIC));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
	}
	
	@Test
	public void testClassWithStaticModifiedAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_n");
		Clazz person = model.createClazz("Person");
		person.with(new Attribute("personalName", DataType.STRING).with(Modifier.STATIC));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
	}
	@Test
	public void testClassWithProtectedModifiedAttribute() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.attribute_o");
		Clazz person = model.createClazz("Person");
		person.with(new Attribute("personalName", DataType.STRING).with(Modifier.PROTECTED));
		model.getGenerator().testGeneratedCode();
//		model.generate("src/test/java");
	}

}
