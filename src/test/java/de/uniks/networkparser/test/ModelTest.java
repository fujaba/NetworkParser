package de.uniks.networkparser.test;

import java.util.Iterator;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.logic.Deep;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.PersonSet;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class ModelTest {
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
		for (String key : values){
			if(key.equals("Albert")){
				Assert.assertEquals(42, values.getInt(key));
			}
			if(key.equals("Stefan")){
				Assert.assertEquals(23, values.getInt(key));
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
		
		SortedMsg root2 = (SortedMsg) map.cloneObject(root, new Filter().withPropertyRegard(Deep.value(1)));
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
//		AtomarCondition filter = new AtomarCondition(new UpdateListener() {
//			@Override
//			public boolean update(Object value) {
//				System.out.println(value);
//				return false;
//			}
//		});
//		map.with(filter);
		map.toJsonObject(uni);
		uni.withStudents(new Student().withFirstName("Stefan"));
		
	}
}
