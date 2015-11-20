package de.uniks.networkparser.test;


import java.util.Collection;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.sort.EntityComparator;

public class debugTest {
//	private RegCalculator calculator;
//	@Before
//	public void init(){
//		calculator = new RegCalculator();
//		calculator.withStandard();
//	}

	@Test
	public void testError(){
		TreeSet<Integer> values= new TreeSet<Integer>();
		
		values.add(1);
		values.add(3);
		values.add(2);

		values.add(5);
		values.add(4);
		
		SortedSet<Integer> subSet = values.subSet(2, 4);
		Assert.assertEquals("OLD subSet:", "2 3 ", getString(subSet));

		SortedSet<Integer> headSet = values.headSet(3);
		Assert.assertEquals("OLD headSet:", "1 2 ", getString(headSet));

		SortedSet<Integer> tailSet = values.tailSet(3);
		Assert.assertEquals("OLD tailSet:", "3 4 5 ", getString(tailSet));
	}

	public String getString(Collection<?> set) {
		StringBuilder sb=new StringBuilder();
		for (Object v : set){
			sb.append(v+ " ");
		}
		return sb.toString();
	}
	@Test
	public void testErrorNew(){
		JsonArray values = new JsonArray();
		values.withComparator(EntityComparator.HASHCODE);
		values.add(1);
		values.add(3);
		values.add(2);

		values.add(5);
		values.add(4);
		
		JsonArray subSet = values.subList(2, 4);
		Assert.assertEquals("NEW subSet:", "3 4 ", getString(subSet));

		JsonArray headSet = values.headSet(3, true);
		Assert.assertEquals("NEW headSet:", "3 ", getString(headSet));

		JsonArray tailSet = values.tailSet(3, true);
		Assert.assertEquals("NEW tailSet:", "3 4 5 ", getString(tailSet));
	}
	
	class IntComparator implements Comparator<Object>{
		@Override
		public int compare(Object o1, Object o2) {
			Integer i1=(Integer)o1;
			Integer i2=(Integer)o2;

			return i1-i2;
		}
		
	}
}
