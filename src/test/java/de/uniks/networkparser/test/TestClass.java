package de.uniks.networkparser.test;

import java.util.Iterator;

import org.junit.Test;

import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.UpdateListenerJson;
import de.uniks.networkparser.list.SimpleList;

public class TestClass {

//	@Test
	public void test(){
		SimpleList<Integer> list=new SimpleList<>();
		list.add(42);
		list.add(23);
		list.with(1,2,3,4,5,6,7);
		for(Iterator<Integer> i = list.iterator();i.hasNext();) {
			System.out.println(i.next());
		}
	}
	
	@Test
	public void testJson(){
		JsonObject jsonA = new JsonObject().withValue("{id:42, no:23, list:[1,2], array:[1,2]}");
		JsonObject jsonB = new JsonObject().withValue("{id:42, no:24, list:[1,2], array:[1,3]}");
		System.out.println(UpdateListenerJson.compareJson(jsonA, jsonB));
		System.out.println(jsonA);
		System.out.println(jsonB);
	}
	
}
