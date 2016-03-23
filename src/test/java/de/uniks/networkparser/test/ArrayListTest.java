package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;
import de.uniks.networkparser.list.SimpleList;

public class ArrayListTest {

   @Test
   public void testReadd() {
	  SimpleList<Integer> list=new SimpleList<Integer>();

	  Assert.assertFalse(list.contains(3));
	  Assert.assertFalse(list.contains(4));
	  Assert.assertFalse(list.contains(5));

	  for(int i=1;i<=500;i++) {
		 list.add(i);
	  }
	  Assert.assertEquals(500, list.size());

	  Assert.assertTrue(list.contains(3));
	  Assert.assertTrue(list.contains(4));
	  Assert.assertTrue(list.contains(5));

	  list.remove((Integer)4);
	  list.remove((Integer)3);
	  list.remove((Integer)2);

	  Assert.assertFalse(list.contains(2));
	  Assert.assertFalse(list.contains(3));
	  Assert.assertFalse(list.contains(4));

	  list.add(3);

	  Assert.assertTrue(list.contains(3));
   }

   @Test
   public void testReaddBig() {
	  SimpleList<Integer> list=new SimpleList<Integer>();

	  Assert.assertFalse(list.contains(3));
	  Assert.assertFalse(list.contains(4));
	  Assert.assertFalse(list.contains(5));

	  for(int i=1;i<=500;i++) {
		 list.add(i);
	  }
	  Assert.assertEquals(500, list.size());

	  Assert.assertTrue(list.contains(3));
	  Assert.assertTrue(list.contains(4));
	  Assert.assertTrue(list.contains(5));

	  list.remove((Integer)4);
	  list.remove((Integer)3);
	  list.remove((Integer)2);

	  Assert.assertFalse(list.contains(3));
	  Assert.assertFalse(list.contains(4));
	  Assert.assertFalse(list.contains(5));

	  list.add(3);

	  Assert.assertTrue(list.contains(3));

	  // try some larger numbers
	  Assert.assertTrue(list.contains(453));
	  Assert.assertTrue(list.contains(454));
	  Assert.assertTrue(list.contains(455));

	  list.remove((Integer)454);
	  list.remove((Integer)453);
	  list.remove((Integer)452);

	  Assert.assertFalse(list.contains(453));
	  Assert.assertFalse(list.contains(454));
	  Assert.assertFalse(list.contains(455));

	  list.add(453);

	  Assert.assertTrue(list.contains(453));

   }
}
