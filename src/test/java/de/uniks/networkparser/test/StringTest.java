package de.uniks.networkparser.test;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.CharacterReader;
import de.uniks.networkparser.converter.ByteConverterHex;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.xml.XMLTokener;

public class StringTest {
	@Test
	public void testgetString() {
		CharacterBuffer test=new CharacterBuffer();
		String ref = "Hallo World"+BaseItem.CRLF+"Stefan";
		
		test.with(ref);
		Assert.assertEquals(ref.length(), test.length());
		XMLTokener tokener = new XMLTokener();
		tokener.withBuffer(ref);
		
		Object item = tokener.getString(tokener.length() - tokener.position());

		String tokenerString = item.toString();
		Assert.assertEquals(ref.length(), tokenerString.length());
		Assert.assertEquals(ref, tokenerString);
	}		
	
	@Test
	public void testUmlaute() {
		String uml = "\u00fcbung";
		System.out.println(uml);
		byte[] umlBytes = uml.getBytes();
		
		String newString = new String(umlBytes, 0, umlBytes.length);
		for(int i=0;i<umlBytes.length;i++) {
			System.out.println(umlBytes[i]+" ");
		}
		System.out.println(newString);
		
	}
	
	
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
	public void testByteCode() throws UnsupportedEncodingException{
		String a = new String(new byte[]{0x42}, "UTF-8");
		Assert.assertEquals("B", a);
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

	@Test
	public void testSomeString(){
		CharacterReader buffer = new CharacterReader().with("Hallo Welt");
		showString(buffer, "Hallo Welt");

		showString(buffer, "Hallo \"meine\" Welt");

		showString(buffer, "\"Hallo meine\" Welt");

		showString(buffer, "Hallo \"meine \\\"kleine\\\"\" Welt");

		DateTimeEntity dateTime = new DateTimeEntity();
		showString(buffer, "HH:MM:SS \"Sekunden\"");
		Assert.assertNotNull(dateTime.toString("HH:MM:SS \"Sekunden\""));
	}

	@Test
	public void testStringSplit(){
		CharacterReader tokener = new CharacterReader().with("[1,\"2,3\",4]");
		if(tokener.charAt(0)=='['&&tokener.charAt(tokener.length()-1)==']'){
			tokener.withStartPosition(1);
			tokener.withBufferLength(tokener.length()-1);
			int count=0;
			CharacterBuffer sc;
			do{
				sc = tokener.nextString(new CharacterBuffer(), true, false, ',');
				if(sc.length()>0){
					Assert.assertNotNull(count);
					output(count++ + ": #" +sc.toString()+ "# -- " +tokener.isString(), null);
				}
			}while (sc.length()>0);
		}
	}

	@Test
	public void testToday(){
		DateTimeEntity date= new DateTimeEntity();
		Assert.assertNotNull(date.toString("ddd. dd.mm.yyyy"));
	}

	public void showString(CharacterReader tokener, String value){
		int count=0;
		CharacterBuffer sub;
		PrintStream stream = null;

		output("zu parsen: " +value, stream);
		tokener.reset();
		tokener.with(value);
		do{
			sub = new CharacterBuffer();
			tokener.nextString(sub, true, false, '"');
			if(sub.length()>0){
				Assert.assertNotNull(count);
				output(count++ + ": #" +sub+ "# -- " +tokener.isString(), stream);
			}
		}while (sub.length()>0);
		output("\n", stream);
	}

	void output(String str, PrintStream stream) {
		if (stream != null) {
			stream.print(str);
		}
	}

	@Test
	public void testSearchText(){
		CharacterBuffer stringTokener = new CharacterBuffer().with("-Harmonie -Illusion -\"E1 E2\"");
		SimpleList<String> stringList = stringTokener.getStringList();
		ArrayList<String> searchList= new ArrayList<String>();
		for (int i=0;i<stringList.size();i++){
			if(stringList.get(i).endsWith("-") && i<stringList.size()-1){
				String temp=stringList.get(i);
				temp=temp.substring(0, temp.length()-1);
				searchList.addAll(stringTokener.splitStrings(temp.trim(), true));
				searchList.add("-" +stringList.get(++i).trim());
			} else {
				searchList.addAll(stringTokener.splitStrings(stringList.get(i), true));
			}
		}
		String[] lastSearchCriteriaItems = searchList.toArray(new String[searchList.size()]);
		Assert.assertEquals(3, lastSearchCriteriaItems.length);
	}

	@Test
	public void testUTF8(){
		String test=new String(new byte[]{-61,-68});
		byte[] bytes = test.getBytes();
		Assert.assertEquals(2, bytes.length);
		Assert.assertNotNull((Character)test.charAt(0));
		Assert.assertNotNull(bytes[0]);
		JsonTokener jsonTokener = (JsonTokener) new JsonTokener().withBuffer(test);
		Assert.assertNotNull(jsonTokener.nextString(new CharacterBuffer(), true, false, '\"'));
		Assert.assertNotNull(jsonTokener.nextString(new CharacterBuffer(), true, false, '\"'));
	}
	
	@Test
	public void testReplace(){
		CharacterBuffer buffer = new CharacterBuffer();
		buffer.with("apple, kiwi, cherry");
		
		Assert.assertEquals("apple, kiwi, cherry", buffer.toString()); // START
		
		buffer.replace(7, 11, "pear");

		Assert.assertEquals("apple, pear, cherry", buffer.toString()); // SAME LENGTH
		
		buffer.replace(7, 11, "orange");
		
		Assert.assertEquals("apple, orange, cherry", buffer.toString()); // LONGER LENGTH
		
		buffer.replace(7, 13, "grape");
		
		Assert.assertEquals("apple, grape, cherry", buffer.toString()); // SHORTER LENGTH
	}
	
	@Test
	public void testReplaceExtended() {
		CharacterBuffer test=new CharacterBuffer();
		test.with("Hallo x");
		test.replace(6, 7, "Welt");
		
		Assert.assertEquals("Hallo Welt", test.toString());
	}

	@Test
	public void testReplaceExtend() {
		CharacterBuffer test=new CharacterBuffer();
		test.with("\t\tIdMap map=new IdMap().withCreator(new HouseCreator()); //<2>");
		test.replace(57, 62, "<i class=\"conum\" data-value=\"2\" />");
		Assert.assertEquals("\t\tIdMap map=new IdMap().withCreator(new HouseCreator()); <i class=\"conum\" data-value=\"2\" />", test.toString());
	}

	
}
