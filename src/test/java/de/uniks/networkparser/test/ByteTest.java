package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.bytes.AES;
import de.uniks.networkparser.bytes.ByteEntity;
import de.uniks.networkparser.bytes.SHA1;
import de.uniks.networkparser.converter.ByteConverterAES;
import de.uniks.networkparser.converter.ByteConverterBinary;
import de.uniks.networkparser.converter.ByteConverterHex;
import de.uniks.networkparser.converter.ByteConverterString;
import de.uniks.networkparser.event.ByteMessage;
import de.uniks.networkparser.event.util.ByteMessageCreator;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.test.model.Apple;
import de.uniks.networkparser.test.model.AppleTree;
import de.uniks.networkparser.test.model.ChatMessage;
import de.uniks.networkparser.test.model.FullAssocs;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.StringMessage;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.AppleCreator;
import de.uniks.networkparser.test.model.util.AppleTreeCreator;
import de.uniks.networkparser.test.model.util.ChatMessageCreator;
import de.uniks.networkparser.test.model.util.FullAssocsCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StringMessageCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;

public class ByteTest{
	@Test
	public void testSHA1() throws NoSuchAlgorithmException, UnsupportedEncodingException {
		String text="Hallo Welt";
		Assert.assertEquals("28cbbc72d6a52617a7abbfff6756d04bbad0106a", SHA1.value(text).toString());
	}

	@Test
	public void testSimpleAES(){
		AES aes = new AES();			 // init AES encrypter class
		aes.withKey("kWmHe8xIsDpfzK4d");  // choose 16 byte password

		String data = "Hello world, here is some sample text.";
		Assert.assertEquals("Original text : [" +data+ "] [" +data.length()+ " bytes]", 38, data.length());

		String encrypted = aes.encode(data).toString();
		Assert.assertEquals("Encrypted text : [" +encrypted+ "] [" +encrypted.length()+ " bytes]", 64, encrypted.length());
		ByteConverterHex converter = new ByteConverterHex();

//		outputStream(encrypted.getBytes(), System.out);
		String hex = converter.toString(new ByteBuffer().with(encrypted)).replace(" ", "");
		outputStream(hex.getBytes(), null);
//		Assert.assertEquals("Encrypted text (as hex) : [" +hex+ "] [" +hex.length()+ " bytes]", 128, hex.length());

		String unencrypted = aes.decode(encrypted).toString();
		Assert.assertEquals("Unencrypted text : [" +unencrypted+ "] [" +unencrypted.length()+ " bytes]", 38, unencrypted.length());
	}

	@Test
	public void testAES(){
		ByteConverterAES aes = new ByteConverterAES();
		aes.withKey("kWmHe8xIsDpfzK4d");  // choose 16 byte password

		String data = "Hello world, here is some sample text.";
		Assert.assertEquals("Original text : [" +data+ "] [" +data.length()+ " bytes]", 38, data.length());

		String encrypted = aes.toString(data).toString();
		Assert.assertEquals("Encrypted text : [" +encrypted+ "] [" +encrypted.length()+ " bytes]", 64, encrypted.length());

		ByteConverterHex converter = new ByteConverterHex();

		String hex = converter.toString(new ByteBuffer().with(encrypted)).replace(" ", "");
		outputStream(hex.getBytes(), null);
//		Assert.assertEquals("Encrypted text (as hex) : [" +hex+ "] [" +hex.length()+ " bytes]", 128, hex.length());

		String unencrypted = new String(aes.decode(encrypted));
		Assert.assertEquals("Unencrypted text : [" +unencrypted+ "] [" +unencrypted.length()+ " bytes]", 38, unencrypted.length());
	}

	@Test
	public void testString(){
		ByteEntity entity= new ByteEntity();
		entity.setValues(42);
		//48=00110000 42=00101010
		String value = entity.toString(new ByteConverterBinary(), true);
		assertEquals("0011011000101010", value);

		Assert.assertEquals(2, entity.getBytes(true).length());
	}

	@Test
	public void testByteEntity(){
		StringMessage msg= new StringMessage("Hallo Welt");
		IdMap map= new IdMap();
		map.with(new StringMessageCreator());
		ByteItem data = map.toByteItem(msg);
		// CLAZZ, StringMessage<0x02>, 0x00, 0x46, Hallo Welt(10 Bytes)
		byte[] array = data.getBytes(false).array();
		assertEquals(14, array.length);
	}

	@Test
	public void testSimpleEntity(){
		SortedMsg msg= new SortedMsg();
		msg.setNumber(42);
		IdMap map= new IdMap();
		map.with(new SortedMsgCreator());
		ByteItem data = map.toByteItem(msg);
		ByteBuffer bytes = data.getBytes(false);
		SortedMsg newMsg = (SortedMsg) map.decode(bytes);
		assertEquals("VALUE", 42, newMsg.getNumber());
	}

	@Test
	public void testChatMessage(){
		ChatMessage chatMessage= new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");

		IdMap byteMap = new IdMap();
		byteMap.with(new ChatMessageCreator());
		ByteItem msg=byteMap.toByteItem(chatMessage);
//		String reference="C-Stefan ALindel\"FDies Aist Aeine ATestnachricht";
		String reference="#cK-Stefan ALindel\"ODies Aist Aeine ATestnachricht";
		String vergleich=msg.toString();
		assertEquals("Wert vergleichen", reference, vergleich);
		assertEquals("Wert vergleichen", reference.length(), vergleich.length());
	}

	@Test
	public void testByteDefault(){
		IdMap map= new IdMap();
		map.with(new UniversityCreator());
		University uni = new University();
		uni.setName("Uni Kassel");
		ByteItem data = map.toByteItem(uni);
		ByteBuffer byteBuffer = data.getBytes(false);
//		assertEquals("ALde.uniks.networkparser.test.model.UniversityOUni Kassel", data.toString(new ByteConverterString()));
		assertEquals("#uOUni Kassel", data.toString(new ByteConverterString()));
		assertEquals(13, byteBuffer.length());
		University decodeObj = (University) map.decode(byteBuffer);

		assertEquals(uni.getName(), decodeObj.getName());
	}

	@Test
	public void testMessages() throws RuntimeException {
		IdMap map = new IdMap();
		map.with(new ByteMessageCreator());
		map.with(new StringMessageCreator());

		ByteMessage message = new ByteMessage().withValue(new byte[] { 52, 50 });
		ByteItem stream = map.toByteItem(message);
		ByteMessage newMessage = (ByteMessage) map.decode(stream.getBytes(false));
		byte[] values = newMessage.getValue();
		assertEquals(values.length, 2);
		assertEquals(values[0], 52);
		assertEquals(values[1], 50);
	}

	@Test
	public void testByteList(){
		IdMap map= new IdMap();
		map.with(new FullAssocsCreator());
		FullAssocs uni = new FullAssocs();
		uni.addPerson("Maier");
		uni.addPerson("Schulz");
		uni.setAnswer(42);
		ByteItem msg = map.toByteItem(uni);
		ByteBuffer byteBuffer = msg.getBytes(false);
		outputStream(byteBuffer, null);
		assertEquals(24, byteBuffer.length());
//		outputStream(byteBuffer);
		String string = msg.toString();
		FullAssocs uni2 = (FullAssocs) map.decode(string);

		List<String> persons = uni2.getPersons();
		assertEquals(persons.get(0), "Maier");
		assertEquals(persons.get(1), "Schulz");
	}

	@Test
	public void testByteDynamic(){
		IdMap map= new IdMap();
		map.with(new SortedMsgCreator());
		SortedMsg sortedMsg = new SortedMsg();
		sortedMsg.setNumber(23);
		ByteItem msg=map.toByteItem(sortedMsg);
		ByteBuffer bytesBuffer = msg.getBytes(false);
//		outputStream(bytesBuffer);
		assertEquals("Len of not dynamic", 7, bytesBuffer.length());

		bytesBuffer = msg.getBytes(true);
//		outputStream(bytesBuffer);
		assertEquals("Len of dynamic", 4, bytesBuffer.length());

	}
	void outputStream(ByteBuffer buffer, PrintStream stream){
		outputStream(buffer.array(), stream);
	}
	void outputStream(byte[] bytes, PrintStream stream){
		if(stream == null) {
			return;
		}
		boolean newline=false;
		for (int i=0;i<bytes.length;i++){
			if(bytes[i]<10){
				stream.print(" 00" +(byte)bytes[i]);
				newline=false;
			} else if(bytes[i]<100){
				stream.print(" 0" +(byte)bytes[i]);
				newline=false;
			} else {
				stream.print(" " +(byte)bytes[i]);
				newline=false;
			}
			if((i+1)%10==0){
				newline=true;
				stream.println("");
			}
		}
		if(!newline){
			stream.println("");
		}
	}

	@Test
	public void testByteHex() throws RuntimeException {
		StringMessage stringMessage = new StringMessage("Test");
		IdMap map = new IdMap();
		map.with(new StringMessageCreator());
		ByteItem encode = map.toByteItem(stringMessage);

		ByteBuffer master = encode.getBytes(false);
		byte[] byteArray=master.array(master.length(), true);

		assertEquals(8, byteArray.length);
		// normal String
		assertEquals("#p\"OTest", encode.toString());

		String hexString = encode.toString(new ByteConverterHex());
		assertEquals("2370224F54657374", hexString);

//		byte[] byteString = ByteIdMap.toByteString(hexString);
//		int i = 0;
//		for (byte test : byteArray) {
//			assertEquals(test, byteString[i++]);
//		}

		StringMessage newMsg = (StringMessage) map.decode(hexString, new ByteConverterHex());
		assertEquals("Value of TextMessage", "Test", newMsg.getValue());
	}

	@Test
	public void testFehler() {
		IdMap map = new IdMap();
		try {
			Object value=map.decode(new ByteBuffer());
			if(value!=null){
				fail("not possible");
			}
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void testAssoc(){
		FullAssocs assocs= new FullAssocs();
		StringMessage msg= new StringMessage();
		msg.setValue("Testnachricht");
		assocs.setMessage(msg);
		assocs.addPassword("Stefan", "42");

		IdMap map= new IdMap();
		map.with(new FullAssocsCreator());
		map.with(new StringMessageCreator());

		ByteItem data = map.toByteItem(assocs);
		ByteBuffer bytes = data.getBytes(false);
		outputStream(bytes, null);
		assertEquals("Length", 36, bytes.length());

		FullAssocs newAssocs=(FullAssocs) map.decode(bytes);

		assertEquals("Passwort fuer Stefan", "42", newAssocs.getPassword("Stefan"));
		assertEquals("Nachricht", "Testnachricht", newAssocs.getMessage().getValue());
	}


	@Test
	public void testSerialization() {
		AppleTree appleTree = new AppleTree();

		appleTree.withHas(new Apple("0", 123.32f, 239f));
		appleTree.withHas(new Apple("1", 5644f, 564f));
		appleTree.withHas(new Apple("2", 1680f, 50f));
		appleTree.withHas(new Apple("3", 54f, 654f));
		appleTree.withHas(new Apple("4", 654f, 333f));

		IdMap map = new IdMap();
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());
		ByteItem item = map.toByteItem(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		Assert.assertEquals(175, bytes.length());
		String string = item.toString();
		Assert.assertEquals(245, string.length());
		Assert.assertEquals(245, map.toByteItem(appleTree, new Filter()).toString().length());
	}
	@Test
	public void testSimpleApple() {
		Apple apple = new Apple("4", 1, 3);
		IdMap map = new IdMap();
		map.with(new AppleCreator());
		ByteItem item = map.toByteItem(apple);
		ByteBuffer bytes = item.getBytes(true);
		Assert.assertEquals(62, bytes.length());
	}

	@Test
	public void testSimpleAppleTree() {
		AppleTree appleTree = new AppleTree();
		appleTree.withHas(new Apple("0", 123.32f, 239f));
		appleTree.withHas(new Apple("0", 123.32f, 239f));

//		appleTree.withHas(new Apple(1, 2, 3));
//		appleTree.withHas(new Apple(4, 5, 6));
//		appleTree.withHas(new Apple(7, 8, 9));
		IdMap map = new IdMap();
		map.with(new AppleCreator(), new AppleTreeCreator());
		ByteItem item = map.toByteItem(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		Assert.assertEquals(100, bytes.length());
	}

	@Test
	public void testSimpleAppleTreePrimitive() {
		AppleTree appleTree = new AppleTree();
		appleTree.withHas(new Apple("2100000000", 123.32f, 239f));
		appleTree.withHas(new Apple("2100000000", 123.32f, 239f));
		IdMap map = new IdMap();
		map.with(new AppleCreator(), new AppleTreeCreator());
		ByteItem item = map.toByteItem(appleTree);
		ByteBuffer bytes = item.getBytes(true);
		Assert.assertEquals(118, bytes.length());
	}

	@Test
	public void testSerializationTwoItems() {
		AppleTree appleTree = new AppleTree();

		appleTree.withHas(new Apple("1", 5644f, 564f));
		appleTree.withHas(new Apple("0", 123.32f, 239f));

		IdMap map = new IdMap();
		map.with(new AppleTreeCreator());
		map.with(new AppleCreator());
//		map.withCreator(new AppleTreeCreator(), new AppleCreator());
		ByteItem item = map.toByteItem(appleTree);

		ByteBuffer bytes = item.getBytes(true);
//		outputStream(bytes.array(), System.out);
		Assert.assertEquals(100, bytes.length());
		String string = item.toString();
		Assert.assertEquals(128, string.length());
	}
}
