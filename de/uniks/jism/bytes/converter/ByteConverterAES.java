package de.uniks.jism.bytes.converter;

import de.uniks.jism.bytes.checksum.AES;

public class ByteConverterAES extends ByteConverter{
	private AES aes;

	public void setKey(String value){
		if(this.aes==null){
			this.aes = new AES();
		}
		this.aes.setKey(value);
	}
	
	public String toString(String values) {
		return aes.encode(values);
	}
	
	@Override
	public String toString(byte[] values, int size) {
		return this.aes.encodeBytes(values);
	}

	@Override
	public byte[] decode(String value) {
		return aes.decodeString(value);
	}

	public String decodeString(String value) {
		return aes.decode(value);
	}
}
