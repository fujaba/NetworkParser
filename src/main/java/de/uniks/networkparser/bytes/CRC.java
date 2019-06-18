package de.uniks.networkparser.bytes;

/*
NetworkParser
The MIT License
Copyright (c) 2010-2016 Stefan Lindel https://github.com/fujaba/NetworkParser/

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

public class CRC extends Checksum {
	/* CRC-8, poly = x^8 + x^2 + x^1 + 1, init = 0
	   1 0000 0111
	   0111 0000 1 */
	public static final int CRC8 = 0x107;
	/* 1000000000000101 */
	public static final int CRC16 = 0x8005;

	public static final int CRC32 = 0xedb88320;
	/* 1000000000000101 */
	public static final int CCITT16 = 0x1021;
	/** The fast CRC table. Computed once when the CRC32 class is loaded. */
	protected int[] crc_table = null;
	private int order;

	public CRC() {
	}

	public CRC(int bitMask) {
		withCRC(bitMask);
	}

	public CRC withCRC(int bitMask) {
		this.order = bitMask;
		if (order == 0) {
			/* Default os CCITT16 */
			order = 16;
			crc_table = null;
		} else if (order == 8) {
			crc_table = getGenTable(false, CRC8);
		} else if (order == 16) {
			crc_table = getGenTable(true, CRC16);
		} else if (order == 32) {
			crc_table = getGenTable(false, CRC32);
		} else {
			crc_table = null;
			order = 0;
		}
		return this;
	}

	/**
	 * Update the CRC value with a byte data.
	 *
	 * @param data The byte data
	 */
	@Override
	public boolean update(int data) {
		if (order == 0) {
			return false;
		}
		super.update(data);
		if (order == 8) {
			value = crc_table[((int) value ^ (byte) data) & 0xFF];
		} else if (order == 16) {
			if (crc_table == null) {
				for (int i = 0; i < 8; i++) {
					boolean bit = ((data >> (7 - i) & 1) == 1);
					boolean c15 = ((value >> 15 & 1) == 1);
					value <<= 1;
					if (c15 ^ bit) {
						value ^= CCITT16;
					}
				}
			} else {
				value = (value >>> 8) ^ crc_table[((int) value ^ data) & 0xff];
			}
		} else if (order == 32) {
			int c = (int) ~value;
			c = crc_table[(c ^ data) & 0xff] ^ (c >>> 8);
			value = ~c;
		}
		return true;
	}

	@Override
	public int getOrder() {
		return order;
	}

	/**
	 * Make the table for a fast CRC.
	 *
	 * @param isReflect is Reflects the lower bits of the value provided
	 * @param polynom   the generator polynom
	 * @return the GenTable
	 */
	public int[] getGenTable(boolean isReflect, int polynom) {
		int[] result = new int[256];
		if (polynom == CRC32) {
			for (int n = 0; n < 256; n++) {
				int c = n;
				for (int k = 8; --k >= 0;) {
					if ((c & 1) != 0)
						c = CRC32 ^ (c >>> 1);
					else
						c = c >>> 1;
				}
				result[n] = c;
			}
			return result;
		}

		int order = getOrder();
		long topBit = (long) 1 << (order - 1);
		long widthMask = (((1 << (order - 1)) - 1) << 1) | 1;

		for (int i = 0; i < 256; i++) {
			result[i] = i;
			if (isReflect) {
				result[i] = Reflect(i, 8);
			}
			result[i] = result[i] << (order - 8);
			for (int j = 0; j < 8; ++j) {
				if ((result[i] & topBit) != 0) {
					result[i] = (result[i] << 1) ^ polynom;
				} else {
					result[i] <<= 1;
				}
			}
			if (isReflect) {
				result[i] = Reflect(result[i], order);
			}
			result[i] &= widthMask;
		}
		return result;
	}

	/** Reflects the lower bits of the value provided.
	* @param data The value to reflect.
	* @param numBits The number of bits to reflect.
	* @return The reflected value.
	*/
	static private int Reflect(int data, int numBits) {
		int temp = data;

		for (int i = 0; i < numBits; i++) {
			long bitMask = (long) 1 << ((numBits - 1) - i);

			if ((temp & (long) 1) != 0) {
				data |= bitMask;
			} else {
				data &= ~bitMask;
			}

			temp >>= 1;
		}
		return data;
	}
}
