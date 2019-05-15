package de.uniks.networkparser.ext.io;

import java.io.InputStream;

import de.uniks.networkparser.buffer.CharacterBuffer;

public class StringInputStream extends InputStream {
	CharacterBuffer mBuf = new CharacterBuffer();
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
		return 0;
	}

	public void with(String value) {
		this.mBuf.with(value);
	}

	public void with(byte[] value) {
		this.mBuf.with(value);
	}

	public static StringInputStream create(StringOutputStream stream) {
		StringInputStream result = new StringInputStream();
		result.with(stream.toString());
		return result;
	}
}
