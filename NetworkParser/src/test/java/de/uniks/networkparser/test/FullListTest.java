package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ListIterator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;

public class FullListTest {

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
		
		subList.first();
		subList.last();
		
		iter.add(new Integer(44));
		
		// coverage for AbstractList
		simpleList.addAll(subList);
		
		simpleList.first();
		simpleList.last();
		
		assertEquals("last should be last", simpleList.last(), simpleList.get(simpleList.size() - 1));

		simpleList.toArray();
		
		simpleList.toArray(new Integer[]{});

		simpleList.toArray(new Integer[99]);
		
		// add more elements to make the list big
		simpleList = new SimpleList<Integer>();
		
		for (int i = 1; i <= 550; i++)
		{
			simpleList.add(new Integer(i));
		}

		simpleList.toArray(new Integer[99]);
	
		assertEquals("simpleList should have 550 elements", 550, simpleList.size());
		
		// try to add 42, again
		Assert.assertFalse(simpleList.add(simpleList.get(41)));

		assertEquals("simpleList should still have 550 elements", 550, simpleList.size());
		
		simpleList.listIterator();
		Integer next = simpleList.listIterator(41).next();
		assertEquals("listiterator[41] should deliver 42", 42, 0+next);
		

		
		clone = simpleList.clone();
		
		clone.remove(0);
		
		simpleList.retainAll(clone);
		simpleList.retainAll(null);
		
		
		assertEquals("simpleList should have 550 elements", 549, simpleList.size());
		assertEquals("simpleList[0] should be 2", 2, 0 + simpleList.first());
		
		simpleList = new SimpleList<Integer>();
		
		simpleList.with(int_01, int_02);
		assertEquals("simpleList should have 2 elements", 2, simpleList.size());
		assertEquals("simpleList[0] should be 1", 1, 0 + simpleList.first());
		
		clone = new SimpleList<Integer>();
		
		simpleList.copyEntity(clone, 1);
		assertEquals("clone should have 1 elements", 1, clone.size());
		assertEquals("clone[0] should be 12", 2, 0 + clone.first());
		
		simpleList.clear();
		
		for (int i = 1; i <= 42; i++)
		{
			simpleList.add(new Integer(i));
		}
		
		clone = (SimpleList<Integer>) simpleList.subSet(new Integer(23), new Integer(25));
		
		assertEquals("wrong number of elements", 2, clone.size());
		assertEquals("clone[0] is wrong", 23, 0 + clone.first());
		
		clone = (SimpleList<Integer>) simpleList.subList(22, 24);
		
		assertEquals("wrong number of elements", 2, clone.size());
		assertEquals("clone[0] is wrong", 23, 0 + clone.first());
		
		clone.removeItemByObject(clone.first());

		assertEquals("wrong number of elements", 1, clone.size());
		assertEquals("clone[0] is wrong", 24, 0 + clone.first());

		int lastIndexOf = clone.lastIndexOf(clone.first());
		assertEquals("wrong index", 0, lastIndexOf);

	}
}
