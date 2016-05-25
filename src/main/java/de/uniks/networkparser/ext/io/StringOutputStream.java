package de.uniks.networkparser.ext.io;

import java.io.IOException;
import java.io.OutputStream;

public class StringOutputStream extends OutputStream {

	StringBuilder mBuf = new StringBuilder();

	@Override
	public String toString() {
		return mBuf.toString();
	}

	@Override
	public void write(int b) throws IOException {
		mBuf.append((char) b);
	}
}