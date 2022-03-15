package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;

public class JsonPeer2PeerTest implements ObjectCondition {
	private IdMap firstMap;
	private IdMap secondMap;
	private int z;
	private SortedMsg firstRoot;
	protected SortedMsg secondRoot;

	@Test
	public void testModel(){

		firstMap = new IdMap();
		firstMap.with(this);
		firstMap.withTimeStamp(1);

		firstMap.with(new SortedMsgCreator());

		secondMap = new IdMap();
		secondMap.with(new SortedMsgCreator());

		firstRoot = new SortedMsg();
		firstRoot.withNumber(1);

		SortedMsg second= new SortedMsg();
		second.withNumber(2);
		firstRoot.setChild(second);

		firstMap.garbageCollection(firstRoot);

		update(new SimpleEvent(SendableEntityCreator.NEW, firstMap.toJsonObject(firstRoot), firstMap, null, null, null));

		SortedMsg third= new SortedMsg();
		third.withNumber(4);
		third.setParent(second);
		third.withNumber(42);
		second.setChild(null);
	}

	@Override
	public boolean update(Object evt) {
		if(evt instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent simpleEvent = (SimpleEvent) evt;
		if(simpleEvent.isNewEvent() == false && simpleEvent.isIdEvent() == false) {
			return true;
		}

		JsonObject jsonObject = (JsonObject) simpleEvent.getEntity();
		Object result=secondMap.decode(jsonObject);
		if(z==0){
			z++;
			assertEquals(2, secondMap.size());
			secondRoot=(SortedMsg) secondMap.getObject(firstMap.getKey(firstRoot));
		} else if(z==1){
			assertEquals(159, jsonObject.toString().length(), "===== add =====");
			assertEquals(2, secondMap.size());
			z++;
		} else if(z==2){
			assertEquals(160, jsonObject.toString().length(), "===== add =====");
			assertEquals(4, secondMap.size());
			z++;
		} else if(z==3){
			assertEquals(159, jsonObject.toString().length(), "===== add =====");
			assertEquals(4, secondMap.size());
			z++;
		} else if(z==4){
			assertEquals(254, jsonObject.toString().length(), "===== add =====");
			assertEquals(4, secondMap.size());
			z++;
		} else if(z==5){
			assertEquals(160, jsonObject.toString().length(), "===== add =====");
			assertEquals(5, secondMap.size());
			z++;
		} else if(z==6){
			assertEquals(242, jsonObject.toString().length(), "===== add =====");
			assertEquals(5, secondMap.size());
			z++;
		} else if(z==7){
			assertEquals(147, jsonObject.toString().length(), "===== add =====");
			assertEquals(5, secondMap.size());
			z++;
		} else if(z==8){
			assertEquals("{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S4\",\"rem\":{\"child\":{\"id\":\"S5\"}}}", jsonObject.toString(), "===== rem =====");
			z++;
			assertEquals(5, secondMap.size());
		} else if(z==9){
			assertEquals("{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S4\",\"rem\":{\"child\":{\"id\":\"S5\"}}}", jsonObject.toString(), "===== rem =====");
//			assertEquals("===== rem =====", "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"S4\",\"rem\":{\"number\":4},\"upd\":{\"number\":42}}", jsonObject.toString());
			z++;
			assertEquals(5, secondMap.size());
		}
		if(z>9){
			assertEquals(376, firstMap.toJsonObject(firstRoot).toString(2).length(), "===== FIRST =====");
			//LAST
			Object secondRoot = secondMap.getObject("J1.S1");
			if(secondRoot != null) {
				assertEquals(385, secondMap.toJsonObject(secondRoot).toString(2).length(), "===== SECOND =====");
				assertEquals(3, firstMap.size(), "===== SIZE FIRST=====");
				assertEquals(5, secondMap.size(), "===== SIZE SECOND=====");
				secondMap.garbageCollection(secondRoot);
				assertEquals(3, secondMap.size(), "===== SIZE SECOND=====");
			}
		}
		return result!=null;
	}
}
