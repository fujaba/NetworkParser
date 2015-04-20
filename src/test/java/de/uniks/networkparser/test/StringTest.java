package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.bytes.converter.ByteConverterHex;

public class StringTest {
	@Test
	public void testString(){
		String simple="<c id=\"C:\\\" />";
		byte[] bytes = simple.getBytes();
		String string = new ByteConverterHex().toString(bytes, bytes.length, 1);
		System.out.println(string);
	}
	
	@Test
	public void testEscape(){
		String ref="Hallo Welt";
		String temp = ref;
		System.out.println(temp);
		for(int i=0;i<6;i++) {
			temp = EntityUtil.quote(temp);
			System.out.println(temp);
		}
		
		System.out.println("=========================");
		
		for(int i=0;i<6;i++) {
			temp = EntityUtil.unQuote(temp);
			System.out.println(temp);
		}
		Assert.assertEquals(ref, temp);
	}
	@Test
	public void testEscapeSimple(){
		String g = "\"\\\"Hallo Welt\\\"\"";
		String t = "\"\\\"\\\\\\\"Hallo Welt\\\\\\\"\\\"\"";
		Assert.assertEquals(g, EntityUtil.unQuote(t));
		
	}
}
