package de.uniks.networkparser.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.sdmlib.test.examples.studyrightWithAssignments.model.Room;

import de.uniks.networkparser.Deep;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.SimpleEvent;
import de.uniks.networkparser.SimpleGrammar;
import de.uniks.networkparser.UpdateCondition;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.ext.ClassModelBuilder;
import de.uniks.networkparser.ext.FileClassModel;
import de.uniks.networkparser.ext.generic.GenericCreator;
import de.uniks.networkparser.ext.generic.SimpleParser;
import de.uniks.networkparser.ext.io.FileBuffer;
import de.uniks.networkparser.ext.io.StringPrintStream;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.CodeCityConverter;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.interfaces.ObjectCondition;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SimpleEventCondition;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleKeyValueList;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.WhiteListCondition;
import de.uniks.networkparser.parser.DebugCondition;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.Barbarian;
import de.uniks.networkparser.test.model.GroupAccount;
import de.uniks.networkparser.test.model.Person;
import de.uniks.networkparser.test.model.Plant;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.AppleCreatorNoIndex;
import de.uniks.networkparser.test.model.util.AppleSet;
import de.uniks.networkparser.test.model.util.AppleTreeCreatorNoIndex;
import de.uniks.networkparser.test.model.util.BarbarianCreator;
import de.uniks.networkparser.test.model.util.ItemCreator;
import de.uniks.networkparser.test.model.util.PersonCreator;
import de.uniks.networkparser.test.model.util.PersonSet;
import de.uniks.networkparser.test.model.util.PlantCreatorNoIndex;
import de.uniks.networkparser.test.model.util.PlantFullNoIndex;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class ModelTest implements ObjectCondition {
	private SimpleList<SimpleEvent> events = new SimpleList<SimpleEvent>();

	@Test
	public void testModelGroupAccount(){
	    Assertions.assertThrowsExactly(UnsupportedOperationException.class, () -> {
    		GroupAccount ga = new GroupAccount();
    		ga.getPersons().add(new Person().withName("Albert"));
    	});
	}

	@Test
	public void testModel(){
		PersonSet persons= new PersonSet();

		persons.with(new Person().withName("Albert"));
		persons.with(new Person().withName("Stefan"));

		int i=0;
		for (Person p : persons){
			if(i==0){
				assertEquals("Albert", p.getName());
			} else {
				assertEquals("Stefan", p.getName());
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
				assertEquals(42, values.getInt(item.getKey()));
			}
			if(item.getKey().equals("Stefan")){
				assertEquals(23, values.getInt(item.getKey()));
			}
		}
	}

	@Test
	public void testIdMapFromIdMap(){
		IdMap map= new IdMap();
		map.with(new PersonCreator());
		assertEquals(8, countMap(map));

		IdMap subMap= new IdMap();
		assertEquals(7, countMap(subMap));
		subMap.with(map);
		assertEquals(9, countMap(subMap));

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
		assertNotSame(root, root2);
		assertEquals(root2.getMsg(), "root");
		assertNotNull(root2.getChild());
		assertEquals(root2.getChild().getMsg(), "Child");
		assertNull(root2.getChild().getChild());
	}

	@Test
	public void testAtomar() {
		University uni = new University();
		uni.addToStudents(new Student().withFirstName("Albert"));
		IdMap map=new IdMap();
		map.with(new UniversityCreator());
		map.with(new StudentCreator());
		map.withListener(UpdateCondition.createAtomarCondition(this));
		events.clear();
		map.toJsonObject(uni);
		uni.withStudents(new Student().withFirstName("Stefan"));
		assertEquals(5, events.size());
	}
	
	@Test
	public void testTransaction() {
		University uni = new University();
		IdMap map=new IdMap();
		map.with(new UniversityCreator(), new StudentCreator());
		UpdateCondition filter = UpdateCondition.createTransaction(map);
		map.withListener(filter);
		filter.withContidion(new StringPrintStream());
		filter.withStart(Student.class);
		filter.withEnd(Student.PROPERTY_LASTNAME);
		
		map.toJsonObject(uni);

		Student student = new Student();
		uni.addToStudents(student);
		student.withFirstName("Albert");
		student.withLastName("Zuendorf");

	}

	@Test
	public void testGeneric() {
		Apple apple = new Apple();
		GenericCreator creator = new GenericCreator(apple);
		creator.setValue(apple, Apple.PROPERTY_X, 23.0, SendableEntityCreator.NEW);
		creator.setValue(apple, Apple.PROPERTY_Y, 42, SendableEntityCreator.NEW);
		creator.setValue(apple, "password", "Albert", SendableEntityCreator.NEW);

		assertEquals(23.0, creator.getValue(apple, Apple.PROPERTY_X));
		assertEquals(42.0, creator.getValue(apple, Apple.PROPERTY_Y));
		assertEquals("Albert", creator.getValue(apple, "password"));
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
		assertEquals(504, jsonObject.toString().length());
	}
	@Test
	public void testJabberChatMessage() {
		Plant flower = new Plant();
		flower.setName("Flower");
		flower.setId("42");
		IdMap map;

		map = new IdMap().withCreator(new Plant());
		JsonObject jsonObject = map.toJsonObject(flower);
		assertNotNull(jsonObject);
//		System.out.println(jsonObject);

		map = new IdMap().withCreator(new PlantCreatorNoIndex());
//		System.out.println(map.toJsonObject(flower));

		map = new IdMap().withCreator(new PlantFullNoIndex());
//		System.out.println(map.toJsonObject(flower));
	}

	@Test
	public void testAppleTree() {
		IdMap map = new IdMap().withCreator(new AppleTreeCreatorNoIndex());
		map.withCreator(new AppleCreatorNoIndex());

		AppleTree appleTree = new AppleTree();
		appleTree.withHas(new Apple("red", 23, 42));

//		System.out.println(map.toJsonObject(appleTree));
	}

	@Test
	public void testBarbar() {
		IdMap map = new IdMap().withCreator(new BarbarianCreator());
		Barbarian barbarian = new Barbarian();
		JsonObject jsonObjectB = map.toJsonObject(barbarian, Filter.createNull());
		JsonObject jsonObject = map.toJsonObject(barbarian);

		assertNotEquals(jsonObject, jsonObjectB);
	}

	@Test
	public void testSpeedImporter() {
		IdMap map=new IdMap();
		map.with(new UniversityCreator());
		map.with(new StudentCreator());


		//		University uni = new University();
//		uni.addToStudents(new Student().withFirstName("Albert"));
//		map.toJsonObject(uni);
//		uni.withStudents(new Student().withFirstName("Stefan"));

//		JsonObject json = map.toJsonObject(uni);
//		FileBuffer.writeFile("src/test/resources/de/uniks/networkparser/test/model.json", json.toString(2));

		CharacterBuffer buffer = FileBuffer.readFile("src/test/resources/de/uniks/networkparser/test/model.json");


		// Really Nessessary???
//		JsonObject json = new JsonObject().withValue(buffer);

 		University uni2 = SimpleParser.decodeModel(buffer, map);
		assertNotNull(uni2);
	}
	
	@Test
	public void testSet() {
		AppleTree tree = new AppleTree();
		Apple empty = new Apple();
		Apple apple = new Apple();
		apple.withX(23).withY(42);
		String errorMsg="";
		try {
			tree.getHas().add(apple);
			errorMsg+="ADD TO EMPTY SET";
		}catch (Exception e) {
		}
		tree.withHas(empty);
		
		try {
			tree.getHas().add(apple);
			errorMsg+="ADD TO SET";
		}catch (Exception e) {
		}
		assertTrue(errorMsg.isEmpty(), errorMsg);
		
		AppleSet appleSet = new AppleSet();
		appleSet.add(empty);
		appleSet.add(apple);
		assertEquals(2, appleSet.size());
	}
	
	@Test
	public void testTest() {
		FileClassModel model = new FileClassModel("de.uniks.networkparser");
		model.readFiles("src/main/java/");
		model.analyseBounds();
		model.withLogger(new NetworkParserLog().withListener(new StringPrintStream()));
	}
	
	@Test
	public void testMSE() {
		FileClassModel model = new FileClassModel("de.uniks.networkparser");
		model.readFiles("src/main/java/");
		
		model.fixClassModel();
		model.analyseLoC(model);
		model.analyseBounds();
//		model.createParserEntity(new File("src/main/java/de/uniks/networkparser/ext/io/TarArchiveEntry.java"), new DebugCondition());

		CodeCityConverter converter = new CodeCityConverter();
		String encode = converter.encode(model);
		FileBuffer.writeFile("build/NetworkParser.mse", encode);
	}
	
	@Test
	public void testMSEIdMap() {
		FileClassModel model = new FileClassModel("de.uniks.networkparser");
		model.readFiles(new File("src/main/java/de/uniks/networkparser/IdMap.java"), new DebugCondition());
		
		model.fixClassModel();
		model.analyseLoC(model);
		model.analyseBounds();
//		model.createParserEntity(new File("src/main/java/de/uniks/networkparser/ext/io/TarArchiveEntry.java"), new DebugCondition());

		CodeCityConverter converter = new CodeCityConverter();
		String encode = converter.encode(model);
		FileBuffer.writeFile("build/NetworkParser-IdMap.mse", encode);
	}
	
	@Test
	public void testProgMeth() {
//		ClassModel model = new ClassModel ("de.uniks.model");
//		Clazz person = model.createClazz("Person");
//		person.withAttribute("name", DataType.STRING)
//				.createAttribute("matrikelno", DataType.INT);
//		Clazz uni = model.createClazz("University").withAttribute("name", DataType.STRING);
//		uni.createBidirectional(person, "students", Association.MANY, "studs", Association.ONE);
////		model.generate("gen");
//		
//		ClassModelBuilder builder = new ClassModelBuilder("de.uniks.model");
//		Clazz student = builder.buildClass("Student");
//		builder.createAttribute("name", DataType.STRING)
//				.createAttribute("matrikelno", DataType.INT);
//		builder.createClass("University").createAttribute("name", DataType.STRING);
//		builder.createAssociation("student", Association.MANY, student, "studs", Association.ONE);
////		builder.build();

		ClassModelBuilder builder = new ClassModelBuilder("de.uniks.model");
		Clazz student = builder.buildClass("Student");
		builder.createAttribute("name", DataType.STRING)
			.createAttribute("matrikelno", DataType.INT);
		builder.createClass("University").createAttribute("name", DataType.STRING);
		builder.createAssociation("student",Association.MANY,student,"studs", Association.ONE);
		ClassModel model = builder.getModel();
		model.dumpHTML("test.html", true);
//		builder.build("gen");
	}
	@Test
	public void testgen() {
		ClassModel model = new ClassModel("me.uniks");
		Clazz uni = model.createClazz("Uni");
		Clazz student = model.createClazz("Student");
		uni.withAssoc(student, "students", 5, "studs", 1);
		
//		model.generate("src/test/java");
	}
	
	@Test
	public void testSimpleGen() {
		ClassModel model = new ClassModel("me.uniks");
		Clazz uni = model.createClazz("Uni");
		Clazz student = model.createClazz("Student");
		uni.withAssoc(student, 42);
//		model.generate("src/test/java");
	}
	
	@Test
	public void testJsonIdMap(){
		IdMap map= new IdMap();
		map.withGrammar(new SimpleGrammar().withFlatFormat(true));
		map.with(new PersonCreator());
		map.withListener(new SimpleEventCondition() {
			@Override
			public boolean update(SimpleEvent event) {
				assertNotNull(event.getEntity());
				return false;
			}
		});
		Person alice = new Person();
		alice.withName("Alice");
		alice.setBalance(42);
	}
	
	@Test
	public void testChaining() {
		org.sdmlib.test.examples.studyrightWithAssignments.model.University uni = new org.sdmlib.test.examples.studyrightWithAssignments.model.University();
		Room math = new Room();
		org.sdmlib.test.examples.studyrightWithAssignments.model.Student alice = new org.sdmlib.test.examples.studyrightWithAssignments.model.Student();
		alice.setName("Alice");
		
		org.sdmlib.test.examples.studyrightWithAssignments.model.Student bob = new org.sdmlib.test.examples.studyrightWithAssignments.model.Student();
		bob.setName("Bob");
		
		math.setStudents(alice, bob);
		uni.setRooms(math);
		
		uni.getRooms().stream().map(p -> p.getStudents())
				.forEach(s->s.forEach(st->st.setCredits(42)));
	}
}
