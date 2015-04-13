package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.junit.Test;

import de.uniks.networkparser.bytes.checksum.CCITT16;
import de.uniks.networkparser.bytes.checksum.Crc16;
import de.uniks.networkparser.bytes.checksum.Crc8;
import de.uniks.networkparser.bytes.checksum.FCS16;
import de.uniks.networkparser.bytes.checksum.SHA1;
import de.uniks.networkparser.bytes.checksum.Sum8;
import de.uniks.networkparser.bytes.converter.ByteConverterHex;

public class CheckSumTest {
	
	@Test
	public void testCCITT16() throws UnsupportedEncodingException{
		CCITT16 crc16= new CCITT16();
		ByteConverterHex converter= new ByteConverterHex();
		byte[] array = converter.decode("030400FB0000000000000240000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");
		
		
//		crc16.update(array);
//		System.out.println(converter.toString(crc16.getByteArray()));
		System.out.println(array.length);
	      Integer crc = 0x0000;          // initial value
	        int polynomial = 0x1021;   // 0001 0000 0010 0001  (0, 5, 12) 

//	        array = "123456789".getBytes("ASCII");

	        for (byte b : array) {
	            for (int i = 0; i < 8; i++) {
	                boolean bit = ((b   >> (7-i) & 1) == 1);
	                boolean c15 = ((crc >> 15    & 1) == 1);
	                crc <<= 1;
	                if (c15 ^ bit) crc ^= polynomial;
	             }
	        }

	        crc &= 0xffff;
	        System.out.println("CRC16-CCITT = " + Integer.toHexString(crc));
	        
	        crc16.update(array);
	        System.out.println(converter.toString(crc16.getByteArray()));
	        
	}
	
	// Albert
	private byte[] test = new byte[]{'A', 'l', 'b', 'e','r','t'};
	@Test
	public void testCRC8(){
		Crc8 crc = new Crc8();

//		for (int i=0;i<test.length;i++){
//			System.out.printf("%1$H ", test[i]);
//		}
//		crc.update(new byte[]{0x41, 0x6C, 0x62, 0x65, 0x72, 0x74});
		crc.update(test);
		assertEquals(218,crc.getValue());
	}
	
	@Test
	public void testCRC16(){
		Crc16 crc= new Crc16();
		crc.update(test);
		assertEquals(14516,crc.getValue());
		
//		0x38B4
	}
	
	@Test
	public void testFCS16(){
		FCS16 crc= new FCS16();
//		printTables(crc.crctab, crc.getGenTable());
		crc.update(test);
		assertEquals(19779,crc.getValue());
	}
	
	public void printTables(int[] left, int[] right){
		for (int i=0;i<256;i++){
			System.out.println(left[i]+ ":" +right[i]);
		}
	}
	
	@Test
	public void testCRC32(){
		
	}
	@Test
	public void testSum8(){
		Sum8 crc= new Sum8();
		crc.update(new byte[]{0x24,(byte) 0xD3,(byte) 0xA3,0x04,0x6D,(byte) 0xC0,0x3A,(byte) 0xF7,0x2F,0x5C});
		assertEquals(135, crc.getValue());
	}

	
	@Test
	public void testSHA1(){
		// Compute digest
		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA1");

			String plaintext = "Stefan"; 
			byte[] bytes = (plaintext).getBytes();
			byte[] digest = sha1.digest(bytes);
			ByteConverterHex converter = new ByteConverterHex();
			
			SHA1 sha12 = new SHA1();
			sha12.update(bytes);
			byte[] value = sha12.getByteArray();
			
			System.out.println(converter.toString(digest, digest.length));
			System.out.println(converter.toString(value, value.length));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	//	
//	
//	CRC crctest= new CRC();
//	int[] table=crctest.make_crc_table(0x107);
//	for (int i=0;i<256;i++){
//		System.out.println(Crc8.CRC8_TABLE[i]+ ":" +table[i]);
//	}
//	
//	Crc16 crc = new Crc16();
////	crc.calculate(new byte[]{0x42});
////	System.out.println("CRC16: " +crc.crc16(new byte[]{0x65, 0x66}, 0, 2));
//	
////	crc.calculate(new byte[]{0x65, 0x66});
////	System.out.println("CHEKCSUM: " +crc.checksum());
//	
//
//	

////	crc.add_bits(00101010, 8);
//	System.out.println("CHEKCSUM: " +crc.getValue());
//	
//	// 38B4
//	System.out.println("ENDE CRC");
//	System.out.println("");
//	System.out.println("");
}
