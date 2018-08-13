package de.uniks.networkparser.test;

import java.beans.PropertyChangeEvent;
import java.io.PrintStream;
import java.math.BigInteger;
import java.util.LinkedHashSet;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Deep;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.UpdateAccumulate;
import de.uniks.networkparser.UpdateListener;
import de.uniks.networkparser.bytes.ByteMessage;
import de.uniks.networkparser.ext.generic.SimpleParser;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.json.JsonTokener;
import de.uniks.networkparser.logic.InstanceOf;
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

public class JsonModellTest implements ObjectCondition {
	private IdMap secondMap;
	BaseItem data;

	@Test
	public void testJsonUpdate(){
		JsonObject json = JsonObject.create("{id:number, upd:{value:42}, rem:{}}");
		JsonTokener tokener = new JsonTokener();
		UpdateListener updateListener = new UpdateListener(null, tokener);
		updateListener.execute(json, null);

		IdMap map = new IdMap();
		SimpleObject so = new SimpleObject();
		so.setValue("value", 42);
//		so.p
		map.put("number", so, true);
		updateListener = new UpdateListener(map, tokener);
		updateListener.execute(json, null);
	}
	

	@Test
	public void testJsonList(){
		LinkedHashSet<Student> users=new LinkedHashSet<Student>();
		users.add(new Student().withName("Albert"));
		JsonObject json = new JsonObject();
		json.add("readyUser", users);
		Assert.assertEquals("{\"readyUser\":[\"Albert 0.0\"]}", json.toString());
	}


	@Test
	public void testGenericJson(){
		Student student = new Student().withName("Albert");
		IdMap map=new IdMap();
		map.withCreator(new StudentCreator());
		Assert.assertEquals(student.getDynamicValue("job"), null);
		student.withDynamicValue("department", "se");

		JsonObject json = map.toJsonObject(student);
		JsonObject props = json.getJsonObject(JsonTokener.PROPS);
		props.put("job", "prof");
		Assert.assertEquals("{\"name\":\"Albert\",\"department\":\"se\",\"job\":\"prof\"}", props.toString());
		
		IdMap mapB=new IdMap();
		mapB.withCreator(new StudentCreator());
		Student albert = (Student) mapB.decode(json);
		
		Assert.assertEquals(albert.getDynamicValue("job"), "prof");
		Assert.assertEquals(albert.getDynamicValue("department"), "se");
		
//		Assert.assertEquals("Uni Kassel", uniKassel.getName());
	}
	
	@Test
	public void testDynamicJson(){
		University uni = new University().withName("Uni Kassel");
		IdMap map=new IdMap();
		map.withCreator(new UniversityCreator(), new StudentCreator());
		JsonObject json = map.toJsonObject(uni);
		University uniKassel = SimpleParser.fromJson(json, University.class);
		Assert.assertEquals("Uni Kassel", uniKassel.getName());
	}


	@Test
	public void testGenericJsonModel(){
		University uni = new University().withName("Uni Kassel");
		uni.withStudents(new Student().withFirstName("Stefan"));

		JsonObject json = SimpleParser.toJson(uni);

		University uniKassel = SimpleParser.fromJson(json, University.class);
		Assert.assertEquals("Uni Kassel", uniKassel.getName());
	}
	@Test
	public void testGenericJsonModelCrazy(){
		University uni = new University().withName("Uni Kassel");
		uni.withStudents(new Student().withFirstName("Stefan"));

		JsonObject json = SimpleParser.toJson(uni);

		University uniKassel = SimpleParser.fromJson(json);
		Assert.assertEquals("Uni Kassel", uniKassel.getName());
	}
	@Test
	public void testGenericJsonCrazy(){
		JsonObject json = JsonObject.create("{name:\"Uni Kassel\"}");
		University uniKassel = SimpleParser.fromJson(json, University.class);
		Assert.assertEquals("Uni Kassel", uniKassel.getName());
	}

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
		Assert.assertFalse(json.indexOf("de.uniks.networkparser.test.model.Room") >=0);
	}

	@Test
	public void testSet(){
		GroupAccount account= new GroupAccount();
		account.createPersons().withName("Albert");
		account.createPersons().withName("Tobi");

		IdMap map= new IdMap();
		map.withTimeStamp(1);
		map.with(new PersonCreator());
		map.with(new GroupAccountCreator());
		String jsonArray = map.toJsonArray(account.getPersons(), Filter.regard(InstanceOf.create(Person.class, Person.PROPERTY_PARENT))).toString(2);
//		System.out.println(jsonArray);
		Assert.assertEquals(229, jsonArray.length());
		Assert.assertEquals("[\r\n"+
				"  {\r\n"+
				"    \"class\":\"de.uniks.networkparser.test.model.Person\",\r\n" +
				"    \"id\":\"P1\",\r\n" + 
				"    \"prop\":{\"name\":\"Albert\"}\r\n" + 
				"  },\r\n" + 
				"  {\r\n" + 
				"    \"class\":\"de.uniks.networkparser.test.model.Person\",\r\n" + 
				"    \"id\":\"P2\",\r\n" + 
				"    \"prop\":{\"name\":\"Tobi\"}\r\n" + 
				"  }\r\n" + 
				"]" + 
				"", jsonArray.toString());
	}

	@Test
	public void testModell(){
		IdMap map= new IdMap();
		map.with(this);
		map.with(new SortedMsgCreator());
		SortedMsg first= new SortedMsg();
		first.withNumber(1);

		SortedMsg second= new SortedMsg();
		second.withNumber(2);
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
		map.withTimeStamp(1);
		JsonObject jsonObject=map.toJsonObject(first);
		Assert.assertEquals(376, jsonObject.toString(2).length());

		secondMap.decode(jsonObject);

		SortedMsg third= new SortedMsg();
		third.withNumber(4);
		second.setChild(third);
		// DEEP 0
		Assert.assertEquals(159, map.toJsonObject(first, Filter.regard(Deep.create(1))).toString().length());
		// DEEP 1
		Assert.assertEquals(328, map.toJsonObject(first, Filter.regard(Deep.create(2))).toString().length());
		// DEEP 2
		Assert.assertEquals(423, map.toJsonObject(first, Filter.regard(Deep.create(3))).toString().length());
		third.updateNumber(2);
		third.withNumber(5);

		Assert.assertEquals(3, map.size());
		second.setChild(null);
	}

	@Override
	public boolean update(Object evt) {
		if(evt instanceof SimpleEvent == false) {
			return false;
		}
		SimpleEvent simpleEvent = (SimpleEvent) evt;

		if(SendableEntityCreator.NEW.equals(simpleEvent.getType())) {
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
		map.withTimeStamp(1);
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());

		map.toJsonObject(tree);
		map.getMapListener().getFilter().withPropertyRegard(new ObjectCondition() {
			@Override
			public boolean update(Object evt) {
				if(evt instanceof SimpleEvent == false) {
					return false;
				}
				SimpleEvent simpleEvent = (SimpleEvent) evt;
//				data = simpleEvent.getEntity();
				return (Apple.PROPERTY_PASSWORD.equals(simpleEvent.getPropertyName()) == false);
			}
		});
		map.withListener(new ObjectCondition() {
			@Override
			public boolean update(Object evt) {
				if(evt instanceof SimpleEvent == false) {
					return false;
				}
				SimpleEvent simpleEvent = (SimpleEvent) evt;
				data = simpleEvent.getEntity();
				return true;
			}
		});
		Apple apple = new Apple();
		apple.withPassword("23");
		apple.withX(23);
		apple.withY(42);
		tree.addToHas(apple);

		Assert.assertNotNull(data);
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.test.model.AppleTree\",\"id\":\"A1\",\"upd\":{\"has\":{\"class\":\"de.uniks.networkparser.test.model.Apple\",\"id\":\"A2\",\"prop\":{\"x\":23,\"y\":42}}}}", data.toString());
	}

	@Test
	public void testBasicMessage() {
		ByteMessage message= new ByteMessage();
		message.withValue("The answer to life the universe and everything is 42.");
		IdMap map=new IdMap();
		map.with(new ByteMessage());
		map.withTimeStamp(1);
		JsonObject jsonObject = map.toJsonObject(message);
		Assert.assertEquals("{\"class\":\"de.uniks.networkparser.bytes.ByteMessage\",\"id\":\"B1\",\"prop\":{\"value\":\"The answer to life the universe and everything is 42.\"}}", jsonObject.toString());

		ByteMessage newMessage = (ByteMessage) map.decode(jsonObject);
		Assert.assertEquals(message.getValue(), newMessage.getValue());
	}
	private int i;
	@Test
	public void testJSONAccumilateAssociation() {
		//TODO MERGE SOME UPDATE NOTIFICATION
		GroupAccount account = new GroupAccount ();
		Person person = new Person();
		IdMap map = new IdMap();
		map.withCreator(new PersonCreator());
		map.withCreator(new GroupAccountCreator());
		map.withTimeStamp(1);
		map.toJsonObject(account);
//		map.toJsonObject(person);
		UpdateAccumulate updateAccumulate = new UpdateAccumulate(map);
		String[] output= {"de.uniks.networkparser.SimpleEvent[propertyName=new; oldValue=null; newValue=null 0.0; propagationId=null; source=de.uniks.networkparser.IdMap (2)]",
						"de.uniks.networkparser.SimpleEvent[propertyName=persons; oldValue=null; newValue=null 0.0; propagationId=null; source=de.uniks.networkparser.IdMap (2)]",
						"de.uniks.networkparser.SimpleEvent[propertyName=balance; oldValue=0.0; newValue=42.0; propagationId=null; source=de.uniks.networkparser.IdMap (2)]",
						"de.uniks.networkparser.SimpleEvent[propertyName=name; oldValue=null; newValue=Albert; propagationId=null; source=de.uniks.networkparser.IdMap (2)]",
						"de.uniks.networkparser.SimpleEvent[propertyName=item; oldValue=null; newValue=null 0.0; propagationId=null; source=de.uniks.networkparser.IdMap (2)]"};
		map.withListener(new ObjectCondition() {

			@Override
			public boolean update(Object value) {
				Assert.assertEquals(output[i++], value.toString());
//				System.out.println(value);
				return false;
			}
		});
		account.withPersons(person);
		person.withBalance(42);
		person.withName("Albert");

		account.createItem();
		Assert.assertNotNull(updateAccumulate);

	}
}
