/*
 * Copyright 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uniks.networkparser.bytes.qr;

import java.util.Arrays;

/**
 * <p>
 * A simple, fast array of bits, represented compactly by an array of ints
 * internally.
 * </p>
 *
 * @author Sean Owen
 */
final class BitArray implements Cloneable {
	private int[] bits;
	private int size;
	private int byteOffset;
	private int bitOffset;

	BitArray() {
		this.size = 0;
		this.bits = new int[1];
	}

	BitArray(int size) {
		this.size = size;
		this.bits = makeArray(size);
	}

	BitArray(byte[] bytes) {
		if (bytes == null) {
			return;
		}
		size = bytes.length;
		this.bits = new int[size];
		for (int i = 0; i < size; i++) {
			this.bits[i] = bytes[i];
		}
	}

	// For testing only
	private BitArray(int[] bits, int size) {
		this.bits = bits;
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	public int getSizeInBytes() {
		return (size + 7) / 8;
	}

	private void ensureCapacity(int size) {
		if (size > bits.length * 32) {
			int[] newBits = makeArray(size);
			System.arraycopy(bits, 0, newBits, 0, bits.length);
			this.bits = newBits;
		}
	}

	/**
	 * @param i bit to get
	 * @return true iff bit i is set
	 */
	public boolean get(int i) {
		return (bits[i / 32] & (1 << (i & 0x1F))) != 0;
	}

	/**
	 * Sets bit i.
	 *
	 * @param i bit to set
	 */
	public void set(int i) {
		bits[i / 32] |= 1 << (i & 0x1F);
	}

	/**
	 * Sets a block of 32 bits, starting at bit i.
	 *
	 * @param i       first bit to set
	 * @param newBits the new value of the next 32 bits. Note again that the
	 *                least-significant bit corresponds to bit i, the
	 *                next-least-significant to i+1, and so on.
	 */
	void setBulk(int i, int newBits) {
		bits[i / 32] = newBits;
	}

	/** Clears all bits (sets to false). */
	public void clear() {
		int max = bits.length;
		for (int i = 0; i < max; i++) {
			bits[i] = 0;
		}
	}

	void appendBit(boolean bit) {
		ensureCapacity(size + 1);
		if (bit) {
			bits[size / 32] |= 1 << (size & 0x1F);
		}
		size++;
	}

	/**
	 * Appends the least-significant bits, from value, in order from
	 * most-significant to least-significant. For example, appending 6 bits from
	 * 0x000001E will append the bits 0, 1, 1, 1, 1, 0 in that order.
	 *
	 * @param value   {@code int} containing bits to append
	 * @param numBits bits from value to append
	 */
	void appendBits(int value, int numBits) {
		if (numBits < 0 || numBits > 32) {
			throw new IllegalArgumentException("Num bits must be between 0 and 32");
		}
		ensureCapacity(size + numBits);
		for (int numBitsLeft = numBits; numBitsLeft > 0; numBitsLeft--) {
			appendBit(((value >> (numBitsLeft - 1)) & 0x01) == 1);
		}
	}

	void appendBitArray(BitArray other) {
		int otherSize = other.size;
		ensureCapacity(size + otherSize);
		for (int i = 0; i < otherSize; i++) {
			appendBit(other.get(i));
		}
	}

	void xor(BitArray other) {
		if (bits.length != other.bits.length) {
			throw new IllegalArgumentException("Sizes don't match");
		}
		for (int i = 0; i < bits.length; i++) {
			// The last byte could be incomplete (i.e. not have 8 bits in
			// it) but there is no problem since 0 XOR 0 == 0.
			bits[i] ^= other.bits[i];
		}
	}

	/**
	 *
	 * @param bitOffset first bit to start writing
	 * @param array     array to write into. Bytes are written most-significant byte
	 *                  first. This is the opposite of the internal representation,
	 *                  which is exposed by {@link #getBitArray()}
	 * @param offset    position in array to start writing
	 * @param numBytes  how many bytes to write
	 */
	void toBytes(int bitOffset, byte[] array, int offset, int numBytes) {
		for (int i = 0; i < numBytes; i++) {
			int theByte = 0;
			for (int j = 0; j < 8; j++) {
				if (get(bitOffset)) {
					theByte |= 1 << (7 - j);
				}
				bitOffset++;
			}
			array[offset + i] = (byte) theByte;
		}
	}

	/**
	 * @return underlying array of ints. The first element holds the first 32 bits,
	 *         and the least significant bit is bit 0.
	 */
	int[] getBitArray() {
		return bits;
	}

	/**
	 * Reverses all bits in the array.
	 */
	void reverse() {
		int[] newBits = new int[bits.length];
		// reverse all int's first
		int len = (size - 1) / 32;
		int oldBitsLen = len + 1;
		for (int i = 0; i < oldBitsLen; i++) {
			long x = (long) bits[i];
			x = ((x >> 1) & 0x55555555L) | ((x & 0x55555555L) << 1);
			x = ((x >> 2) & 0x33333333L) | ((x & 0x33333333L) << 2);
			x = ((x >> 4) & 0x0f0f0f0fL) | ((x & 0x0f0f0f0fL) << 4);
			x = ((x >> 8) & 0x00ff00ffL) | ((x & 0x00ff00ffL) << 8);
			x = ((x >> 16) & 0x0000ffffL) | ((x & 0x0000ffffL) << 16);
			newBits[len - i] = (byte) x;
		}
		// now correct the int's if the bit size isn't a multiple of 32
		if (size != oldBitsLen * 32) {
			int leftOffset = oldBitsLen * 32 - size;
			int mask = 1;
			for (int i = 0; i < 31 - leftOffset; i++) {
				mask = (mask << 1) | 1;
			}
			int currentInt = (newBits[0] >> leftOffset) & mask;
			for (int i = 1; i < oldBitsLen; i++) {
				int nextInt = newBits[i];
				currentInt |= nextInt << (32 - leftOffset);
				newBits[i - 1] = (byte) currentInt;
				currentInt = (nextInt >> leftOffset) & mask;
			}
			newBits[oldBitsLen - 1] = (byte) currentInt;
		}
		bits = newBits;
	}

	private static int[] makeArray(int size) {
		return new int[(size + 31) / 32];
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof BitArray)) {
			return false;
		}
		BitArray other = (BitArray) o;
		return size == other.size && Arrays.equals(bits, other.bits);
	}

	@Override
	public int hashCode() {
		return 31 * size + Arrays.hashCode(bits);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder(size);
		for (int i = 0; i < size; i++) {
			if ((i & 0x07) == 0) {
				result.append(' ');
			}
			result.append(get(i) ? 'X' : '.');
		}
		return result.toString();
	}

	@Override
	public BitArray clone() {
		return new BitArray(bits.clone(), size);
	}

	/**
	 * @return number of bits that can be read successfully
	 */
	int available() {
		return 8 * (bits.length - byteOffset) - bitOffset;
	}

	/**
	 * @param numBits number of bits to read
	 * @return int representing the bits read. The bits will appear as the
	 *         least-significant bits of the int
	 * @throws IllegalArgumentException if numBits is not in [1,32] or more than is
	 *                                  available
	 */
	int readBits(int numBits) {
		if (numBits < 1 || numBits > 32 || numBits > available()) {
			throw new IllegalArgumentException(String.valueOf(numBits));
		}

		int result = 0;

		// First, read remainder from current byte
		if (bitOffset > 0) {
			int bitsLeft = 8 - bitOffset;
			int toRead = numBits < bitsLeft ? numBits : bitsLeft;
			int bitsToNotRead = bitsLeft - toRead;
			int mask = (0xFF >> (8 - toRead)) << bitsToNotRead;
			result = (bits[byteOffset] & mask) >> bitsToNotRead;
			numBits -= toRead;
			bitOffset += toRead;
			if (bitOffset == 8) {
				bitOffset = 0;
				byteOffset++;
			}
		}

		// Next read whole bytes
		if (numBits > 0) {
			while (numBits >= 8) {
				result = (result << 8) | (bits[byteOffset] & 0xFF);
				byteOffset++;
				numBits -= 8;
			}

			// Finally read a partial byte
			if (numBits > 0) {
				int bitsToNotRead = 8 - numBits;
				int mask = (0xFF >> bitsToNotRead) << bitsToNotRead;
				result = (result << numBits) | ((bits[byteOffset] & mask) >> bitsToNotRead);
				bitOffset += numBits;
			}
		}
		return result;
	}
}