package de.uniks.networkparser.test.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Clazz;

public class SuperClazzTest {

	@Test
	public void testSuperAdd() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");

		Clazz interfaceClazz = model.createClazz("InterfaceClazz");
		interfaceClazz.enableInterface();

		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(superClazz, interfaceClazz);

		assertEquals(1, regularClazz.getSuperClazzes(false).size());
		assertEquals(1, regularClazz.getInterfaces(false).size());
	}
	
	@Test
	public void testSuperAdd2() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");

		Clazz interfaceClazz = model.createClazz("InterfaceClazz");
		interfaceClazz.enableInterface();

		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(superClazz);
		regularClazz.withSuperClazz(interfaceClazz);

		assertEquals(1, regularClazz.getSuperClazzes(false).size());
		assertEquals(1, regularClazz.getInterfaces(false).size());
	}
	
	@Test
	public void testSuperAdd3() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");

		Clazz interfaceClazz = model.createClazz("InterfaceClazz");
		interfaceClazz.enableInterface();

		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(interfaceClazz);
		regularClazz.withSuperClazz(superClazz);

		assertEquals(1, regularClazz.getSuperClazzes(false).size());
		assertEquals(1, regularClazz.getInterfaces(false).size());
	}
	
	@Test
	public void testSuperAdd4() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");

		Clazz interfaceClazz = model.createClazz("InterfaceClazz");
		interfaceClazz.enableInterface();

		Clazz interfaceClazz2 = model.createClazz("InterfaceClazz2");
		interfaceClazz2.enableInterface();

		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(superClazz, interfaceClazz);
		regularClazz.withSuperClazz(interfaceClazz2);

		assertEquals(1, regularClazz.getSuperClazzes(false).size());
		assertEquals(2, regularClazz.getInterfaces(false).size());
	}

	@Test
	public void testSuperAdd5() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");
		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(superClazz);

		assertEquals(1, regularClazz.getSuperClazzes(false).size());
	}

	@Test
	public void testSuperAdd6() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");
		Clazz interfaceClazz = model.createClazz("InterfaceClazz");
		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(superClazz, interfaceClazz);
//		regularClazz.withSuperClazz(interfaceClazz);
		interfaceClazz.enableInterface();

		assertEquals(1, regularClazz.getSuperClazzes(false).size());
		assertEquals(1, regularClazz.getInterfaces(false).size());
	}

	@Test
	public void testSuperRemove1() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");
		Clazz interfaceClazz = model.createClazz("InterfaceClazz");
		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(superClazz, interfaceClazz);
		interfaceClazz.enableInterface();

		assertEquals(1, regularClazz.getSuperClazzes(false).size());
		assertEquals(1, regularClazz.getInterfaces(false).size());

		regularClazz.remove(superClazz);

		assertEquals(0, regularClazz.getSuperClazzes(false).size());
		assertEquals(1, regularClazz.getInterfaces(false).size());

		regularClazz.remove(interfaceClazz);

		assertEquals(0, regularClazz.getSuperClazzes(false).size());
		assertEquals(0, regularClazz.getInterfaces(false).size());

	}

}