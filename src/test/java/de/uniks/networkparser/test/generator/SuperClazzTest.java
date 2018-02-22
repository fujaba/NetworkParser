package de.uniks.networkparser.test.generator;

import org.junit.Assert;
import org.junit.Test;

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

		Assert.assertEquals(1, regularClazz.getSuperClazzes(false).size());
		Assert.assertEquals(1, regularClazz.getInterfaces(false).size());
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

		Assert.assertEquals(1, regularClazz.getSuperClazzes(false).size());
		Assert.assertEquals(1, regularClazz.getInterfaces(false).size());
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

		Assert.assertEquals(1, regularClazz.getSuperClazzes(false).size());
		Assert.assertEquals(1, regularClazz.getInterfaces(false).size());
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

		Assert.assertEquals(1, regularClazz.getSuperClazzes(false).size());
		Assert.assertEquals(2, regularClazz.getInterfaces(false).size());
	}

	@Test
	public void testSuperAdd5() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");
		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(superClazz);

		Assert.assertEquals(1, regularClazz.getSuperClazzes(false).size());
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

		Assert.assertEquals(1, regularClazz.getSuperClazzes(false).size());
		Assert.assertEquals(1, regularClazz.getInterfaces(false).size());
	}

	@Test
	public void testSuperRemove1() {
		ClassModel model = new ClassModel();

		Clazz superClazz = model.createClazz("SuperClazz");
		Clazz interfaceClazz = model.createClazz("InterfaceClazz");
		Clazz regularClazz = model.createClazz("RegularClazz");

		regularClazz.withSuperClazz(superClazz, interfaceClazz);
		interfaceClazz.enableInterface();

		Assert.assertEquals(1, regularClazz.getSuperClazzes(false).size());
		Assert.assertEquals(1, regularClazz.getInterfaces(false).size());

		regularClazz.withoutSuperClazz(superClazz);

		Assert.assertEquals(0, regularClazz.getSuperClazzes(false).size());
		Assert.assertEquals(1, regularClazz.getInterfaces(false).size());

		regularClazz.withoutSuperClazz(interfaceClazz);

		Assert.assertEquals(0, regularClazz.getSuperClazzes(false).size());
		Assert.assertEquals(0, regularClazz.getInterfaces(false).size());

	}

}