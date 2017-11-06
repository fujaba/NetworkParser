package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedList;
import de.uniks.networkparser.test.model.SortedMsg;

public class SimpleArrayListTest {
	
	@Test
	public void testSetElement() {
		SimpleSet<String> set=new SimpleSet<String>();
		
		SimpleList<String> list=new SimpleList<String>();
		set.add("Hallo", "Welt", "Stefan");
		set.add("Stefan");
		
		list.add("Stefan", "Stefan");
		Assert.assertEquals(3, set.size());
		Assert.assertEquals(2, list.size());
	}
	
	@Test
	public void testSortElement() {
		SortedList<SortedMsg> list = new SortedList<SortedMsg>(true);
		list.add(new SortedMsg().withNumber(22));
		list.add(new SortedMsg().withNumber(42));
		list.add(new SortedMsg().withNumber(1));
		list.add(new SortedMsg().withNumber(23));
		list.add(new SortedMsg().withNumber(80));
		Assert.assertEquals(1, list.get(0).getNumber());
		Assert.assertEquals(22, list.get(1).getNumber());
		Assert.assertEquals(23, list.get(2).getNumber());
		Assert.assertEquals(42, list.get(3).getNumber());
		Assert.assertEquals(80, list.get(4).getNumber());
	}
	
	@Test
	public void testRemoveFirstItem() {
		SimpleList<String> list = new SimpleList<String>().with("Hello", "World", "Test");
		list.remove(0);
		ArrayList<String> newList = new ArrayList<String>(list);
		Assert.assertEquals("World", newList.get(0));
	}

	@Test
	public void testSimpleList() {
		SimpleSet<String> simpleSet = new SimpleSet<String>();
		simpleSet.withFlag(SimpleSet.ALLOWDUPLICATE);
		simpleSet.add("Hallo");
		simpleSet.add("Welt");
		simpleSet.add("Welt");
		simpleSet.add("Simple");
		Assert.assertEquals(1, simpleSet.getPositionKey("Welt", false));
		Assert.assertEquals(2, simpleSet.getPositionKey("Welt", true));
		for (int i = 0; i < 420; i++) {
			simpleSet.add("!");
		}
		Assert.assertEquals(1, simpleSet.getPositionKey("Welt", false));
		Assert.assertEquals(2, simpleSet.getPositionKey("Welt", true));
	}

	@Test
	public void testBigListTest() {
		SimpleSet<Integer> list = new SimpleSet<Integer>();
		for (int i = 0; i < 500; i++) {
			list.add(i);
		}
		Assert.assertEquals(500, list.size());
		SimpleSet<Integer> newList = new SimpleSet<Integer>();
		newList.withList(list);
		Assert.assertEquals(500, newList.size());

		SimpleSet<Integer> newListOBJ = new SimpleSet<Integer>();
		newListOBJ.with(list.toArray(new Object[list.size()]));
		Assert.assertEquals(500, newList.size());

	}

	@Test
	public void testReorderItems() {
		SimpleList<String> list = new SimpleList<String>();
		list.add("Test"); // ["Test"]
		list.add("Hello"); // ["Test","Hello"]
		list.remove(1); // ["Test"]
		list.add(0, "Hello"); // ["Hello", "Test"]
		list.add("World"); // ["Hello", "Test", "World"]
		list.remove(2); // ["Hello", "Test"]
		list.add(1, "World"); // ["Hello", "World", "Test"]
		ArrayList<String> newList = new ArrayList<String>(list);
		Assert.assertEquals("Test", newList.get(2));
		Assert.assertEquals(3, list.size());
	}

	@Test
	public void testInsertItems() {
		SimpleList<String> list = new SimpleList<String>();
		list.add("Hello");
		list.add("Test");
		list.add(1, "World");
		ArrayList<String> newList = new ArrayList<String>(list);
		Assert.assertEquals("Test", newList.get(2));
		Assert.assertEquals(3, list.size());
	}

	@Test
	public void testInsertMoreItems() {
		SimpleList<String> list = new SimpleList<String>();
		list.add("!");
		list.add(0, "Test");
		list.add(0, "Hello");
		list.add(1, "World");
		ArrayList<String> newList = new ArrayList<String>(list); // [Hello,
																	// World,
																	// Test, !]
		Assert.assertEquals("Test", newList.get(2));
		Assert.assertEquals(4, list.size());
	}

	@Test
	public void test() {
		ArrayList<TestObject> list = gtTestList();
		SimpleList<TestObject> simpleArrayList = new SimpleList<SimpleArrayListTest.TestObject>();
		for (TestObject testObject : list) {
			simpleArrayList.add(testObject);
		}
		for (int i = 0; i < simpleArrayList.size(); i++) {
			assertEquals(list.get(i), simpleArrayList.get(i));
		}

		for (int i = 0; i < simpleArrayList.size(); i++) {
			assertEquals(i, simpleArrayList.indexOf(list.get(i)));
		}
	}

	@Test
	public void testRetainAll() {
		HashSet<Integer> itemA = new HashSet<Integer>();
		itemA.add(1);
		itemA.add(2);
		itemA.add(3);

		HashSet<Integer> itemB = new HashSet<Integer>();
		itemB.add(1);
		itemB.add(2);

		itemA.retainAll(itemB);
		Assert.assertEquals(2, itemA.size());
		Assert.assertEquals(2, itemB.size());
	}

	private ArrayList<TestObject> gtTestList() {
		ArrayList<TestObject> list = new ArrayList<TestObject>();
		TestObject obj0 = new TestObject(Integer.MIN_VALUE);
		list.add(obj0);
		TestObject obj1 = new TestObject(-10);
		list.add(obj1);
		TestObject obj2 = new TestObject(-1);
		list.add(obj2);
		TestObject obj3 = new TestObject(0);
		list.add(obj3);
		TestObject obj4 = new TestObject(1);
		list.add(obj4);
		TestObject obj5 = new TestObject(10);
		list.add(obj5);
		TestObject obj6 = new TestObject(Integer.MAX_VALUE);
		list.add(obj6);
		return list;
	}

	class TestObject {
		private int hash;

		public TestObject(int hash) {
			this.hash = hash;
		}

		@Override
		public int hashCode() {
			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			return super.equals(obj);
		}
	}

	@Test
	public void testListKeySet() {
		SimpleKeyValueList<Integer, Integer> list = new SimpleKeyValueList<Integer, Integer>();
		list.add(99, 99);
		list.add(100, 100);
		list.add(1, 1);
		list.add(2, 2);
		list.add(3, 3);
		list.add(4, 4);

		list.remove(99);
		list.remove(100);
		list.add(5, 5);
		Assert.assertEquals("Map Visible CaseSensitive (5)", list.toString());
		list.add(6, 6);
		list.add(7, 7);
		Object[] array = list.keySet().toArray();
		Assert.assertEquals(7, array.length);
		Assert.assertEquals(1, array[0]);
	}

	@Test
	public void testMap()
			throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		SimpleKeyValueList<Integer, Integer> map = new SimpleKeyValueList<Integer, Integer>();
		Field declaredField = map.getClass().getSuperclass().getDeclaredField("elements");
		declaredField.setAccessible(true);
		map.put(99, 99);
		map.put(100, 100);
		map.put(1, 1);
		map.put(2, 2);
		map.put(3, 3);
		map.put(4, 4);
		map.pack();
		map.remove(99);
		map.remove(100);
		map.put(5, 5);

		Object[] items = (Object[]) declaredField.get(map);
		Object[] keys = (Object[]) items[0];

		Assert.assertEquals(5, keys[0]);
		Assert.assertEquals(null, keys[1]);
		Assert.assertEquals(1, keys[2]);
		Assert.assertEquals(2, keys[3]);
		Assert.assertEquals(3, keys[4]);
		Assert.assertEquals(4, keys[5]);
	}

	@Test
	public void testReadd() {
		SimpleList<Integer> list = new SimpleList<Integer>();

		Assert.assertFalse(list.contains(3));
		Assert.assertFalse(list.contains(4));
		Assert.assertFalse(list.contains(5));

		for (int i = 1; i <= 500; i++) {
			list.add(i);
		}
		Assert.assertEquals(500, list.size());

		Assert.assertTrue(list.contains(3));
		Assert.assertTrue(list.contains(4));
		Assert.assertTrue(list.contains(5));

		list.remove((Integer) 4);
		list.remove((Integer) 3);
		list.remove((Integer) 2);

		Assert.assertFalse(list.contains(2));
		Assert.assertFalse(list.contains(3));
		Assert.assertFalse(list.contains(4));

		list.add(3);

		Assert.assertTrue(list.contains(3));
	}

	@Test
	public void testReaddBig() {
		SimpleList<Integer> list = new SimpleList<Integer>();

		Assert.assertFalse(list.contains(3));
		Assert.assertFalse(list.contains(4));
		Assert.assertFalse(list.contains(5));

		for (int i = 1; i <= 500; i++) {
			list.add(i);
		}
		Assert.assertEquals(500, list.size());

		Assert.assertTrue(list.contains(3));
		Assert.assertTrue(list.contains(4));
		Assert.assertTrue(list.contains(5));

		list.remove((Integer) 4);
		list.remove((Integer) 3);
		list.remove((Integer) 2);

		Assert.assertFalse(list.contains(3));
		Assert.assertFalse(list.contains(4));
		Assert.assertFalse(list.contains(5));

		list.add(3);

		Assert.assertTrue(list.contains(3));

		// try some larger numbers
		Assert.assertTrue(list.contains(453));
		Assert.assertTrue(list.contains(454));
		Assert.assertTrue(list.contains(455));

		list.remove((Integer) 454);
		list.remove((Integer) 453);
		list.remove((Integer) 452);

		Assert.assertFalse(list.contains(453));
		Assert.assertFalse(list.contains(454));
		Assert.assertFalse(list.contains(455));

		list.add(453);

		Assert.assertTrue(list.contains(453));
	}

}
