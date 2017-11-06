package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.beans.PropertyChangeEvent;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Deep;
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.TextDiff;
import de.uniks.networkparser.UpdateAccumulate;
import de.uniks.networkparser.ext.PropertyChangeEventWrapper;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.Equals;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.Barbarian;
import de.uniks.networkparser.test.model.Change;
import de.uniks.networkparser.test.model.ChatMessage;
import de.uniks.networkparser.test.model.FullAssocs;
import de.uniks.networkparser.test.model.FullMessage;
import de.uniks.networkparser.test.model.House;
import de.uniks.networkparser.test.model.Item;
import de.uniks.networkparser.test.model.ListEntity;
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
import de.uniks.networkparser.test.model.util.HouseCreator;
import de.uniks.networkparser.test.model.util.ItemCreator;
import de.uniks.networkparser.test.model.util.LocationCreator;
import de.uniks.networkparser.test.model.util.MapEntryElementCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StringMessageCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;
import de.uniks.networkparser.xml.EMFJsonGrammar;

public class JsonTest extends IOClasses {
	@Test
	public void testJsonEquals() {
		JsonObject jsonObject = new JsonObject();
		jsonObject.withKeyValue("msg", "init");
		
		Assert.assertFalse(jsonObject.equals(Equals.create("msg", "game")));
		Assert.assertTrue(jsonObject.equals(Equals.create("msg", "init")));
	}
	
	
	@Test
	public void testJSONDuplicate() {
		JsonObject json = new JsonObject().withValue("{\"type\":\"new\", \"type\":\"old\"}");
		Assert.assertEquals(json.get("type"), "old");
		Assert.assertEquals(1, json.size());
	}
	
	@Test
	public void testJSONDelete() {
		JsonObject json = new JsonObject().withValue("{\"id\":42, \"dice\":3}");
		Assert.assertEquals(json.getValue("id"), 42);
		Assert.assertEquals(json.getValue("dice"), 3);
		json.without("id");
		String key = json.getKeyByIndex(0);
		Assert.assertEquals(json.getValue(key), 3);
	}
	
	@Test
	public void testJSONEmptyKey() {
		JsonObject json = new JsonObject().withValue("{\"id\":42, }");
		Assert.assertEquals(json.get("id"), 42);
		Assert.assertEquals(1, json.size());
	}
	
	@Test
	public void testJSONPath() {
		JsonObject json = new JsonObject().withValue("{\"id\":\"D:\\\\Roellmedia\\\\\"\n\r}");
		Assert.assertEquals("D:\\Roellmedia\\", json.get("id"));
	}

	@Test
	public void testJSONFunction() {
		String functionJson = "{body:\"public main() {\r\n\tconsole.log('Hallo Welt');\n\t}\"}";

		JsonObject jsonObject = new JsonObject();
		new JsonTokener().withBuffer(functionJson).parseToEntity(jsonObject);
		Assert.assertEquals(
				"{\"body\":\"public main() {\\u000d\\u000a\\u0009console.log(\'Hallo Welt\');\\u000a\\u0009}\"}",
				jsonObject.toString(2));
	}

	@Test
	public void testJSONInsert() {
		JsonObject item = new JsonObject().withValue("id", "K444", "value", "42");
		item.add(0, "class", "JsonObject");
		Assert.assertEquals("{\"class\":\"JsonObject\",\"id\":\"K444\",\"value\":\"42\"}", item.toString());
	}

	@Test
	public void testJSONString() {
		JsonObject item = new JsonObject().withValue("{name:\"\\\"Stefan\\\"\", value:42}");
		item.add(0, "class", "JsonObject");
		Assert.assertEquals("{\"class\":\"JsonObject\",\"name\":\"\\\"Stefan\\\"\",\"value\":42}", item.toString());
	}

	@Test
	public void testJSONList() {
		JsonObject item = new JsonObject();
		assertEquals(item.toString(), "{}");
		item.addToList("id", 23);
		assertEquals(item.toString(), "{\"id\":23}");
		item.addToList("id", 42);
		assertEquals("{\"id\":[23,42]}", item.toString());
	}

	@Test
	public void testJSONPrimitive() {
		JsonObject item = new JsonObject();
		item.put("idlong", 23L);
		item.put("idint", 23);
		item.put("iddouble", 42.1);
		assertEquals(23L, item.get("idlong"));
		assertEquals(23, item.get("idint"));
		assertEquals("Wrong", 42.1d, item.getDouble("iddouble"), 0.00001);
	}

	@Test
	public void testJSONMap() {
		IdMap map = new IdMap();
		map.with(new FullAssocsCreator());
		map.withTimeStamp(1);
		FullAssocs assoc = new FullAssocs();
		assoc.addPassword("Stefan", "42");
		assoc.addPassword("Flo", "23");
		assoc.addAssoc(assoc);
		JsonObject text = map.toJsonObject(assoc);
		String master = "{\"class\":\"de.uniks.networkparser.test.model.FullAssocs\",\"id\":\"F1\",\"prop\":{\"passwords\":[{\"class\":\"de.uniks.networkparser.list.ObjectMapEntry\",\"key\":\"Flo\",\"value\":\"23\"},{\"class\":\"de.uniks.networkparser.list.ObjectMapEntry\",\"key\":\"Stefan\",\"value\":\"42\"}],\"fullmap\":[{\"class\":\"de.uniks.networkparser.list.ObjectMapEntry\",\"key\":{\"class\":\"de.uniks.networkparser.test.model.FullAssocs\",\"id\":\"F1\"},\"value\":{\"class\":\"de.uniks.networkparser.test.model.FullAssocs\",\"id\":\"F1\"}}]}}";
		assertEquals(master, text.toString());

		FullAssocs newAssoc = (FullAssocs) map.decode(new JsonObject().withValue(text.toString()));
		assertEquals("Passwords", 2, newAssoc.getPasswords().size());
	}

	@Test
	public void testSimpleMap() {
		IdMap map = new IdMap();
		map.with(new FullAssocsCreator());
		FullAssocs uni = new FullAssocs();
		uni.addPassword("Stefan", "test");

		JsonObject jsonObject = map.toJsonObject(uni);

		IdMap mapReserve = new IdMap();
		mapReserve.with(new FullAssocsCreator());
		mapReserve.decode(jsonObject.toString());
	}

	@Test
	public void testSimpleJson() {
		IdMap map = new IdMap();
		map.with(new ChangeCreator());
		map.withTimeStamp(1);
		Change change = new Change();

		JsonObject data = new JsonObject();
		data.put("id", "name");
		data.put("value", "42");

		change.setValue(data);
		JsonObject jsonObject = map.toJsonObject(change);

		IdMap mapReserve = new IdMap();
		mapReserve.with(new ChangeCreator());
		Change item = (Change) mapReserve.decode(jsonObject.toString());
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Change\",\"id\":\"C1\",\"prop\":{\"value\":{\"id\":\"name\",\"value\":\"42\"}}}", jsonObject.toString());
		assertEquals(item.getValue().getString("value"), "42");
	}

	@Test
	public void testSimpleList() {
		FullAssocs fullAssocs = new FullAssocs();
		fullAssocs.addPerson("Kassem");
		fullAssocs.addPerson("Stefan");

		IdMap map = new IdMap();
		map.with(new FullAssocsCreator());
		map.withTimeStamp(1);

		JsonObject jsonObject = map.toJsonObject(fullAssocs);
		String data = jsonObject.toString(2);
		Assert.assertEquals(141, data.length());

		FullAssocs newfullAssocs = (FullAssocs) map.decode(data);
		assertNotNull(newfullAssocs);
	}

	@Test
	public void testJson() {
		JsonObject test = new JsonObject();
		JsonObject child = new JsonObject();
		child.put("id", "test");
		child.put("value", 2);
		child.put("child", new JsonObject().withValue("id", "42"));
		test.put("child", child);
		assertEquals("{\"child\":{\"id\":\"test\",\"value\":2,\"child\":{\"id\":\"42\"}}}", test.toString());
		test.setValueItem("child.value", 42);
		assertEquals("{\"child\":{\"id\":\"test\",\"value\":42,\"child\":{\"id\":\"42\"}}}", test.toString());
	}

	@Test
	public void testJsonArray() {
		JsonArray first = new JsonArray();
		JsonArray child = new JsonArray();
		child.add(new JsonArray());
		child.add(new JsonArray().withValue(new JsonObject().withValue("id", "42")));
		first.add(child);
		assertEquals("[[[],[{\"id\":\"42\"}]]]", first.toString());
	}

	@Test
	public void testStringJson() {
		String jsonText = "{\"id\":\"10.1.1.126;c10\",\"class\":\"de.uni.kassel.peermessage.model.Change\",\"prop\":{\"value\":\"42\"}}";
		Change change = new Change();
		change.setKey(new Long(42));
		change.setValue(new JsonObject().withValue(jsonText));
		change.setList(new JsonArray().withValue(new JsonObject().withValue(jsonText)));
		IdMap map = new IdMap();
		map.withSession(";");
		map.with(new ChangeCreator());
		JsonObject json = map.toJsonObject(change);
		Change change2 = (Change) map.decode(json);
		assertNotNull(change2);
		assertEquals(new Long(42), change2.getKey());
		assertEquals(jsonText, change2.getValue().toString());
		assertEquals(change.getList().size(), change2.getList().size());
	}

	@Test
	public void testJsonArraySplit() {
		String text = "[\"Hallo Welt\",{\"id\":\"42\"}]";
		JsonArray jsonArray = new JsonArray().withValue(text);

		assertEquals(2, jsonArray.size());
		assertEquals("Hallo Welt", jsonArray.get(0));
	}

	@Test
	public void testSortJson() {
		String text = "{\"id\":\"42\", \"class\":\"de.uniks.networkparser.test.model.SortedMsg\" \"props\":{\"key:\":\"\", \"value\":{}}}";
		IdMap map = new IdMap();
		map.with(new SortedMsgCreator());
		Object item = map.decode(text);
		Assert.assertNotNull(item);
	}

	@Test
	public void testJsonParsing() {
		// Modell
		Change change = new Change();
		change.setKey(new Long(42));

		// Map
		IdMap map = new IdMap();
		map.withTimeStamp(1);
		map.with(new ChangeCreator());

		// Serialisation
		JsonObject jsonObject = map.toJsonObject(change);
		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Change\",\"id\":\"C1\",\"prop\":{\"key\":42}}",
				jsonObject.toString());
	}

	@Test
	public void testJsonIdMapNoPackage() {
		String className = "SimpleClass";
		char firstChar = className.charAt(className.lastIndexOf(".") + 1);
		assertEquals('S', firstChar);
	}

	@Test
	public void createJson() {
		JsonObject json = new JsonObject().withValue("{id:42}");
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
		assertEquals(42, ((JsonObject) array.get(0)).get("id"));
	}

	@Test
	public void testSortedMap() {
		IdMap map = new IdMap();
		map.with(new SortedMsgCreator());
		map.withTimeStamp(1);
		SortedMsg first = new SortedMsg();
		first.withNumber(1);

		SortedMsg third = new SortedMsg();
		third.withNumber(3);
		first.setChild(third);

		SortedMsg second = new SortedMsg();
		second.withNumber(2);

		third.setChild(second);
		JsonArray jsonArray = new JsonArray();
		jsonArray.withComparator(JsonTokener.PROPS + "." + SortedMsg.PROPERTY_ID);
		map.toJsonArray(first, jsonArray, null);
		assertEquals(3, jsonArray.size());

		// [
		// {"id":"J1.S3","class":"de.uni.kassel.peermessage.model.SortedMsg","prop":{"id":2}},
		// {"id":"J1.S2","class":"de.uni.kassel.peermessage.model.SortedMsg","prop":{"id":3,"child":"J1.S3"}},
		// {"id":"J1.S1","class":"de.uni.kassel.peermessage.model.SortedMsg","prop":{"id":1,"child":"J1.S2"}}
		// ]

		String reference = "["
				+ "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S1\",\"prop\":{\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S2\"}}},"
				+ "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S3\",\"prop\":{\"number\":2,\"parent\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S2\"}}},"
				+ "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S2\",\"prop\":{\"number\":3,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S3\"},\"parent\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S1\"}}}"
				+ "]";

		assertEquals(reference, jsonArray.toString());
	}

	/**
	 * <pre>
	 *		   0..1	 rooms0..n
	 * University ------------------------- Room
	 *		   university		&gt;	   rooms
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

		IdMap map = UniversityCreator.createIdMap("s1");
		map.withTimeStamp(1);
		JsonArray jsonArray = map.toJsonArray(kassel, new Filter().withConvertable(Deep.create(1)));
		String jsonString = jsonArray.toString(2);
		assertEquals(2664, jsonString.length());

		jsonArray = map.toJsonArray(kassel, new Filter().withConvertable(new Deep().withDepth(0)));

		jsonString = jsonArray.toString(2);
		assertEquals(637, jsonString.length());

		jsonArray = map.toJsonArray(kassel);

		jsonString = jsonArray.toString(2);

		assertEquals(2664, jsonString.length());

		IdMap readMap = UniversityCreator.createIdMap("s2");

		University clone = (University) readMap.decode(jsonArray);

		Assert.assertEquals("wrong number of rooms", 2, clone.sizeOfRooms());
	}

	@Test
	public void testChatMessage() {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");

		IdMap jsonMap = new IdMap();
		jsonMap.with(new ChatMessageCreator());
		jsonMap.withTimeStamp(1);

		String reference = "{\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"id\":\"C1\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"txt\":\"Dies ist eine Testnachricht\"\r\n  }\r\n}";
		JsonObject actual = jsonMap.toJsonObject(chatMessage);
		assertEquals("WERT Vergleichen", reference, actual.toString(2));
		assertEquals(reference.length(), actual.toString(2).length());

		String msg = actual.toString(2);

		IdMap map = new IdMap();
		map.with(new ChatMessageCreator());

		ChatMessage newChatMsg = (ChatMessage) map.decode(new JsonObject().withValue(msg));
		Assert.assertNotNull(newChatMsg);
	}

	@Test
	public void testFull() {
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");
		IdMap jsonMap = new IdMap();
		jsonMap.withTimeStamp(1);
		
		jsonMap.with(new ChatMessageCreator());

		String reference = "{\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"id\":\"C1\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"txt\":\"Dies ist eine Testnachricht\"\r\n  }\r\n}";
		JsonObject actual = jsonMap.toJsonObject(chatMessage);
		assertEquals("WERT Vergleichen", reference, actual.toString(2));

		reference = "{\r\n  \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n  \"id\":\"C1\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan Lindel\",\r\n    \"time\":null,\r\n    \"txt\":\"Dies ist eine Testnachricht\",\r\n    \"count\":0,\r\n    \"activ\":false\r\n  }\r\n}";
		actual = jsonMap.toJsonObject(chatMessage, Filter.createFull());
		assertEquals("WERT Vergleichen", reference, actual.toString(2));

		// Array
		reference = "[\r\n  {\r\n    \"class\":\"de.uniks.networkparser.test.model.ChatMessage\",\r\n    \"id\":\"C1\",\r\n    \"prop\":{\r\n      \"sender\":\"Stefan Lindel\",\r\n      \"time\":null,\r\n      \"txt\":\"Dies ist eine Testnachricht\",\r\n      \"count\":0,\r\n      \"activ\":false\r\n    }\r\n  }\r\n]";
		JsonArray actualArray = jsonMap.toJsonArray(chatMessage, Filter.createFull());
		assertEquals("WERT Vergleichen", reference, actualArray.toString(2));
	}

	@Test
	public void testFullMessage() {
		Date date = new Date();
		date.setTime(1330538995929L);
		FullMessage msg = new FullMessage(date, 42, "Hallo Welt");
		msg.setLocation(new Location(42, 23));
		IdMap map = new IdMap();
		map.withTimeStamp(1);
		// map.setTypSave(true);
		map.with(new FullMessageCreator());
		map.with(new LocationCreator());

		String jsonString = map.toJsonObject(msg).toString();
		String textString = "{\"class\":\"de.uniks.networkparser.test.model.FullMessage\",\"id\":\"F1\",\"prop\":{"
				+ "\"txt\":\"Hallo Welt\"," + "\"number\":42,"
				+ "\"date\":{\"class\":\"java.util.Date\",\"value\":1330538995929},"
				+ "\"location\":{\"class\":\"de.uniks.networkparser.test.model.Location\",\"id\":\"L2\",\"prop\":{\"x\":42,\"y\":23}}"
				+ "}}";

		assertEquals(textString, jsonString);
		FullMessage mapItem = (FullMessage) map.decode(new JsonObject().withValue(jsonString));
		assertEquals(42, mapItem.getValue());
		assertEquals("Hallo Welt", mapItem.getText());
		assertEquals(1330538995929L, mapItem.getDate().getTime());
	}

	@Test
	public void testRest() {
		IdMap map = new IdMap();
		map.with(new FullMessageCreator());
		map.with(new LocationCreator());
		map.withTimeStamp(1);
		FullMessage msg = new FullMessage(42, "Hallo Welt");
		msg.setLocation(new Location(42, 23));
//FIXME		map.with(new RestCounter("http://myname.org/rest/"));
		JsonObject json = map.toJsonObject(msg, new Filter().withConvertable(Deep.create(0)));
		Assert.assertEquals(
				"{\"class\":\"de.uniks.networkparser.test.model.FullMessage\",\"id\":\"F1\",\"prop\":{\"txt\":\"Hallo Welt\",\"number\":42,\"location\":{\"class\":\"de.uniks.networkparser.test.model.Location\",\"id\":\"L2\"}}}",
				json.toString());
	}

	@Test
	public void testMapTest() {
		IdMap map = new IdMap();
		map.with(new MapEntryElementCreator());
		MapEntryElement item = new MapEntryElement();

		HashMap<String, String> passwords = new HashMap<String, String>();
		passwords.put("Flo", "23");
		passwords.put("Stefan", "42");

		item.addToValue("passwords", passwords);
		JsonObject json = map.toJsonObject(item);
		String data = json.toString();
		JsonObject jsonObject = new JsonObject().withValue(data);

		// decode
		MapEntryElement itemNew = (MapEntryElement) map.decode(jsonObject);
		assertNotNull(itemNew.getValue());
		assertEquals(itemNew.getValue().size(), 1);
		Object passNew = itemNew.getValue().get("passwords");
		if (passNew instanceof Map<?, ?>) {
			assertEquals(2, ((Map<?, ?>) passNew).size());
		}
	}

	@Test
	public void calculation042() {
		SortedMsg parent = new SortedMsg();
		parent.withNumber(1);
		SortedMsg child = new SortedMsg();
		child.withNumber(2);

		parent.setChild(child);

		IdMap map = new IdMap();
		map.with(new SortedMsgCreator()).withTimeStamp(1);
		String ref = "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S1\",\"prop\":{\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S2\",\"prop\":{\"number\":2,\"parent\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S1\"}}}}}";
		assertEquals(ref, map.toJsonObject(parent).toString());

		ref = "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"number\":2,\"parent\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S1\"}}}";
		Filter filter = new Filter().withIdFilter(BooleanCondition.create(false));
		assertEquals(ref, map.toJsonObject(parent, filter).toString());
	}

	@Test
	public void testEscapeText() {
		IdMap map = new IdMap();
		map.with(new StringMessageCreator());
		StringMessage stringMessage = new StringMessage("C:\\TEST\\MY\\WORLD.TXT");

		JsonObject jsonObject = map.toJsonObject(stringMessage);
		String msg = jsonObject.toString();

		String reference = "{\"class\":\"de.uniks.networkparser.test.model.StringMessage\",\"id\":\"0\",\"prop\":{\"value\":\"C:\\\\TEST\\\\MY\\\\WORLD.TXT\"}}";

		Assert.assertEquals(reference, msg);

		IdMap mapReverse = new IdMap();
		mapReverse.with(new StringMessageCreator());

		StringMessage test = (StringMessage) mapReverse.decode(jsonObject.toString());

		Assert.assertEquals("C:\\TEST\\MY\\WORLD.TXT", test.getValue());
	}

	@Test
	public void testJsonArrayCount() {
		SortedMsg parent = new SortedMsg();
		parent.withNumber(1);
		SortedMsg child = new SortedMsg();
		child.withNumber(2);

		parent.setChild(child);

		IdMap map = new IdMap();
		map.with(new SortedMsgCreator());
		HashSet<Object> items = new HashSet<Object>();
		items.add(parent);
		items.add(child);
		assertEquals(2, map.toJsonArray(items, Filter.createFull()).size());
	}

	@Test
	public void testFullList() {
		Apple apple = new Apple();
		IdMap map = new IdMap();
		map.with(new AppleCreator());
		map.withTimeStamp(1);

		// ARRAY
		Assert.assertEquals(
				"[{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"A1\",\"prop\":{\"pass\":null,\"x\":0,\"y\":0,\"owner\":null}}]",
				map.toJsonArray(apple, Filter.createFull()).toString());

		// OBJECT
		Assert.assertEquals(
				"{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"A1\",\"prop\":{\"pass\":null,\"x\":0,\"y\":0,\"owner\":null}}",
				map.toJsonObject(apple, Filter.createFull()).toString());

	}

	@Test
	public void testSimple() {
		IdMap encodeMap = new IdMap().with(new EMFJsonGrammar()).withTimeStamp(1);

		encodeMap.with(new PersonCreator());
		Person person = new Person().withName("Albert").withBalance(42);
		String shortString = encodeMap.toJsonObject(person, Filter.createSimple()).toString();
		Assert.assertEquals(49, shortString.length());

		IdMap decodeMap = new IdMap().with(new EMFJsonGrammar());
		decodeMap.with(new PersonCreator());
		Person item = (Person) decodeMap.decode(new JsonObject().withValue(shortString));
		Assert.assertEquals("Albert", item.getName());
		Assert.assertEquals(42, item.getBalance(), 0.000001);
	}

	@Test
	public void testServerJson() {
		String json = "{\"@ts\":\"1368185625179\",\"@src\":\"Barbarian@2b40c3b9\",\"@prop\":\"position\",\"@nv\":\"42\"}";

		IdMap map = new IdMap();
		map.with(new BarbarianCreator());
		map.with(new GameCreator());
		map.with(new EMFJsonGrammar());

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
		Assert.assertEquals(42, item.get("Number"));
		Assert.assertEquals(23, item.get("number"));

		// Dont allow Duplicate
		JsonObject item2 = new JsonObject().withCaseSensitive(false);
		item2.withValue(json);
		Assert.assertEquals(42, item2.get("Number"));
		Assert.assertEquals(42, item2.get("number"));

		JsonObject item3 = new JsonObject();

		item3.put("id", "23");
		item3.put("id", "42");
		Assert.assertEquals("{\"id\":\"42\"}", item3.toString());
	}

	@Test
	public void testJSONInJson() {
		JsonObject subsubItem = new JsonObject().withKeyValue("value", "Hallo Welt");
		JsonObject subItem = new JsonObject().withKeyValue("id", subsubItem.toString());
		JsonObject item = new JsonObject().withKeyValue("item", subItem.toString());

		String itemString = item.toString();
		Assert.assertEquals(58, itemString.length());

		JsonObject newItem = new JsonObject().withValue(itemString);
		String newItemString = newItem.getString("item");
		JsonObject newSubItem = new JsonObject().withValue(newItemString);
		Assert.assertEquals(35, newSubItem.toString().length());

		// StringBuffer readFile =
		// readFile("test/StringThatDoesNotUnquote2.txt");
		// String stringValue = "{\"id\":
		// \"zuenFamilyChatServerSpace.R61\",\"prop\": {\"isToManyProperty\":
		// true,";
		// stringValue +="\"changeMsg\":
		// \"{\\\"\"upd\\\"\":{\\\"\"observedObjects\\\"\":{\\\"\"prop\\\"\":{\\\"\"text\\\"\":\\\"\"<script>\\\"u000a
		// var json = {\\\"u000d\\\"u000a
		// \\\"\"typ\\\"\":\\\"\"objectdiagram\\\"\",\\\"u000d\\\"u000a
		// \\\"\"style\\\"\":null\\\"u000d\\\"u000a};</script>\\\"u000a\\\"\",\\\"\"storyboard\\\"\":{\\\"\"class\\\"\":\\\"\"org.sdmlib.storyboards.Storyboard\\\"\",\\\"\"id\\\"\":\\\"\"tester.S2\\\"\"}}}}}\"}}";
		// JsonObject newItemFile= new
		// JsonObject().withValue(readFile.toString());
		// Object object = newItemFile.get("changeMsg");
		// JsonObject withValue = new
		// JsonObject().withValue(readFile.toString());
	}

	@Test
	public void testJsonCompare() {
		JsonObject jsonA = new JsonObject().withValue("{id:42, no:23, list:[1,2], array:[1,2]}");
		JsonObject jsonB = new JsonObject().withValue("{id:42, no:24, list:[1,2], array:[1,3]}");
		JsonObject same = new JsonObject();
		TextDiff diffList = new TextDiff();
		
		Assert.assertFalse(EntityUtil.compareEntity(jsonA, jsonB, diffList, same));
		Assert.assertEquals("{\"id\":42,\"list\":[1,2]}", same.toString());
		Assert.assertEquals("{\"no\":23,\"array\":[2]}", jsonA.toString());
		Assert.assertEquals("{\"no\":24,\"array\":[3]}", jsonB.toString());
	}

	@Test
	public void testLong() {
		JsonObject item = new JsonObject().withValue("value", "1234567890");
		Assert.assertEquals(1234567890, item.getLong("value"));
	}

	@Test
	public void testJsonArrayTest() {
		JsonArray array = new JsonArray();
		array.with(1, 2, 3, 4, 5);
		Assert.assertEquals("[1,2,3,4,5]", array.toString());
		array.with(new JsonArray().with(6, 7));
		array.with(8);
		array.with(new JsonObject().with("id", 42));
		array.with(new JsonObject().with("id", 42, "class", "JsonObject"));

		Assert.assertEquals(
				"[\r\n  1,\r\n  2,\r\n  3,\r\n  4,\r\n  5,\r\n  [\r\n    6,\r\n    7\r\n  ],\r\n  8,\r\n  {\"id\":42},\r\n  {\r\n    \"id\":42,\r\n    \"class\":\"JsonObject\"\r\n  }\r\n]",
				array.toString(2));
	}

	@Test
	public void testSimpleSpaceTest() {
		String content = " \t\r\n{id:22}";
		JsonObject json = new JsonObject().withValue(content);
		Assert.assertEquals(22, json.getInt("id"));

	}

	@Test
	public void testGetStringWithDefault() {
		// See:
		// https://github.com/fujaba/NetworkParser/issues/3
		String content = "{\"foo\": null}";
		JsonObject json = new JsonObject().withValue(content);
		Assert.assertEquals(null, json.getString("foo", null));
		Assert.assertEquals("", json.getString("foo", ""));
		Assert.assertEquals("bar", json.getString("foo", "bar"));
	}

	@Test
	public void testRekursiv() {
		ListEntity root = new ListEntity();
		ListEntity test = new ListEntity();
		ListEntity child = new ListEntity().withOwner(new ListEntity().withOwner(test));
		root.withChildren(new ListEntity(), child);
		root.withChildren(child);
		child.withChildren(root);
		IdMap map = new IdMap();
		map.withTimeStamp(1);
		map.with(new ListEntity());
		JsonObject json = map.toJsonObject(root);
		Assert.assertEquals(1285, json.toString(2).length());

		IdMap mapDecode = new IdMap();
		mapDecode.with(new ListEntity());
		Object rootDecode = mapDecode.decode(json);
		Assert.assertNotNull(rootDecode);
	}

	@Test
	public void testJsonFilterRegard() {
		FullMessage fullMessage = new FullMessage();
		fullMessage.setText("Hallo Welt");

		fullMessage.setLocation(new Location(1, 2));
		IdMap map = new IdMap();
		map.withTimeStamp(1);
		map.with(new FullMessageCreator());
		map.with(new LocationCreator());

		// Pre
		assertEquals("Hallo Welt", fullMessage.getText());
		assertEquals("1:2", fullMessage.getLocation().toStringShort());

		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.FullMessage\",\"id\":\"F1\",\"prop\":{\"txt\":\"Hallo Welt\",\"location\":{\"class\":\"de.uniks.networkparser.test.model.Location\",\"id\":\"L2\",\"prop\":{\"x\":1,\"y\":2}}}}",
				map.toJsonObject(fullMessage).toString());

		// Post
		assertEquals("Hallo Welt", fullMessage.getText());
	}

	@Test
	public void testJsonUpdateTest() {
		House house=new House();
		house.setFloor(4);
		house.setName("University");
		IdMap map=new IdMap().with(new HouseCreator());
		map.withTimeStamp(1);
		
		SimpleList<String> messages= new SimpleList<String>();
		messages.add("{\"class\":\"de.uniks.networkparser.test.model.House\",\"id\":\"H1\",\"prop\":{\"name\":\"University\",\"floor\":4}}");
		messages.add("{\"class\":\"de.uniks.networkparser.test.model.House\",\"id\":\"H1\",\"rem\":{\"floor\":4},\"upd\":{\"floor\":42}}");
		
		map.with(new ObjectCondition() {
			@Override
			public boolean update(Object event) {
				if(event instanceof SimpleEvent == false) {
					return false;
				}
				SimpleEvent simpleEvent = (SimpleEvent) event;
				String testMessage = messages.first();
				
				String updateMessage = simpleEvent.getEntity().toString();
				Assert.assertEquals(testMessage, updateMessage);
				if(messages.size()>1) {
					messages.remove(0);
				}
				return false;
			}
		});

		JsonObject json = map.toJsonObject(house);
		String string=json.toString();

		IdMap decodeMap=new IdMap().with(new HouseCreator());
		House newHouse = (House) decodeMap.decode(string);

		// Old Model
		Assert.assertEquals(4, newHouse.getFloor());
		Assert.assertEquals("University", newHouse.getName());

		// Update old Model
		house.setFloor(42);

		decodeMap.decode(messages.first());

		Assert.assertEquals(42, newHouse.getFloor());
	}

	@Test
	public void testImport(){
		StringBuffer result=readFile("location.json");
		JsonObject item = new JsonObject().withValue(result.toString());
		assertEquals(((JsonArray)item.get("results")).size(), 1);
	}

	@Test
	public void testEmpty(){
		String json="{\n" +
		   "\t\"results\" : [],\n" +
		   "\t\"status\" : \"ZERO_RESULTS\"\n" +
		"}";

		JsonObject item = new JsonObject().withValue(json);
		assertNotNull(item);
	}

	@Test
	public void testPropertyChange() {
		IdMap map=new IdMap();
		map.with(new PropertyChangeEventWrapper());
		map.with(new PersonCreator());
		map.with(new ItemCreator());

		Person person = new Person();
		Item item = new Item();
		PropertyChangeEvent propertyChange = new PropertyChangeEvent(person, "child", null, item);

		JsonObject encode = map.toJsonObject(propertyChange);

		//Decode
		IdMap decodeMap=new IdMap();
		decodeMap.with(new PropertyChangeEventWrapper());
		decodeMap.with(new PersonCreator());
		decodeMap.with(new ItemCreator());

		PropertyChangeEvent decode = (PropertyChangeEvent) decodeMap.decode(encode.toString());
		Assert.assertEquals(person.getClass(), decode.getSource().getClass());
		Assert.assertEquals("child", decode.getPropertyName());
	}

	@Test
	public void testJSONAccumilate() {
		Person person = new Person();
		IdMap map = new IdMap();
		map.withCreator(new PersonCreator());
		map.withTimeStamp(1);
		map.toJsonObject(person);
		UpdateAccumulate updateAccumulate = new UpdateAccumulate();
		
		map.getMapListener().suspendNotification(updateAccumulate);
		map.withListener(new ObjectCondition() {
			@Override
			public boolean update(Object value) {
				Assert.fail();
				return false;
			}
		});
		
		person.setName("Albert");
		person.setBalance(42);
		map.getMapListener().resetNotification();
		
		System.out.println(updateAccumulate.getChange());
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.Person\",\"id\":\"P1\",\"upd\":{\"name\":\"Albert\",\"balance\":42},\"rem\":{\"balance\":0}}", updateAccumulate.getChange().toString());
	}
	
//	@Test
//	public void testErrorHandler() {
//		JsonObject item = new JsonObject();
//		item.put("port", new SimpleKeyValueList<String, String>());
//		ErrorHandler errorHandler = new ErrorHandler();
//		try {
//			item.getInt("port");
//		}catch (Exception e) {
//			errorHandler.saveException(e);
//			
//			// TODO: handle exception
//		}
//		
//	}
}
