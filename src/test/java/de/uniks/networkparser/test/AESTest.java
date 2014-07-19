package de.uniks.networkparser.test;

import org.junit.Test;

import de.uniks.networkparser.bytes.checksum.AES;
import de.uniks.networkparser.bytes.converter.ByteConverterAES;
import de.uniks.networkparser.bytes.converter.ByteConverterHex;

public class AESTest {
	@Test
	public void testSimpleAES(){
		AES aes = new AES();			 // init AES encrypter class
		aes.setKey("kWmHe8xIsDpfzK4d");  // choose 16 byte password
		
		String data = "Hello world, here is some sample text.";
		System.out.println("Original text : ["+data+"] ["+data.length()+" bytes]");
		
		String encrypted = aes.encode(data); 
		System.out.println("Encrypted text : ["+encrypted+"] ["+encrypted.length()+" bytes]");
		
		ByteConverterHex converter = new ByteConverterHex();
		
		String hex = converter.toString(encrypted.getBytes()).replace(" ", "");
		System.out.println("Encrypted text (as hex) : ["+hex+"] ["+hex.length()+" bytes]");
		
		String unencrypted = aes.decode(encrypted); 
		System.out.println("Unencrypted text : ["+unencrypted+"] ["+unencrypted.length()+" bytes]");
	}
	
	@Test
	public void testAES(){
		ByteConverterAES aes = new ByteConverterAES();
		aes.setKey("kWmHe8xIsDpfzK4d");  // choose 16 byte password
		
		String data = "Hello world, here is some sample text.";
		System.out.println("Original text : ["+data+"] ["+data.length()+" bytes]");
		
		String encrypted = aes.toString(data); 
		System.out.println("Encrypted text : ["+encrypted+"] ["+encrypted.length()+" bytes]");
		
		ByteConverterHex converter = new ByteConverterHex();
		
		String hex = converter.toString(encrypted.getBytes()).replace(" ", "");
		System.out.println("Encrypted text (as hex) : ["+hex+"] ["+hex.length()+" bytes]");
		
		String unencrypted = aes.decodeString(encrypted); 
		System.out.println("Unencrypted text : ["+unencrypted+"] ["+unencrypted.length()+" bytes]");
	}
}
