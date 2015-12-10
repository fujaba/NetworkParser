package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.House;

public class SimpleJsonTest {
	private String updateMessage;
	@Test
	public void testSimple() {
		House house=new House();
		house.setFloor(4);
		house.setName("University");
		JsonIdMap map=new JsonIdMap().withCreator(new House());
		map.withUpdateListenerSend(new UpdateListener() {
			

			@Override
			public boolean update(String typ, BaseItem source, Object target, String property, Object oldValue,
					Object newValue) {
				updateMessage = source.toString();
				Assert.assertEquals("{\"id\":\"J1.H1\",\"class\":\"de.uniks.networkparser.test.model.House\",\"rem\":{\"floor\":4},\"upd\":{\"floor\":42}}", source.toString());
				return false;
			}
		});
		
		JsonObject json = map.encode(house);
		String string=json.toString();
		
		JsonIdMap decodeMap=new JsonIdMap().withCreator(new House());
		House newHouse = (House) decodeMap.decode(string);

		// Old Model
		Assert.assertEquals(4, newHouse.getFloor());
		Assert.assertEquals("University", newHouse.getName());
		
		
		// Update old Model
		house.setFloor(42);
			
		decodeMap.decode(updateMessage);
		
		Assert.assertEquals(42, newHouse.getFloor());
		
		
		
	}
}
