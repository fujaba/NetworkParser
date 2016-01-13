package de.uniks.networkparser.test;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.PersonSet;

public class ModelTest {
	@Test
	public void testModel(){
		PersonSet persons= new PersonSet();
		
		persons.with(new Person().withName("Albert"));
		persons.with(new Person().withName("Stefan"));
		
		int i=0;
		for (Person p : persons){
			if(i==0){
				Assert.assertEquals("Albert", p.getName());
			} else {
				Assert.assertEquals("Stefan", p.getName());
			}
			i++;
		}
	}
	
	@Test
	public void testMap(){
		SimpleKeyValueList<String, Integer> values= new SimpleKeyValueList<String, Integer>();
		
		values.with("Albert", 42);
		values.with("Stefan", 23);
		for (String key : values){
			if(key.equals("Albert")){
				Assert.assertEquals(42, values.getInt(key));
			}
			if(key.equals("Stefan")){
				Assert.assertEquals(23, values.getInt(key));
			}
		}
	}
	
	@Test
	public void testIdMapFromIdMap(){
		JsonIdMap map= new JsonIdMap();
		map.with(new PersonCreator());
		Assert.assertEquals(6, countMap(map));
		
		JsonIdMap subMap= new JsonIdMap();
		Assert.assertEquals(5, countMap(subMap));
		subMap.with(map);
		Assert.assertEquals(6, countMap(subMap));
		
	}
	
	private int countMap(JsonIdMap map){
		int count=0;
		for (Iterator<SendableEntityCreator> i = map.iterator();i.hasNext();){
			i.next();
			count++;
		}
		return count;
		
	}
}
