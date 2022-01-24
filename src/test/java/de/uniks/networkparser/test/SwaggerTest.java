package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

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
