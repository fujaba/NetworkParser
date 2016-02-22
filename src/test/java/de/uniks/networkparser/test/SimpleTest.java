package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashSet;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.RestCounter;
import de.uniks.networkparser.event.util.DateCreator;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.ChatMessage;
import de.uniks.networkparser.test.model.FullAssocs;
import de.uniks.networkparser.test.model.FullMessage;
import de.uniks.networkparser.test.model.Location;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.ChatMessageCreator;
import de.uniks.networkparser.test.model.util.FullAssocsCreator;
import de.uniks.networkparser.test.model.util.FullMessageCreator;
import de.uniks.networkparser.test.model.util.LocationCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;

public class SimpleTest {
	@Test
	public void testJSONMap(){
		IdMap map= new IdMap();
		map.with(new FullAssocsCreator());
		FullAssocs assoc= new FullAssocs();
		assoc.addPassword("Stefan", "42");
		assoc.addPassword("Flo", "23");
		assoc.addAssoc(assoc);
		JsonObject text=map.toJsonObject(assoc);
		String master="{\"class\":\"de.uniks.networkparser.test.model.FullAssocs\",\"id\":\"J1.F1\",\"prop\":{\"passwords\":[{\"class\":\"de.uniks.networkparser.event.ObjectMapEntry\",\"key\":\"Flo\",\"value\":\"23\"},{\"class\":\"de.uniks.networkparser.event.ObjectMapEntry\",\"key\":\"Stefan\",\"value\":\"42\"}],\"fullmap\":[{\"class\":\"de.uniks.networkparser.event.ObjectMapEntry\",\"key\":{\"class\":\"de.uniks.networkparser.test.model.FullAssocs\",\"id\":\"J1.F1\"},\"value\":{\"class\":\"de.uniks.networkparser.test.model.FullAssocs\",\"id\":\"J1.F1\"}}]}}";
		assertEquals(master, text.toString());

		FullAssocs newAssoc = (FullAssocs) map.decode(new JsonObject().withValue(text.toString()));
		assertEquals("Passwords", 2, newAssoc.getPasswords().size());
	}
//	@Test
	public void testSimple() {
		IdMap map = new IdMap();
		Apple apple = new Apple();
		apple.withY(23);
		apple.withX(42);
		map.with(new AppleCreator());
		
//		System.out.println(map.toJsonObject(apple).toString());
		System.out.println(map.toJsonArray(apple).toString());
	}
	
//	@Test
	public void testDate() {
		IdMap map = new IdMap();
		Date now = new Date();
		map.with(new DateCreator());
		
		System.out.println(map.toJsonObject(now).toString());
	}
	
//	@Test
	public void testSimpleList(){
		FullAssocs fullAssocs = new FullAssocs();
		fullAssocs.addPerson("Kassem");
		fullAssocs.addPerson("Stefan");

		IdMap map= new IdMap();
		map.with(new FullAssocsCreator());


		JsonObject jsonObject = map.toJsonObject(fullAssocs);
		String data = jsonObject.toString(2);
		System.out.println(data);
		Assert.assertEquals(144, data.length());

		FullAssocs newfullAssocs = (FullAssocs) map.decode(data);
		assertNotNull(newfullAssocs);
	}

	
//	@Test
	public void testSimpleSortedItem(){
		SortedMsg parent= new SortedMsg();
		parent.setNumber(1);
		SortedMsg child= new SortedMsg();
		child.setNumber(2);
		
		parent.setChild(child);
		IdMap map= new IdMap();
		map.with(new SortedMsgCreator());
		String ref="{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S1\",\"prop\":{\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S2\",\"prop\":{\"number\":2,\"parent\":{\"id\":\"J1.S1\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\"}}}}}";
		assertEquals(ref, map.toJsonObject(parent).toString());
	}
//	@Test
	public void calculation042() {
		SortedMsg parent= new SortedMsg();
		parent.setNumber(1);
		SortedMsg child= new SortedMsg();
		child.setNumber(2);

		parent.setChild(child);


		IdMap map= new IdMap();
		map.with(new SortedMsgCreator());
		String ref="{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S1\",\"prop\":{\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S2\",\"prop\":{\"number\":2,\"parent\":{\"id\":\"J1.S1\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\"}}}}}";
		assertEquals(ref, map.toJsonObject(parent).toString());

		ref = "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"number\":2,\"parent\":{\"id\":\"J1.S1\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\"}}}";
		Filter filter = new Filter().withIdFilter(BooleanCondition.value(false));
		assertEquals(ref, map.toJsonObject(parent, filter).toString());
	}
	
//	@Test
	public void testFull(){
		ChatMessage chatMessage= new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");
		IdMap jsonMap = new IdMap();
		jsonMap.with(new ChatMessageCreator());

		String reference="{\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"id\":\"J1.C1\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"txt\":\"Dies ist eine Testnachricht\"\r\n  }\r\n}";
		JsonObject actual=jsonMap.toJsonObject(chatMessage);
		assertEquals("WERT Vergleichen", reference, actual.toString(2));

		reference="{\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"id\":\"J1.C1\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"time\":null,\r\n    \"txt\":\"Dies ist eine Testnachricht\",\r\n    \"count\":0,\r\n    \"activ\":false\r\n  }\r\n}";
		actual=jsonMap.toJsonObject(chatMessage, new Filter().withFull(true));
		assertEquals("WERT Vergleichen", reference, actual.toString(2));


		// Array
		reference="[\r\n  {\r\n    \"id\":\"J1.C1\",\r\n    \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n    \"prop\":{\r\n      \"sender\":\"Stefan Lindel\",\r\n      \"time\":null,\r\n      \"txt\":\"Dies ist eine Testnachricht\",\r\n      \"count\":0,\r\n      \"activ\":false\r\n    }\r\n  }\r\n]";
		JsonArray actualArray=jsonMap.toJsonArray(chatMessage, new Filter().withFull(true));
		//FIXME JSONARRAY DONT WORK
		assertEquals("WERT Vergleichen", reference, actualArray.toString(2));
	}	
}
