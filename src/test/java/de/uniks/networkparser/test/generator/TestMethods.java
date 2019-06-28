package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.Os;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.DataTypeMap;
import de.uniks.networkparser.graph.DataTypeSet;
import de.uniks.networkparser.graph.Parameter;

public class TestMethods {

	@Test
	public void testClassWithMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_a");
		Clazz person = model.createClazz("Person");

		person.createMethod("think");
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithVoidMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_b");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.VOID);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithVoidAndNamelessParameterMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_c");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.VOID, new Parameter(DataType.STRING));
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithVoidAndParameterMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_d");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.VOID, new Parameter(DataType.STRING).with("value"));
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	// FIXME Parametertyp bisher nur in Form de.uniks.networkparser.graph.Clazz bei Methodenparametern

	@Test
	public void testClassWithVoidAndClassParameterMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_e");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.createMethod("think", DataType.VOID, new Parameter(DataType.create(room)).with("room"));
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testClassWithVoidAndSimpleClassParameterMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_f");
		Clazz person = model.createClazz("Person");
		Clazz room = model.createClazz("Room");

		person.createMethod("think", DataType.VOID, new Parameter(DataType.create(room)).with("room"));
		person.createMethod("read", DataType.VOID, new Parameter(room).with("room"));

		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");
	}


	@Test
	public void testClassWithVoidAndSetParameterMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_g");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.VOID, new Parameter(DataTypeSet.create(DataType.STRING)).with("values"));
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithVoidAndMapParameterMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_h");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.VOID, new Parameter(DataTypeMap.create(DataType.STRING, DataType.STRING)).with("values"));
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithVoidAndMultipleParametersMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_i");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.VOID, new Parameter(DataType.STRING).with("value"), new Parameter(DataType.INT));
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithNonVoidMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_j");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.STRING);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithNonVoidAndParameterMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_k");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.STRING, new Parameter(DataType.STRING));
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithMultipleMethods() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_l");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.STRING, new Parameter(DataType.STRING));
		person.createMethod("dontThink", DataType.VOID);
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithMethodAndBody() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_m");
		Clazz person = model.createClazz("Person");

		person.createMethod("think", DataType.VOID)
				.withBody("		String thought = \"\";\n");
		model.getGenerator().removeAndGenerate("java");
//		model.generate("src/test/java");

	}

	@Test
	public void testClassWithBooleanMethod() {
		if(Os.isGenerator() == false) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.methods_n");
		Clazz person = model.createClazz("Person");

		person.createMethod("checkSomething", DataType.BOOLEAN);
		model.getGenerator().removeAndGenerate("java");

	}
}
