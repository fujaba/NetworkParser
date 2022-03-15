package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.TextDiff;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedList;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.Fruit;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.AppleSet;
import de.uniks.networkparser.test.model.util.FruitSet;

public class FullListTest {
	@Test
	public void AppleSetDupplicate() {
		AppleSet set = new AppleSet();
		Apple apple = new Apple();
		apple.withX(23).withY(42);
		set.add(apple);
		set.add(apple);
		assertEquals(1, set.size());
		
		
		SimpleKeyValueList<String, Integer> map = new SimpleKeyValueList<String, Integer>();
		map.put("Hallo", 23);
		map.put("World", 42);
		assertEquals(2, map.size());
	}

	@Test
	public void SortedListAppleSetDupplicate() {
		SortedList<SortedMsg> list = new SortedList<SortedMsg>(true);

		SortedMsg first = new SortedMsg().withMsg("First").withNumber(1);
		SortedMsg second = new SortedMsg().withMsg("Second").withNumber(2);
		SortedMsg third = new SortedMsg().withMsg("Third").withNumber(3);
		SortedMsg newSecond = new SortedMsg().withMsg("new Second").withNumber(1);

		list.add(first);
		list.add(second);
		list.add(third);

		assertEquals(first, list.get(0));
		assertEquals(second, list.get(1));
		assertEquals(third, list.get(2));

		list.add(newSecond);

		assertEquals(first, list.get(0));
		assertEquals(newSecond, list.get(1));
		assertEquals(second, list.get(2));
		assertEquals(third, list.get(3));
		assertEquals(4, list.size());
	}

	@Test
	public void AppleSet() {
		AppleSet set = new AppleSet();
		set.with("Hallo");
	}

	@Test
	public void CollectionCompare() {
		ArrayList<Object> masterA=new ArrayList<Object>();
		ArrayList<String> slave=new ArrayList<String>();
		slave.add("Hallo");
		slave.add("Welt");
		masterA.add(slave);

		ArrayList<Object> masterB=new ArrayList<Object>();
		slave=new ArrayList<String>();
		slave.add("Hallo");
		slave.add("Welt");
		masterB.add(slave);

		assertTrue(EntityUtil.compareEntity(masterA, masterB));
	}

	private JsonObject createHelpClass(String name, String color) {
		JsonObject master=new JsonObject();
		JsonArray assoc=new JsonArray();
		master.add("trainers", assoc);
		JsonObject trainer=new JsonObject();
		JsonObject props = new JsonObject();
		assoc.add(trainer);
		trainer.add("props", props);
		props.add("name", name);
		props.add("color", color);
		return master;
	}

	@Test
	public void compareJsonWithDifference() {
		JsonObject master = this.createHelpClass("Alice", "green");
		JsonObject slave = this.createHelpClass("Bob", "blue");
		TextDiff diffs=new TextDiff();
		assertFalse(EntityUtil.compareEntity(master, slave, diffs, null));
	}

	@Test
	public void CollectionWith() {
		SimpleList<String> item=new SimpleList<String>();
		item.withType(String.class);
		item.with("Hallo", "Welt", 42, null);
		assertEquals(2, item.size());
	}

	@Test
	public void CollectionGetValue() {
		SimpleList<String> item=new SimpleList<String>();
		for(int i=0;i<500;i++) {
			item.add("Number_"+i);
		}
		assertEquals("Number_42", item.getValue("Number_42"));
	}

	@Test
	public void CollectionBidiValue() {
		SimpleKeyValueList<String, Integer> bidiMap=new SimpleKeyValueList<String, Integer>();
		bidiMap.add("Stefan", 23);
		bidiMap.add("Tobi", 3);
		bidiMap.add("Albert", 42);
		bidiMap.remove("Tobi");
		assertNull(bidiMap.getValue("Albert42"));
	}

	@Test
	public void CollectionBidiValueDelete() {
		SimpleKeyValueList<String, Integer> bidiMap=new SimpleKeyValueList<String, Integer>().withFlag(SimpleKeyValueList.BIDI);
		bidiMap.add("Stefan", 23);
		bidiMap.add("Tobi", 3);
		bidiMap.add("Albert", 42);
		bidiMap.removePos(2);
		assertNull(bidiMap.getValue("Albert"));
	}

	@Test
	public void CollectionBidiValueDeleteSub() {
		SimpleKeyValueList<String, Integer> bidiMap=new SimpleKeyValueList<String, Integer>().withFlag(SimpleKeyValueList.BIDI);
		String delete = "Albert.42";
		bidiMap.add("Check", 23);
		bidiMap.add("Stefan", 23);
		bidiMap.add("Tobi", 3);
		bidiMap.add(delete, 42);
		bidiMap.without(delete);
		bidiMap.without("Check");
		assertNull(bidiMap.getValue("Albert.42"));
	}

	@Test
	public void CollectionBidiGetValue() {
		SimpleKeyValueList<String, Integer> bidiMap=new SimpleKeyValueList<String, Integer>().withFlag(SimpleKeyValueList.BIDI);
		String delete = "Albert.42";
		bidiMap.add("Stefan", 23);
		bidiMap.add("Tobi", 3);
		bidiMap.add("Albert", 3);
//		bidiMap.add(delete, 42);
		assertNull(bidiMap.getValue(delete));
	}

	@Test
	public void CollectionWithInstanceOf() {
		FruitSet item=new FruitSet();
		item.with(new Fruit().withX(1).withY(1));
		item.with(new Apple().withX(2).withX(2));

		assertEquals(2, item.size());
		AppleSet newSet=new AppleSet();
		newSet.withType(Apple.class);
		newSet.withList(item);

		assertEquals(1, newSet.size());

		newSet.with(new Fruit().withX(3).withY(3));

		assertEquals(1, newSet.size());
	}

	@Test
	public void list() {
		SimpleList<String> simpleList = new SimpleList<String>();
		simpleList.add("Hallo");

		assertEquals("Hallo",simpleList.get(0));
		SimpleKeyValueList<String, Integer> map = new SimpleKeyValueList<String, Integer>();
		map.flag();
		map.add("Stefan", 42);
		assertEquals("Map Visible CaseSensitive (1) [Stefan]",map.toString());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void simpleListFunctionality()
	{
		// create a set of some 42 names
		SimpleSet<Integer> simpleList = new SimpleSet<Integer>();

		Integer int_01 = Integer.valueOf(1);
		Integer int_02 = Integer.valueOf(2);

		assertEquals(0, simpleList.size(), "List should be empty");
		assertTrue(! simpleList.contains(int_01), "List should not yet contain added element");

		simpleList.add(int_01);

		assertEquals(1, simpleList.size(), "List should contain 1 element");
		assertTrue(simpleList.contains(int_01), "List should contain added element");

		// add it again
		simpleList.add(int_01);

		// should have no effect
		assertEquals(1, simpleList.size(), "List should contain 1 element");
		assertTrue(simpleList.contains(int_01), "List should contain added element");

		// iterate through it
		int counter = 0;
		for (Integer integer : simpleList) {
			counter++;
			assertNotNull(integer);
		}

		assertEquals(1, counter, "iteration should have counted one element");

		// remove an element, it does not contain
		simpleList.remove(int_02);

		assertEquals(1, simpleList.size(), "List should contain 1 element");
		assertTrue(simpleList.contains(int_01), "List should contain added element");

		// clone the list
		SimpleSet<Integer> clone = simpleList.clone();

		assertEquals(1, simpleList.size(), "List should contain 1 element");
		assertTrue(simpleList.contains(int_01), "List should contain added element");

		assertEquals(1, clone.size(), "List should contain 1 element");
		assertTrue(clone.contains(int_01), "List should contain added element");

		// remove it
		simpleList.remove(int_01);

		assertEquals(0, simpleList.size(), "List should be empty");
		assertTrue(!simpleList.contains(int_01), "List should not yet contain added element");

		assertEquals(1, clone.size(), "List should contain 1 element");
		assertTrue(clone.contains(int_01), "List should contain added element");

		// iterate through it
		counter = 0;
		for (Integer integer : simpleList) {
			counter++;
			assertNotNull(integer);
		}

		assertEquals(0, counter, "iteration should have counted zero elements");

		// remove again

		simpleList.remove(int_01);

		assertEquals(0, simpleList.size(), "List should be empty");
		assertTrue(!simpleList.contains(int_01),"List should not yet contain added element");

		// some small list for testing sublist
		for (int i = 0; i < 42; i++)
		{
			simpleList.add(Integer.valueOf(i));
		}

		SimpleSet<Integer> subList = simpleList.subList(10, 20);

		assertEquals(Integer.valueOf(10), subList.get(0), "sublist[0] should be 10");

		subList = simpleList.subList(42, 20);

		assertEquals(0, subList.size(), "sublist should be empty");

		subList = simpleList.subList(40, 42);

		assertEquals(2, subList.size(), "sublist should have two elements");

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

		iter.set(Integer.valueOf(42));

		iter.next();

		subList.remove(subList.size()-1);

		iter.set(Integer.valueOf(43));

		iter.add(Integer.valueOf(44));

		try {
			iter.remove();
		} catch (Exception e) {}

		iter.previous();

		try {
		  iter.remove();
		} catch (Exception e) {}

		subList.first();
		subList.last();

		iter.add(Integer.valueOf(44));

		// coverage for AbstractList
		simpleList.addAll(subList);

		simpleList.first();
		simpleList.last();

		assertEquals(simpleList.last(), simpleList.get(simpleList.size() - 1), "last should be last");

		simpleList.toArray();

		simpleList.toArray(new Integer[]{});

		simpleList.toArray(new Integer[99]);

		// add more elements to make the list big
		simpleList = new SimpleSet<Integer>();

		for (int i = 1; i <= 550; i++)
		{
			simpleList.add(Integer.valueOf(i));
		}

		simpleList.toArray(new Integer[99]);

		assertEquals(550, simpleList.size(), "simpleList should have 550 elements");

		// try to add 42, again
		assertFalse(simpleList.add(simpleList.get(41)));

		assertEquals(550, simpleList.size(), "simpleList should still have 550 elements");

		simpleList.listIterator();
		Integer next = simpleList.listIterator(41).next();
		assertEquals(42, 0+next, "listiterator[41] should deliver 42");

		clone = simpleList.clone();

		clone.remove(0);

		simpleList.retainAll(clone);
		simpleList.retainAll(null);

		assertEquals(549, simpleList.size(), "simpleList should have 549 elements");
		assertEquals(2, 0 + simpleList.first(), "simpleList[0] should be 2");

		simpleList = new SimpleSet<Integer>();

		simpleList.with(int_01, int_02);
		assertEquals(2, simpleList.size(), "simpleList should have 2 elements");
		assertEquals(1, 0 + simpleList.first(), "simpleList[0] should be 1");

		clone = new SimpleSet<Integer>();

		simpleList.copyEntity(clone, 1);
		assertEquals(1, clone.size(), "clone should have 1 elements");
		assertEquals(2, 0 + clone.first(), "clone[0] should be 12");

		//FIXME TEST ELEMENTS ARE NOT NULL
		simpleList.clear();

		for (int i = 1; i <= 42; i++)
		{
			simpleList.add(Integer.valueOf(i));
		}

		clone = (SimpleSet<Integer>) simpleList.subSet(Integer.valueOf(23), Integer.valueOf(25));

		assertEquals(2, clone.size(), "wrong number of elements");
		assertEquals(23, 0 + clone.first(), "clone[0] is wrong");

		clone = (SimpleSet<Integer>) simpleList.subList(22, 24);

		assertEquals(2, clone.size(), "wrong number of elements");
		assertEquals(23, 0 + clone.first(), "clone[0] is wrong");

		clone.removeByObject(clone.first());

		assertEquals(1, clone.size(), "wrong number of elements");
		assertEquals(24, 0 + clone.first(), "clone[0] is wrong");

		int lastIndexOf = clone.lastIndexOf(clone.first());
		assertEquals(0, lastIndexOf, "wrong index");
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
		assertNull(object[1]); //AbstractArray.BIG_KEY
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
		assertTrue(found);
		Field declaredField = queue.getClass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] object = (Object[]) declaredField.get(queue);
		assertNull(object[1]); // AbstractArray.BIG_KEY
		assertNull(object[4]); // AbstractArray.BIG_VALUE
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
		assertNull(object[1]); // AbstractArray.BIG_KEY
		queue.remove(0);
		assertEquals(queue.size(), 498);
		Integer integer = queue.get(0);
		assertEquals(integer, Integer.valueOf(2));
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
//		assertEquals(13, object.length);
		assertEquals(6, object.length);

		list.remove(0);
		list.remove(0);
//		list.remove(0);
//		list.remove(0);
		assertNotNull(object);

		Object[] array = list.toArray();

		list.add(42);

		Object[] arrayB = list.toArray();

		assertEquals(array.length + 1, arrayB.length);
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
		assertEquals(4, list.size());
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
		assertEquals(4, list.size());
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
		assertEquals(3, list.size());
		assertEquals(Integer.valueOf(5), list.get(2));
	}

	@Test
	public void testPackList() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SimpleList<Integer> list=new SimpleList<Integer>();
		list.with(1,2,3);

		Field declaredField = list.getClass().getSuperclass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] object = (Object[]) declaredField.get(list);
		assertEquals(9, object.length);

		list.pack();

		object = (Object[]) declaredField.get(list);

		assertEquals(3, object.length);

		for(int i=4;i<501;i++) {
			list.add(i);
		}
		Object[] items= (Object[]) declaredField.get(list);
		object=(Object[])items[0];

		assertEquals(596, object.length);

		list.pack();

		object= (Object[]) declaredField.get(list);
		assertEquals(500, object.length);
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

		assertEquals(6, object.length);

		list.pack();

		object = (Object[]) declaredField.get(list);

		assertEquals(4, object.length);

		for(int i=4;i<501;i++) {
			list.add(i, i);
		}

		items= (Object[]) declaredField.get(list);
		object=(Object[])items[0];

		assertEquals(509, object.length);

		list.pack();

		items= (Object[]) declaredField.get(list);
		object=(Object[])items[0];
		assertEquals(500, object.length);
	}

	@Test
	public void testSimpleBigSet() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		SimpleList<String> list=new SimpleList<String>().withSize(20);
		Field declaredField = list.getClass().getSuperclass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		Object[] items = (Object[]) declaredField.get(list);

		// New Size: size + size / 2 + 5;
		assertEquals(35, items.length);
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

	@Test
	public void testSize() {
	    Assertions.assertThrows(ConcurrentModificationException.class, () -> {
	        SimpleSet<Apple> appleTree=new SimpleSet<Apple>();
	        ArrayList<Apple> list=new ArrayList<Apple>();
	        for(int i=0;i<430;i++) {
	            Apple item = new Apple().withPassword("Apple"+i);
	            appleTree.add(item);
	            if(i % 50 == 0) {
	                list.add(item);
	            }
	        }
	        assertEquals(430, appleTree.size());

	        for(Apple item : appleTree) {
	            appleTree.remove(42);
	            if(appleTree.size()<400) {
	                break;
	            }
	            item.setX(0);
	            appleTree.indexOf(list.get(0));
	        }
	  });
	}

	@Test
	public void testSizeAdvance() {
		SimpleSet<Apple> appleTree=new SimpleSet<Apple>();
		ArrayList<Apple> list=new ArrayList<Apple>();
		for(int i=0;i<430;i++) {
			Apple item = new Apple().withPassword("Apple"+i);
			appleTree.add(item);
			if(i % 50 == 0) {
				list.add(item);
			}
		}
		assertEquals(430, appleTree.size());

		for(Iterator<Apple> iterator = appleTree.iterator(false);iterator.hasNext();) {
			Apple apple = iterator.next();
			appleTree.remove(42);
			if(appleTree.size()<400) {
				break;
			}
			apple.setX(0);
			appleTree.indexOf(list.get(0));
		}
	}

	@Test
	public void testSizeIteratorRemove() {
		SimpleSet<Apple> appleTree=new SimpleSet<Apple>();
		for(int i=0;i<20;i++) {
			Apple item = new Apple().withPassword("Apple"+i);
			appleTree.add(item);
		}
		for(Iterator<Apple> iterator = appleTree.iterator();iterator.hasNext();) {
			iterator.next();
			iterator.remove();
		}
	}

	@Test
	public void retainAll(){
		SimpleList<String> item = new SimpleList<String>();
		item.with("Stefan", "Alex", "Albert");

		SimpleList<String> itemB = new SimpleList<String>();
		itemB.with("Stefan", "Alex", "Christian");

		item.retainAll(itemB);

		assertEquals(2, item.size());

		SimpleList<String> clone =  (SimpleList<String>)item.clone();
		assertEquals(2, clone.size());
	}

	@Test
	public void Comparator(){
		SortedList<String> item = new SortedList<String>(false);
		item.withComparator(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		item.with("Stefan", "Alex", "Albert");
		assertEquals("Albert", item.get(0));
		assertEquals("Alex", item.get(1));
		assertEquals("Stefan", item.get(2));
	}


	@Test
	public void testFullList() {
		SimpleSet<Integer> smallList;
		smallList = createTest(3);
		testList(smallList);
		smallList = createTest(10);
		testList(smallList);
	}
	private SimpleSet<Integer> createTest(int size) {
		SimpleSet<Integer> smallList = new SimpleSet<Integer>();
		smallList.add(1);
		smallList.add(1, 2);
		smallList.add(1, 2);
		smallList.with(Integer.valueOf(3));
		return smallList;
	}
	private void testList(SimpleSet<Integer> smallList) {
		Integer theThree = Integer.valueOf(3);
		Integer theOne = Integer.valueOf(1);
		assertEquals(Integer.valueOf(1), smallList.first());
		assertEquals(theThree, smallList.last());
		assertEquals(3, smallList.size());
		assertEquals(2, smallList.indexOf(theThree));
		assertEquals(theThree, smallList.get(2));
		assertEquals(theThree, smallList.getKeyByIndex(2));
		assertEquals(2, smallList.getPositionKey(3, false));
//		assertEquals(-1, smallList.getPositionValue(3));
		assertEquals(1, smallList.removeByObject(2));
		assertEquals(theOne, smallList.remove(0));

		assertEquals(0, smallList.indexOf(theThree));
		assertFalse(smallList.isAllowDuplicate());
		assertFalse(smallList.isAllowEmptyValue());
		assertTrue(smallList.isCaseSensitive());
		assertFalse(smallList.isComparator());
		assertFalse(smallList.isEmpty());
		assertTrue(smallList.isVisible());
		assertFalse(smallList.isReadOnly());
	}
}
