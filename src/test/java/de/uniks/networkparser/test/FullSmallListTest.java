package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.list.SimpleList;

public class FullSmallListTest {
	
	@Test
	public void testFullList() {
		SimpleList<Integer> smallList;
		smallList = createTest(3);
		testList(smallList);
		smallList = createTest(10);
		testList(smallList);
	}
	private SimpleList<Integer> createTest(int size) {
		SimpleList<Integer> smallList = new SimpleList<Integer>();
		smallList.add(1);
		smallList.add(1, 2);
		smallList.add(1, 2);
		smallList.with(new Integer(3));
		return smallList;
	}
	private void testList(SimpleList<Integer> smallList) {
		Integer theThree = new Integer(3);
		Integer theOne = new Integer(1);
		Assert.assertEquals(new Integer(1), smallList.first());
		Assert.assertEquals(theThree, smallList.last());
		Assert.assertEquals(3, smallList.size());
		Assert.assertEquals(2, smallList.indexOf(theThree));
		Assert.assertEquals(theThree, smallList.get(2));
		Assert.assertEquals(theThree, smallList.getKeyByIndex(2));
		Assert.assertEquals(2, smallList.getPositionKey(3));
		Assert.assertEquals(-1, smallList.getPositionValue(3));
		Assert.assertEquals(1, smallList.removeByObject(2));
		Assert.assertEquals(theOne, smallList.remove(0));

		Assert.assertEquals(0, smallList.indexOf(theThree));
		Assert.assertFalse(smallList.isAllowDuplicate());
		Assert.assertFalse(smallList.isAllowEmptyValue());
		Assert.assertTrue(smallList.isCaseSensitive());
		Assert.assertFalse(smallList.isComparator());
		Assert.assertFalse(smallList.isEmpty());
		Assert.assertTrue(smallList.isVisible());
		Assert.assertFalse(smallList.isReadOnly());

		//iteratorReverse()
		//lastIndexOf(Object)
		//listIterator()
		//listIterator(int)
		//		public BaseItem getNewList(boolean keyValue) {
//		public SimpleList<V> clone() {
//		public SimpleList<V> subList(int fromIndex, int toIndex) {
//		public boolean remove(Object o)
//	addAll(Collection<? extends V>)
//	addAll(int, Collection<? extends V>)
//	copyEntity(BaseItem, int)
//	removeItemByObject(V)
//	set(int, V)
//	subSet(V, V)
//	withAll(Object...)
//	withAll(Object...)
//	withList(Collection<?>)
//	addFlag(byte)
//	clear()
//	comparator()
//	contains(Object)
//	containsAll(Collection<?>)
//	flag()
//	getLastPositionKey(Object)
//	getNewList(boolean)
//	getSignalFlag()
//	getValueItem(Object)
//	init(Collection<?>)
//	init(int)
//	init(Object[], int)
//	iterator()
//	lastindexOf(Object)
//	move(int, int)
//	removeAll(Collection<?>)
//	retainAll(Collection<?>)
//	subList(int, int)
//	toArray()
//	toArray(T[])
//	withAllowDuplicate(boolean)
//	withAllowEmptyValue(boolean)
//	withCaseSensitive(boolean)
//	withFlag(int)
//	withList(Collection<?>)
//	without(Object...)
//	withVisible(boolean)
	}
}
