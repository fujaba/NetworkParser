package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.xml.XMLEntity;

public class TestClass {

	@Test
	public void test(){
		XMLEntity xmlEntity = new XMLEntity().withValue("<chatmsg folder=\"C:\\temp\\\\\" />");
		Assert.assertEquals("C:\\temp\\", xmlEntity.getValue("folder"));
		
//		SimpleList<Integer> list=new SimpleList<>();
//		list.add(42);
//		list.add(23);
//		list.with(1,2,3,4,5,6,7);
//		for(Iterator<Integer> i = list.iterator();i.hasNext();) {
//			System.out.println(i.next());
//		}
	}
}
