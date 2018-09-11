package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Modifier;

public class TestSuperClazzes {

	@Test
	public void testClazzAsSuperClazz() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.superclazzes_a");
		Clazz person = model.createClazz("Person");
		model.createClazz("Pupil").withSuperClazz(person);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testClazzAsKidClazz() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.superclazzes_b");
		Clazz person = model.createClazz("Person");
		Clazz pupil = model.createClazz("Pupil");

		person.withKidClazzes(pupil);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testClazzAsSuperClazzWithMultipleKids() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.superclazzes_c");
		Clazz person = model.createClazz("Person");
		model.createClazz("Pupil").withSuperClazz(person);
		model.createClazz("Teacher").withSuperClazz(person);
		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testChangeSuperClazz() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.superclazzes_d");
		Clazz person = model.createClazz("Person");
		Clazz teacher = model.createClazz("Teacher");
		Clazz pupil = model.createClazz("Pupil").withSuperClazz(teacher);

		pupil.withSuperClazz(person);

		model.getGenerator().testGeneratedCode("java");
//		model.generate("src/test/java");
	}

	@Test
	public void testAbstractAssociation() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.superclazzes_e");
		Clazz person = model.createClazz("Person");
		person.with(Modifier.create("abstract"));
		Clazz teacher = model.createClazz("Teacher");
		model.createClazz("Pupil").withSuperClazz(person);

		person.withAttribute("name", DataType.STRING);

		teacher.withBidirectional(person, "person", Association.ONE, "teacher", Association.ONE);

		model.getGenerator().testGeneratedCode("java");
	}

	@Test
	public void testMultipleAbstractAssociation() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model = new ClassModel("org.sdmlib.simple.model.superclazzes_f");
		Clazz person = model.createClazz("Person");
		person.with(Modifier.create("abstract"));
		Clazz teacher = model.createClazz("Teacher");
		teacher.with(Modifier.create("abstract"));
		model.createClazz("Pupil").withSuperClazz(person);

		person.withAttribute("name", DataType.STRING);

		teacher.withBidirectional(person, "person", Association.ONE, "teacher", Association.ONE);

		model.getGenerator().testGeneratedCode("java");
	}

//FIXME Wrong call of RemoveYou
//	   @Override
//	   public void removeYou()
//	   {
//	
//	      super.removeYou();
//
//	      setPerson(null);
//	      getPropertyChangeSupport().firePropertyChange("REMOVE_YOU", this, null);
//	   }
}
