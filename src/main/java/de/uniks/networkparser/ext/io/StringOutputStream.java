package de.uniks.networkparser.ext.io;

import java.io.OutputStream;

/**
 * The Class StringOutputStream.
 *
 * @author Stefan
 */
public class StringOutputStream extends OutputStream {
	StringBuilder mBuf = new StringBuilder();

	/**
	 * To string.
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		return mBuf.toString();
	}

	/**
	 * Write.
	 *
	 * @param b the b
	 */
	@Override
	public void write(int b) {
		mBuf.append((char) b);
	}
}
