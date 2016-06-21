package de.uniks.networkparser.converter;
import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;
import de.uniks.networkparser.bytes.AES;

public class ByteConverterAES extends ByteConverter {
	private AES aes;

	public ByteConverterAES withKey(String value) {
		if (this.aes == null) {
			this.aes = new AES();
		}
		this.aes.withKey(value);
		return this;
	}

	public CharacterBuffer toString(String values) {
		return aes.encode(values);
	}

	@Override
	public String toString(BufferedBuffer values) {
		return this.aes.encode(values).toString();
	}

	@Override
	public byte[] decode(String value) {
		return aes.decodeString(value);
	}
}
