package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.json.JsonObject;

public class TestJsonDecoder {

	@Test
	public void testDecode() {
		CharacterBuffer buffer = FileBuffer.readFile("C:\\temp\\Confnet\\20170920_122234_history.json");
		JsonObject json = new JsonObject().withValue(buffer);
		System.out.println(json);
	}
	
}
