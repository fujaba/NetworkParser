package de.uniks.networkparser.test;


import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ArraySimpleList;

public class ListTest {
	@Test
	public void retainAll(){
		ArraySimpleList<String> item = new ArraySimpleList<String>();
		item.with("Stefan", "Alex", "Albert");
		
		
		ArraySimpleList<String> itemB = new ArraySimpleList<String>();
		itemB.with("Stefan", "Alex", "Christian");
		
		item.retainAll(itemB);
		
		Assert.assertEquals(2, item.size());
		
		ArraySimpleList<String> clone =  (ArraySimpleList<String>)item.clone();
		Assert.assertEquals(2, clone.size());
	}
	
	@Test
	public void Comparator(){
		ArraySimpleList<String> item = new ArraySimpleList<String>();
		item.withComparator(new Comparator<String>() {
			
			@Override
			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
		});
		item.with("Stefan", "Alex", "Albert");
		Assert.assertEquals("Albert", item.get(0));
		Assert.assertEquals("Alex", item.get(1));
		Assert.assertEquals("Stefan", item.get(2));
	}
}
