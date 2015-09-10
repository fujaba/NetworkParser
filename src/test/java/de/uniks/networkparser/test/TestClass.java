package de.uniks.networkparser.test;

import java.util.Iterator;

import org.junit.Test;

import de.uniks.networkparser.list.SimpleList;

public class TestClass {

	@Test
	public void test(){
		SimpleList<Integer> list=new SimpleList<>();
		list.add(42);
		list.add(23);
		list.with(1,2,3,4,5,6,7);
		for(Iterator<Integer> i = list.iterator();i.hasNext();) {
			System.out.println(i.next());
		}
	}
}
