package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.FullMessage;
import de.uniks.networkparser.test.model.Location;
import de.uniks.networkparser.test.model.creator.FullMessageCreator;
import de.uniks.networkparser.test.model.creator.LocationCreator;

public class JsonFilterTest {
	@Test
	public void testJsonFilter(){
//FIXME		JsonFilter filter=new JsonFilter(42, "Stefan", "Albert", "Flo");
//		JsonIdMap map=new JsonIdMap();
//		map.withCreator(new JsonFilterCreator());
//		JsonObject jsonObject = map.toJsonObject(filter);
//		assertNotNull(jsonObject);
//		String string = jsonObject.toString();
//		JsonObject newItem = new JsonObject(string);
//		
//		JsonIdMap mapB=new JsonIdMap();
//		mapB.withCreator(new JsonFilterCreator());
//		JsonFilter jsonFilterB = (JsonFilter) mapB.readJson(newItem);
//		
//		assertEquals(3, jsonFilterB.getItems().length);
//		System.out.println(jsonObject.toString(2));
	}
	
	@Test
	public void testJsonFilterRegard(){
		FullMessage fullMessage = new FullMessage();
		fullMessage.setText("Hallo Welt");
		
		fullMessage.setLocation(new Location(1,2));
		JsonIdMap map=new JsonIdMap();
		map.withCreator(new FullMessageCreator());
		map.withCreator(new LocationCreator());
		
		// Pre
		assertEquals("Hallo Welt", fullMessage.getText());
		assertEquals("1:2", fullMessage.getLocation().toStringShort());

		assertEquals("{\"class\":\"de.uniks.networkparser.test.model.FullMessage\",\"id\":\"J1.F1\",\"prop\":{\"txt\":\"Hallo Welt\",\"location\":{\"class\":\"de.uniks.networkparser.test.model.Location\",\"id\":\"J1.L2\",\"prop\":{\"x\":1,\"y\":2}}}}", map.toJsonObject(fullMessage).toString());
			
		// Post
		assertEquals("Hallo Welt", fullMessage.getText());
	}
}
