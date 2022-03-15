package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.list.EntityComparator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.list.SimpleSet;
import de.uniks.networkparser.list.SortedList;
import de.uniks.networkparser.list.SpeedList;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.Student;

public class SimpleArrayListTest {

	@Test
	public void testDebugInfo() {
		Object[][] intern = new Object[5][2];
		SimpleKeyValueList<Integer, Student> map = new SimpleKeyValueList<Integer, Student>();
		for(int i=0;i<5;i++) {
			Student stud = new Student().withName("Student "+i);
			intern[i] = new Object[] {i, stud};
			map.add(i, stud);
		}
	}
	
	@Test
	public void testSetElement() {
		SimpleSet<String> set=new SimpleSet<String>();

		SimpleList<String> list=new SimpleList<String>();
		set.add("Hallo", "Welt", "Stefan");
		set.add("Stefan");

		list.add("Stefan", "Stefan");
		assertEquals(3, set.size());
		assertEquals(2, list.size());
	}

	@Test
	public void testSortElement() {
		SortedList<SortedMsg> list = new SortedList<SortedMsg>(true);
		list.add(new SortedMsg().withNumber(22));
		list.add(new SortedMsg().withNumber(42));
		list.add(new SortedMsg().withNumber(1));
		list.add(new SortedMsg().withNumber(23));
		list.add(new SortedMsg().withNumber(80));
		assertEquals(1, list.get(0).getNumber());
		assertEquals(22, list.get(1).getNumber());
		assertEquals(23, list.get(2).getNumber());
		assertEquals(42, list.get(3).getNumber());
		assertEquals(80, list.get(4).getNumber());
	}
	
	@Test
	public void testEMPTYLIST() {
		SpeedList<Integer> list = new SpeedList<Integer>();
		assertNotNull(list.toString());
	}

	@Test
	public void testRemoveFirstItem() {
		SimpleList<String> list = new SimpleList<String>().with("Hello", "World", "Test");
		list.remove(0);
		ArrayList<String> newList = new ArrayList<String>(list);
		assertEquals("World", newList.get(0));
	}

	@Test
	public void testSimpleList() {
		SimpleSet<String> simpleSet = new SimpleSet<String>();
		simpleSet.withFlag(SimpleSet.ALLOWDUPLICATE);
		simpleSet.add("Hallo");
		simpleSet.add("Welt");
		simpleSet.add("Welt");
		simpleSet.add("Simple");
		assertEquals(1, simpleSet.getPositionKey("Welt", false));
		assertEquals(2, simpleSet.getPositionKey("Welt", true));
		for (int i = 0; i < 420; i++) {
			simpleSet.add("!");
		}
		assertEquals(1, simpleSet.getPositionKey("Welt", false));
		assertEquals(2, simpleSet.getPositionKey("Welt", true));
	}

	@Test
	public void testBigListTest() {
		SimpleSet<Integer> list = new SimpleSet<Integer>();
		for (int i = 0; i < 500; i++) {
			list.add(i);
		}
		assertEquals(500, list.size());
		SimpleSet<Integer> newList = new SimpleSet<Integer>();
		newList.withList(list);
		assertEquals(500, newList.size());

		SimpleSet<Integer> newListOBJ = new SimpleSet<Integer>();
		newListOBJ.with(list.toArray(new Object[list.size()]));
		assertEquals(500, newList.size());

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
		assertEquals("Test", newList.get(2));
		assertEquals(3, list.size());
	}

	@Test
	public void testInsertItems() {
		SimpleList<String> list = new SimpleList<String>();
		list.add("Hello");
		list.add("Test");
		list.add(1, "World");
		ArrayList<String> newList = new ArrayList<String>(list);
		assertEquals("Test", newList.get(2));
		assertEquals(3, list.size());
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
		assertEquals("Test", newList.get(2));
		assertEquals(4, list.size());
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
		assertEquals(2, itemA.size());
		assertEquals(2, itemB.size());
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
		assertEquals("Map Visible CaseSensitive (5)", list.toString());
		list.add(6, 6);
		list.add(7, 7);
		Object[] array = list.keySet().toArray();
		assertEquals(7, array.length);
		assertEquals(1, array[0]);
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

		assertEquals(5, keys[0]);
		assertEquals(null, keys[1]);
		assertEquals(1, keys[2]);
		assertEquals(2, keys[3]);
		assertEquals(3, keys[4]);
		assertEquals(4, keys[5]);
	}

	@Test
	public void testReadd() {
		SimpleList<Integer> list = new SimpleList<Integer>();

		assertFalse(list.contains(3));
		assertFalse(list.contains(4));
		assertFalse(list.contains(5));

		for (int i = 1; i <= 500; i++) {
			list.add(i);
		}
		assertEquals(500, list.size());

		assertTrue(list.contains(3));
		assertTrue(list.contains(4));
		assertTrue(list.contains(5));

		list.remove((Integer) 4);
		list.remove((Integer) 3);
		list.remove((Integer) 2);

		assertFalse(list.contains(2));
		assertFalse(list.contains(3));
		assertFalse(list.contains(4));

		list.add(3);

		assertTrue(list.contains(3));
	}

	@Test
	public void testReaddBig() {
		SimpleList<Integer> list = new SimpleList<Integer>();

		assertFalse(list.contains(3));
		assertFalse(list.contains(4));
		assertFalse(list.contains(5));

		for (int i = 1; i <= 500; i++) {
			list.add(i);
		}
		assertEquals(500, list.size());

		assertTrue(list.contains(3));
		assertTrue(list.contains(4));
		assertTrue(list.contains(5));

		list.remove((Integer) 4);
		list.remove((Integer) 3);
		list.remove((Integer) 2);

		assertFalse(list.contains(3));
		assertFalse(list.contains(4));
		assertFalse(list.contains(5));

		list.add(3);

		assertTrue(list.contains(3));

		// try some larger numbers
		assertTrue(list.contains(453));
		assertTrue(list.contains(454));
		assertTrue(list.contains(455));

		list.remove((Integer) 454);
		list.remove((Integer) 453);
		list.remove((Integer) 452);

		assertFalse(list.contains(453));
		assertFalse(list.contains(454));
		assertFalse(list.contains(455));

		list.add(453);

		assertTrue(list.contains(453));
	}


	@Test
	public void testSortedMap() {
		SimpleKeyValueList<Double, String> map = new SimpleKeyValueList<Double, String>().withComparator(EntityComparator.createComparator());
		map.add(42, "Welt");
		map.add(100, "Stefan");
		map.add(23, "Hallo");

		CharacterBuffer sb=new CharacterBuffer();
		for(int i=0;i<map.size();i++) {
			sb.add(map.getValueByIndex(i));
			sb.add(" ");
		}

		assertEquals("Hallo Welt Stefan ", sb.toString());
	}
	
	@Test
	public void testDoubleMap() {
		SimpleKeyValueList<Double, String> map = new SimpleKeyValueList<Double, String>().withComparator(EntityComparator.createComparator());
		for(int i=0;i<10;i++) {
			double d = i;
			map.add(d, "" +d);
		}
		assertEquals(10, map.size());
		for(int i=0;i<10;i++) {
			assertEquals(""+i+".0", map.getValue((double)i));
		}
	}
	@Test
	public void testDoubleMapAdd() {
		SimpleKeyValueList<Double, String> map = new SimpleKeyValueList<Double, String>().withComparator(EntityComparator.createComparator());
		map.put(7.0, "7");
		map.put(14.0, "14");
		map.put(3.0, "3");
		assertEquals(3, map.size());
	}

	@Test
	public void testReadOnly() {
		SimpleSet<String> set = new SimpleSet<String>().with("Albert", "Stefan");
		set.withFlag(SimpleSet.READONLY);
		
		try {
			// Test Add
			set.add("Karli");
			assertEquals(2, set.size());
			fail( "My method didn't throw when I expected it to" );
		} catch (Exception expectedException) {}

		ArrayList<String> arrayList = new ArrayList<String>();
		arrayList.add("Karli");
		set.addAll(arrayList);
		assertEquals(2, set.size());

		try {
			set.remove("Stefan");
			assertEquals(2, set.size());
		} catch (Exception expectedException) {}

		try {
			arrayList = new ArrayList<String>();
			arrayList.add("Stefan");
			set.removeAll(arrayList);
			assertEquals(2, set.size());
		} catch (Exception expectedException) {}
		
		try {
			arrayList = new ArrayList<String>();
			arrayList.add("Stefan");
			set.retainAll(arrayList);
			assertEquals(2, set.size());
		} catch (Exception expectedException) {}
		
//		set.removeIf(filter)

		try {
			set.clear();
			assertEquals(2, set.size());
			fail( "My method didn't throw when I expected it to" );
		} catch (Exception expectedException) {}
		//ITERATOR
		//        public void remove() {
//        public void set(E e) {
//        public void add(E e) {
	}
}
