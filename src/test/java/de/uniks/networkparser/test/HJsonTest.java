package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

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
		Assert.assertEquals(json.get("id"), 42);
		Assert.assertEquals(json.size(), 1);
		Assert.assertEquals("{\"id\":42}", json.toString());
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
		Assert.assertEquals(json.get("id"), 42);
		Assert.assertEquals(json.get("prename").toString(), "Albert");
		Assert.assertEquals(json.get("lastname"), "Zuendorf");
		Assert.assertEquals(json.size(), 3);
		Assert.assertEquals("{\"id\":42,\"prename\":Albert,\"lastname\":\"Zuendorf\"}", json.toString());
	}
}
