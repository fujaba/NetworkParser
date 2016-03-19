package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.ByteConverterHex;

public class StringTest {
	@Test
	public void testStringReplace(){
		CharacterBuffer buffer = new CharacterBuffer().with("My %DEEP is not the %DEEP");
		buffer.replace("%DEEP", "1");
		Assert.assertEquals("My 1 is not the 1", buffer.toString());
	}
	
	@Test
	public void testStringReplaceLong(){
		CharacterBuffer buffer = new CharacterBuffer().with("My %ID is not the %ID");
		buffer.replace("%ID", "4223");
		Assert.assertEquals("My 4223 is not the 4223", buffer.toString());
	}
	
	@Test
	public void testString(){
		String simple="<c id=\"C:\\\" />";
		ByteBuffer bytes = new ByteBuffer().with(simple.getBytes());
		String string = new ByteConverterHex().toString(bytes, 1);
		Assert.assertEquals("3C 63 20 69 64 3D 22 43 3A 5C 22 20 2F 3E ", string);
	}

	@Test
	public void testEscape(){
		String ref="Hallo Welt";
		String temp = ref;
		for(int i=0;i<6;i++) {
			temp = EntityUtil.quote(temp);
		}

		for(int i=0;i<6;i++) {
			temp = EntityUtil.unQuote(temp);
		}
		Assert.assertEquals(ref, temp);
	}

	@Test
	public void testEscapeSimple(){
		String g = "\"\\\"Hallo Welt\\\"\"";
		String t = "\"\\\"\\\\\\\"Hallo Welt\\\\\\\"\\\"\"";
		Assert.assertEquals(g, EntityUtil.unQuote(t));
	}

	@Test
	public void testEscapeSimpleHTML(){
		char[] txt = new char[]{'H','a', 'l', 'l', 228};
		String example = new String(txt);
		EntityUtil util = new EntityUtil();
		String encode = util.encode(example);
		Assert.assertEquals(example, util.decode(encode));
	}
}
