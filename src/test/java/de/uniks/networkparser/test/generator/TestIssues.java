package de.uniks.networkparser.test.generator;

import org.junit.Test;

import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.Method;

public class TestIssues {
	@Test
	public void testIsuue29() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model=new ClassModel("org.sdmlib.simple.model.issue29");
		Clazz a = model.createClazz("A").enableInterface();
		
		Clazz b = model.createClazz("B");
		Clazz c = model.createClazz("C");
		
		b.withSuperClazz(a);
		c.withSuperClazz(a);
		b.withBidirectional(c, "c", Cardinality.ONE, "b", Cardinality.ONE);
		
		model.getGenerator().testGeneratedCode();
	}
//	@Test
	public void testIsuue30() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model=new ClassModel("org.sdmlib.simple.model.issue30");
		Clazz zoombieOwner = model.createClazz("ZoombieOwner").enableInterface();
		
		Clazz a = model.createClazz("A");
		Clazz ground = model.createClazz("Ground");
		
		a.withSuperClazz(zoombieOwner);
		a.withSuperClazz(ground);
		Method method = a.createMethod("checkEnd");
		method.with(DataType.BOOLEAN).withBody("");

		model.getGenerator().testGeneratedCode();
	}
	@Test
	public void testsMultiExtends() {
		if(Generator.DISABLE) {
			return;
		}
		ClassModel model=new ClassModel("org.sdmlib.simple.model.issue31");
		Clazz a = model.createClazz("A");
		Clazz b = model.createClazz("B");
		Clazz c = model.createClazz("C");
		
		a.withSuperClazz(b);
		a.withSuperClazz(c);
		model.getGenerator().testGeneratedCode();
		
	}

}
