package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.bytes.BitEntity;
import de.uniks.networkparser.bytes.BitValue;
import de.uniks.networkparser.bytes.ByteSimpleMap;
import de.uniks.networkparser.test.model.BitDate;
import de.uniks.networkparser.test.model.NumberFormat;
import de.uniks.networkparser.test.model.util.BitDateCreator;
import de.uniks.networkparser.test.model.util.NumberFormatCreator;

public class BitMapTest {

	@Test
	public void testSimpleBits(){
		// 42 = 101010
		// 23 = 010111
		//  5 = 000101
		//  8 = 001000
		
		ByteSimpleMap map= new ByteSimpleMap();
		ByteBuffer buffer=ByteBuffer.allocate(2);
		buffer.put(new byte[]{8});
		buffer.put(new byte[]{42});
		NumberFormatCreator createrClass = new NumberFormatCreator("len", 4, 4);
		createrClass.addBitEntity("number",BitEntity.BIT_NUMBER,  "8", BitEntity.BIT_STRING, "len", BitEntity.BIT_REFERENCE);
		map.with(createrClass);
		NumberFormat numberItem = (NumberFormat) map.decode(buffer, createrClass);
		Assert.assertNotNull(numberItem);
		
		Assert.assertEquals(42, numberItem.getNumber());
	}
	
	@Test
	public void testDate(){
		// 25 0C AF 11 (37 12 175 17)
		//	   0		 9		17		25
		// Bits: 0010 0101 0000 1100 1010 1111 0001 0001
		//		 |Minute|   | hour|yL|  day |yU  |Month
		// 15.01.13 12:37
		ByteSimpleMap map= new ByteSimpleMap();
		ByteBuffer buffer=ByteBuffer.allocate(4);
		buffer.put(new byte[]{0x25,0x0C,(byte) 0xAF,0x11});
		
		BitDateCreator createrClass = new BitDateCreator();
		createrClass.addField(BitDate.PROPERTY_DAY, 19,5);
		createrClass.addField(BitDate.PROPERTY_MONTH, 28,4);
		createrClass.addField(BitDate.PROPERTY_HOUR, 11,5);
		createrClass.addField(BitDate.PROPERTY_MINUTE, 2,6);

		// and year
		BitEntity bitEntity = new BitEntity().with(BitDate.PROPERTY_YEAR, BitEntity.BIT_NUMBER);
		bitEntity.add(new BitValue(24, 4));
		bitEntity.add(new BitValue(16, 3));
		createrClass.addField(bitEntity);
		
		BitDate entity=(BitDate) map.decode(buffer, createrClass);
		
		assertNotNull(entity);
		
		assertEquals(15, entity.getDay());
		assertEquals(1, entity.getMonth());
		assertEquals(13, entity.getYear());
		assertEquals(12, entity.getHour());
		assertEquals(37, entity.getMinute());
	}
}
