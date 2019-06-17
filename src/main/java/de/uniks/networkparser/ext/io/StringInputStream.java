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

	public StringInputStream with(String value) {
		this.mBuf.with(value);
		return this;
	}

	public StringInputStream with(byte[] value) {
		this.mBuf.with(value);
		return this;
	}

	public static StringInputStream create(StringOutputStream stream) {
		StringInputStream result = new StringInputStream();
		if(stream != null) {
			result.with(stream.toString());
		}
		return result;
	}
}
