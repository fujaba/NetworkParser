package de.uniks.networkparser.test;

import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Deep;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.AtomarCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.WhiteListCondition;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.ItemCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.PersonSet;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class ModelTest implements ObjectCondition {
	private SimpleList<SimpleEvent> events = new SimpleList<SimpleEvent>(); 

	@Test(expected = UnsupportedOperationException.class)
	public void testModelGroupAccount(){
		GroupAccount ga = new GroupAccount();
		ga.getPersons().add(new Person().withName("Albert"));
	}
	
	@Test
	public void testModel(){
		PersonSet persons= new PersonSet();

		persons.with(new Person().withName("Albert"));
		persons.with(new Person().withName("Stefan"));

		int i=0;
		for (Person p : persons){
			if(i==0){
				Assert.assertEquals("Albert", p.getName());
			} else {
				Assert.assertEquals("Stefan", p.getName());
			}
			i++;
		}
	}

	@Test
	public void testMap(){
		SimpleKeyValueList<String, Integer> values= new SimpleKeyValueList<String, Integer>();

		values.with("Albert", 42);
		values.with("Stefan", 23);
		for(Iterator<Entry<String, Integer>> i = values.iterator();i.hasNext();){
			Entry<String, Integer> item = i.next();
			if(item.getKey().equals("Albert")){
				Assert.assertEquals(42, values.getInt(item.getKey()));
			}
			if(item.getKey().equals("Stefan")){
				Assert.assertEquals(23, values.getInt(item.getKey()));
			}
		}
	}

	@Test
	public void testIdMapFromIdMap(){
		IdMap map= new IdMap();
		map.with(new PersonCreator());
		Assert.assertEquals(8, countMap(map));

		IdMap subMap= new IdMap();
		Assert.assertEquals(7, countMap(subMap));
		subMap.with(map);
		Assert.assertEquals(8, countMap(subMap));

	}

	private int countMap(IdMap map){
		int count=0;
		for (Iterator<SendableEntityCreator> i = map.iterator();i.hasNext();){
			i.next();
			count++;
		}
		return count;
	}

	@Test
	public void testClone(){
		SortedMsg root = new SortedMsg();
		root.setMsg("root");
		SortedMsg child1 = new SortedMsg();
		child1.setMsg("Child");
		SortedMsg child2 = new SortedMsg();
		child2.setMsg("ChildChild");

		root.setChild(child1);
		child1.setChild(child2);


		IdMap map=new IdMap();
		map.with(new SortedMsgCreator());

		SortedMsg root2 = (SortedMsg) map.cloneObject(root, new Filter().withPropertyRegard(Deep.create(1)));
		Assert.assertNotSame(root, root2);
		Assert.assertEquals(root2.getMsg(), "root");
		Assert.assertNotNull(root2.getChild());
		Assert.assertEquals(root2.getChild().getMsg(), "Child");
		Assert.assertNull(root2.getChild().getChild());
	}

	@Test
	public void testAtomar() {
		University uni = new University();
		uni.addToStudents(new Student().withFirstName("Albert"));
		IdMap map=new IdMap();
		map.with(new UniversityCreator());
		map.with(new StudentCreator());
		map.withListener(new AtomarCondition(this));
		events.clear();
		map.toJsonObject(uni);
		uni.withStudents(new Student().withFirstName("Stefan"));
		Assert.assertEquals(5, events.size());
	}

	@Test
	public void testGeneric() {
		Apple apple = new Apple();
		GenericCreator creator = new GenericCreator(apple);
		creator.setValue(apple, Apple.PROPERTY_X, 23.0, SendableEntityCreator.NEW);
		creator.setValue(apple, Apple.PROPERTY_Y, 42, SendableEntityCreator.NEW);
		creator.setValue(apple, "password", "Albert", SendableEntityCreator.NEW);

		Assert.assertEquals(23.0, creator.getValue(apple, Apple.PROPERTY_X));
		Assert.assertEquals(42.0, creator.getValue(apple, Apple.PROPERTY_Y));
		Assert.assertEquals("Albert", creator.getValue(apple, "password"));
	}

	@Override
	public boolean update(Object value) {
		events.add((SimpleEvent) value);
		return true;
	}
	
	@Test
	public void testJsonArray() {
		University uni = new University();
		Student karli = uni.createStudents().withFirstName("Karli");
		Student alice = uni.createStudents().withFirstName("Alice");
		
		karli.withFriends(alice);
		IdMap map=new IdMap();
		map.with(new UniversityCreator());
		map.with(new StudentCreator());
	}		
	
	@Test
	public void testWhiteList() {
		University uni = new University();
		Student karli = uni.createStudents().withFirstName("Karli");
		Student alice = uni.createStudents().withFirstName("Alice");
		alice.createItem().withValue(42);
		
		karli.withFriends(alice);
		IdMap map=new IdMap().withTimeStamp(1);
		map.with(new UniversityCreator());
		map.with(new StudentCreator());
		map.withCreator(new ItemCreator());

		JsonObject jsonObject = map.toJsonObject(uni, Filter.regard(new WhiteListCondition().with(uni.getClass()).with(alice.getClass())));
		Assert.assertEquals(434, jsonObject.toString().length());
	}

}
