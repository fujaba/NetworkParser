package de.uniks.networkparser.ext.io;

import java.io.IOException;
import java.io.InputStream;

public class StringInputStream extends InputStream {

	StringBuilder mBuf = new StringBuilder();
	private int pos;

	@Override
	public String toString() {
		return mBuf.toString();
	}

	@Override
	public int read() throws IOException {
		if(pos<mBuf.length()) {
			return mBuf.charAt(pos++);
		}
		return 0;
	}
	
	public void withData(String value) {
		this.mBuf.append(value);
	}
	
	public static StringInputStream create(StringOutputStream stream) {
		StringInputStream result = new StringInputStream();
		result.withData(stream.toString());
		return result;
	}
}