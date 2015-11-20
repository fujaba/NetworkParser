package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.xml.XMLEntity;

public class TestClass {

	@Test
	public void test(){
		XMLEntity xmlEntity = new XMLEntity().withValue("<chatmsg folder=\"C:\\temp\\\\\" />");
		Assert.assertEquals("C:\\temp\\", xmlEntity.getValue("folder"));
	}
}
