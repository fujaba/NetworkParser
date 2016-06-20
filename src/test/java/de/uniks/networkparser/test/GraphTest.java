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
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.converter.DotConverter;
import de.uniks.networkparser.converter.GraphConverter;
import de.uniks.networkparser.converter.YUMLConverter;
import de.uniks.networkparser.event.util.DateCreator;
import de.uniks.networkparser.graph.Annotation;
import de.uniks.networkparser.graph.Association;
import de.uniks.networkparser.graph.AssociationTypes;
import de.uniks.networkparser.graph.Attribute;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.Clazz.ClazzType;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphImage;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.GraphPatternMatch;
import de.uniks.networkparser.graph.GraphTokener;
import de.uniks.networkparser.graph.Method;
import de.uniks.networkparser.graph.Modifier;
import de.uniks.networkparser.graph.Parameter;
import de.uniks.networkparser.graph.util.AnnotationSet;
import de.uniks.networkparser.graph.util.AssociationSet;
import de.uniks.networkparser.graph.util.AttributeSet;
import de.uniks.networkparser.graph.util.ClazzSet;
import de.uniks.networkparser.graph.util.MethodSet;
import de.uniks.networkparser.graph.util.ModifierSet;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.Condition;
import de.uniks.networkparser.interfaces.Entity;
import de.uniks.networkparser.interfaces.EntityList;
import de.uniks.networkparser.json.JsonArray;
import de.uniks.networkparser.json.JsonObject;
import de.uniks.networkparser.list.SimpleList;
import de.uniks.networkparser.logic.BooleanCondition;
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
import de.uniks.networkparser.test.model.ludo.creator.DiceCreator;
import de.uniks.networkparser.test.model.ludo.creator.FieldCreator;
import de.uniks.networkparser.test.model.ludo.creator.LudoCreator;
import de.uniks.networkparser.test.model.ludo.creator.PawnCreator;
import de.uniks.networkparser.test.model.ludo.creator.PlayerCreator;
import de.uniks.networkparser.test.model.util.ChatMessageCreator;
import de.uniks.networkparser.test.model.util.RoomCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StudentCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;
import de.uniks.networkparser.xml.HTMLEntity;

public class GraphTest {
	@Test
	public void testDataType() {
		DataType dataType = DataType.create("int");
		Assert.assertEquals(dataType.toString(), "DataType.INT");
	}

	@Test
	public void testGraph() {
		GraphList list = new GraphList();
		Clazz node = new Clazz().with("Item");
		node.with(new GraphImage().with("karli.png"));
		list.with(node);

		GraphConverter converter = new GraphConverter();
		Assert.assertEquals(
				"{\"typ\":\"classdiagram\",\"style\":null,\"nodes\":[{\"typ\":\"clazz\",\"id\":\"Item\",\"head\":{\"src\":\"karli.png\"}}]}",
				converter.convert(list, false).toString());
	}

	@Test
	public void testSuperClasses() {
		Clazz student = new Clazz().with("Student");
		Clazz person = new Clazz().with("Person");
		student.withSuperClazz(person);

		Assert.assertEquals(student.getSuperClass(), person);
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
		JsonArray jsonArray = map.toJsonArray(list, new Filter().withFull(true).withPropertyRegard(BooleanCondition.value(true)));
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
		JsonArray jsonArray = map.toJsonArray(list, new Filter().withFull(true).withPropertyRegard(BooleanCondition.value(true)));
		Assert.assertEquals(3, jsonArray.size());
		
		jsonArray = map.toJsonArray(list, new Filter().withFull(true).withPropertyRegard(BooleanCondition.value(true)));
		Assert.assertEquals(3, jsonArray.size());
	}
	
	@Test
	public void testComplex() {
		Clazz student = new Clazz().with("Student");
		Clazz person = new Clazz().with("Person");
		Clazz uni = new Clazz().with("Uni");
		student.withSuperClazz(person);

		Assert.assertEquals(student.getSuperClass(), person);
		Assert.assertTrue(person.getKidClazzes(false).contains(student));

		uni.withBidirectional(student, "stud", Cardinality.MANY, "owner", Cardinality.ONE);
	}

	@Test
	public void testModell() {
		Clazz clazz = new Clazz().with("Student");
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
		jsonIdMap.with(new FieldCreator()).with(new LudoCreator()).with(new PawnCreator()).with(new PlayerCreator());

		Ludo ludo = new Ludo();

		Player tom = ludo.createPlayers().withName("Tom").withColor("blue");
		Player sabine = ludo.createPlayers().withName("Sabine").withColor(RED);
		// tom.createDice().withValue(6);
		tom.createPawns().withColor("blue"); // IS IS THE DIFFERENT

		Field tomStartField = tom.createStart().withColor("blue").withKind("start");
		sabine.createPawns().withColor(RED).withPos(tomStartField);

		JsonArray jsonArray = jsonIdMap.toJsonArray(ludo);
		// showDebugInfos(jsonArray, 1428, System.out);

		GraphConverter graphConverter = new GraphConverter();

		// May be 8 Asssocs and write 11
		JsonObject converter = graphConverter.convertToJson(GraphTokener.CLASS, jsonArray, true);

		YUMLConverter converterYUML = new YUMLConverter();
		GraphList root = graphConverter.convertGraphList(GraphTokener.CLASS, jsonArray);
		Assert.assertEquals(
				"[Ludo]-[Player|color:String;name:String],[Player]-[Field|color:String;kind:String],[Player]-[Pawn|color:String],[Field]-[Pawn|color:String]",
				converterYUML.convert(root, true));

		showDebugInfos(converter, 1569, null);
	}

	@Test
	public void testLudoStoryboard() {
		IdMap jsonIdMap = new IdMap();
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
		showDebugInfos(jsonArray, 5089, null);
		GraphConverter graphConverter = new GraphConverter();

		// May be 8 Asssocs and write 11
		JsonObject converter = graphConverter.convertToJson(GraphTokener.CLASS, jsonArray, true);
		showDebugInfos(converter, 2496, null);
	}

	private void showDebugInfos(Entity json, int len, PrintStream stream) {
		if (stream != null) {
			stream.println("###############################");
			stream.println(json.toString(2));
			stream.println("###############################");
		}
		Assert.assertEquals(len, json.toString(2).length());
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
		map.with(new SortedMsgCreator());

		JsonArray jsonArray = map.toJsonArray(root, new Filter().withFull(true));
		GraphConverter graphConverter = new GraphConverter();
		JsonObject objectModel = graphConverter.convertToJson(GraphTokener.OBJECT, jsonArray, true);
		showDebugInfos(objectModel, 658, null);

		JsonObject clazzModel = graphConverter.convertToJson(GraphTokener.CLASS, jsonArray, true);
		showDebugInfos(clazzModel, 492, null);
		Assert.assertEquals(new CharacterBuffer().withLine("{").withLine("  \"typ\":\"classdiagram\",")
				.withLine("  \"style\":null,").withLine("  \"nodes\":[").withLine("    {")
				.withLine("      \"typ\":\"clazz\",").withLine("      \"id\":\"SortedMsg\",")
				.withLine("      \"attributes\":[").withLine("        \"number:Integer\",")
				.withLine("        \"msg:String\"").withLine("      ]").withLine("    }").withLine("  ],")
				.withLine("  \"edges\":[").withLine("    {").withLine("      \"typ\":\"ASSOCIATION\",")
				.withLine("      \"source\":{").withLine("        \"cardinality\":\"one\",")
				.withLine("        \"property\":\"child\",").withLine("        \"id\":\"SortedMsg\"")
				.withLine("      },").withLine("      \"target\":{").withLine("        \"cardinality\":\"one\",")
				.withLine("        \"property\":\"parent\",").withLine("        \"id\":\"SortedMsg\"")
				.withLine("      }").withLine("    }").withLine("  ]").with("}").toString(), clazzModel.toString(2));
	}

	@Test
	public void testClazzTest() {
		Clazz ludo = new Clazz().with("Ludo");
		Clazz player = new Clazz().with("Player");
		ludo.withBidirectional(player, "players", Cardinality.MANY, "game", Cardinality.ONE);
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

		JsonObject converter = graphConverter.convertToJson(GraphTokener.CLASS, jsonArray, true);
		showDebugInfos(converter, 569, null);
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
		jsonMap.with(new ChatMessageCreator());
		IdMap yumlParser = new IdMap();
		yumlParser.withKeyValue(jsonMap.getKeyValue())
			.with(jsonMap);

		String parseObject = yumlParser.toObjectDiagram(chatMessage).toString();
		assertEquals(
				url
						+ "[J1.C1 : ChatMessage|sender=Stefan Lindel;txt=Dies ist eine Testnachricht;count=0;activ=false]-[J1.D2 : Date|value=1350978000017]",
				url + parseObject);

		jsonMap = new IdMap();
		jsonMap.with(new UniversityCreator());
		jsonMap.with(new RoomCreator());
		University uni = new University();
		uni.setName("Wilhelmshoehe Allee");
		Room room = new Room();
		room.setName("1340");
		uni.addToRooms(room);

		assertEquals(url + "[J1.U3 : University]",
				url + yumlParser.toObjectDiagram(uni).toString());

		assertEquals(url + "[University]", url + yumlParser.toClassDiagram(uni).toString());
	}

	@Test
	public void testSimpleGraphList() {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz().with("UniKassel").with("University"));
		uni.createAttribute("name", DataType.STRING);
		uni.createMethod("init()");
		Clazz student = list.with(new Clazz().with("Stefan").with("Student"));
		student.withUniDirectional(uni, "owner", Cardinality.ONE);
		YUMLConverter converter = new YUMLConverter();
		Assert.assertEquals("[University|name:String]<-[Student]", converter.convert(list, true));
	}

	@Test
	public void testSimpleBiGraphList() {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz().with("UniKassel").with("University"));
		uni.createAttribute("name", DataType.STRING);
		uni.createMethod("init()");
		Clazz student = list.with(new Clazz().with("Stefan").with("Student"));
		student.withBidirectional(uni, "owner", Cardinality.ONE, "students", Cardinality.MANY);
		YUMLConverter converter = new YUMLConverter();
		Assert.assertEquals("[University|name:String]-[Student]", converter.convert(list, true));
	}

	@Test
	public void testSimpleYUMLGraph() {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz().with("UniKassel").with("University"));
		uni.createAttribute("name", DataType.STRING);
		list.with(new Clazz().with("Stefan").with("Student"));
		YUMLConverter converter = new YUMLConverter();
		Assert.assertEquals("[University|name:String],[Student]", converter.convert(list, true));
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
		GraphList model = new GraphList().withTyp(GraphTokener.CLASS);

		Clazz abstractArray = model.with(new Clazz().with("AbstractArray"));
		abstractArray.createAttribute("elements", DataType.create("Object[]"));
		abstractArray.createAttribute("size", DataType.INT);
		abstractArray.createAttribute("index", DataType.INT);
		abstractArray.createAttribute("flag", DataType.BYTE);
		Clazz baseItem = model.with(new Clazz().with(ClazzType.INTERFACE).with("BaseItem"));
		Clazz iterable = model.with(new Clazz().with("Iterable<V>"));
		Clazz abstractList = model.with(new Clazz().with("AbstractList<V>"));
		Clazz simpleList = model.with(new Clazz().with("SimpleList<V>"));
		Clazz simpleSet = model.with(new Clazz().with("SimpleSet<V>"));
		Clazz simpleKeyValueList = model.with(new Clazz().with("SimpleKeyValueList<K, V>"));
		Clazz map = model.with(new Clazz().with("Map<K, V>"));
		Clazz list = model.with(new Clazz().with("List<V>"));
		Clazz set = model.with(new Clazz().with("Set<V>"));

//		baseItem.withInterface(true);

		model.with(Association.create(abstractArray, baseItem).with(AssociationTypes.IMPLEMENTS));
		model.with(Association.create(abstractArray, iterable).with(AssociationTypes.IMPLEMENTS));
		model.with(Association.create(abstractList, abstractArray).with(AssociationTypes.GENERALISATION));

		model.with(Association.create(simpleKeyValueList, abstractArray).with(AssociationTypes.GENERALISATION));
		model.with(Association.create(simpleList, abstractList).with(AssociationTypes.GENERALISATION));
		model.with(Association.create(simpleSet, abstractList).with(AssociationTypes.GENERALISATION));

		model.with(Association.create(simpleKeyValueList, map).with(AssociationTypes.IMPLEMENTS));
		model.with(Association.create(simpleList, list).with(AssociationTypes.IMPLEMENTS));
		model.with(Association.create(simpleSet, set).with(AssociationTypes.IMPLEMENTS));

		docEnvironment.writeJson("simpleCollection.html", "../src/main/resources/de/uniks/networkparser/graph/", new GraphConverter().convertToJson(model, true));
	}

	@Test
	public void testWriteSimpleHTML() {
		HTMLEntity htmlEntity = new HTMLEntity();
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/diagramstyle.css");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/graph.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/dagre.min.js");
		htmlEntity.withHeader("../src/main/resources/de/uniks/networkparser/graph/drawer.js");

		GraphList model = new GraphList().withTyp(GraphTokener.CLASS);
		Clazz uni = model.with(new Clazz().with("University"));
		uni.createAttribute("name", DataType.STRING);
		Clazz person = model.with(new Clazz().with("Person"));

		uni.withBidirectional(person, "has", Cardinality.MANY, "studis", Cardinality.ONE);
		Assert.assertEquals(669, htmlEntity.withGraph(model).toString(2).length());
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
		Clazz uni = list.with(new Clazz().with("UniKassel").with("University"));
		uni.createAttribute("name", DataType.STRING);
		uni.createMethod("init()");
		Clazz student = list.with(new Clazz().with("Stefan").with("Student"));
		student.withUniDirectional(uni, "owner", Cardinality.ONE);

		String convert = list.toString(new DotConverter(true));

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
	public void testFullGraph() {
		GraphList model = new GraphList();
		model.with("de.uniks.networkparser");
		Clazz person = model.createClazz("Person");
		Clazz uni = model.createClazz("University");
		ClazzSet clazzes = model.getClazzes();
		Assert.assertEquals(person, clazzes.get(0));
		Assert.assertEquals(uni, clazzes.get(1));
		
		Attribute name = person.createAttribute("name", DataType.STRING);
		Attribute id = person.createAttribute("id", DataType.INT);
		
		Method initMethod = person.createMethod("init").with(Annotation.OVERRIDE);
		Method toStringMethod = person.createMethod("toString", new Parameter(DataType.INT)).with(DataType.STRING);
		person.withBidirectional(uni, "owner", Cardinality.ONE, "studs", Cardinality.MANY);

		AttributeSet attributes = person.getAttributes();
		Assert.assertEquals(name, attributes.get(0));
		Assert.assertEquals(id, attributes.get(1));
		
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
		Assert.assertEquals(1, listOfAnnotation.getAttributes().size());
		
		Modifier private1 = Modifier.PRIVATE;
		initMethod.with(private1);
		name.with(private1);
		person.with(private1);
		
		ModifierSet listOfModifier = new ModifierSet().with(initMethod.getModifier());
		listOfModifier.with(name.getModifier());
		listOfModifier.with(person.getModifier());
		Assert.assertEquals(1, listOfModifier.getClazzes().size());
		Assert.assertEquals(1, listOfModifier.getMethods().size());
		Assert.assertEquals(1, listOfModifier.getAttributes().size());
		
		// Navigate over Full Model
		ClazzSet list = new ClazzSet().with(person, uni);
		Assert.assertEquals(2, list.getModifiers().size());
		Assert.assertEquals(2, list.getMethods().size());

		
		listOfAnnotation = list.getAnnotations();
		Assert.assertEquals(1, listOfAnnotation.size());
		
		AssociationSet listOfAssocuation = list.getAssociations();
		Assert.assertEquals(1, listOfAssocuation.getClazzes().size());
		Assert.assertEquals(1, listOfAssocuation.getOther().size());
		Assert.assertEquals(1, listOfAssocuation.getOtherClazz().size());
		Assert.assertEquals(1, listOfAssocuation.size());

		AttributeSet listOfAttribute = list.getAttributes();
		Assert.assertEquals(2, listOfAttribute.size());
		Assert.assertEquals(1, listOfAttribute.getClazzes().size());
		Assert.assertEquals(1, listOfAttribute.getAnnotations().size());
		Assert.assertEquals(1, listOfAttribute.getModifiers().size());
		Assert.assertEquals(2, listOfAttribute.getDataTypes().size());

//		ParameterSet	2044	69%	22	50%	2	7	4	15	0	5	0	1
	}
}
