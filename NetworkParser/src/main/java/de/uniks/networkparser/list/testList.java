package de.uniks.networkparser.list;

import org.junit.Test;
import static org.junit.Assert.*;

public class testList {

	@Test
	public void list() {
//		SimpleList<String> simpleList = new SimpleList<String>();
//		simpleList.add("Hallo");
//		
//		System.out.println(simpleList.get(0));
		
		
		SimpleKeyValueList<String, Integer> map = new SimpleKeyValueList<String, Integer>();
		map.flag();
		map.add("Stefan", 42);
		
		System.out.println(map);
	}
	
	@Test
	public void simpleListFunctionality()
	{
		// create a set of some 42 names
		SimpleList<Integer> simpleList = new SimpleList<Integer>();
		
		Integer int_01 = new Integer(1);
		simpleList.add(int_01);
		
		assertTrue("List should contain added element", simpleList.contains(int_01));
		
	}
}
