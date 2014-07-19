package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.xml.XMLEntity;

public class CrossCompilerTest {
	
	@Test
	public void crossCompileXMLToJson(){
		String xml="<de.uni.kassel.peermessage.model.ChatMessage sender=\"Stefan\" value=\"Test\">Meine Nachricht</de.uni.kassel.peermessage.model.ChatMessage>";
		JsonObject testjson=new JsonObject().withEntity(new XMLEntity().withValue(xml));
		String json="{\r\n  \"class\":\"de.uni.kassel.peermessage.model.ChatMessage\",\r\n  \"value\":\"Meine Nachricht\",\r\n  \"prop\":{\r\n    \"sender\":\"Stefan\",\r\n    \"value\":\"Test\"\r\n  }\r\n}";
		assertEquals(json, testjson.toString(2));
	}
	@Test
	public void crossCompileXMLToJsonSorted(){
		String xml="<de.uni.kassel.peermessage.model.SortedMsg number=\"1\"><de.uni.kassel.peermessage.model.SortedMsg number=\"2\"/></de.uni.kassel.peermessage.model.SortedMsg>";
		JsonObject testjson=new JsonObject().withEntity(new XMLEntity().withValue(xml));
		String json="{\"class\":\"de.uni.kassel.peermessage.model.SortedMsg\",\"prop\":{\"number\":\"1\",\"de.uni.kassel.peermessage.model.SortedMsg\":{\"class\":\"de.uni.kassel.peermessage.model.SortedMsg\",\"prop\":{\"number\":\"2\"}}}}";

		assertEquals(json, testjson.toString());
	}
}
