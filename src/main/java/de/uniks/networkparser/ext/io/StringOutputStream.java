package de.uniks.networkparser.ext.io;

import java.io.OutputStream;

public class StringOutputStream extends OutputStream {
	StringBuilder mBuf = new StringBuilder();

	@Override
	public String toString() {
		return mBuf.toString();
	}

	@Override
	public void write(int b) {
		mBuf.append((char) b);
	}
}
