package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.converter.ByteConverterHex;
import de.uniks.networkparser.converter.ByteConverterString;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.test.model.FullAssocs;
import de.uniks.networkparser.test.model.StringMessage;
import de.uniks.networkparser.test.model.University;
import de.uniks.networkparser.test.model.util.FullAssocsCreator;
import de.uniks.networkparser.test.model.util.SortedMsgCreator;
import de.uniks.networkparser.test.model.util.StringMessageCreator;
import de.uniks.networkparser.test.model.util.UniversityCreator;
import de.uniks.networkparser.xml.XMLEntity;
import de.uniks.networkparser.xml.XMLTokener;

public class SimpleTest {
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
}
