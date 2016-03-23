package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;

public class GLocationJsonTest extends IOClasses{
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
}
