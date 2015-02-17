package de.uniks.networkparser.list;

import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.junit.Test;

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
		}

		assertEquals("iteration should have counted zero elements", 0, counter);

		// remove again
		simpleList.remove(int_01);

		assertEquals("List should be empty", 0, simpleList.size());
		assertTrue("List should not yet contain added element", ! simpleList.contains(int_01));

		// some small list for testing sublist
		for (int i = 1; i <= 42; i++)
		{
			simpleList.add(new Integer(i));
		}

		SimpleList<Integer> subList = simpleList.subList(10, 20);

		assertEquals("sublist[0] should be 10", new Integer(11), subList.get(0));

		subList = simpleList.subList(42, 20);

		assertEquals("sublist should be empty", 0, subList.size());

		subList = simpleList.subList(40, 42);

		assertEquals("sublist should have one element", 1, subList.size());

		// test iterator
		ListIterator<Integer> iter = subList.iteratorReverse();

		try {
			iter.set(int_01);
		} catch (Exception e) {}		

		iter.hasPrevious();

		iter.nextIndex();

		iter.previousIndex();

		try {
			iter.next();
		} catch (Exception e) {}		

		iter.previous();
		try {
			iter.previous();
		} catch (Exception e) {}	

		iter.set(new Integer(42));
		
		iter.next();

		subList.remove(subList.size()-1);
		
		iter.set(new Integer(43));
		
		iter.add(new Integer(44));
		
		try {
			iter.remove();
		} catch (Exception e) {}
		
		iter.previous();
		
		iter.remove();
	}
}
