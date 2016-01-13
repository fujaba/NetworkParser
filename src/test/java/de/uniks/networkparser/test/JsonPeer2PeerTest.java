package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;

public class JsonPeer2PeerTest implements UpdateListener{
	private JsonIdMap firstMap;
	private JsonIdMap secondMap;
	private int z;
	private SortedMsg firstRoot;
	protected SortedMsg secondRoot;

	@Test
	public void testModel(){
		
		firstMap = new JsonIdMap();
		firstMap.with(this);
		
		firstMap.with(new SortedMsgCreator());
		
		secondMap = new JsonIdMap();
		secondMap.with(new SortedMsgCreator());
		
		firstRoot = new SortedMsg();
		firstRoot.setNumber(1);
		
		SortedMsg second= new SortedMsg();
		second.setNumber(2);
		firstRoot.setChild(second);
		
		firstMap.garbageCollection(firstRoot);
		update(null, firstMap.toJsonObject(firstRoot), null, null, null, null);
		
		SortedMsg third= new SortedMsg();
		third.setNumber(4);
		third.setParent(second);
		third.setNumber(42);
		second.setChild(null);
	}

	@Override
	public boolean update(String typ, BaseItem source, Object target, String property,
			Object oldValue, Object newValue) {
		JsonObject jsonObject = (JsonObject) source;
		Object result=secondMap.decode(jsonObject);
		if(z==0){
			z++;
			assertEquals(2, secondMap.size());
			secondRoot=(SortedMsg) secondMap.getObject(firstMap.getKey(firstRoot));
		} else if(z==1){
			Assert.assertEquals("===== add =====", 251, jsonObject.toString().length()); 
			assertEquals(3, secondMap.size());
			z++;
		} else if(z==2){
			Assert.assertEquals("===== rem =====", "{\"id\":\"J1.S3\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"rem\":{\"number\":4},\"upd\":{\"number\":42}}", jsonObject.toString());
			z++;
			assertEquals(3, secondMap.size());
		} else if(z==3){
			Assert.assertEquals("===== rem =====", "{\"id\":\"J1.S2\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"rem\":{\"child\":{\"id\":\"J1.S3\"}}}", jsonObject.toString());
			z++;
			assertEquals(3, secondMap.size());
		}
		if(z>3){
			Assert.assertEquals("===== FIRST =====",385, firstMap.toJsonObject(firstRoot).toString(2).length());
			//LAST
			Object secondRoot = secondMap.getObject("J1.S1");
			Assert.assertEquals("===== SECOND =====",385, secondMap.toJsonObject(secondRoot).toString(2).length());
			Assert.assertEquals("===== SIZE FIRST=====",3, firstMap.size());
			Assert.assertEquals("===== SIZE SECOND=====",3, secondMap.size());
			secondMap.garbageCollection(secondRoot);
			Assert.assertEquals("===== SIZE SECOND=====",2, secondMap.size());
		}
		return result!=null;
	}
}
