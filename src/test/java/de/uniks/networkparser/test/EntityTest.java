package de.uniks.networkparser.test;

import static org.junit.Assert.*;
import java.util.HashMap;
import org.junit.Test;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.XMLEntity;

public class EntityTest {
	@Test
	public void crossCompileXMLToJson(){
		String xml="<de.uni.kassel.peermessage.model.ChatMessage sender=\"Stefan\" value=\"Test\">Meine Nachricht</de.uni.kassel.peermessage.model.ChatMessage>";
		JsonObject testjson= new JsonObject().withEntity(new XMLEntity().withValue(xml));
		String json="{\r\n  \"class\":\"de.uni.kassel.peermessage.model.ChatMessage\",\r\n  \"value\":\"Meine Nachricht\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan\",\r\n    \"value\":\"Test\"\r\n  }\r\n}";
		assertEquals(json, testjson.toString(2));
	}
	@Test
	public void crossCompileXMLToJsonSorted(){
		String xml="<de.uni.kassel.peermessage.model.SortedMsg number=\"1\"><de.uni.kassel.peermessage.model.SortedMsg number=\"2\"/></de.uni.kassel.peermessage.model.SortedMsg>";
		JsonObject testjson= new JsonObject().withEntity(new XMLEntity().withValue(xml));
		String json="{\"class\":\"de.uni.kassel.peermessage.model.SortedMsg\",\"prop\":{\"number\":\"1\",\"de.uni.kassel.peermessage.model.SortedMsg\":{\"class\":\"de.uni.kassel.peermessage.model.SortedMsg\",\"prop\":{\"number\":\"2\"}}}}";

		assertEquals(json, testjson.toString());
	}

	@Test
	public void testEntity(){
		JsonObject entity= new JsonObject().withValue("id", "23");
		JsonObject child = new JsonObject().withValue("id", "42");
		entity.put("child", child);
		JsonArray list = new JsonArray();
		list.add(new JsonObject().withValue("id", "0"));
		list.add(new JsonObject().withValue("id", "1"));
		list.add(new JsonObject().withValue("id", "2"));
		child.put("list", list);

		assertEquals("23",entity.getValue("id"));
		assertEquals("42",entity.getValue("child.id"));
		assertEquals("0",entity.getValue("child.list[0].id"));
		assertEquals("1",entity.getValue("child.list[1].id"));
		assertEquals("2",entity.getValue("child.list[2].id"));
		assertEquals("2",entity.getValue("child.list[L].id"));
		assertEquals("{\"id\":\"2\"}",entity.getValue("child.list[L]").toString());
	}

	@Test
	public void testClone(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("id", "23");
		map.put("init", "true");
		map.put("destroy", "false");
		map.put("int", "23");
		map.put("double", "23.5");
		JsonObject cloneEntity = (JsonObject) new JsonObject().withMap(map);
		assertEquals("Must be 23", "23", cloneEntity.getString("id"));
		assertTrue("Must be True", cloneEntity.getBoolean("init"));
		assertFalse("Must be False", cloneEntity.getBoolean("destroy"));

		assertEquals("Must be 23", 23, cloneEntity.getInt("int"));
		assertEquals("Must be 23.5", 23.5, cloneEntity.getDouble("double"), 0.05);
		cloneEntity.increment("int");
		assertEquals("Must be 24", 24, cloneEntity.getInt("int"));
	}

}
