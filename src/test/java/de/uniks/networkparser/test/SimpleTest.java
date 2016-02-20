package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.beans.PropertyChangeEvent;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.event.util.DateCreator;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.logic.SimpleMapEvent;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.FullAssocs;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.FullAssocsCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;

public class SimpleTest implements UpdateListener{
	
	private IdMap firstMap;
	private IdMap secondMap;
	private int z;
	private SortedMsg firstRoot;
	protected SortedMsg secondRoot;

//	@Test
	public void testSimple() {
		IdMap map = new IdMap();
		Apple apple = new Apple();
		apple.withY(23);
		apple.withX(42);
		map.with(new AppleCreator());
		
		System.out.println(map.toJsonObject(apple).toString());
	}
	
//	@Test
	public void testDate() {
		IdMap map = new IdMap();
		Date now = new Date();
		map.with(new DateCreator());
		
		System.out.println(map.toJsonObject(now).toString());
	}
	
//	@Test
	public void testSimpleList(){
		FullAssocs fullAssocs = new FullAssocs();
		fullAssocs.addPerson("Kassem");
		fullAssocs.addPerson("Stefan");

		IdMap map= new IdMap();
		map.with(new FullAssocsCreator());


		JsonObject jsonObject = map.toJsonObject(fullAssocs);
		String data = jsonObject.toString(2);
		System.out.println(data);
		Assert.assertEquals(144, data.length());

		FullAssocs newfullAssocs = (FullAssocs) map.decode(data);
		assertNotNull(newfullAssocs);
	}

	
//	@Test
	public void testSimpleSortedItem(){
		SortedMsg parent= new SortedMsg();
		parent.setNumber(1);
		SortedMsg child= new SortedMsg();
		child.setNumber(2);
		
		parent.setChild(child);
		IdMap map= new IdMap();
		map.with(new SortedMsgCreator());
		String ref="{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S1\",\"prop\":{\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S2\",\"prop\":{\"number\":2,\"parent\":{\"id\":\"J1.S1\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\"}}}}}";
		assertEquals(ref, map.toJsonObject(parent).toString());
	}
//	@Test
	public void calculation042() {
		SortedMsg parent= new SortedMsg();
		parent.setNumber(1);
		SortedMsg child= new SortedMsg();
		child.setNumber(2);

		parent.setChild(child);


		IdMap map= new IdMap();
		map.with(new SortedMsgCreator());
		String ref="{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S1\",\"prop\":{\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"id\":\"J1.S2\",\"prop\":{\"number\":2,\"parent\":{\"id\":\"J1.S1\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\"}}}}}";
		assertEquals(ref, map.toJsonObject(parent).toString());

		ref = "{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"number\":1,\"child\":{\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"number\":2,\"parent\":{\"id\":\"J1.S1\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\"}}}";
		Filter filter = new Filter().withIdFilter(BooleanCondition.value(false));
		assertEquals(ref, map.toJsonObject(parent, filter).toString());
	}
	
	@Test
	public void testModel(){

		IdMap firstMap = new IdMap();
//		firstMap.with(this);

		firstMap.with(new SortedMsgCreator());

		IdMap secondMap = new IdMap();
		secondMap.with(new SortedMsgCreator());

		SortedMsg firstRoot = new SortedMsg();
		firstRoot.setNumber(1);

		SortedMsg second= new SortedMsg();
		second.setNumber(2);
		firstRoot.setChild(second);

		firstMap.garbageCollection(firstRoot);
		
//		update();
		new SimpleMapEvent(IdMap.NEW, firstMap, null).with(firstMap.toJsonObject(firstRoot));

		SortedMsg third= new SortedMsg();
		third.setNumber(4);
		third.setParent(second);
		third.setNumber(42);
		second.setChild(null);
	}
	
	@Override
	public boolean update(PropertyChangeEvent event) {
		SimpleMapEvent simpleEvent = (SimpleMapEvent) event;
		
		JsonObject jsonObject = (JsonObject) simpleEvent.getEntity();
		Object result=secondMap.decode(jsonObject);
		if(z==0){
			z++;
			assertEquals(2, secondMap.size());
			secondRoot=(SortedMsg) secondMap.getObject(firstMap.getKey(firstRoot));
		} else if(z==1){
			Assert.assertEquals("===== add =====", 251, jsonObject.toString().length());
			assertEquals(3, secondMap.size());
			z++;
		} else if(z==3){
			Assert.assertEquals("===== rem =====", "{\"id\":\"J1.S3\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"rem\":{\"number\":4},\"upd\":{\"number\":42}}", jsonObject.toString());
			z++;
			assertEquals(3, secondMap.size());
		} else if(z==4){
			Assert.assertEquals("===== rem =====", "{\"id\":\"J1.S2\",\"class\":\"de.uniks.networkparser.test.model.SortedMsg\",\"rem\":{\"child\":{\"id\":\"J1.S3\"}}}", jsonObject.toString());
			z++;
			assertEquals(3, secondMap.size());
		}
		if(z>4){
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
