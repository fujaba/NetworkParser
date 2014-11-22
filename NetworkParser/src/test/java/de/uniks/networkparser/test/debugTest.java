package de.uniks.networkparser.test;


import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

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
		
//		for (Integer v : values){
//			System.out.println(v);
//		}
		
		SortedSet<Integer> subSet = values.subSet(2, 4);
		System.out.print("OLD subSet:");
		for (Integer v : subSet){
			System.out.print(v+ " ");
		}
		System.out.println();

		SortedSet<Integer> headSet = values.headSet(3);
		System.out.print("OLD headSet:");
		for (Integer v : headSet){
			System.out.print(v+ " ");
		}
		System.out.println();
		
		SortedSet<Integer> tailSet = values.tailSet(3);
		System.out.print("OLD tailSet:");
		for (Integer v : tailSet){
			System.out.print(v+ " ");
		}
		System.out.println();
}
	
	@Test
	public void testErrorNew(){
		JsonArray values = new JsonArray();
		values.withComparator(EntityComparator.HASHCODE);
//		values.withComparator(new IntComparator());
		values.add(1);
		values.add(3);
		values.add(2);

		values.add(5);
		values.add(4);
		
//		for (Integer v : values){
//			System.out.println(v);
//		}
		
		JsonArray subSet = values.subSet(2, 4);
		System.out.print("NEW subSet:");
		for (Object v : subSet){
			System.out.print(v+ " ");
		}
		System.out.println();

		JsonArray headSet = values.headSet(3, true);
		System.out.print("NEW headSet:");
		for (Object v : headSet){
			System.out.print(v+ " ");
		}
		System.out.println();
		
		JsonArray tailSet = values.tailSet(3, true);
		System.out.print("NEW tailSet:");
		for (Object v : tailSet){
			System.out.print(v+ " ");
		}
		System.out.println();
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
