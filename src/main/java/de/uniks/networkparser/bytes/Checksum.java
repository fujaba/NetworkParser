package de.uniks.networkparser.bytes;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://www.github.com/fujaba/NetworkParser/

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/

/**
 * Checksum Abstract Class
 * @author Stefan
 */
public abstract class Checksum {
	/** The crc data checksum so far. */
	protected long value;
	protected long length;

	/**
	 * Creates an AbstractChecksum.
	 */
	public Checksum() {
		reset();
	}

	/**
	 * Resets the checksum to its initial value for further use.
	 */
	public void reset() {
		value = 0;
		length = 0;
	}

	/**
	 * Updates the checksum with the specified byte.
	 *
	 * @param data the byte
	 * @return success to add the new Value
	 */
	public boolean update(int data) {
		length++;
		return true;
	}

	/**
	 * Updates the checksum with the specified byte.
	 *
	 * @param b the item to update
	 * @return success
	 */
	public boolean update(byte b) {
		update((int) (b & 0xFF));
		return true;
	}

	/**
	 * Updates the current checksum with the specified array of bytes.
	 *
	 * @param bytes  the byte array to update the checksum with
	 * @param offset the start offset of the data
	 * @param length the number of bytes to use for the update
	 * @return success
	 */
	public boolean update(byte[] bytes, int offset, int length) {
		for (int i = offset; i < length + offset; i++) {
			update(bytes[i]);
		}
		return true;
	}

	/**
	 * Updates the current checksum with the specified array of bytes.
	 *
	 * @param bytes bytearray of items
	 * @return success
	 */
	public boolean update(byte[] bytes) {
		if (bytes != null) {
			return update(bytes, 0, bytes.length);
		}
		return false;
	}

	/**
	 * Returns the value of the checksum.
	 *
	 * @see #getByteArray()
	 * @return the value of checksum
	 */
	public long getValue() {
		int len = getOrder() / 8;
		int max = 1;
		for (int i = 0; i < len; i++) {
			max *= 256;
		}
		max--;
		return value & max;
	}

	/**
	 * Returns the length of the processed bytes.
	 *
	 * @return the length of checksum
	 */
	public long getLength() {
		return length;
	}

	/**
	 * Returns the result of the computation as byte array.
	 *
	 * @return a new ByteArray
	 */
	public byte[] getByteArray() {
		long value = getValue();

		int maxlen = Integer.SIZE / 8;
		byte[] test = new byte[maxlen];
		int count = 0;
		while (value > 0) {
			test[maxlen - 1 - count++] = (byte) (value % 256);
			value = value >> 8; /* um 1 Byte shiften */
		}
		if (count == 0) {
			return new byte[] { 0x00 };
		}
		byte[] result = new byte[count];
		for (int z = 0; z < count; z++) {
			result[z] = test[(maxlen - count) + z];
		}
		return result;
	}

	/**
	 * @return the Orderindex
	 */
	public abstract int getOrder();
}
