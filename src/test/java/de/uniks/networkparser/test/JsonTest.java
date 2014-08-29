package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.RestCounter;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.json.SimpleGrammar;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.Barbarian;
import de.uniks.networkparser.test.model.Change;
import de.uniks.networkparser.test.model.ChatMessage;
import de.uniks.networkparser.test.model.FullAssocs;
import de.uniks.networkparser.test.model.FullMessage;
import de.uniks.networkparser.test.model.Location;
import de.uniks.networkparser.test.model.MapEntryElement;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.StringMessage;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.BarbarianCreator;
import de.uniks.networkparser.test.model.util.ChangeCreator;
import de.uniks.networkparser.test.model.util.ChatMessageCreator;
import de.uniks.networkparser.test.model.util.FullAssocsCreator;
import de.uniks.networkparser.test.model.util.FullMessageCreator;
import de.uniks.networkparser.test.model.util.GameCreator;
import de.uniks.networkparser.test.model.util.LocationCreator;
import de.uniks.networkparser.test.model.util.MapEntryElementCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StringMessageCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class JsonTest {
	@Test
	public void testJSONFunction(){
		String functionJson="{body:\"public main() {\r\n\tconsole.log('Hallo Welt');\n\t}\"}";
		
		JsonObject jsonObject=new JsonObject();
		new JsonTokener().withAllowCRLF(true).withText(functionJson).parseToEntity(jsonObject);
		System.out.println(jsonObject.toString(2));
	}

	
	@Test
	public void testJSONInsert(){
		JsonObject item = new JsonObject().withValue("id", "K444", "value", "42");
		item.add(0, "class", "JsonObject");
		System.out.println(item.toString());
	}
	@Test
	public void testJSONList(){
		JsonObject item = new JsonObject();
		assertEquals(item.toString(), "{}");
		item.addToList("id", 23);
		assertEquals(item.toString(), "{\"id\":23}");
		item.addToList("id", 42);
		assertEquals(item.toString(), "{\"id\":[23,42]}");
	}
	
	
	@Test
	public void testJSONPrimitive(){
		JsonObject item = new JsonObject();
		item.put("idlong", 23L);
		item.put("idint", 23);
		item.put("iddouble", 42.1);
		assertEquals(23L, item.get("idlong"));
		assertEquals(23, item.get("idint"));
		assertEquals("Wrong", 42.1d, item.getDouble("iddouble"), 0.00001);
	}
	
	
	@Test
	public void testJSONMap(){
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new FullAssocsCreator());
		FullAssocs assoc=new FullAssocs();
		assoc.addPassword("Stefan", "42");
		assoc.addPassword("Flo", "23");
		assoc.addAssoc(assoc);
		JsonObject text=map.toJsonObject(assoc);
		String master="{\"class\":\"de.uniks.networkparser.test.model.FullAssocs\",\"id\":\"J1.F1\",\"prop\":{\"passwords\":[{\"class\":\"de.uniks.networkparser.event.ObjectMapEntry\",\"key\":\"Flo\",\"value\":\"23\"},{\"class\":\"de.uniks.networkparser.event.ObjectMapEntry\",\"key\":\"Stefan\",\"value\":\"42\"}],\"fullmap\":[{\"class\":\"de.uniks.networkparser.event.ObjectMapEntry\",\"key\":{\"id\":\"J1.F1\"},\"value\":{\"id\":\"J1.F1\"}}]}}";
		assertEquals(master, text.toString());
		
		FullAssocs newAssoc = (FullAssocs) map.decode(new JsonObject().withValue(text.toString()));
		assertEquals("Passwords", 2, newAssoc.getPasswords().size());
	}
	
	
	@Test
	public void testSimpleMap() {
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new FullAssocsCreator());
		FullAssocs uni = new FullAssocs();
		uni.addPassword("Stefan", "test");
		
		JsonObject jsonObject = map.toJsonObject(uni);
		
		JsonIdMap mapReserve=new JsonIdMap();
		mapReserve.withCreator(new FullAssocsCreator());
		mapReserve.decode(jsonObject.toString());
	}
	
	@Test
	public void testSimpleJson() {
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new ChangeCreator());
		Change change = new Change();
		
		JsonObject data = new JsonObject();
		data.put("id", "name");
		data.put("value", "42");
		
		change.setValue(data);
		JsonObject jsonObject = map.toJsonObject(change);
		
		JsonIdMap mapReserve=new JsonIdMap();
		mapReserve.withCreator(new ChangeCreator());
		System.out.println(jsonObject.toString());
		Change item = (Change) mapReserve.decode(jsonObject.toString());
		assertEquals(item.getValue().getString("value"), "42");
	}
	
	
	
	@Test
	public void testSimpleList(){
		FullAssocs fullAssocs = new FullAssocs();
		fullAssocs.addPerson("Kassem");
		fullAssocs.addPerson("Stefan");
		
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new FullAssocsCreator());
		
		
		JsonObject jsonObject = map.toJsonObject(fullAssocs);
		String data = jsonObject.toString(2);
		System.out.println(data);
		
		FullAssocs newfullAssocs = (FullAssocs) map.decode(data);
		assertNotNull(newfullAssocs);
	}

	@Test
	public void testJson(){
		JsonObject test=new JsonObject();
		JsonObject child=new JsonObject();
		child.put("id", "test");
		child.put("value", 2);
		child.put("child", new JsonObject().withValue("id", "42"));
		test.put("child", child);
		assertEquals("{\"child\":{\"id\":\"test\",\"value\":2,\"child\":{\"id\":\"42\"}}}", test.toString());
		test.setValue("child.value", 42);
		assertEquals("{\"child\":{\"id\":\"test\",\"value\":42,\"child\":{\"id\":\"42\"}}}", test.toString());
	}

	@Test
	public void testJsonArray(){
		JsonArray first=new JsonArray();
		JsonArray child=new JsonArray();
		child.add(new JsonArray());
		child.add(new JsonArray().withValue(new JsonObject().withValue("id", "42")));
		first.add(child);
		assertEquals("[[[],[{\"id\":\"42\"}]]]", first.toString());
	}
	
	@Test
	public void testStringJson(){
		String jsonText="{\"id\":\"10.1.1.126;c10\",\"class\":\"de.uni.kassel.peermessage.model.Change\",\"prop\":{\"value\":\"42\"}}";
		Change change=new Change();
		change.setKey(new Long(42));		
		change.setValue(new JsonObject().withValue(jsonText));
		change.setList(new JsonArray().withValue(new JsonObject().withValue(jsonText)));
		JsonIdMap map=new JsonIdMap();
		map.getCounter().withPrefixId(";");
		map.withCreator(new ChangeCreator());
 		JsonObject json = map.toJsonObject(change);
		Change change2=(Change) map.decode(json);
		assertNotNull(change2);
		assertEquals(new Long(42), change2.getKey());
		assertEquals(jsonText, change2.getValue().toString());
		assertEquals(change.getList().size(), change2.getList().size());
	}

	@Test
	public void testJsonArraySplit(){
		String text="[\"Hallo Welt\",{\"id\":\"42\"}]";
		JsonArray jsonArray = new JsonArray().withValue(text);
		
		assertEquals(2, jsonArray.size());
		assertEquals("Hallo Welt", jsonArray.get(0));
	}
	
	@Test
	public void testSortJson(){
		String text = "{\"id\":\"42\", \"class\":\"de.uniks.networkparser.test.model.SortedMsg\" \"props\":{\"key:\":\"\", \"value\":{}}}";
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new SortedMsgCreator());
		Object item = map.decode(text);
		Assert.assertNotNull(item);
	}

	@Test
	public void testJsonParsing(){
		// Modell
		Change change=new Change();
		change.setKey(new Long(42));
		
		// Map
		JsonIdMap map=new JsonIdMap();
		map.withSessionId(null);
		map.withCreator(new ChangeCreator());
		
		// Serialisation
		JsonObject jsonObject = map.toJsonObject(change);
		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Change\",\"id\":\"C1\",\"prop\":{\"key\":42}}", jsonObject.toString());
	}

	@Test
	public void testJsonIdMapNoPackage(){
		String className="SimpleClass";
		char firstChar = className.charAt(className.lastIndexOf(".") + 1);
		assertEquals('S', firstChar);
	}
	
	@Test
	public void createJson(){
		JsonObject json=new JsonObject().withValue("{id:42}");
		assertEquals(42, json.get("id"));
		Throwable e = null;

		  try {
			  new JsonArray().withValue("{id:42}");
		  } catch (Throwable ex) {
		    e = ex;
		  }

		  assertTrue(e instanceof RuntimeException);
		  
		  
		  JsonArray array = new JsonArray().withValue("[{id:42}]");
		  assertEquals(1, array.size());
		  assertEquals(42,  ((JsonObject)array.get(0)).get("id"));
	}
	
	@Test
	public void testSortedMap(){
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new SortedMsgCreator());
		SortedMsg first=new SortedMsg();
		first.setNumber(1);
		
		SortedMsg third=new SortedMsg();
		third.setNumber(3);
		first.setChild(third);
		
		SortedMsg second=new SortedMsg();
		second.setNumber(2);
		
		third.setChild(second);
		JsonArray jsonArray = new JsonArray();
		jsonArray.withComparator(JsonIdMap.JSON_PROPS+"."+SortedMsg.PROPERTY_ID);
		map.toJsonArray(first, jsonArray, null);
		assertEquals(3, jsonArray.size());
		
//		[
//			{"id":"J1.S3","class":"de.uni.kassel.peermessage.model.SortedMsg","prop":{"id":2}},
//			{"id":"J1.S2","class":"de.uni.kassel.peermessage.model.SortedMsg","prop":{"id":3,"child":"J1.S3"}},
//			{"id":"J1.S1","class":"de.uni.kassel.peermessage.model.SortedMsg","prop":{"id":1,"child":"J1.S2"}}
//		]
		
		String reference = "["+
				"{\"id\":\"J1.S1\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"prop\":{\"number\":1,\"child\":{\"id\":\"J1.S2\"}}},"+
				"{\"id\":\"J1.S3\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"prop\":{\"number\":2,\"parent\":{\"id\":\"J1.S2\"}}},"+
				"{\"id\":\"J1.S2\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"prop\":{\"number\":3,\"child\":{\"id\":\"J1.S3\"},\"parent\":{\"id\":\"J1.S1\"}}}"+
			"]";
				
		assertEquals(reference, jsonArray.toString());
	}
	
	/**
	 * <pre>
	 *           0..1     rooms0..n
	 * University ------------------------- Room
	 *           university        &gt;       rooms
	 * </pre>
	 */
	@Test
	public void testAlbertJson() {
		University kassel = new University().withName("Kassel");

		Room lab = new Room().withName("lab").withUniversity(kassel);

		Room lab2 = new Room().withName("lab2").withUniversity(kassel);

		new Room().withName("lego desk").withParent(lab2);

		new Student().withName("Tobi").withUniversity(kassel).withIn(lab);

		new Student().withName("Nina").withUniversity(kassel).withIn(lab);

		JsonIdMap map = UniversityCreator.createIdMap("s1");

//		System.out.println("depth 1: ");
		JsonArray jsonArray = map.toJsonArray(kassel, new Filter().withConvertable(Deep.value(1)));

		String jsonString = jsonArray.toString(2);
//		System.out.println(jsonString);
		assertEquals(979, jsonString.length());

//		System.out.println(jsonString);

//		System.out.println("depth 0: ");
		jsonArray = map.toJsonArray(kassel, new Filter().withConvertable(new Deep().withDeep(0)));
		
		jsonString = jsonArray.toString(2);
		assertEquals(255, jsonString.length());
//		System.out.println(jsonString);

//		System.out.println("Full depth: ");
		jsonArray = map.toJsonArray(kassel);

		jsonString = jsonArray.toString(2);

//		System.out.println(jsonString);
		assertEquals(1127, jsonString.length());

		JsonIdMap readMap = UniversityCreator.createIdMap("s2");

		University clone = (University) readMap.decode(jsonArray);

		Assert.assertEquals("wrong number of rooms", 2, clone.sizeOfRooms());
	}
	
	@Test
	public void testChatMessage(){
		ChatMessage chatMessage=new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");
		
		JsonIdMap jsonMap = new JsonIdMap();
		jsonMap.withCreator(new ChatMessageCreator());
		
		String reference="{\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"id\":\"J1.C1\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"txt\":\"Dies ist eine Testnachricht\"\r\n  }\r\n}";
		System.out.println(reference);
		JsonObject actual=jsonMap.toJsonObject(chatMessage);
		assertEquals("WERT Vergleichen", reference, actual.toString(2));
		assertEquals(reference.length(), actual.toString(2).length());
		
		String msg = actual.toString(2);
		
		JsonIdMap map = new JsonIdMap();
		map.withCreator(new ChatMessageCreator());
		
		ChatMessage newChatMsg = (ChatMessage) map.decode(new JsonObject().withValue(msg));
		System.out.println(newChatMsg);
	}
	
	@Test
	public void testFull(){
		ChatMessage chatMessage=new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");
		JsonIdMap jsonMap = new JsonIdMap();
		jsonMap.withCreator(new ChatMessageCreator());
		
		String reference="{\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"id\":\"J1.C1\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"txt\":\"Dies ist eine Testnachricht\"\r\n  }\r\n}";
		JsonObject actual=jsonMap.toJsonObject(chatMessage);
		assertEquals("WERT Vergleichen", reference, actual.toString(2));
		
		reference="{\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"id\":\"J1.C1\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"time\":null,\r\n    \"txt\":\"Dies ist eine Testnachricht\",\r\n    \"count\":0,\r\n    \"activ\":false\r\n  }\r\n}";
		actual=jsonMap.toJsonObject(chatMessage, new Filter().withFull(true));
		assertEquals("WERT Vergleichen", reference, actual.toString(2));
		
		
		// Array
		reference="[{\r\n  \"id\":\"J1.C1\",\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"time\":null,\r\n    \"txt\":\"Dies ist eine Testnachricht\",\r\n    \"count\":0,\r\n    \"activ\":false\r\n  }\r\n}]";
		JsonArray actualArray=jsonMap.toJsonArray(chatMessage, new Filter().withFull(true));
		assertEquals("WERT Vergleichen", reference, actualArray.toString(2));
	}
		
	
	@Test
	public void testFullMessage() {
		Date date=new Date();
		date.setTime(1330538995929L);
		FullMessage msg = new FullMessage(date, 42, "Hallo Welt");
		msg.setLocation(new Location(42, 23));
		JsonIdMap map = new JsonIdMap();
//		map.setTypSave(true);
		map.withCreator(new FullMessageCreator());
		map.withCreator(new LocationCreator());
		
		String jsonString = map.toJsonObject(msg).toString();
		String textString="{\"class\":\"de.uniks.networkparser.test.model.FullMessage\",\"id\":\"J1.F1\",\"prop\":{" +
				"\"txt\":\"Hallo Welt\"," +
				"\"number\":42," +
				"\"date\":{\"class\":\"java.util.Date\",\"value\":1330538995929}," +
				"\"location\":{\"class\":\"de.uniks.networkparser.test.model.Location\",\"id\":\"J1.L2\",\"prop\":{\"x\":42,\"y\":23}}" +
				"}}";
		
		assertEquals(textString, jsonString);
		FullMessage mapItem = (FullMessage) map.decode(new JsonObject().withValue(jsonString));
		assertEquals(42, mapItem.getValue());
		assertEquals("Hallo Welt", mapItem.getText());
		assertEquals(1330538995929L, mapItem.getDate().getTime());
	}
	
	@Test
	public void testRest(){
		JsonIdMap map = new JsonIdMap();	
		map.withCreator(new FullMessageCreator());
		map.withCreator(new LocationCreator());
		FullMessage msg = new FullMessage(42, "Hallo Welt");
		msg.setLocation(new Location(42, 23));
		map.withCounter(new RestCounter("http://myname.org/rest/"));
		JsonObject json = map.toJsonObject(msg, new Filter().withConvertable(new Deep().withDeep(0)));
		System.out.println(json);
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.FullMessage\",\"id\":\"http://myname.org/rest/de.uniks.networkparser.test.model.fullmessage/1\",\"prop\":{\"txt\":\"Hallo Welt\",\"number\":42,\"location\":{\"id\":\"http://myname.org/rest/de.uniks.networkparser.test.model.location/2\"}}}", json.toString());
	}
	
	
	@Test
	public void testMapTest(){
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new MapEntryElementCreator());
		MapEntryElement item=new MapEntryElement();
		
		HashMap<String, String> passwords=new HashMap<String, String>();
		passwords.put("Flo", "23");
		passwords.put("Stefan", "42");
				
		item.addToValue("passwords", passwords);
		JsonObject json=map.toJsonObject(item);
		String data = json.toString();
		JsonObject jsonObject = new JsonObject().withValue(data);
		
		// decode
		MapEntryElement itemNew=(MapEntryElement) map.decode(jsonObject);
		assertNotNull(itemNew.getValue());
		assertEquals(itemNew.getValue().size(), 1);
		Object passNew = itemNew.getValue().get("passwords");
		if(passNew instanceof Map<?, ?>){
			assertEquals(((Map<?, ?>)passNew).size(), 2);
		}
//		System.out.println(json);
	}
	
	@Test
	public void calculation042() {
		SortedMsg parent=new SortedMsg();
		parent.setNumber(1);
		SortedMsg child=new SortedMsg();
		child.setNumber(2);
		
		parent.setChild(child);
		
		
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new SortedMsgCreator());
		String ref="{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S1\",\"prop\":{\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S2\",\"prop\":{\"number\":2,\"parent\":{\"id\":\"J1.S1\"}}}}}";
		assertEquals(ref, map.toJsonObject(parent).toString());

		ref = "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"number\":2,\"parent\":{\"id\":\"J1.S1\"}}}";
		Filter filter = new Filter().withIdFilter(BooleanCondition.value(false));
		assertEquals(ref, map.toJsonObject(parent, filter).toString());
	}
	
	@Test
	public void testEscapeText(){
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new StringMessageCreator());
		StringMessage stringMessage = new StringMessage("C:\\TEST\\MY\\WORLD.TXT");
		
		JsonObject jsonObject = map.toJsonObject(stringMessage);
		String msg = jsonObject.toString();
		System.out.println(msg);
		
		String reference="{\"class\":\"de.uniks.networkparser.test.model.StringMessage\",\"id\":\"J1.S1\",\"prop\":{\"value\":\"C:\\TEST\\MY\\WORLD.TXT\"}}";

		Assert.assertEquals(reference, msg);
		
		JsonIdMap mapReverse=new JsonIdMap();
		mapReverse.withCreator(new StringMessageCreator());
		
		
		StringMessage test = (StringMessage) mapReverse.decode(jsonObject.toString());
		
		Assert.assertEquals("C:\\TEST\\MY\\WORLD.TXT", test.getValue());
	}
	
	
	@Test
	public void testJsonArrayCount() {
		SortedMsg parent=new SortedMsg();
		parent.setNumber(1);
		SortedMsg child=new SortedMsg();
		child.setNumber(2);
		
		parent.setChild(child);
		
		
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new SortedMsgCreator());
		HashSet<Object> items=new HashSet<Object>();
		items.add(parent);
		items.add(child);
		assertEquals(2, map.toJsonArray(items, new Filter().withFull(true)).size());
	}

	@Test
	public void testFullList() {
		Apple apple = new Apple();
		JsonIdMap map = new JsonIdMap();
		map.withCreator(new AppleCreator());

		// ARRAY
		Assert.assertEquals("[{\"id\":\"J1.A1\",\"class\":\"de.uniks.networkparser.test.model.Apple\",\"prop\":{\"value\":0,\"x\":0,\"y\":0,\"owner\":null}}]", map.toJsonArray(apple, new Filter().withFull(true)).toString());

		// OBJECT
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"J1.A1\",\"prop\":{\"value\":0,\"x\":0,\"y\":0,\"owner\":null}}", map.toJsonObject(apple, new Filter().withFull(true)).toString());

	}
	
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

	@Test
	public void testServerJson(){
		String json = "{\"@ts\":\"1368185625179\",\"@src\":\"Barbarian@2b40c3b9\",\"@prop\":\"position\",\"@nv\":\"42\"}";
		
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new BarbarianCreator());
		map.withCreator(new GameCreator());
		map.withGrammar(new EMFGrammar());
		
		Barbarian barbar = (Barbarian) map.decode(new JsonObject().withValue(json));
		
		Assert.assertNotNull(barbar);
		Assert.assertEquals(barbar.getPosition(), 42);
		
		Assert.assertNull(barbar.getGame());
		json = "{\"@ts\":\"1368185625179\",\"@src\":\"Barbarian@2b40c3b9\",\"@prop\":\"game\",\"@nv\":\"Game@55a92d3a\"}";
		
		map.decode(new JsonObject().withValue(json));
		
		Assert.assertNotNull(barbar.getGame());
	}
	
	@Test
	public void testDuplicate() {
		String json = "{number=23, Number=42}";
		
		JsonObject item = new JsonObject().withValue(json);
		// Duplicate allow
		Assert.assertEquals(item.get("Number"), 42);
		Assert.assertEquals(item.get("number"), 23);
		

		// Dont allow Duplicate
		JsonObject item2 = new JsonObject().withAllowDuplicate(false);
		item2.withValue(json);
		Assert.assertEquals(item2.get("Number"), 42);
		Assert.assertEquals(item2.get("number"), 42);
	}
}
