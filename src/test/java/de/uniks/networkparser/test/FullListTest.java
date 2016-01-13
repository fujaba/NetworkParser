package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.ListIterator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;

public class FullListTest {

	@Test
	public void list() {
		SimpleList<String> simpleList = new SimpleList<String>();
		simpleList.add("Hallo");

		Assert.assertEquals("Hallo",simpleList.get(0));


		SimpleKeyValueList<String, Integer> map = new SimpleKeyValueList<String, Integer>();
		map.flag();
		map.add("Stefan", 42);
		Assert.assertEquals("Map Visible CaseSensitive (1)",map.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void simpleListFunctionality()
	{
		// create a set of some 42 names
		SimpleSet<Integer> simpleList = new SimpleSet<Integer>();

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
			Assert.assertNotNull(integer);
		}

		assertEquals("iteration should have counted one element", 1, counter);

		// remove an element, it does not contain
		simpleList.remove(int_02);

		assertEquals("List should contain 1 element", 1, simpleList.size());
		assertTrue("List should contain added element", simpleList.contains(int_01));

		// clone the list
		SimpleSet<Integer> clone = simpleList.clone();

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
			Assert.assertNotNull(integer);
		}

		assertEquals("iteration should have counted zero elements", 0, counter);

		// remove again

		simpleList.remove(int_01);

		assertEquals("List should be empty", 0, simpleList.size());
		assertTrue("List should not yet contain added element", ! simpleList.contains(int_01));

		// some small list for testing sublist
		for (int i = 0; i < 42; i++)
		{
			simpleList.add(new Integer(i));
		}

		SimpleSet<Integer> subList = simpleList.subList(10, 20);

		assertEquals("sublist[0] should be 10", new Integer(10), subList.get(0));

		subList = simpleList.subList(42, 20);

		assertEquals("sublist should be empty", 0, subList.size());

		subList = simpleList.subList(40, 42);

		assertEquals("sublist should have two elements", 2, subList.size());

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
		
		try {
		  iter.remove();
		} catch (Exception e) {}
	  
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
		simpleList = new SimpleSet<Integer>();
		
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
		
		
		assertEquals("simpleList should have 549 elements", 549, simpleList.size());
		assertEquals("simpleList[0] should be 2", 2, 0 + simpleList.first());
		
		simpleList = new SimpleSet<Integer>();
		
		simpleList.with(int_01, int_02);
		assertEquals("simpleList should have 2 elements", 2, simpleList.size());
		assertEquals("simpleList[0] should be 1", 1, 0 + simpleList.first());
		
		clone = new SimpleSet<Integer>();
		
		simpleList.copyEntity(clone, 1);
		assertEquals("clone should have 1 elements", 1, clone.size());
		assertEquals("clone[0] should be 12", 2, 0 + clone.first());
		
		//FIXME TEST ELEMENTS ARE NOT NULL
		simpleList.clear();
		
		for (int i = 1; i <= 42; i++)
		{
			simpleList.add(new Integer(i));
		}
		
		clone = (SimpleSet<Integer>) simpleList.subSet(new Integer(23), new Integer(25));
		
		assertEquals("wrong number of elements", 2, clone.size());
		assertEquals("clone[0] is wrong", 23, 0 + clone.first());
		
		clone = (SimpleSet<Integer>) simpleList.subList(22, 24);
		
		assertEquals("wrong number of elements", 2, clone.size());
		assertEquals("clone[0] is wrong", 23, 0 + clone.first());
		
		clone.removeByObject(clone.first());

		assertEquals("wrong number of elements", 1, clone.size());
		assertEquals("clone[0] is wrong", 24, 0 + clone.first());

		int lastIndexOf = clone.lastIndexOf(clone.first());
		assertEquals("wrong index", 0, lastIndexOf);
	}

	@Test
	public void simpleListNoHash() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		SimpleList<Integer> queue=new SimpleList<Integer>().withAllowDuplicate(true);
		for(int i=1;i<500;i++) {
			queue.add(i);
		}
		Field declaredField = queue.getClass().getSuperclass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] object = (Object[]) declaredField.get(queue);
		Assert.assertNull(object[1]); //AbstractArray.BIG_KEY
	}

	@Test
	public void simpleKeyValueList() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		SimpleKeyValueList<Integer, Integer> queue=new SimpleKeyValueList<Integer, Integer>().withFlag(SimpleKeyValueList.BIDI).withAllowDuplicate(true);
		boolean found=false;
		for(int i=1;i<500;i++) {
			if(i==1) {
				found = true;
			}
			queue.add(i, i);
		}
		Assert.assertTrue(found);
		Field declaredField = queue.getClass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] object = (Object[]) declaredField.get(queue);
		Assert.assertNull(object[1]); // AbstractArray.BIG_KEY
		Assert.assertNull(object[4]); // AbstractArray.BIG_VALUE
	}
	
	@Test
	public void simpleListQueue() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException
	{
		SimpleList<Integer> queue=new SimpleList<Integer>().withAllowDuplicate(true);
		for(int i=1;i<500;i++) {
			queue.add(i);
		}
		Field declaredField = queue.getClass().getSuperclass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] object = (Object[]) declaredField.get(queue);
		Assert.assertNull(object[1]); // AbstractArray.BIG_KEY
		queue.remove(0);
		Assert.assertEquals(queue.size(), 498);
		Integer integer = queue.get(0);
		Assert.assertEquals(integer, new Integer(2));
	}
	
	@Test
	public void testdd() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SimpleList<Integer> list=new SimpleList<Integer>();
		for(int i=1;i<6;i++) {
			list.add(i);
		}
//		list.add(42);
		Field declaredField = list.getClass().getSuperclass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] object = (Object[]) declaredField.get(list);
//		Assert.assertEquals(13, object.length);
		Assert.assertEquals(6, object.length);
		
		list.remove(0);
		list.remove(0);
//		list.remove(0);
//		list.remove(0);
		Assert.assertNotNull(object);
		
		Object[] array = list.toArray();
		
		list.add(42);
		
		Object[] arrayB = list.toArray();
		
		Assert.assertEquals(array.length + 1, arrayB.length);
	}
	
	@Test
	public void testCicle() {
		SimpleList<Integer> list=new SimpleList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.remove(0);
		list.add(6);
		list.removeByObject(3);
		Assert.assertEquals(4, list.size());
	}

	@Test
	public void testMiddle() {
		SimpleList<Integer> list=new SimpleList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.removeByObject(3);
		Assert.assertEquals(4, list.size());
	}
	
	@Test
	public void testFirstMiddle() {
		SimpleList<Integer> list=new SimpleList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		list.add(4);
		list.add(5);
		list.remove(0);
		list.removeByObject(3);
		Assert.assertEquals(3, list.size());
		Assert.assertEquals(new Integer(5), list.get(2));
	}

	@Test
	public void testPackList() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SimpleList<Integer> list=new SimpleList<Integer>();
		list.with(1,2,3);
		
		Field declaredField = list.getClass().getSuperclass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] object = (Object[]) declaredField.get(list);
		Assert.assertEquals(9, object.length);
		
		list.pack();
		
		object = (Object[]) declaredField.get(list);
		
		Assert.assertEquals(3, object.length);
		
		for(int i=4;i<501;i++) {
			list.add(i);
		}
		Object[] items= (Object[]) declaredField.get(list);
		object=(Object[])items[0];

		Assert.assertEquals(596, object.length);
		
		list.pack();
		
		object= (Object[]) declaredField.get(list);
		Assert.assertEquals(500, object.length);
	}

	@Test
	public void testPackMap() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SimpleKeyValueList<Integer, Integer> list=new SimpleKeyValueList<Integer, Integer>();
		list.with(1,1);
		list.with(2, 2);
		list.with(3, 3);
		
		Field declaredField = list.getClass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] items = (Object[]) declaredField.get(list);
		Object[] object=(Object[])items[0];
		
		Assert.assertEquals(6, object.length);
		
		list.pack();
		
		object = (Object[]) declaredField.get(list);
		
		Assert.assertEquals(4, object.length);
		
		for(int i=4;i<501;i++) {
			list.add(i, i);
		}

		items= (Object[]) declaredField.get(list);
		object=(Object[])items[0];

		Assert.assertEquals(509, object.length);
		
		list.pack();
		
		items= (Object[]) declaredField.get(list);
		object=(Object[])items[0];
		Assert.assertEquals(500, object.length);
	}
	
	@Test
	public void testSimpleBigSet() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SimpleList<String> list=new SimpleList<String>().withSize(20);
		Field declaredField = list.getClass().getSuperclass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] items = (Object[]) declaredField.get(list);
		
		// New Size: size + size / 2 + 5;
		Assert.assertEquals(35, items.length);
	}
	
	@Test
	public void testSimpleBigRemoved() {
		SimpleKeyValueList<Integer, String> list=new SimpleKeyValueList<Integer, String>();
		for(int i=0;i<500;i++) {
			list.put(i, ""+i);	
		}
		for(int i=0;i<500;i+=2) {
			list.removeByObject(i);
		}
	}
}
