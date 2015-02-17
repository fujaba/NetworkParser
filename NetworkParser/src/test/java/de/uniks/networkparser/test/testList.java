package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import static org.junit.Assert.*;

public class testList {

	@Test
	public void list() {
		SimpleList<String> simpleList = new SimpleList<String>();
		simpleList.add("Hallo");
		
		System.out.println(simpleList.get(0));
		
		
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
		Integer int_02 = new Integer(2);
		
		
		assertEquals("List should be empty", 0, simpleList.size());
		assertTrue("List should not yet contain added element", ! simpleList.contains(int_01));
		
		simpleList.add(int_01);
		
		assertEquals("List should contain 1 element", 1, simpleList.size());
		assertTrue("List should contain added element", simpleList.contains(int_01));
		
		// add it again
		simpleList.add(int_01);
		
		// should have no effect
		assertEquals("List should contain 1 element", 1, simpleList.size());
		assertTrue("List should contain added element", simpleList.contains(int_01));

		// iterate through it
		int counter = 0;
		for (Integer integer : simpleList) {
			counter++;
			System.out.println(integer);
		}
		
		assertEquals("iteration should have counted one element", 1, counter);

		// remove an element, it does not contain
		simpleList.remove(int_02);

		assertEquals("List should contain 1 element", 1, simpleList.size());
		assertTrue("List should contain added element", simpleList.contains(int_01));

		// clone the list
		SimpleList<Integer> clone = simpleList.clone();
				
		assertEquals("List should contain 1 element", 1, simpleList.size());
		assertTrue("List should contain added element", simpleList.contains(int_01));
		
		assertEquals("List should contain 1 element", 1, clone.size());
		assertTrue("List should contain added element", clone.contains(int_01));
				
						
		// remove it
		simpleList.remove(int_01);

		assertEquals("List should be empty", 0, simpleList.size());
		assertTrue("List should not yet contain added element", ! simpleList.contains(int_01));

		assertEquals("List should contain 1 element", 1, clone.size());
		assertTrue("List should contain added element", clone.contains(int_01));
				

		
		// iterate through it
		counter = 0;
		for (Integer integer : simpleList) {
			counter++;
			System.out.println(integer);
		}
		
		assertEquals("iteration should have counted zero elements", 0, counter);
		
		// remove again
		simpleList.remove(int_01);

		assertEquals("List should be empty", 0, simpleList.size());
		assertTrue("List should not yet contain added element", ! simpleList.contains(int_01));
		
		

	}
}
