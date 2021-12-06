package de.uniks.networkparser.ext.io;

import java.io.InputStream;

import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;

public class BufferedByteInputStream extends InputStream {
	BufferedBuffer mBuf = new CharacterBuffer();
	private int pos;

	@Override
	public String toString() {
		return mBuf.toString();
	}

	@Override
	public int read() {
		if (pos < mBuf.length()) {
			return mBuf.charAt(pos++);
		}
		return -1;
	}

	public BufferedByteInputStream with(String value) {
		this.mBuf.with(value);
		return this;
	}

	public BufferedByteInputStream with(byte[] value) {
		this.mBuf.with(value);
		return this;
	}
	
	public BufferedByteInputStream withStream(BufferedBuffer buffer) {
		this.mBuf = buffer;
		return this;
	}


	public static BufferedByteInputStream create(StringOutputStream stream) {
		BufferedByteInputStream result = new BufferedByteInputStream();
		if (stream != null) {
			result.with(stream.toString());
		}
		return result;
	}
}
