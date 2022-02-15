package de.uniks.networkparser.ext.io;

import java.io.IOException;
import java.io.InputStream;

import de.uniks.networkparser.buffer.BufferedBuffer;
import de.uniks.networkparser.buffer.CharacterBuffer;


/**
 * Buffered InputStream for String or Byte.
 *
 * @author Stefan Lindel
 */
public class BufferedByteInputStream extends InputStream {
	BufferedBuffer mBuf = new CharacterBuffer();
	private int pos;

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
	 * Read.
	 *
	 * @return the int
	 */
	@Override
	public int read() {
		if (pos < mBuf.length()) {
			return mBuf.charAt(pos++);
		}
		return -1;
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the buffered byte input stream
	 */
	public BufferedByteInputStream with(String value) {
		this.mBuf.with(value);
		return this;
	}

	/**
	 * With.
	 *
	 * @param value the value
	 * @return the buffered byte input stream
	 */
	public BufferedByteInputStream with(byte[] value) {
		this.mBuf.with(value);
		return this;
	}
	
	/**
	 * With stream.
	 *
	 * @param buffer the buffer
	 * @return the buffered byte input stream
	 */
	public BufferedByteInputStream withStream(BufferedBuffer buffer) {
		this.mBuf = buffer;
		return this;
	}
	
	/**
	 * With stream.
	 *
	 * @param stream the stream
	 * @return the buffered byte input stream
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public BufferedByteInputStream withStream(InputStream stream) throws IOException
	{
		this.mBuf.addStream(stream);
        return this;
	}

	/**
	 * Creates the.
	 *
	 * @param stream the stream
	 * @return the buffered byte input stream
	 */
	public static BufferedByteInputStream create(StringOutputStream stream) {
		BufferedByteInputStream result = new BufferedByteInputStream();
		if (stream != null) {
			result.with(stream.toString());
		}
		return result;
	}
}
