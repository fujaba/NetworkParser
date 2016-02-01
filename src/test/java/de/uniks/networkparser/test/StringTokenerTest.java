package de.uniks.networkparser.test;

import java.io.PrintStream;
import java.util.ArrayList;
import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.DateTimeEntity;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.CharacterReader;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleList;

public class StringTokenerTest {
	@Test
	public void testString(){
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
			tokener.withLength(tokener.length()-1);
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
//		String test="{id:\"Hüttenberg\"}";
		String test="ü";
		byte[] bytes = test.getBytes();
		Assert.assertEquals(2, bytes.length);
		Assert.assertNotNull((Character)test.charAt(0));
		Assert.assertNotNull(bytes[0]);
		JsonTokener jsonTokener = (JsonTokener) new JsonTokener().withBuffer(test);
		Assert.assertNotNull(jsonTokener.nextString(new CharacterBuffer(), true, false, '\"'));
		Assert.assertNotNull(jsonTokener.nextString(new CharacterBuffer(), true, false, '\"'));
	}
}
