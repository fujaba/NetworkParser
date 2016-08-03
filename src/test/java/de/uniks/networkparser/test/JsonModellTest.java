package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;
import java.io.PrintStream;
import java.math.BigInteger;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Deep;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.bytes.ByteMessage;
import de.uniks.networkparser.bytes.ByteMessageCreator;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.UpdateListener;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.logic.InstanceOf;
import de.uniks.networkparser.logic.Not;
import de.uniks.networkparser.logic.Or;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.AppleTreeCreator;
import de.uniks.networkparser.test.model.util.GroupAccountCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.RoomCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class JsonModellTest implements UpdateListener {
	private IdMap secondMap;
	BaseItem data;

	@Test
	public void testuniWithStudents(){
		University uni = new University();
		uni.withStudents(new Student().withFirstName("Albert").withStudNo("geheim"));
		uni.withStudents(new Student().withFirstName("Stefan"));
		
		IdMap map=new IdMap();
		map.withCreator(new UniversityCreator(), new StudentCreator());
		String json = map.toJsonArray(uni, Filter.regard(InstanceOf.create(Student.PROPERTY_STUD_NO))).toString();
		Assert.assertFalse(json.indexOf("geheim") >= 0);
	}
	
	@Test
	public void testuniWithStudentsAndRoom(){
		University uni = new University();
		uni.withRooms(new Room().withName("R1339"));
		Student albert = new Student().withFirstName("Albert").withStudNo("geheim");
		uni.withStudents(albert);
		uni.withStudents(new Student().withFirstName("Stefan"));
		
		IdMap map=new IdMap();

		InstanceOf classFilter = InstanceOf.create(Student.class);
		map.withCreator(new UniversityCreator(), new StudentCreator(), new RoomCreator());
		Or filter= Or.create(InstanceOf.create(Student.PROPERTY_STUD_NO), classFilter.withWhiteList(true));
		String json = map.toJsonArray(uni, Filter.regard(filter)).toString(2);
		Assert.assertFalse(json.indexOf("geheim") >= 0);
		System.out.println(json);
		Assert.assertFalse(json.indexOf("de.uniks.networkparser.test.model.Room") >=0);
	}
	
	@Test
	public void testSet(){
		GroupAccount account= new GroupAccount();
		account.createPersons().withName("Albert");
		account.createPersons().withName("Tobi");

		IdMap map= new IdMap();
		map.with(new PersonCreator());
		map.with(new GroupAccountCreator());
		Assert.assertEquals(175, map.toJsonArray(account.getPersons(), Filter.regard(InstanceOf.create(Person.class, Person.PROPERTY_PARENT))).toString(2).length());
	}

	@Test
	public void testModell(){
		IdMap map= new IdMap();
		map.with(this);
		map.with(new SortedMsgCreator());
		SortedMsg first= new SortedMsg();
		first.setNumber(1);

		SortedMsg second= new SortedMsg();
		second.setNumber(2);
		first.setChild(second);

		String sample="Hallo Welt";

		byte[] dataByte = sample.getBytes();
		Assert.assertEquals("Actual Size of String", 10, dataByte.length);

		// test string
		String text = "Hello world!";
		Assert.assertEquals("" + text+ "(" +text.length()+ ")", 12, text.length());

		// convert to big integer
		BigInteger number = new BigInteger(text.getBytes());

		// convert back
		new String(number.toByteArray());

		this.secondMap= new IdMap();
		secondMap.with(this);
		secondMap.with(new SortedMsgCreator());

		JsonObject jsonObject=map.toJsonObject(first);
		Assert.assertEquals(385, jsonObject.toString(2).length());

		secondMap.decode(jsonObject);

		SortedMsg third= new SortedMsg();
		third.setNumber(4);
		second.setChild(third);
		// DEEP 0
		Assert.assertEquals(165, map.toJsonObject(first, Filter.regard(Deep.create(1))).toString().length());
		// DEEP 1
		Assert.assertEquals(340, map.toJsonObject(first, Filter.regard(Deep.create(2))).toString().length());
		// DEEP 2
		Assert.assertEquals(438, map.toJsonObject(first, Filter.regard(Deep.create(3))).toString().length());
		third.updateNumber(2);
		third.setNumber(5);

		Assert.assertEquals(3, map.size());
		second.setChild(null);
	}

	@Override
	public boolean update(Object evt) {
		SimpleEvent simpleEvent = (SimpleEvent) evt;

		if(IdMap.NEW.equals(simpleEvent.getType())) {
			JsonObject jsonObject = (JsonObject) simpleEvent.getEntity();
			printToStream("Send: " +jsonObject, null);
//			secondMap.decode(jsonObject);
			return true;
		}
		PropertyChangeEvent event = (PropertyChangeEvent) evt;
		printToStream("ReceiveOBJ: Typ:" +event.getPropertyName()+ "value:" +event.getNewValue(), null);
		return false;
	}

	void printToStream(String str, PrintStream stream) {
		if(stream != null) {
			stream.println(str);
		}
	}


	@Test
	public void testFilterAtomar() {
		AppleTree tree=new AppleTree();

		IdMap map = new IdMap();
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());

		map.toJsonObject(tree);
		map.with(new UpdateListener() {
			@Override
			public boolean update(Object event) {
				SimpleEvent simpleEvent = (SimpleEvent) event;
				data = simpleEvent.getEntity();
				return (Apple.PROPERTY_PASSWORD.equals(simpleEvent.getPropertyName()) == false);
			}
		});
		Apple apple = new Apple();
		apple.withPassword("23");
		apple.withX(23);
		apple.withY(42);
		tree.addToHas(apple);

		Assert.assertNotNull(data);
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.AppleTree\",\"id\":\"J1.A1\",\"upd\":{\"has\":{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"J1.A2\",\"prop\":{\"x\":23,\"y\":42}}}}", data.toString());
	}

	@Test
	public void testBasicMessage() {
		ByteMessage message= new ByteMessage();
		message.withValue("The answer to life the universe and everything is 42.");
		IdMap map=new IdMap();
		map.with(new ByteMessageCreator());
		JsonObject jsonObject = map.toJsonObject(message);
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.bytes.ByteMessage\",\"id\":\"J1.B1\",\"prop\":{\"value\":\"The answer to life the universe and everything is 42.\"}}", jsonObject.toString());

		ByteMessage newMessage = (ByteMessage) map.decode(jsonObject);
		Assert.assertEquals(message.getValue(), newMessage.getValue());
	}
}
