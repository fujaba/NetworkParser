package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;

public class JsonPeer2PeerTest implements UpdateListener{
	private IdMap firstMap;
	private IdMap secondMap;
	private int z;
	private SortedMsg firstRoot;
	protected SortedMsg secondRoot;

	@Test
	public void testModel(){

		firstMap = new IdMap();
		firstMap.with(this);

		firstMap.with(new SortedMsgCreator());

		secondMap = new IdMap();
		secondMap.with(new SortedMsgCreator());

		firstRoot = new SortedMsg();
		firstRoot.withNumber(1);

		SortedMsg second= new SortedMsg();
		second.withNumber(2);
		firstRoot.setChild(second);

		firstMap.garbageCollection(firstRoot);

		update(new SimpleEvent(IdMap.NEW, firstMap.toJsonObject(firstRoot), firstMap, null, null, null));

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
		if(simpleEvent.isNewEvent() == false) {
			return true;
		}

		JsonObject jsonObject = (JsonObject) simpleEvent.getEntity();
		Object result=secondMap.decode(jsonObject);
		if(z==0){
			z++;
			assertEquals(2, secondMap.size());
			secondRoot=(SortedMsg) secondMap.getObject(firstMap.getKey(firstRoot));
		} else if(z==1){
			Assert.assertEquals("===== add =====", 239, jsonObject.toString().length());
			assertEquals(2, secondMap.size());
			z++;
		} else if(z==2){
			Assert.assertEquals("===== add =====", 166, jsonObject.toString().length());
			assertEquals(4, secondMap.size());
			z++;
		} else if(z==3){
			Assert.assertEquals("===== add =====", 165, jsonObject.toString().length());
			assertEquals(4, secondMap.size());
			z++;
		} else if(z==4){
			Assert.assertEquals("===== add =====", 263, jsonObject.toString().length());
			assertEquals(4, secondMap.size());
			z++;
		} else if(z==5){
			Assert.assertEquals("===== add =====", 166, jsonObject.toString().length());
			assertEquals(5, secondMap.size());
			z++;
		} else if(z==6){
			Assert.assertEquals("=====  =====", 251, jsonObject.toString().length());
			assertEquals(5, secondMap.size());
			z++;
		} else if(z==7){
			Assert.assertEquals("===== add =====", 153, jsonObject.toString().length());
			assertEquals(5, secondMap.size());
			z++;
		} else if(z==8){
			Assert.assertEquals("===== rem =====", "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S5\",\"rem\":{\"number\":4},\"upd\":{\"number\":42}}", jsonObject.toString());
			z++;
			assertEquals(5, secondMap.size());
		} else if(z==9){
			Assert.assertEquals("===== rem =====", "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S4\",\"rem\":{\"child\":{\"id\":\"J1.S5\"}}}", jsonObject.toString());
			z++;
			assertEquals(5, secondMap.size());
		}
		if(z>9){
			Assert.assertEquals("===== FIRST =====",385, firstMap.toJsonObject(firstRoot).toString(2).length());
			//LAST
			Object secondRoot = secondMap.getObject("J1.S1");
			if(secondRoot != null) {
				Assert.assertEquals("===== SECOND =====",385, secondMap.toJsonObject(secondRoot).toString(2).length());
				Assert.assertEquals("===== SIZE FIRST=====",3, firstMap.size());
				Assert.assertEquals("===== SIZE SECOND=====",5, secondMap.size());
				secondMap.garbageCollection(secondRoot);
				Assert.assertEquals("===== SIZE SECOND=====",3, secondMap.size());
			}
		}
		return result!=null;
	}
}
