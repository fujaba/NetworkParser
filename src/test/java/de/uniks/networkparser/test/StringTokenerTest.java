package de.uniks.networkparser.test;

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
		System.out.println(dateTime.toString("HH:MM:SS \"Sekunden\""));
	}
	
	@Test
	public void testStringSplit(){
		StringTokener tokener = (StringTokener) new StringTokener().withBuffer("[1,\"2,3\",4]");
		if(tokener.charAt(0)=='['&&tokener.charAt(tokener.length()-1)==']'){
			tokener.setIndex(1);
			tokener.withLength(tokener.length()-1);
			int count=0;
			StringContainer sc;
			//FIXME change to ""
			do{
				sc = tokener.nextString(new StringContainer(), true, ',');
				if(sc.length()>0){
					System.out.println(count++ + ": #" +sc.toString()+ "# -- " +tokener.isString());
				}
			}while (sc.length()>0);
		}
	}
	
	@Test
	public void testToday(){
		DateTimeEntity date= new DateTimeEntity();
	   System.out.println(date.getTime());
		System.out.println(date.toString("ddd. dd.mm.yyyy"));
	}
	
	public void showString(StringTokener tokener, String value){
		int count=0;
		StringContainer sub;
		
		System.out.println("zu parsen: " +value);
		tokener.withBuffer(value);
		do{
			sub = new StringContainer();
			tokener.nextString(sub, true, '"');
			if(sub.length()>0){
				System.out.println(count++ + ": #" +sub+ "# -- " +tokener.isString());
			}
		}while (sub.length()>0);
		System.out.println();
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
		System.out.println(bytes.length);
		System.out.println(test.length());
		System.out.println((Character)test.charAt(0));
		System.out.println(bytes[0]);
		JsonTokener jsonTokener = (JsonTokener) new JsonTokener().withBuffer(test);
		System.out.println(jsonTokener.nextString(new StringContainer(), true, '\"'));
		System.out.println(jsonTokener.nextString(new StringContainer(), true, '\"'));
	}
}
