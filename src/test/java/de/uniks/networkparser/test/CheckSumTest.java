package de.uniks.networkparser.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.Assert;
import org.junit.Test;

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.buffer.DERBuffer;
import de.uniks.networkparser.bytes.AES;
import de.uniks.networkparser.bytes.CRC;
import de.uniks.networkparser.bytes.FCS16;
import de.uniks.networkparser.bytes.RSAKey;
import de.uniks.networkparser.bytes.SHA1;
import de.uniks.networkparser.bytes.Sum;
import de.uniks.networkparser.converter.ByteConverter64;
import de.uniks.networkparser.converter.ByteConverterHex;

public class CheckSumTest {
	@Test
	public void testRSA() throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
		RSAKey key = RSAKey.generateKey(11, 13, 143);
		key.withPubExp(23);
		BigInteger encrypt = key.encrypt(BigInteger.valueOf(7));
		
		Assert.assertEquals(2, encrypt.intValue());
		
//		key = RSAKey.generateKey(512);
		
		DERBuffer publicStream = key.getPublicStream();
		ByteConverter64 converter = new ByteConverter64();
		String string = converter.toString(publicStream);
		Assert.assertNotNull(string);
//		BASE64Decoder b64 = new BASE64Decoder();
//		byte[] decoded = b64.decodeBuffer(string);
	
//		byte[] decode = converter.decode(string);
		
//		X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
//	      KeyFactory kf = KeyFactory.getInstance("RSA");
//	      PublicKey generatePublic2 = kf.generatePublic(spec);

		key = RSAKey.generateKey(1024);
		StringBuilder textEncrypt = key.encrypt("Hallo");
		
//		System.out.println(textEncrypt.toString());
		
//FIXME		RSAKey descriptKey = RSAKey.getDecryptKey(1024, key.getPrivateKey());
//		descriptKey.decrypt(textEncrypt.toString());
	}
	
	@Test
	public void testCRC32() {
		CRC crc = new CRC(32);
		int[] genTable = crc.getGenTable(false, CRC.CRC32);
		StringBuffer sb=new StringBuffer();
		for(int i=0;i<genTable.length;i++) {
			sb.append(String.format("%4d", (byte)genTable[i]));
			if(i % 10 == 9) {
				sb.append("\r\n");
				break;
			}
		}
		Assert.assertEquals("   0-106  44 -70  25-113  53 -93  50 -92\r\n", sb.toString());
	}

	@Test
	public void testX509() throws NoSuchAlgorithmException{
//	      Signature s1 = Signature.getInstance("SHA256withECDSA");
//	      Provider provider = s1.getProvider();
//	      System.out.println(provider);
//	      s1.initVerify(publicKey);
//	      s1.update(message.getBytes());
//	      BASE64Decoder b64 = new BASE64Decoder();
//	      byte[] decodeBuffer = b64.decodeBuffer(signature);
//
//	      return s1.verify(decodeBuffer);
//
//		String publicKeyPEM = keyString.replace(BEGIN_PUBLIC_KEY, "");
//		      
//	      BASE64Decoder b64 = new BASE64Decoder();
//	      byte[] decoded = b64.decodeBuffer(publicKeyPEM);

	}

	@Test
	public void testCCITT16() throws UnsupportedEncodingException{
		CRC crc16= new CRC(0);
		ByteConverterHex converter= new ByteConverterHex();
		byte[] array = converter.decode("030400FB0000000000000240000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000");

		Assert.assertEquals(60, array.length);
		Integer crc = 0x0000; // initial value
		int polynomial = 0x1021; // 0001 0000 0010 0001 (0, 5, 12)

		// array = "123456789".getBytes("ASCII");

		for (byte b : array) {
			for (int i = 0; i < 8; i++) {
				boolean bit = ((b >> (7 - i) & 1) == 1);
				boolean c15 = ((crc >> 15 & 1) == 1);
				crc <<= 1;
				if (c15 ^ bit)
					crc ^= polynomial;
			}
		}

		crc &= 0xffff;
		Assert.assertEquals("CRC16-CCITT", "98ec", Integer.toHexString(crc));

		crc16.update(array);
		ByteBuffer buffer = new ByteBuffer().with(crc16.getByteArray());
		Assert.assertEquals("CRC16-CCITT", "98EC", converter.toString(buffer));

	}

	// Albert
	private byte[] test = new byte[]{'A', 'l', 'b', 'e','r','t'};
	@Test
	public void testCRC8(){
		CRC crc = new CRC(8);

		crc.update(test);
		assertEquals(218,crc.getValue());
	}

	@Test
	public void testCRC16(){
		CRC crc= new CRC(16);
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

	public void printTables(int[] left, int[] right, PrintStream stream){
		for (int i=0;i<256;i++){
			stream.println(left[i]+ ":" +right[i]);
		}
	}

	@Test
	public void testSum8(){
		Sum crc= new Sum().withOrder(8);
		crc.update(new byte[]{0x24,(byte) 0xD3,(byte) 0xA3,0x04,0x6D,(byte) 0xC0,0x3A,(byte) 0xF7,0x2F,0x5C});
		assertEquals(135, crc.getValue());
	}

	@Test
	public void testAES() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException{
		byte[] key = new byte[]{'4', '2'}; // TODO
		AES aes= new AES().withKey(key);
		CharacterBuffer encode = aes.encode(new CharacterBuffer().withValue("Albert is safe"));
		ByteConverterHex converter = new ByteConverterHex();
		Assert.assertEquals("5252bded4e25fe90f24f71a7dd1eb92e", converter.toString(encode).toLowerCase());

		aes= new AES().withKey("12345678901234567890");
		encode = aes.encode(new CharacterBuffer().withValue("Albert is safe"));
		Assert.assertEquals("1349173f3e17b09b2cfa75a2dfa075a7", converter.toString(encode).toLowerCase());

		aes= new AES().withKey("123456789012345678901234567890");
		encode = aes.encode(new CharacterBuffer().withValue("Albert is safe"));
		Assert.assertEquals("1052d5850e8205921ec53fa885d6f0cc", converter.toString(encode).toLowerCase());

	}

	@Test
	public void testSHA1(){
		// Compute digest
		MessageDigest sha1;
		try {
			sha1 = MessageDigest.getInstance("SHA1");

			String plaintext = "Stefan";
			byte[] bytes = (plaintext).getBytes();
			ByteBuffer digest = new ByteBuffer().with(sha1.digest(bytes));
			ByteConverterHex converter = new ByteConverterHex();

			SHA1 sha12 = new SHA1();
			sha12.update(bytes);
			ByteBuffer value = new ByteBuffer().with(sha12.getByteArray());
			Assert.assertEquals("E3500A442761EF40F1772C5D858397824B6FB5BD", converter.toString(digest));
			Assert.assertEquals("E3500A442761EF40F1772C5D858397824B6FB5BD", converter.toString(value));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
