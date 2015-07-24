package de.uniks.networkparser.test;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.House;

public class SimpleJsonTest {
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
				System.out.println(source);
				return false;
			}
		});
		
		JsonObject json = map.encode(house);
		String string=json.toString();
		
		JsonIdMap decodeMap=new JsonIdMap().withCreator(new House());
		House newHouse = (House) decodeMap.decode(string);

		house.setFloor(42);
		Assert.assertEquals(4, newHouse.getFloor());
		Assert.assertEquals("University", newHouse.getName());
	}
}
