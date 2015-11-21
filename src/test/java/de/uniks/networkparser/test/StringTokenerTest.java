package de.uniks.networkparser.test;

import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.StringTokener;
import de.uniks.networkparser.date.DateTimeEntity;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.string.StringContainer;

public class StringTokenerTest {
	@Test
	public void testString(){
		StringTokener tokener=(StringTokener) new StringTokener().withBuffer("Hallo Welt");
		showString(tokener, "Hallo Welt");
		
		showString(tokener, "Hallo \"meine\" Welt");
		
		showString(tokener, "\"Hallo meine\" Welt");
		
		showString(tokener, "Hallo \"meine \\\"kleine\\\"\" Welt");
		
		
		DateTimeEntity dateTime = new DateTimeEntity();
		showString(tokener, "HH:MM:SS \"Sekunden\"");
		Assert.assertNotNull(dateTime.toString("HH:MM:SS \"Sekunden\""));
	}
	
	@Test
	public void testStringSplit(){
		StringTokener tokener = (StringTokener) new StringTokener().withBuffer("[1,\"2,3\",4]");
		if(tokener.charAt(0)=='['&&tokener.charAt(tokener.length()-1)==']'){
			tokener.setIndex(1);
			tokener.withLength(tokener.length()-1);
			int count=0;
			StringContainer sc;
			do{
				sc = tokener.nextString(new StringContainer(), true, ',');
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
	
	public void showString(StringTokener tokener, String value){
		int count=0;
		StringContainer sub;
		PrintStream stream = null;
		
		output("zu parsen: " +value, stream);
		tokener.withBuffer(value);
		do{
			sub = new StringContainer();
			tokener.nextString(sub, true, '"');
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
		StringTokener stringTokener = (StringTokener) new StringTokener().withBuffer("-Harmonie -Illusion -\"E1 E2\"");
		ArrayList<String> stringList = stringTokener.getStringList();
		ArrayList<String> searchList= new ArrayList<String>();
		for (int i=0;i<stringList.size();i++){
			if(stringList.get(i).endsWith("-") && i<stringList.size()-1){
				String temp=stringList.get(i);
				temp=temp.substring(0, temp.length()-1);
				searchList.addAll(stringTokener.getString(temp.trim(), true));
				searchList.add("-" +stringList.get(++i).trim());
			} else {
				searchList.addAll(stringTokener.getString(stringList.get(i), true));
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
		Assert.assertEquals(2, test.length());
		Assert.assertNotNull((Character)test.charAt(0));
		Assert.assertNotNull(bytes[0]);
		JsonTokener jsonTokener = (JsonTokener) new JsonTokener().withBuffer(test);
		Assert.assertNotNull(jsonTokener.nextString(new StringContainer(), true, '\"'));
		Assert.assertNotNull(jsonTokener.nextString(new StringContainer(), true, '\"'));
	}
}
