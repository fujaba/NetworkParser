package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.bytes.converter.ByteConverterHex;

public class StringTest {
	@Test
	public void testSTring(){
		String simple="<c id=\"C:\\\" />";
		byte[] bytes = simple.getBytes();
		String string = new ByteConverterHex().toString(bytes, bytes.length, 1);
		System.out.println(string);
		
	}
}
