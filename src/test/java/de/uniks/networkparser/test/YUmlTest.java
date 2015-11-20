package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.graph.GraphCardinality;
import de.uniks.networkparser.graph.GraphClazz;
import de.uniks.networkparser.graph.GraphDataType;
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
		jsonMap.withCreator(new ChatMessageCreator());
		GraphIdMap yumlParser = new GraphIdMap();
		yumlParser.withKeyValue(jsonMap.getKeyValue())
			.withCreator(jsonMap);

		String parseObject = yumlParser.parseObject(chatMessage);
		assertEquals(
				url
						+ "[J1.C1 : ChatMessage|sender=Stefan Lindel;txt=Dies ist eine Testnachricht;count=0;activ=false]-[J1.D2 : Date|value=1350978000017]",
				url + parseObject);

		jsonMap = new JsonIdMap();
		jsonMap.withCreator(new UniversityCreator());
		jsonMap.withCreator(new RoomCreator());
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
	public void testSimpleGrahList() {
		GraphList list = new GraphList();
		GraphClazz uni = list.with(new GraphClazz().withId("UniKassel").withClassName("University"));
		uni.withAttribute("name", GraphDataType.STRING);
		uni.withMethod("init()");
		GraphClazz student = list.with(new GraphClazz().withId("Stefan").withClassName("Student"));
		student.withAssoc(uni, "owner", GraphCardinality.ONE);
		YUMLConverter converter = new YUMLConverter();
		Assert.assertEquals("[University|name:String]-[Student]", converter.convert(list, true));
	}	
}
