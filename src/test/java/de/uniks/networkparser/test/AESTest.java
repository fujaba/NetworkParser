package de.uniks.networkparser.test;

import java.io.PrintStream;

import org.junit.Assert;
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
		Assert.assertEquals("Original text : [" +data+ "] [" +data.length()+ " bytes]", 38, data.length());
		
		String encrypted = aes.encode(data);
		Assert.assertEquals("Encrypted text : [" +encrypted+ "] [" +encrypted.length()+ " bytes]", 64, encrypted.length());
		
		ByteConverterHex converter = new ByteConverterHex();
		
		outputStream(encrypted.getBytes(), System.out);
		String hex = converter.toString(encrypted.getBytes()).replace(" ", "");
		Assert.assertEquals("Encrypted text (as hex) : [" +hex+ "] [" +hex.length()+ " bytes]", 128, hex.length());
		
		String unencrypted = aes.decode(encrypted);
		Assert.assertEquals("Unencrypted text : [" +unencrypted+ "] [" +unencrypted.length()+ " bytes]", 38, unencrypted.length());
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
	public void testAES(){
		ByteConverterAES aes = new ByteConverterAES();
		aes.setKey("kWmHe8xIsDpfzK4d");  // choose 16 byte password
		
		String data = "Hello world, here is some sample text.";
		Assert.assertEquals("Original text : [" +data+ "] [" +data.length()+ " bytes]", 38, data.length()); 
		
		String encrypted = aes.toString(data);
		Assert.assertEquals("Encrypted text : [" +encrypted+ "] [" +encrypted.length()+ " bytes]", 64, encrypted.length()); 
		
		ByteConverterHex converter = new ByteConverterHex();
		
		String hex = converter.toString(encrypted.getBytes()).replace(" ", "");
		Assert.assertEquals("Encrypted text (as hex) : [" +hex+ "] [" +hex.length()+ " bytes]", 128, hex.length()); 
		
		String unencrypted = aes.decodeString(encrypted); 
		Assert.assertEquals("Unencrypted text : [" +unencrypted+ "] [" +unencrypted.length()+ " bytes]", 38, unencrypted.length()); 
	}
}
