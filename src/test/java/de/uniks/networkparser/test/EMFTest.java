package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.emf.EMF2Graph;
import de.uniks.networkparser.graph.GraphList;

public class EMFTest extends IOClasses{

	@Test
	public void testEMF() {
//		StringBuffer value = readFile(EMFTest.class.getResource("testcase4-in.petrinet").toString());
//		EmfIdMap2 map=new EmfIdMap2();
//		Object decode = map.decode(value.toString());
//		System.out.println(decode);
	}

	@Test
	public void testEMFDecode() {
		StringBuffer value = readFile("railway.ecore");
		GraphList model = EMF2Graph.decode(value.toString());
		System.out.println(model);
//		EmfIdMap2 map=new EmfIdMap2();
//		Object decode = map.decode(value.toString());
//		System.out.println(decode);
	}
}
