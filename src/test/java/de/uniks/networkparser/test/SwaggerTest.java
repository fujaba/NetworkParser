package de.uniks.networkparser.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.uniks.networkparser.ext.http.Swagger;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.json.JsonObject;

public class SwaggerTest {

	@Test
	public void testJson() {
		FileBuffer buffer = new FileBuffer().withFile("C:\\Arbeit\\wrk_spc\\NetworkParser\\IoConfigApi.json");
		JsonObject json = new JsonObject().withValue(buffer);
		Swagger swagger = new Swagger().withValue(json);
		
		System.out.println(json);
		
		assertNotNull(swagger);
	}
}
