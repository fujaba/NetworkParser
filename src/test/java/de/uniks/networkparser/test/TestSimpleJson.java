package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.SimpleGrammar;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.creator.PersonCreator;

public class TestSimpleJson {

	@Test
	public void testSimple(){
		JsonIdMap encodeMap=new JsonIdMap().withGrammar(new SimpleGrammar());
		
		encodeMap.withCreator(new PersonCreator());
		Person person = new Person().withName("Albert").withBalance(42);
		String shortString = encodeMap.toJsonObject(person).toString();
		System.out.println(shortString);
		
		JsonIdMap decodeMap=new JsonIdMap().withGrammar(new SimpleGrammar());
		decodeMap.withCreator(new PersonCreator());
		Person item = (Person) decodeMap.decode(new JsonObject().withValue(shortString));
		Assert.assertEquals("Albert", item.getName());
		Assert.assertEquals(42, item.getBalance(), 0.000001);
		
	}
	
}
