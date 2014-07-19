package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import de.uniks.networkparser.bytes.ByteEntity;
import de.uniks.networkparser.bytes.ByteFilter;
import de.uniks.networkparser.bytes.ByteIdMap;
import de.uniks.networkparser.bytes.converter.ByteConverterBinary;
import de.uniks.networkparser.bytes.converter.ByteConverterHex;
import de.uniks.networkparser.bytes.converter.ByteConverterString;
import de.uniks.networkparser.event.ByteMessage;
import de.uniks.networkparser.event.creator.ByteMessageCreator;
import de.uniks.networkparser.interfaces.BufferedBytes;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.test.model.ChatMessage;
import de.uniks.networkparser.test.model.FullAssocs;
import de.uniks.networkparser.test.model.SortedMsg;
import de.uniks.networkparser.test.model.StringMessage;
import de.uniks.networkparser.test.model.Uni;
import de.uniks.networkparser.test.model.creator.ChatMessageCreator;
import de.uniks.networkparser.test.model.creator.FullAssocsCreator;
import de.uniks.networkparser.test.model.creator.SortedMsgCreator;
import de.uniks.networkparser.test.model.creator.StringMessageCreator;
import de.uniks.networkparser.test.model.creator.UniCreator;

public class ByteTest{
	@Test
	public void testString(){
		ByteEntity entity=new ByteEntity();
		entity.setValues(42);
		//48=00110000 42=00101010
		String value = entity.toString(new ByteConverterBinary(), true);
		System.out.println(value);
		assertEquals("0011011000101010", value);
		
		System.out.println(entity.getBytes(true).length());
	}
	
	@Test
	public void testByteEntity(){
		StringMessage msg=new StringMessage("Hallo Welt");
		ByteIdMap map=new ByteIdMap();
		map.addCreator(new StringMessageCreator());
		ByteItem data = map.encode(msg);
		// CLAZZ, StringMessage<0x02>, 0x00, 0x46, Hallo Welt(10 Bytes)
		byte[] array = data.getBytes(false).array();
		assertEquals(14, array.length);
	}
	
	@Test
	public void testSimpleEntity(){
		SortedMsg msg=new SortedMsg();
		msg.setNumber(42);
		ByteIdMap map=new ByteIdMap();
		map.addCreator(new SortedMsgCreator());
		ByteItem data = map.encode(msg);
		BufferedBytes bytes = data.getBytes(false);
		SortedMsg newMsg = (SortedMsg) map.decode(bytes);
		assertEquals("VALUE", 42, newMsg.getNumber());
	}
	
	@Test
	public void testChatMessage(){
		ChatMessage chatMessage=new ChatMessage();
		chatMessage.setText("Dies ist eine Testnachricht");
		chatMessage.setSender("Stefan Lindel");
		
		ByteIdMap byteMap = new ByteIdMap();
		byteMap.addCreator(new ChatMessageCreator());
		ByteItem msg=byteMap.encode(chatMessage);
//		String reference="C-Stefan ALindel\"FDies Aist Aeine ATestnachricht";
		String reference=" ?B .Stefan ALindel\"FDies Aist Aeine ATestnachricht";
		String vergleich=msg.toString();
		System.out.println(reference);
		System.out.println(vergleich);
		assertEquals("Wert vergleichen", reference.length(), vergleich.length());
	}
	
	@Test
	public void testByteDefault(){
		ByteIdMap map=new ByteIdMap();
		map.addCreator(new UniCreator());
		Uni uni = new Uni();
		uni.setName("Uni Kassel");
		ByteItem data = map.encode(uni);
		BufferedBytes byteBuffer = data.getBytes(false);
		assertEquals("AEde.uniks.networkparser.test.model.UniOUni Kassel", data.toString(new ByteConverterString()));
		assertEquals(50, byteBuffer.length());
		Uni decodeObj = (Uni) map.decode(byteBuffer);
		
		assertEquals(uni.getName(), decodeObj.getName());
	}
	
	@Test
	public void testMessages() throws RuntimeException {
		ByteIdMap map = new ByteIdMap();
		map.addCreator(new ByteMessageCreator());
		map.addCreator(new StringMessageCreator());

		ByteMessage message = new ByteMessage().withValue(new byte[] { 52, 50 });
		ByteItem stream = map.encode(message);
		ByteMessage newMessage = (ByteMessage) map.decode(stream.getBytes(false));
		byte[] values = newMessage.getValue();
		assertEquals(values.length, 2);
		assertEquals(values[0], 52);
		assertEquals(values[1], 50);
	}
	
	@Test
	public void testByteList(){
		ByteIdMap map=new ByteIdMap();
		map.addCreator(new FullAssocsCreator());
		FullAssocs uni = new FullAssocs();
		uni.addPerson("Maier");
		uni.addPerson("Schulz");
		uni.setAnswer(42);
		ByteItem msg = map.encode(uni);
		BufferedBytes byteBuffer = msg.getBytes(false);
		outputStream(byteBuffer);
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
		ByteIdMap map=new ByteIdMap();
		map.addCreator(new SortedMsgCreator());
		SortedMsg sortedMsg = new SortedMsg();
		sortedMsg.setNumber(23);
		ByteItem msg=map.encode(sortedMsg, new ByteFilter());
		BufferedBytes bytesBuffer = msg.getBytes(false);

//		outputStream(bytesBuffer);
		
		assertEquals("Len of not dynamic", 7, bytesBuffer.length());
		
		bytesBuffer = msg.getBytes(true);
		
//		outputStream(bytesBuffer);
		
		assertEquals("Len of dynamic", 4, bytesBuffer.length());
		
	}
	private void outputStream(BufferedBytes buffer){
		byte[] bytes=buffer.getValue(buffer.length()); 
		
		System.out.println("Length: "+bytes.length);
		boolean newline=false;
		for(int i=0;i<bytes.length;i++){
			if(bytes[i]<10){
				System.out.print(" 00"+(byte)bytes[i]);
				newline=false;
			}else if(bytes[i]<100){
				System.out.print(" 0"+(byte)bytes[i]);
				newline=false;
			}else{
				System.out.print(" "+(byte)bytes[i]);
				newline=false;
			}
			if((i+1)%10==0){
				newline=true;
				System.out.println("");
			}
		}
		if(!newline){
			System.out.println("");
		}
		buffer.withPosition(0);
	}
	
	@Test
	public void testByteHex() throws RuntimeException {
		StringMessage stringMessage = new StringMessage("Test");
		ByteIdMap map = new ByteIdMap();
		map.addCreator(new StringMessageCreator());
		ByteItem encode = map.encode(stringMessage);

		BufferedBytes master = encode.getBytes(false);
		byte[] byteArray=master.getValue(master.length());
		
		assertEquals(8, byteArray.length);
		// normal String
		assertEquals("# #\"OTest", encode.toString());
		
		String hexString = encode.toString(new ByteConverterHex());
		assertEquals("2302224F54657374", hexString);
		
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
		ByteIdMap map = new ByteIdMap();
		try {
			Object value=map.decode(new byte[] {});
			if(value!=null){
				fail("not possible");
			}
		} catch (RuntimeException e) {
		}
	}

	@Test
	public void testAssoc(){
		FullAssocs assocs=new FullAssocs();
		StringMessage msg=new StringMessage();
		msg.setValue("Testnachricht");
		assocs.setMessage(msg);
		assocs.addPassword("Stefan", "42");
		
		ByteIdMap map=new ByteIdMap();
		map.addCreator(new FullAssocsCreator());
		map.addCreator(new StringMessageCreator());
		
		ByteItem data = map.encode(assocs);
		BufferedBytes bytes = data.getBytes(false);
		outputStream(bytes);
		assertEquals("Length", 36, bytes.length());
		
		FullAssocs newAssocs=(FullAssocs) map.decode(bytes);
		
		assertEquals("Passwort fuer Stefan", "42", newAssocs.getPassword("Stefan"));
		assertEquals("Nachricht", "Testnachricht", newAssocs.getMessage().getValue());
	}
}
