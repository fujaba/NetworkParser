package de.uniks.networkparser.list;

import org.junit.Test;

public class testList {

	@Test
	public void list() {
		SimpleList<String> simpleList = new SimpleList<String>();
		simpleList.add("Hallo");
		
		System.out.println(simpleList.get(0));
		
		
		SimpleKeyValueList<String, Integer> map = new SimpleKeyValueList<String, Integer>();
		map.add("Stefan", 42);
		
		System.out.println(map);
	}
}
