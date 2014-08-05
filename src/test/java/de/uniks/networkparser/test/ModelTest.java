package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.ArrayEntityList;
import de.uniks.networkparser.json.JsonIdMap;
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
		for(Person p : persons){
			if(i==0){
				Assert.assertEquals("Albert", p.getName());
			}else{
				Assert.assertEquals("Stefan", p.getName());
			}
			i++;
		}
	}
	
	@Test
	public void testMap(){
		ArrayEntityList<String, Integer> values=new ArrayEntityList<String, Integer>();
		
		values.with("Albert", 42);
		values.with("Stefan", 23);
		for(String key : values){
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
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new PersonCreator());
		Assert.assertEquals(6, map.getCreators().size());
		System.out.println(map.getCreators().size());
		
		JsonIdMap subMap=new JsonIdMap();
		Assert.assertEquals(5, subMap.getCreators().size());
		subMap.withCreator(map);
		Assert.assertEquals(6, subMap.getCreators().size());
		
	}
}
