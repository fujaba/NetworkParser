package de.uniks.networkparser.test;



import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.PersonSet;

public class HashTableTest
{
	public static final String FORMAT="%5d";
	private static ArrayList<Person> items = new ArrayList<Person>();
	public static int COUNT;
	public static PrintStream stream;
	
	public static void printToStream(String string) {
		if(HashTableTest.stream != null) {
			HashTableTest.stream.println(string);
		}
	}
	
	@BeforeClass
	public static void initDummy(){
		// VM Arg
		// -Dcount=1000000
		if(System.getProperty("count")!=null){
			HashTableTest.COUNT = Integer.valueOf((String)System.getProperty("count"));
		}else{
//			HashTableTest.COUNT = 1000 * 1000;
			HashTableTest.COUNT = 100;
		}
//		HashTableTest.stream = System.out;
		printToStream("Run test for "+HashTableTest.COUNT+" items");
		for (int i = 0; i < COUNT; i++) {
			items.add(new Person().withName("p" + i));
		}
	}
	
	private Collection<Person> add(String label, Collection<Person> list){
		long currentTimeMillis = System.currentTimeMillis();
		
		for (int i = 0; i < COUNT; i++) {
			list.add(items.get(i));
		}
		
		String end = String.format(FORMAT, (System.currentTimeMillis() - currentTimeMillis));
		
		printToStream(label+ " Add:		  " +end+ "ms = number of persons: " + list.size());	
		return list;
	}

	private void contains(String label, List<Person> list){
		int step=100;
		long currentTimeMillis = System.currentTimeMillis();

		for (int i = 0; i < items.size(); i += step) {
			Assert.assertTrue("not in list: "+i+"="+items.get(i), list.contains(items.get(i)));
		}
		String end = String.format(FORMAT, (System.currentTimeMillis() - currentTimeMillis));
		printToStream(label+ " contains:	 " +end+ "ms for " +list.size()/step + " Objects");
	}
	
	private void getter(String label, List<Person> list){
		int step=1000;
		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < items.size(); i += step) {
			Assert.assertNotNull("not in list", list.get(i));
		}
		String end = String.format(FORMAT, (System.currentTimeMillis() - currentTimeMillis));
		printToStream(label+ " getter(index):" +end+ "ms for " +list.size()/step + " Objects");
	}
	
	private void getter(String label, Set<Person> list){
		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < items.size(); i += 1000) {
			int pos=0;
			for (Iterator<Person> iterator = list.iterator();iterator.hasNext();) {
				if(pos==i){
					Assert.assertNotNull("not in list "+ i , iterator.next());
					break;
				} else {
					iterator.next();
					pos++;
				}
			}
		}
		String end = String.format(FORMAT, (System.currentTimeMillis() - currentTimeMillis));
		printToStream(label+ " getter(index):" + end + " for 100 Objects");
	}
	
	private void contains(String label, Set<Person> list){
		long currentTimeMillis = System.currentTimeMillis();
		for (int i = 0; i < items.size(); i += 100) {
			Assert.assertTrue("not in list", list.contains(items.get(i)));
		}
		String end = String.format(FORMAT, (System.currentTimeMillis() - currentTimeMillis));
		printToStream(label+ " contains:	 " + end + " for 10000 Objects");
	}
	
	private void iterator(String label, Collection<Person> list){
		long currentTimeMillis = System.currentTimeMillis();
		for (Iterator<Person> i = list.iterator();i.hasNext();){
			Assert.assertNotNull(i.next());
		}
		String end = String.format(FORMAT, (System.currentTimeMillis() - currentTimeMillis));
		printToStream(label+ " iterator:	 " + end);
	}
	
	private void removeObject(String label, Collection<Person> list){
		long currentTimeMillis = System.currentTimeMillis();

		for (int i = 0; i < items.size(); i += 100) {
			printToStream(""+i);
			if(i==0) {
				printToStream("HH");
			}
			list.remove(items.get(i));
		}
		int c=0;
		for (Iterator<Person> i = list.iterator();i.hasNext();){
			Person item = i.next();
			c++;
			Assert.assertNotNull("Item "+c+"/"+list.size()+" are null", item);
		}
		String end = String.format(FORMAT, (System.currentTimeMillis() - currentTimeMillis));
		printToStream(label+ " removeObject: " + end+ "(" +list.size()+ ")");
	}
	
	private void test(String text, Set<Person> items){
		add(text, items);
		contains(text, items);
		iterator(text, items);
		getter(text, items);
		removeObject(text, items);
	}
	private void test(String text, List<Person> items){
		add(text, items);
		contains(text, items);
		iterator(text, items);
		getter(text, items);
		removeObject(text, items);
	}	

	@Test
	public void testLists(){
		test("ArrayList	:", new ArrayList<Person>());
		test("LinkedHashSet:", new LinkedHashSet<Person>());
		test("PersonSet	:", new PersonSet());
	}
	
   @Test
   public void testSmallList() {
	   PersonSet personSet = new PersonSet();
	   Person newValue = new Person();
	   personSet.add(newValue);
	   personSet.add(new Person());
	   personSet.remove(newValue);
	   Assert.assertEquals(1, personSet.size());
   }
	
	
   @Test
   public void testInsertion()
   {
	   long currentTimeMillis = System.currentTimeMillis();
	   
	  GroupAccount groupAccount = new GroupAccount();
	  
	  PersonSet personSet = new PersonSet();
	  
	  for (int i = 0; i < COUNT; i++)
	  {
		 Person person = groupAccount.createPersons().withName("p" + i);
		 
		 if (i % 100 == 0)
		 {
			personSet.add(person);
		 }
	  }
	  
	  
	  printToStream("	 number of persons: " + groupAccount.getPersons().size() + " probe size: " + personSet.size());
	  printToStream("	 " +(System.currentTimeMillis() - currentTimeMillis ));
//	  for (Person person : personSet)
//	  {
//		 Assert.assertTrue("not in list", groupAccount.getPersons().contains(person));
//		 groupAccount.withoutPersons(person);
//		 Assert.assertFalse("still in list", groupAccount.getPersons().contains(person));
//		 
//	  }
//
//	  Assert.assertTrue("not in list", groupAccount.getPersons().contains(personSet.get(personSet.size()-1)));
   }
}
