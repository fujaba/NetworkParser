package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.ChatMessage;
import de.uniks.networkparser.test.model.Entity;
import de.uniks.networkparser.test.model.FIXMLMessage;
import de.uniks.networkparser.test.model.JabberBindMessage;
import de.uniks.networkparser.test.model.JabberChatMessage;
import de.uniks.networkparser.test.model.ListItem;
import de.uniks.networkparser.test.model.StringMessage;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.XMLTestEntity;
import de.uniks.networkparser.test.model.XMLTestItem;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.AppleTreeCreator;
import de.uniks.networkparser.test.model.util.ApplicationMessageCreator;
import de.uniks.networkparser.test.model.util.ChatMessageCreator;
import de.uniks.networkparser.test.model.util.EntityCreator;
import de.uniks.networkparser.test.model.util.FIXMLMessageCreator;
import de.uniks.networkparser.test.model.util.JabberChatMessageCreator;
import de.uniks.networkparser.test.model.util.ListItemCreator;
import de.uniks.networkparser.test.model.util.MyXMLEntityCreator;
import de.uniks.networkparser.test.model.util.StringMessageCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;
import de.uniks.networkparser.test.model.util.XMLTestItemCreator;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

public class XMLTest extends IOClasses{
	@Test
	public void testSimpleExport(){
		AppleTree appleTree = new AppleTree();
		appleTree.addToHas(new Apple());

		IdMap map=new IdMap();
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());

		Assert.assertEquals(133, map.toSimpleXML(appleTree).toString().length());
	}

	@Test
	public void test(){
		XMLEntity xmlEntity = new XMLEntity().withValue("<chatmsg folder=\"C:\\temp\\\\\" />");
		Assert.assertEquals("C:\\temp\\", xmlEntity.getValue("folder"));
	}

	@Test
	public void testSimple(){
		String xml="<br/><br/>";
		IdMap map= new IdMap();
		map.decode(xml);

		xml="<listitem>" +
				"<item value=\"Stefan\"/>" +
				"<item value=\"Nicole\"/>" +
				"<item value=\"Papa\">" +
				"	<item value=\"Sophie\"/>" +
				"</item>" +
				"</listitem>";
		map.with(new ListItemCreator());
		map.with(new EntityCreator());
		ListItem listItem = (ListItem) map.decode(xml);
		assertEquals(3, listItem.getChild().size());
		Entity[] children= new Entity[3];
		listItem.getChild().toArray(children);

		assertEquals("Stefan", children[0].get("value"));
		assertEquals("Nicole", children[1].get("value"));
		assertEquals("Papa", children[2].get("value"));
		assertEquals("Sophie", children[2].getChild().get("value"));

		Entity childByName = listItem.getChildByName("Papa");
		assertNotNull(childByName.getChild());
	}

	@Test
	public void testEscape(){
		String xml="<chatmsg id=\"42\\\" name=\"Stefan\"></chatmsg>";
		XMLEntity xmlEntity = new XMLEntity().withValue(xml);
		Assert.assertEquals("42\\", xmlEntity.getString("id"));
		Assert.assertEquals("Stefan", xmlEntity.getString("name"));
	}

	@Test
	public void testSonderfaelle(){
		String xml="<chatmsg sender=\"Stefan (\\\"Eraser\\\")\">0<1</chatmsg>";
		IdMap map= new IdMap();
		map.with(new MyXMLEntityCreator());
		XMLTestEntity decode = (XMLTestEntity) map.decode(xml);
		assertEquals("Stefan (\\\"Eraser\\\")", decode.getSender());
		assertEquals("0<1", decode.getText());
	}

	@Test
	public void simpleXMLDOM(){
		String xml="<chatmsg sender=\"Stefan (\\\"Eraser\\\")\"><child item=\"child\">Value</child><child item=\"2.Kind\"/></chatmsg>";
		XMLEntity xmlEntity = new XMLEntity().withValue(new XMLTokener().withBuffer(xml).withAllowQuote(true));
		xmlEntity.toString();
		assertEquals(1, xmlEntity.size());
		assertEquals(xml, xmlEntity.toString());
	}

	@Test
	public void testChildXML(){
		String xml = "<uni name=\"Kassel\"><!-- Meine erster Test --><fg value=\"se\"><no><id></id></no><user>Stefan Lindel</user></fg></uni>";
		String xmlWithoutComment = "<uni name=\"Kassel\"><fg value=\"se\"><user>Stefan Lindel</user></fg></uni>";
		IdMap map = new IdMap();
		map.with(new UniversityCreator());
		University entity = (University) map.decode(xml);
		assertEquals("Kassel", entity.getName());
		assertEquals("se", entity.getValue());
		assertEquals("Stefan Lindel", entity.getUser());

		assertEquals(xmlWithoutComment, map.toSimpleXML(entity).toString());
	}

	@Test
	public void createXML(){
		XMLEntity xmlEntity;
		xmlEntity= new XMLEntity().withValue("<p id=\"42\" />");
		assertEquals("42", xmlEntity.get("id"));

		xmlEntity= new XMLEntity().withValue("<p id=\"42\"/>");
		assertEquals("42", xmlEntity.get("id"));

		xmlEntity= new XMLEntity().withValue("<p id=\"42\"></p>");
		assertEquals("42", xmlEntity.get("id"));
	}

	@Test
	public void testXML(){
		String xml="<chatmsg>ich <b>bin</b> gut</chatmsg>";
		IdMap map= new IdMap();
		map.with(new MyXMLEntityCreator());
		XMLTestEntity decode = (XMLTestEntity) map.decode(xml);
		assertEquals("ich <b>bin</b> gut", decode.getText());
		xml="<chatmsg>ich bin<hr/> gut</chatmsg>";
		decode = (XMLTestEntity) map.decode(xml);
		assertEquals("ich bin<hr/> gut", decode.getText());
	}

	@Test
	public void testUni() {
		String xml = "<uni name=\"Kassel\"><fg value=\"se\"><user>Stefan Lindel</user></fg></uni>";
		IdMap map = new IdMap();
		map.with(new UniversityCreator());
		University entity = (University) map.decode(xml);
		assertEquals("Kassel", entity.getName());
		assertEquals("se", entity.getValue());
		assertEquals("Stefan Lindel", entity.getUser());

		assertEquals(xml, map.toSimpleXML(entity).toString());
	}

	@Test
	public void testXMLUniExt(){
		String xml = "<uni name=\"Kassel\"><child><value>Ich</value></child><fg value=\"se\"><user>Stefan Lindel</user></fg><not-supported/><not-supported></not-supported><!-- NIX VERSTEHEN --></uni>";
		IdMap map = new IdMap();
		map.with(new UniversityCreator());
		University entity = (University) map.decode(xml);
		assertEquals("Kassel", entity.getName());
		assertEquals("se", entity.getValue());
		assertEquals("Stefan Lindel", entity.getUser());
		assertEquals("Ich", entity.getIch());
	}

	@Test
	public void testXMLwithTwoChildrenUniExt(){
		String xml = "<uni name=\"Kassel\"><child /><child /></uni>";
		IdMap map = new IdMap();
		XMLEntity entity = (XMLEntity) map.decode(xml);
		Assert.assertEquals(2, entity.getChildrenCount());
	}

	@Test
	public void testXMLwithTwoChildrenAndValues(){
		String xml = "<row r=\"3\" spans=\"1:3\"><c r=\"A3\" s=\"1\"/><c r=\"B3\" t=\"s\"><v>6</v></c><c r=\"C3\" t=\"s\"><v>7</v></c></row>";
		IdMap map = new IdMap();
		XMLEntity entity = (XMLEntity) map.decode(xml);
		Assert.assertEquals(3, entity.getChildrenCount());
	}

	@Test
	public void testChatMessage(){
		ChatMessage chatMessage= new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");

		IdMap map = new IdMap();
		map.with(new ChatMessageCreator());

		String reference="<chatmsg sender=\"Stefan Lindel\" txt=\"Dies ist eine Testnachricht\"/>";
		XMLEntity actual=map.toSimpleXML(chatMessage);
		assertEquals("WERT Vergleichen", reference, actual.toString(2));
		assertEquals(reference.length(), actual.toString(2).length());

		String msg = actual.toString(2);

		IdMap mapDecoder = new IdMap();
		mapDecoder.with(new ChatMessageCreator());

		ChatMessage newChatMsg = (ChatMessage) mapDecoder.decode(new XMLEntity().withValue(msg));
		Assert.assertNotNull(newChatMsg);
	}

	@Test
	public void testXMLParser() {
		IdMap map = new IdMap();
		map.with(new StringMessageCreator());

		StringMessage msg = new StringMessage("Hallo World");

		XMLEntity xml = map.toSimpleXML(msg);
//		"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
		assertEquals("<p value=\"Hallo World\"/>", xml.toString(0));

		Object result = map.decode("<p value=\"Hallo Welt\" />");
		assertEquals(((StringMessage) result).getValue(), "Hallo Welt");
		result = map.decode(xml.toString());
		assertEquals(((StringMessage) result).getValue(), "Hallo World");
		result = map.decode("<p value=\"Hallo Welt\"></p>");
		assertEquals(((StringMessage) result).getValue(), "Hallo Welt");
	}

	@Test
	public void testXMLChatMessage() {
		String xml = "<message to=\"androidpeer@googlemail.com\" type=\"chat\" id=\"28\" from=\"peercenter@googlemail.com/Talk.v1054B9F0AA7\"><body>test</body><active xmlns=\"http://jabber.org/protocol/chatstates\"/><nos:x value=\"disabled\" xmlns:nos=\"google:nosave\"/><arc:record otr=\"false\" xmlns:arc=\"http://jabber.org/protocol/archive\"/></message>";
		IdMap map = new IdMap();
		map.with(new JabberChatMessageCreator());
//		map.addCreator(new JabberBindMessageCreator());
		JabberChatMessage chat = (JabberChatMessage) map.decode(xml);
		assertEquals("test", chat.getBody());

		JabberChatMessage newMessage = new JabberChatMessage();
		newMessage.setId("Cw0pc-0");
		newMessage.setTo("peercenter@googlemail.com");
		newMessage.setBody("Hallo Welt");
		XMLEntity encoding = map.toSimpleXML(newMessage);

		assertEquals(
				"<message to=\"peercenter@googlemail.com\" id=\"Cw0pc-0\"><body>Hallo Welt</body></message>",
				encoding.toString());
		JabberBindMessage jabberBind = new JabberBindMessage();
		jabberBind.setId("TEST");
		jabberBind.setResource("AndroidPeer");

		ChatMessage chatMessage= new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");

		IdMap xmlMap = new IdMap();
		xmlMap.with(new ChatMessageCreator());
		String reference="<chatmsg sender=\"Stefan Lindel\" txt=\"Dies ist eine Testnachricht\"/>";
		assertEquals("WERT Vergleichen", reference, xmlMap.toSimpleXML(chatMessage).toString(2));
	}

	public static StringBuilder readFile(File file) throws IOException {
		StringBuilder result = new StringBuilder();
		BufferedReader in = new BufferedReader(new FileReader(file));

		String line = in.readLine();
		while (line != null) {
			result.append(line).append('\n');
			line = in.readLine();
		}
		in.close();

		return result;
	}

	@Test
	public void testXMLTest() throws IOException{
		XMLTestItem item = new XMLTestItem();
		item.setBody("Hallo Welt");
		item.setId(42);
		item.setUser("Stefan");
		item.setValue("new Value");

		IdMap map = new IdMap();
		map.with(new XMLTestItemCreator());
		XMLEntity xmlEmF = map.toSimpleXML(item);
		Assert.assertEquals("<item id=\"42\"><user>Stefan</user><body txt=\"Hallo Welt\">new Value</body></item>", xmlEmF.toString());

		XMLTestItem newItem = (XMLTestItem) map.decode(xmlEmF.toString());
		Assert.assertEquals(item.getBody(), newItem.getBody());
		Assert.assertEquals(item.getId(), newItem.getId());
		Assert.assertEquals(item.getUser(), newItem.getUser());
		Assert.assertEquals(item.getValue(), newItem.getValue());
	}

	@Test
	public void testXMLCompare(){
		XMLEntity xmlA = new XMLEntity().withKeyValue("id", 42).setType("p");
		XMLEntity xmlB = new XMLEntity().withKeyValue("id", 42).setType("p");
		xmlA.withKeyValue("no", 23);
		xmlB.withKeyValue("no", 24);
		xmlA.withChild(new XMLEntity().setType("1"));
		xmlA.withChild(new XMLEntity().setType("2"));
		xmlB.withChild(new XMLEntity().setType("1"));
		xmlB.withChild(new XMLEntity().setType("3"));

		Assert.assertFalse(EntityUtil.compareEntity(xmlA, xmlB));
		Assert.assertEquals("<p no=\"23\"><2/></p>", xmlA.toString());
		Assert.assertEquals("<p no=\"24\"><3/></p>", xmlB.toString());
	}
	
	@Test
	public void testPattern() {
		String XMLText = readFile("test3.xml").toString();

		IdMap map= new IdMap();
		map.with(new FIXMLMessageCreator());
		map.with(new ApplicationMessageCreator());

		FIXMLMessage item = (FIXMLMessage) map.decode(XMLText);
		assertNotNull(item);
		assertNotNull(item.getApplicationmessage());
	}

	@Test
	public void testSimpleXMLEntity(){
		String str = readFile("test3.xml").toString();

		XMLEntity item= new XMLEntity();
		item.withValue(str);
		Assert.assertEquals(505, item.toString().length());
	}
	
	@Test
	public void testJISMEngine(){
		StringBuffer stringBuffer = readFile("template.html");

		IdMap decoder= new IdMap();
		String data = stringBuffer.toString();
		Object decode = decoder.decode(data);
		assertNotNull(decode);
	}
}
