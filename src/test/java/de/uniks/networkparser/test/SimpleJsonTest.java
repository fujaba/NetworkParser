package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.logic.SimpleMapEvent;
import de.uniks.networkparser.test.model.House;
import de.uniks.networkparser.test.model.util.HouseCreator;

public class SimpleJsonTest {
	private String updateMessage;
	@Test
	public void testSimple() {
		House house=new House();
		house.setFloor(4);
		house.setName("University");
		IdMap map=new IdMap().with(new HouseCreator());
		map.with(new UpdateListener() {
			@Override
			public boolean update(PropertyChangeEvent event) {
				SimpleMapEvent simpleEvent = (SimpleMapEvent) event;
				
				updateMessage = simpleEvent.getEntity().toString();
				Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.House\",\"id\":\"J1.H1\",\"rem\":{\"floor\":4},\"upd\":{\"floor\":42}}", updateMessage.toString());
				return false;
			}
		});

		JsonObject json = map.toJsonObject(house);
		String string=json.toString();

		IdMap decodeMap=new IdMap().with(new HouseCreator());
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
