package de.uniks.networkparser.test;


import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.xml.HTMLEntities;

public class HTMLTest {

	@Test
	public void testHTML(){
		HTMLEntities html = new HTMLEntities();
		String txt = "Stefan <Test>";
		String encode = html.encode(txt);
		Assert.assertEquals("Stefan &lt;Test&gt;", encode);
		
		Assert.assertEquals(txt, html.decode(encode));
	}
}
