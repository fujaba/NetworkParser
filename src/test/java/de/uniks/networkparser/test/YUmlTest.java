package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import java.util.Date;
import org.junit.Assert;
import org.junit.Test;
import de.uniks.networkparser.graph.Cardinality;
import de.uniks.networkparser.graph.Clazz;
import de.uniks.networkparser.graph.DataType;
import de.uniks.networkparser.graph.GraphIdMap;
import de.uniks.networkparser.graph.GraphList;
import de.uniks.networkparser.graph.YUMLConverter;
import de.uniks.networkparser.json.JsonIdMap;
import de.uniks.networkparser.test.model.ChatMessage;
import de.uniks.networkparser.test.model.Room;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.ChatMessageCreator;
import de.uniks.networkparser.test.model.util.RoomCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class YUmlTest {
	@Test
	public void testYUML() {
		String url = "http://yuml.me/diagram/class/";
		ChatMessage chatMessage = new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");
		Date date = new Date();
		date.setTime(1350978000017L);
		chatMessage.setDate(date);

		JsonIdMap jsonMap = new JsonIdMap();
		jsonMap.with(new ChatMessageCreator());
		GraphIdMap yumlParser = new GraphIdMap();
		yumlParser.withKeyValue(jsonMap.getKeyValue())
			.with(jsonMap);

		String parseObject = yumlParser.parseObject(chatMessage);
		assertEquals(
				url
						+ "[J1.C1 : ChatMessage|sender=Stefan Lindel;txt=Dies ist eine Testnachricht;count=0;activ=false]-[J1.D2 : Date|value=1350978000017]",
				url + parseObject);

		jsonMap = new JsonIdMap();
		jsonMap.with(new UniversityCreator());
		jsonMap.with(new RoomCreator());
		University uni = new University();
		uni.setName("Wilhelmshoehe Allee");
		Room room = new Room();
		room.setName("1340");
		uni.addToRooms(room);

		assertEquals(url + "[J1.U3 : University]",
				url + yumlParser.parseObject(uni));

		assertEquals(url + "[University]", url + yumlParser.parseClass(uni));
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
	public void testSimpleGraph() {
		GraphList list = new GraphList();
		Clazz uni = list.with(new Clazz().with("UniKassel").with("University"));
		uni.createAttribute("name", DataType.STRING);
		list.with(new Clazz().with("Stefan").with("Student"));
		YUMLConverter converter = new YUMLConverter();
		Assert.assertEquals("[University|name:String],[Student]", converter.convert(list, true));
	}
}
