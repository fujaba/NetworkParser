package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.json.JsonObject;

public class HJsonTest {
	@Test
	public void testHJsonComment() {
		CharacterBuffer sb=new CharacterBuffer();
		sb.withLine("{");
		sb.withLine("# First Comment");
		sb.withLine("id:42 // End Comment");
		sb.withLine("// End Comment");
		sb.withLine("/* Multiline");
		sb.withLine("Comment */");
		sb.withLine("}");
		JsonObject json=new JsonObject().withValue(sb.toString());
		assertEquals(json.get("id"), 42);
		assertEquals(json.size(), 1);
		assertEquals("{\"id\":42}", json.toString());
	}
	@Test
	public void testHJsonWithoutComma() {
		CharacterBuffer sb=new CharacterBuffer();
		sb.withLine("{");
		sb.withLine("id:42 // End Comment");
		sb.withLine("prename:Albert // End Comment");
		sb.withLine("lastname:\"Zuendorf\"");
		sb.withLine("}");
		JsonObject json=new JsonObject().withValue(sb.toString());
		assertEquals(json.get("id"), 42);
		assertEquals(json.get("prename").toString(), "Albert");
		assertEquals(json.get("lastname"), "Zuendorf");
		assertEquals(json.size(), 3);
		assertEquals("{\"id\":42,\"prename\":\"Albert\",\"lastname\":\"Zuendorf\"}", json.toString());
	}
}
