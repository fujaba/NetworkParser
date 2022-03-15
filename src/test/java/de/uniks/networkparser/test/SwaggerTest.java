package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.ext.io.FileBuffer;

public class SwaggerTest {

	@Test
	public void testJson() {
		FileBuffer buffer = new FileBuffer().withFile("Api.json");
//		JsonObject json = new JsonObject().withValue(buffer);
//		Swagger swagger = new Swagger().withValue(json);
		
		assertNotNull(buffer);
	}
}
