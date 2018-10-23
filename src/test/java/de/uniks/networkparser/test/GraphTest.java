package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.SimpleObject;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.DotConverter;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.converter.YUMLConverter;
import de.uniks.networkparser.ext.ClassModel;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.AnnotationSet;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationSet;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.AttributeSet;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.ClazzSet;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.DataTypeMap;
import de.uniks.networkparser.graph.DataTypeSet;
import de.uniks.networkparser.graph.GraphImage;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphOptions;
import de.uniks.networkparser.graph.GraphPatternMatch;
import de.uniks.networkparser.graph.GraphSimpleSet;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.GraphUtil;
import de.uniks.networkparser.graph.Match;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.MethodSet;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.ModifierSet;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.Throws;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.DateCreator;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.BooleanCondition;
import de.uniks.networkparser.parser.TemplateResultFragment;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.ChatMessage;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.Student;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.ludo.Field;
import de.uniks.networkparser.test.model.ludo.Ludo;
import de.uniks.networkparser.test.model.ludo.LudoColor;
import de.uniks.networkparser.test.model.ludo.Pawn;
import de.uniks.networkparser.test.model.ludo.Player;
import de.uniks.networkparser.test.model.ludo.util.DiceCreator;
import de.uniks.networkparser.test.model.ludo.util.FieldCreator;
import de.uniks.networkparser.test.model.ludo.util.LudoCreator;
import de.uniks.networkparser.test.model.ludo.util.PawnCreator;
import de.uniks.networkparser.test.model.ludo.util.PlayerCreator;
import de.uniks.networkparser.test.model.util.ChatMessageCreator;
import de.uniks.networkparser.test.model.util.RoomCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;
import de.uniks.networkparser.xml.EMFTokener;
import de.uniks.networkparser.xml.HTMLEntity;
import de.uniks.networkparser.xml.JDLTokener;
import de.uniks.networkparser.xml.XMLEntity;

public class GraphTest {

	@Test
	public void SetTest() {
		GraphSimpleSet list = new GraphSimpleSet();
		Clazz last = null;
		int size = 459;
		for(int i=1;i<=size;i++) {
			last = new Clazz("CLASSNAME_"+i);
			list.add(last);
		}
		Assert.assertEquals(list.size(), size);
		last = new Clazz("CLASSNAME_0");
		list.add(last);
		list.add(last);
		list.add(new Clazz("CLASSNAME_NEW"));


		Assert.assertEquals(list.size(), size + 2);
	}
	
	@Test
	public void testGraphConvert() {
		GraphConverter converter = new GraphConverter().withFull(true);
		ClassModel model = new ClassModel();
		Clazz uni = model.createClazz("Uni");
		Clazz student = model.createClazz("Student");
		student.createAttribute("uni", DataType.create(uni));
		Entity json = converter.convertToJson(model, true, true);
		
		ClassModel modelB = (ClassModel) converter.convertFromJson(json, new ClassModel());
		Assert.assertEquals(modelB.getClazzes().size(), 2);
		
		String meta = "{\"type\":\"classdiagram\",\"id\":\"i.love.networkparser\",\"nodes\":[{\"modifiers\":\"public\",\"type\":\"class\",\"id\":\"Student\",\"attributes\":[{\"id\":\"uni\",\"modifiers\":\"private\",\"type\":\"Uni\"}]}]}";
		ClassModel modelC = (ClassModel) converter.convertFromJson(JsonObject.create(meta), new ClassModel());
		Assert.assertEquals(modelC.getClazzes().size(), 2);
	}

	@Test
	public void testSimpleGeneralization() {
		GraphList model=new GraphList();
		Clazz creature = model.createClazz("Creature");
		creature.createAttribute("age", DataType.INT);
		creature.createMethod("live");
		creature.enableInterface();

		Clazz food = model.createClazz("Food");
		creature.withBidirectional(food, "eat", Association.MANY, "meal", Association.ONE);


		Clazz person = model.createClazz("Person").withSuperClazz(creature);
		person.createAttribute("name", DataType.STRING);
		person.createMethod("go");
		person.withBidirectional(food, "has", Association.MANY, "owner", Association.ONE);

		Assert.assertEquals(2, person.getMethods().size());
		Assert.assertEquals(2, person.getAttributes().size());
		Assert.assertEquals(3, person.getAssociations().size());
	}


	@Test
	public void testSimpleObject() {
		SimpleObject so = SimpleObject.create("number", "value", 42);
		IdMap map = new IdMap();
		map.withTimeStamp(1);
		JsonObject jsonObject = map.toJsonObject(so);
		Assert.assertEquals("{\"class\":\"number\",\"id\":\"S1\",\"prop\":{\"value\":42}}", jsonObject.toString());
		jsonObject = map.toJsonObject(so, Filter.createSimple());
		Assert.assertEquals("{\"class\":\"number\",\"id\":\"S1\",\"value\":42}", jsonObject.toString());

		Assert.assertEquals(so.getValue(), 42);
	}

	@Test
	public void testModifier() {
		GraphList model = new GraphList();
		Clazz person = model.createClazz("Person");
		Assert.assertEquals("public", person.getModifier().toString());

		person.with(Modifier.ABSTRACT);
		person.with(Modifier.ABSTRACT);

		Assert.assertEquals("public abstract", person.getModifier().toString());

		person.with(Modifier.FINAL);

		Assert.assertEquals("public abstract final", person.getModifier().toString());

		Clazz uni = model.createClazz("Uni");

		Assert.assertEquals("public", uni.getModifier().toString());
		uni.with(Modifier.ABSTRACT);

		Assert.assertEquals("public abstract", uni.getModifier().toString());

		Clazz student = model.createClazz("Student");
		student.with(Modifier.create(Modifier.ABSTRACT));

		Assert.assertEquals("public abstract", student.getModifier().toString());

		Assert.assertEquals("public abstract", uni.getModifier().toString());
		Assert.assertEquals("public abstract final", person.getModifier().toString());
	}


	@Test
	public void testDupplicateAssoc() {
		Clazz person=new Clazz("Person");
		Clazz uni=new Clazz("Uni");
		person.withBidirectional(uni, "owner", Association.ONE, "has", Association.MANY);
		person.withBidirectional(uni, "owner", Association.ONE, "has", Association.MANY);

		Assert.assertEquals(1, uni.getAssociations().size());
		Assert.assertEquals(1, person.getAssociations().size());

		person.withBidirectional(uni, "ownerB", Association.ONE, "hasN", Association.MANY);
		Assert.assertEquals(2, uni.getAssociations().size());
		Assert.assertEquals(2, person.getAssociations().size());

	}

	@Test
	public void testImplements() {
		GraphList list = new GraphList();

		Clazz person=list.createClazz("Person").enableInterface();
		Clazz student=new Clazz("Student");
		student.withAttribute("name", DataType.STRING);

		student.withSuperClazz(person);

		list.fixClassModel();

		GraphConverter converter = new GraphConverter();
		showDebugInfos(converter.convertToJson(list, true, false), 501, null);
	}

	@Test
	public void testDataType() {
		DataType dataType = DataType.create("int");
		Assert.assertEquals(dataType.toString(), "DataType.INT");
	}

	@Test
	public void testGraph() {
		GraphList list = new GraphList();
		Clazz node = new Clazz("Item");
		GraphUtil.setGraphImage(node, new GraphImage().with("karli.png"));
		list.with(node);

		GraphConverter converter = new GraphConverter();
		Assert.assertEquals(
				"{\"type\":\"classdiagram\",\"nodes\":[{\"type\":\"class\",\"id\":\"Item\",\"head\":{\"src\":\"karli.png\"}}]}",
				converter.convertToJson(list, false, false).toString());
	}

	@Test
	public void testSuperClassesAsInterface() {
		Clazz student = new Clazz("Student");
		Clazz person = new Clazz("Person");
		student.withSuperClazz(person);
		Assert.assertNotNull(student.getSuperClazzes(false).first());
		Assert.assertEquals(student.getAssociations().first().getType(), AssociationTypes.GENERALISATION);
		person.enableInterface();
		Assert.assertNull(student.getSuperClazzes(false).first());
		Assert.assertEquals(student.getAssociations().first().getType(), AssociationTypes.IMPLEMENTS);
	}

	@Test
	public void testSuperClasses() {
		Clazz student = new Clazz("Student");
		Clazz person = new Clazz("Person");
		student.withSuperClazz(person);

		Assert.assertEquals(student.getSuperClazzes(false).first(), person);
		Assert.assertTrue(person.getKidClazzes(false).contains(student));
	}

	@Test
	public void testDuplicateJsonClass() {
		University uni = new University().withName("Uni Kassel");
		Student student = new Student().withName("Stefan");
		Room room= new Room().withName("MathRoom");
		student.withIn(room);
		uni.withStudents(student);

		IdMap map=new IdMap();
		map.withCreator(new UniversityCreator(), new StudentCreator(), new RoomCreator());
		SimpleList<Object> list = new SimpleList<Object>();
		list.with(uni, student);
		JsonArray jsonArray = map.toJsonArray(list, Filter.createFull().withPropertyRegard(BooleanCondition.create(true)));
		Assert.assertEquals(3, jsonArray.size());
	}

	@Test
	public void testDuplicateJsonClassComplex() {
		University uni = new University().withName("Uni Kassel");
		Student student = new Student().withName("Stefan");
		Room room= new Room().withName("MathRoom");
		student.withIn(room);
		uni.withStudents(student);

		IdMap map=new IdMap();
		map.withCreator(new UniversityCreator(), new StudentCreator(), new RoomCreator());
		SimpleList<Object> list = new SimpleList<Object>();
		list.with(uni, student, room);
		JsonArray jsonArray = map.toJsonArray(list, Filter.createFull().withPropertyRegard(BooleanCondition.create(true)));
		Assert.assertEquals(3, jsonArray.size());

		jsonArray = map.toJsonArray(list, Filter.createFull().withPropertyRegard(BooleanCondition.create(true)));
		Assert.assertEquals(3, jsonArray.size());
	}

	@Test
	public void testComplex() {
		Clazz student = new Clazz("Student");
		Clazz person = new Clazz("Person");
		Clazz uni = new Clazz("Uni");
		student.withSuperClazz(person);

		Assert.assertEquals(student.getSuperClazzes(false).first(), person);
		Assert.assertTrue(person.getKidClazzes(false).contains(student));

		uni.withBidirectional(student, "stud", Association.MANY, "owner", Association.ONE);
	}

	@Test
	public void testModell() {
		Clazz clazz = new Clazz("Student");
		clazz.createAttribute("name", DataType.STRING);
		clazz.createAttribute("age", DataType.INT);
		PrintStream output = null; // System.out;
		// output = System.out;
		// SimpleSet<Attribute> filterAttributes =
		// clazz.getAttributes().each(value -> "name".equals(value.getName()));

		AttributeSet filterAttributesAll = clazz.getAttributes();
		for (Attribute attribute : filterAttributesAll) {
			if (output != null) {
				output.println("All: " + attribute.getName());
			}
		}

		AttributeSet filterAttributesA = clazz.getAttributes().filter(Attribute.NAME.equals("name"));
		for (Attribute attribute : filterAttributesA) {
			if (output != null) {
				output.println("Equals: " + attribute.getName());
			}
		}

		AttributeSet filterAttributesB = clazz.getAttributes().hasName("name");
		for (Attribute attribute : filterAttributesB) {
			if (output != null) {
				output.println("Equals: " + attribute.getName());
			}
		}

		AttributeSet filterAttributesC = clazz.getAttributes().filter(Attribute.NAME.not("name"))
				.filter(new Condition<Attribute>() {
					@Override
					public boolean update(Attribute value) {
						return value.getClazz() != null;
					}
				});
		for (Attribute attribute : filterAttributesC) {
			if (output != null) {
				output.println("Not: " + attribute.getName());
			}
		}

		// SimpleSet<Attribute> filterAttributesA =
		// clazz.getAttributes().has(u"name", Attribute.NAME, Condition.EQUALS);
		// SimpleSet<Attribute> filterAttributesB =
		// clazz.getAttributes(StringFilter.equalsIgnoreCase(Attribute.PROPERTY_NAME,
		// "name"));
		// filterAttributesA.get
	}

	private static final String RED = "red";

	@Test
	public void SimpleModel() {
		IdMap jsonIdMap = new IdMap();
		jsonIdMap.withTimeStamp(1);
		jsonIdMap.with(new FieldCreator()).with(new LudoCreator()).with(new PawnCreator()).with(new PlayerCreator());

		Ludo ludo = new Ludo();

		Player tom = ludo.createPlayers().withName("Tom").withColor("blue");
		Player sabine = ludo.createPlayers().withName("Sabine").withColor(RED);
		// tom.createDice().withValue(6);
		tom.createPawns().withColor("blue"); // IS IS THE DIFFERENT

		Field tomStartField = tom.createStart().withColor("blue").withKind("start");
		sabine.createPawns().withColor(RED).withPos(tomStartField);

		JsonArray jsonArray = jsonIdMap.toJsonArray(ludo);
		showDebugInfos(jsonArray, 2255, null);
		jsonArray.replaceAllValues(IdMap.CLASS, "de.uniks.networkparser.test.model.ludo.", "");
		showDebugInfos(jsonArray, 1553, null);

		GraphConverter graphConverter = new GraphConverter();

		// May be 8 Asssocs and write 11
		YUMLConverter converterYUML = new YUMLConverter();
		GraphList root  = graphConverter.convertGraphList(GraphTokener.CLASSDIAGRAM, jsonArray);

		Entity converter = graphConverter.convertToJson(GraphTokener.CLASSDIAGRAM, jsonArray, true);
		showDebugInfos(converter, 1505, null);


		root = graphConverter.convertGraphList(GraphTokener.CLASSDIAGRAM, jsonArray);
		Assert.assertEquals("[Field|color:String;kind:String]-[Pawn|color:String],[Field]-[Player|color:String;name:String],[Ludo]-[Player],[Pawn]-[Player]",
				converterYUML.convert(root, true));

		showDebugInfos(converter, 1505, null);
	}

	@Test
	public void testLudoStoryboard() {
		IdMap jsonIdMap = new IdMap();
		jsonIdMap.withTimeStamp(1);
		jsonIdMap.with(new DateCreator()).with(new DiceCreator()).with(new FieldCreator()).with(new LudoCreator())
				.with(new PawnCreator()).with(new PlayerCreator());

		// create a simple ludo storyboard
		Ludo ludo = new Ludo();
		Player tom = ludo.createPlayers().withName("Tom").withColor("blue").withEnumColor(LudoColor.blue);
		Player sabine = ludo.createPlayers().withName("Sabine").withColor(RED).withEnumColor(LudoColor.red);
		tom.createDice().withValue(6);
		Pawn p2 = tom.createPawns().withColor("blue");
		Field tomStartField = tom.createStart().withColor("blue").withKind("start");
		sabine.createStart().withColor(RED).withKind("start");
		Field tmp = tomStartField;
		for (int i = 0; i < 4; i++) {
			tmp = tmp.createNext();
		}
		tom.createBase().withColor("blue").withKind("base").withPawns(p2);
		sabine.createPawns().withColor(RED).withPos(tomStartField);
		JsonArray jsonArray = jsonIdMap.toJsonArray(ludo);
		showDebugInfos(jsonArray, 4966, null);
		GraphConverter graphConverter = new GraphConverter();

		// May be 8 Asssocs and write 11
		Entity converter = graphConverter.convertToJson(GraphTokener.CLASSDIAGRAM, jsonArray, true);
		showDebugInfos(converter, 2143, null);
	}

	private void showDebugInfos(Entity json, int len, PrintStream stream) {
		if (stream != null) {
			stream.println("###############################");
			stream.println(json.toString(2));
			stream.println("###############################");
		}
		Assert.assertEquals(len, json.toString(2).length());
	}
	private void showDebugInfos(String value, int len, PrintStream stream) {
		if (stream != null) {
			stream.println("###############################");
			stream.println(value);
			stream.println("###############################");
		}
		Assert.assertEquals(len, value.length());
	}

	private void showDebugInfos(EntityList json, int len, PrintStream stream) {
		if (stream != null) {
			stream.println("###############################");
			stream.println(json.toString(2));
			stream.println("###############################");
		}
		Assert.assertEquals(len, json.toString(2).length());
	}

	@Test
	public void testSimpleGraph() {
		SortedMsg root = new SortedMsg();
		root.withMsg("Hallo Welt");

		root.setChild(new SortedMsg().withMsg("Child"));

		IdMap map = new IdMap();
		map.withTimeStamp(1);
		map.with(new SortedMsgCreator());

		JsonArray jsonArray = map.toJsonArray(root, Filter.createFull());
		GraphConverter graphConverter = new GraphConverter();
		Entity objectModel = graphConverter.convertToJson(GraphTokener.OBJECTDIAGRAM, jsonArray, true);
		showDebugInfos(objectModel, 619, null);

		Entity clazzModel = graphConverter.convertToJson(GraphTokener.CLASSDIAGRAM, jsonArray, true);
		showDebugInfos(clazzModel, 464, null);
		Assert.assertEquals(new CharacterBuffer().withLine("{").withLine("  \"type\":\"classdiagram\",")
				.withLine("  \"nodes\":[").withLine("    {")
				.withLine("      \"type\":\"class\",").withLine("      \"id\":\"SortedMsg\",")
				.withLine("      \"attributes\":[").withLine("        \"msg:String\",")
				.withLine("        \"number:Integer\"").withLine("      ]").withLine("    }").withLine("  ],")
				.withLine("  \"edges\":[").withLine("    {").withLine("      \"type\":\"assoc\",")
				.withLine("      \"source\":{")
				.withLine("        \"property\":\"child\",")
				.withLine("        \"cardinality\":1,")
				.withLine("        \"id\":\"SortedMsg\"")
				.withLine("      },").withLine("      \"target\":{")
				.withLine("        \"property\":\"parent\",")
				.withLine("        \"cardinality\":1,")
				.withLine("        \"id\":\"SortedMsg\"")
				.withLine("      }").withLine("    }").withLine("  ]").with("}").toString(), clazzModel.toString(2));
	}

	@Test
	public void testClazzAttributes() {
		Clazz player = new Clazz("Player");
		player.withAttribute("name", DataType.STRING);
		player.withAttribute("name", DataType.STRING);
		player.withMethod("checkend()", DataType.BOOLEAN);
		Assert.assertEquals("checkend", player.getMethods().first().getName());
		Assert.assertEquals(1, player.getAttributes().size());
	}

	@Test
	public void testClazzTest() {
		Clazz ludo = new Clazz("Ludo");
		Clazz player = new Clazz("Player");
		ludo.withBidirectional(player, "players", Association.MANY, "game", Association.ONE);
		Assert.assertNotNull(ludo);
	}

	@Test
	public void testLudoToMany() {
		IdMap jsonIdMap = new IdMap();
		jsonIdMap.with(new LudoCreator()).with(new PlayerCreator());

		// create a simple ludo storyboard
		Ludo ludo = new Ludo();
		ludo.createPlayers().withName("Tom").withColor("blue").withEnumColor(LudoColor.blue);
		ludo.createPlayers().withName("Sabine").withColor(RED).withEnumColor(LudoColor.red);

		JsonArray jsonArray = jsonIdMap.toJsonArray(ludo);
		GraphConverter graphConverter = new GraphConverter();

		Entity converter = graphConverter.convertToJson(GraphTokener.CLASSDIAGRAM, jsonArray, true);
		showDebugInfos(converter, 542, null);
	}
	@Test
	public void testGraphPatternTest() {
		IdMap mapA = new IdMap();
		mapA.with(new StudentCreator());
		mapA.with(new UniversityCreator());

//		IdMap mapB = new IdMap();
//		mapB.with(new StudentCreator());
//		mapB.with(new UniversityCreator());

		University uniA = new University().withName("Uni Kassel");

		University uniB = new University().withName("Uni Kassel");

		GraphPatternMatch diff = mapA.getDiff(uniA, uniB, false);

		Assert.assertEquals(0, diff.size());
	}

	@Test
	public void testYUML() {
		String url = "http://yuml.me/diagram/class/";
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");
		Date date = new Date();
		date.setTime(1350978000017L);
		chatMessage.setDate(date);

		IdMap jsonMap = new IdMap();
		jsonMap.add(new ChatMessageCreator());
		IdMap yumlParser = new IdMap();
		yumlParser.add(jsonMap);
		yumlParser.withTimeStamp(1);

		String parseObject = yumlParser.toObjectDiagram(chatMessage).toString();
		assertEquals(
				url	+ "[C1 : ChatMessage|activ=false;count=0;sender=Stefan Lindel;txt=Dies ist eine Testnachricht]-[D2 : Date|value=1350978000017]",
				url + parseObject);

		jsonMap = new IdMap();
		jsonMap.with(new UniversityCreator());
		jsonMap.with(new RoomCreator());
		University uni = new University();
		uni.setName("Wilhelmshoehe Allee");
		Room room = new Room();
		room.setName("1340");
		uni.addToRooms(room);

		assertEquals(url + "[U3 : University]",
				url + yumlParser.toObjectDiagram(uni).toString());

		assertEquals(url + "[University]", url + yumlParser.toClassDiagram(uni).toString());
	}

	@Test
	public void testSimpleGraphList() {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz("University"));
		uni.createAttribute("name", DataType.STRING);
		uni.createMethod("init()");
		Clazz student = list.with(new Clazz("Student"));
		student.withUniDirectional(uni, "owner", Association.ONE);
		YUMLConverter converter = new YUMLConverter();
		Assert.assertEquals("[Student]->[University|name:String]", converter.convert(list, true));
	}

	@Test
	public void testSimpleBiGraphList() {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz("University"));
		uni.createAttribute("name", DataType.STRING);
		uni.createMethod("init()");
		Clazz student = list.with(new Clazz("Student"));
		student.withBidirectional(uni, "owner", Association.ONE, "students", Association.MANY);
		YUMLConverter converter = new YUMLConverter();
		Assert.assertEquals("[Student]-[University|name:String]", converter.convert(list, true));
	}

	@Test
	public void testSimpleYUMLGraph() {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz("University"));
		uni.createAttribute("name", DataType.STRING);
		list.with(new Clazz("Student"));
		YUMLConverter converter = new YUMLConverter();
		Assert.assertEquals("[Student],[University|name:String]", converter.convert(list, true));
	}


	@Test
	public void testHTMLEntity() throws IOException {
		HTMLEntity htmlEntity = new HTMLEntity();

		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/graph.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/dagre.min.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/drawer.js");

		Assert.assertEquals(438, htmlEntity.toString(2).length());

		DocEnvironment docEnvironment = new DocEnvironment();
		GraphList model = new GraphList().withType(GraphTokener.CLASSDIAGRAM);

		Clazz abstractArray = model.with(new Clazz("AbstractArray"));
		abstractArray.createAttribute("elements", DataType.create("Object[]"));
		abstractArray.createAttribute("size", DataType.INT);
		abstractArray.createAttribute("index", DataType.INT);
		abstractArray.createAttribute("flag", DataType.BYTE);
		Clazz baseItem = model.with(new Clazz("BaseItem").enableInterface());
		Clazz iterable = model.with(new Clazz("Iterable<V>"));
		Clazz abstractList = model.with(new Clazz("AbstractList<V>"));
		Clazz simpleList = model.with(new Clazz("SimpleList<V>"));
		Clazz simpleSet = model.with(new Clazz("SimpleSet<V>"));
		Clazz simpleKeyValueList = model.with(new Clazz("SimpleKeyValueList<K, V>"));
		Clazz map = model.with(new Clazz("Map<K, V>"));
		Clazz list = model.with(new Clazz("List<V>"));
		Clazz set = model.with(new Clazz("Set<V>"));

//		baseItem.withInterface(true);

		GraphUtil.setAssociation(model, Association.create(abstractArray, baseItem).with(AssociationTypes.IMPLEMENTS));
		GraphUtil.setAssociation(model, Association.create(abstractArray, iterable).with(AssociationTypes.IMPLEMENTS));
		GraphUtil.setAssociation(model, Association.create(abstractList, abstractArray).with(AssociationTypes.GENERALISATION));

		GraphUtil.setAssociation(model, Association.create(simpleKeyValueList, abstractArray).with(AssociationTypes.GENERALISATION));
		GraphUtil.setAssociation(model, Association.create(simpleList, abstractList).with(AssociationTypes.GENERALISATION));
		GraphUtil.setAssociation(model, Association.create(simpleSet, abstractList).with(AssociationTypes.GENERALISATION));

		GraphUtil.setAssociation(model, Association.create(simpleKeyValueList, map).with(AssociationTypes.IMPLEMENTS));
		GraphUtil.setAssociation(model, Association.create(simpleList, list).with(AssociationTypes.IMPLEMENTS));
		GraphUtil.setAssociation(model, Association.create(simpleSet, set).with(AssociationTypes.IMPLEMENTS));

		docEnvironment.writeJson("simpleCollection.html", "../src/main/resources/de/uniks/networkparser/graph/", new GraphConverter().convertToJson(model, true, false));
	}

	@Test
	public void testWriteSimpleHTML() {
		HTMLEntity htmlEntity = new HTMLEntity();
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagram.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/dagre.min.js");

		GraphList model = new GraphList().withType(GraphTokener.CLASSDIAGRAM);
		Clazz uni = model.with(new Clazz("University"));
		uni.createAttribute("name", DataType.STRING);
		Clazz person = model.with(new Clazz("Person"));

		uni.withBidirectional(person, "has", Association.MANY, "studis", Association.ONE);
		String result = htmlEntity.withGraph(model).toString(2);
		showDebugInfos( result, 814, null);
	}

	@Test
	public void testMethodBody() throws NoSuchMethodException, SecurityException {
		Apple apple = new Apple();
		java.lang.reflect.Method method = apple.getClass().getMethod("setOwner", AppleTree.class);

		method.getModifiers();
	}

	@Test
	public void testDotShort() {
		String item="strict graph ethane {1}";
		DotConverter map = new DotConverter();
		map.decode(item);
	}

	@Test
	public void testDotShortest() {
		String item="graph{1}";
		DotConverter map = new DotConverter();
		GraphList list = (GraphList) map.decode(item);
		Assert.assertEquals(1, list.getNodes().size());
	}

	@Test
	public void testDotSimple() {
		String item="digraph G {"+BaseItem.CRLF
			  +"\"Welcome\" -> \"To\""+BaseItem.CRLF
			  +"\"To\" -> \"Web\""+BaseItem.CRLF
			  +"\"To\" -> \"GraphViz!\""+BaseItem.CRLF
			+"}";
		DotConverter map = new DotConverter();
		GraphList list = (GraphList) map.decode(item);
		Assert.assertEquals(4, list.getNodes().size());
	}
	@Test
	public void testDotPM() {
		String item="graph smallworld {"+BaseItem.CRLF
			+"1 -> 2"+BaseItem.CRLF
			+"2 -- 3"+BaseItem.CRLF
			+"3 -- 4"+BaseItem.CRLF
			+"4 -- 1"+BaseItem.CRLF
			+"1 -- 5"+BaseItem.CRLF
			+"2 -- 5"+BaseItem.CRLF
			+"3 -- 5"+BaseItem.CRLF
			+"4 -- 5}";

		DotConverter map = new DotConverter();
		GraphList list = (GraphList) map.decode(item);
		Assert.assertEquals(5, list.getNodes().size());
	}
	@Test
	public void testDotPMAttribute() {
		String item="graph smallworld {"+BaseItem.CRLF
			+"1[BONUS=2,ID=ISLAND] -- 2[BONUS=3]}";
		DotConverter map = new DotConverter();
		GraphList list = (GraphList) map.decode(item);
		Assert.assertEquals(2, list.getNodes().size());
	}

	@Test
	public void testDotConverter() throws IOException {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz("University"));
		uni.createAttribute("name", DataType.STRING);
		uni.createMethod("init()");
		Clazz student = list.with(new Clazz("Student"));
		student.withUniDirectional(uni, "owner", Association.ONE);

		String convert = list.toString(new DotConverter().withRemovePackage(true));

		new File("build").mkdir();
		FileWriter fstream = new FileWriter("build/dotFile.dot");
		BufferedWriter out = new BufferedWriter(fstream);
		out.write(convert);
		//Close the output stream
		out.close();

	  //		String[] command = new String[] { makeimageFile, , "." };
		//		String path = "../GraphViz/win32/";
		//		 String[] command = new String[] { path+"dot", "build/dotFile.dot", "-Tsvg", "-o", "build/dotFile.svg" };
		//		 ProcessBuilder processBuilder = new ProcessBuilder(command);
		//		 processBuilder.redirectErrorStream(true);
		//		 processBuilder.redirectOutput(Redirect.INHERIT);
		//		 processBuilder.start();
	}

	@Test
	public void testCoverage() {
		Assert.assertFalse(AssociationTypes.isEdge(null));
		Assert.assertEquals(AssociationTypes.ASSOCIATION, AssociationTypes.valueOf("ASSOCIATION"));
		Assert.assertEquals(8, AssociationTypes.values().length);

		Assert.assertEquals(GraphOptions.TYP.HTML, GraphOptions.TYP.valueOf("HTML"));
		Assert.assertEquals(4, GraphOptions.TYP.values().length);

		Assert.assertEquals(GraphOptions.RANK.LR, GraphOptions.RANK.valueOf("LR"));
		Assert.assertEquals(2, GraphOptions.RANK.values().length);

		Assert.assertEquals(GraphOptions.LINETYP.CENTER, GraphOptions.LINETYP.valueOf("CENTER"));
		Assert.assertEquals(2, GraphOptions.LINETYP.values().length);

		GraphList model = new GraphList();
		model.setAuthorName("Stefan");
		model.with("de.uniks.networkparser");
		Clazz person = model.createClazz("Person");
		Assert.assertEquals(1, model.getClazzes().size());

		AssociationSet set = new AssociationSet();
		set.add(new Association(person));
		Assert.assertEquals(1, set.size());
	}

	@Test
	public void testMember() {
		GraphList model = new GraphList();
		model.createClazz("Person");
		Assert.assertNull(model.getValue("Blub"));
		model.add(new Match());
		model.getClazzes(new BooleanCondition().withValue(true));
		DataTypeSet dtSet = DataTypeSet.create(DataType.STRING);
		Assert.assertNotNull(dtSet);
		Assert.assertTrue(dtSet.equals(DataTypeSet.create(DataType.STRING)));
		Assert.assertEquals("SimpleSet<String>", dtSet.getName(true));

		Assert.assertNotNull(DataTypeMap.create(DataType.STRING, DataType.STRING));
//		create(Clazz)	9	0%		n/a	1	1	2	2	1	1
//		create(String)	9	0%		n/a	1	1	2	2	1	1
//		getGeneric()	3	0%		n/a	1	1	1	1	1	1
//		hashCode()
	}

	@Test
	public void testFullGraph() {
		GraphList model = new GraphList();
		model.setAuthorName("Stefan");
		model.with("de.uniks.networkparser");
		Clazz person = model.createClazz("Person");
		Clazz uni = model.createClazz("University");
		ClazzSet clazzes = model.getClazzes();
		Assert.assertEquals(person, clazzes.get(0));
		Assert.assertEquals(uni, clazzes.get(1));

		Attribute name = person.createAttribute("name", DataType.STRING);
		Attribute id = person.createAttribute("id", DataType.INT);

		Method initMethod = person.createMethod("init").with(Annotation.OVERRIDE);
		initMethod.with(new Throws("Exception"));
		Method toStringMethod = person.createMethod("toString", new Parameter(DataType.INT)).with(DataType.STRING);
		person.withBidirectional(uni, "owner", Association.ONE, "studs", Association.MANY);

		AttributeSet attributes = person.getAttributes();
		Assert.assertEquals(name, attributes.get(1));
		Assert.assertEquals(id, attributes.get(0));

		MethodSet methods = person.getMethods();
		Assert.assertEquals(initMethod, methods.get(0));
		Assert.assertEquals(toStringMethod, methods.get(1));
		Assert.assertEquals(1, methods.getClazzes().size());
		Assert.assertEquals(1, methods.getAnnotations().size());
		Assert.assertEquals(1, methods.getModifiers().size());
		Assert.assertEquals(2, methods.getReturnTypes().size());
		Assert.assertEquals(1, methods.getParameters().size());

		Assert.assertEquals(1, methods.getParameters().getMethods().size());
		Assert.assertEquals(1, methods.getParameters().getDataTypes().size());

		// Full Methods for AnnotationSet
		Annotation override = Annotation.OVERRIDE;
		initMethod.with(override.newInstance());
		name.with(override.newInstance());
		person.with(override.newInstance());

		AnnotationSet listOfAnnotation = new AnnotationSet().with(override);
		Assert.assertEquals(1, listOfAnnotation.getClazzes().size());
		Assert.assertEquals(1, listOfAnnotation.getMethods().size());
		Assert.assertEquals(0, listOfAnnotation.getAttributes().size());

		Modifier private1 = Modifier.PRIVATE;
		initMethod.with(private1);
		name.with(private1);
		person.with(private1);

		ModifierSet listOfModifier = new ModifierSet().with(initMethod.getModifier());
		listOfModifier.with(name.getModifier());
		listOfModifier.with(person.getModifier());
		Assert.assertEquals(1, listOfModifier.getClazzes().size());
		Assert.assertEquals(1, listOfModifier.getMethods().size());
		Assert.assertEquals(0, listOfModifier.getAttributes().size());

		// Navigate over Full Model
		ClazzSet list = new ClazzSet().with(person, uni);
		Assert.assertEquals(2, list.getModifiers().size());
		Assert.assertEquals(2, list.getMethods().size());


		listOfAnnotation = list.getAnnotations();
		Assert.assertEquals(1, listOfAnnotation.size());

		AssociationSet listOfAssocuation = list.getAssociations();
		Assert.assertEquals(2, listOfAssocuation.getClazzes().size());
		Assert.assertEquals(1, listOfAssocuation.getOther().size());
		Assert.assertEquals(2, listOfAssocuation.getOtherClazz().size());
		Assert.assertEquals(2, listOfAssocuation.size());

		AttributeSet listOfAttribute = list.getAttributes();
		Assert.assertEquals(2, listOfAttribute.size());
		Assert.assertEquals(1, listOfAttribute.getClazzes().size());
		Assert.assertEquals(1, listOfAttribute.getAnnotations().size());
		Assert.assertEquals(1, listOfAttribute.getModifiers().size());
		Assert.assertEquals(2, listOfAttribute.getDataTypes().size());

//		ParameterSet	2044	69%	22	50%	2	7	4	15	0	5	0	1
	}

	@Test
	public void testJDLGraph() {
		GraphList graphList = new GraphList();
		Clazz uni = graphList.createClazz("University");
		Clazz person = graphList.createClazz("Person");
		person.createAttribute("name", DataType.STRING);

		uni.withBidirectional(person, "has", Association.MANY, "owner", Association.ONE);

		IdMap map=new IdMap();
		JDLTokener tokener = new JDLTokener();
		BaseItem encode = map.encode(graphList, tokener);
		Assert.assertNotNull(encode);

		EMFTokener emfTokener = new EMFTokener();

		XMLEntity xmi = emfTokener.toXMI(graphList);
		Assert.assertNotNull(xmi);
//		System.out.println(xmi.toString(2));
	}
	
	@Test
	public void GraphConverterTest() {
		ClassModel model=new ClassModel();
		model.createClazz("Person");
		
		GraphConverter converter = new GraphConverter();
		TemplateResultFragment convertToMetaText = converter.convertToMetaText(model, true, true);
		Assert.assertNotNull(convertToMetaText);
//		System.out.println(convertToMetaText.getValue().toString());
	}

	@Test
	public void GraphConverterTestMethod() {
		ClassModel model=new ClassModel();
		Clazz person = model.createClazz("Person");
		Attribute attr = person.createAttribute("name", DataType.STRING);
		attr.with(Modifier.FINAL);
		Method method = person.createMethod("name", DataType.BOOLEAN, new Parameter(DataType.STRING));
		method.with(Modifier.FINAL);
		
		
		GraphConverter converter = new GraphConverter();
		TemplateResultFragment convertToMetaText = converter.convertToMetaText(model, true, false);
		Assert.assertNotNull(convertToMetaText);
//		System.out.println(convertToMetaText.getValue().toString());
		
//		System.out.println("#### HEADER ###");
//		System.out.println(""+convertToMetaText.getHeaders());
	}
	
	@Test
	public void GraphConverterTestAdvanced() {
		ClassModel oldModel = new ClassModel("de.uniks.model");
		oldModel.createClazz("Clazz");

		ClassModel model = new ClassModel("de.uniks.model");
		
//		Advance=	private void testClazzFileAddWithoutMetaWithFilesAdvance()
//		{
//			de.uniks.networkparser.ext.ClassModel model = new de.uniks.networkparser.ext.ClassModel("de.uniks.model");
//			de.uniks.networkparser.ext.ModelGenerator generator = model.getGenerator("src/main/java");
//			de.uniks.networkparser.graph.Clazz clazz = generator.createClazz("Clazz");
//			generator.applyChange();
//		}
		GraphConverter converter = new GraphConverter();
		TemplateResultFragment convertToMetaText = converter.convertToMetaText(model, false, false);
		
		Assert.assertNotNull(convertToMetaText);
//		System.out.println(convertToMetaText.getValue().toString());
	}

}
