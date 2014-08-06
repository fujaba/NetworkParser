package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.uniks.networkparser.interfaces.MapUpdateListener;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;

public class JsonPeer2PeerTest implements MapUpdateListener{
	private JsonIdMap firstMap;
	private JsonIdMap secondMap;
	private int z;
	private SortedMsg firstRoot;
	protected SortedMsg secondRoot;

	@Test
	public void testModel(){
		
		firstMap = new JsonIdMap();
		firstMap.withUpdateMsgListener(this);
		
		firstMap.withCreator(new SortedMsgCreator());
		
		secondMap = new JsonIdMap();
		secondMap.withCreator(new SortedMsgCreator());
		
		firstRoot = new SortedMsg();
		firstRoot.setNumber(1);
		
		SortedMsg second=new SortedMsg();
		second.setNumber(2);
		firstRoot.setChild(second);
		
//		System.out.println(firstMap.toJsonObject(first).toString(2));
		firstMap.startCarbageCollection(firstRoot);
		System.out.println("SEND ALL");
		sendUpdateMsg(null, null, null, null, firstMap.toJsonObject(firstRoot));
		
		SortedMsg third=new SortedMsg();
		third.setNumber(4);
		System.out.println("ADD THIRD");
//		second.setChild(third);
		third.setParent(second);
		System.out.println("ADD THIRD END");
		third.setNumber(42);
		second.setChild(null);
	}

	@Override
	public boolean sendUpdateMsg(Object target, String property, Object oldObj, Object newObject,
			JsonObject jsonObject) {
		boolean result=secondMap.executeUpdateMsg(jsonObject);
//		System.out.println(secondMap.size());
		if(z==0){
			z++;
			assertEquals(2, secondMap.size());
			secondRoot=(SortedMsg) secondMap.getObject(firstMap.getKey(firstRoot));
//			System.out.println(secondMap.size());
		}else if(z==1){
			System.out.println(jsonObject.toString(2));
			assertEquals(3, secondMap.size());
//			System.out.println(secondMap.size());
			z++;
		}else if(z==2){
			System.out.println(jsonObject.toString(2));
			z++;
//			System.out.println(secondMap.size());
		}else if(z==3){
			System.out.println(jsonObject.toString(2));
			z++;
//			System.out.println(secondMap.size());
		}
		if(z>3){
			System.out.println("===== FIRST =====");
			System.out.println(firstMap.toJsonObject(firstRoot).toString(2));
			//LAST
			System.out.println("===== SECOND =====");
			Object secondRoot = secondMap.getObject("J1.S1");
			System.out.println(secondMap.toJsonObject(secondRoot).toString(2));
			System.out.println("===== SECOND =====");
			System.out.println(firstMap.size());
			System.out.println(secondMap.size());
			secondMap.garbageCollection(secondRoot);
			System.out.println(secondMap.size());
		}
		return result;
	}


	@Override
	public boolean skipCollision(Object masterObj, String key, Object value,
			JsonObject removeJson, JsonObject updateJson) {
		return false;
	}

	@Override
	public boolean isReadMessages(String key, Object element, JsonObject props,
			String type) {
		return false;
	}

	@Override
	public boolean readMessages(String key, Object element, Object value,
			JsonObject props, String type) {
		return false;
	}
}
