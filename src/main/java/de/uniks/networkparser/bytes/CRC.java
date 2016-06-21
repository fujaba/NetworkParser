package de.uniks.networkparser.bytes;

/*
 NetworkParser
 Copyright (c) 2011 - 2015, Stefan Lindel
 All rights reserved.

 Licensed under the EUPL, Version 1.1 or (as soon they
 will be approved by the European Commission) subsequent
 versions of the EUPL (the "Licence");
 You may not use this work except in compliance with the Licence.
 You may obtain a copy of the Licence at:

 http://ec.europa.eu/idabc/eupl5

 Unless required by applicable law or agreed to in
 writing, software distributed under the Licence is
 distributed on an "AS IS" basis,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 express or implied.
 See the Licence for the specific language governing
 permissions and limitations under the Licence.
*/

public class CRC extends Checksum {
	// CRC-8, poly = x^8 + x^2 + x^1 + 1, init = 0
	// 1 0000 0111
	// 0111 0000 1
	public static final int CRC8 = 0x107;
	 // 1000000000000101
	public static final int CRC16 = 0x8005;

	public static final int CRC32 = 0xedb88320;
	// 1000000000000101
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
		if(order == 0){
			// Default os CCITT16
			order = 16;
			crc_table = null;
//			crc_table = getGenTable(true, CCITT16);
		} else if(order == 8) {
			crc_table = getGenTable(false, CRC8);
		} else if(order == 16) {
			crc_table = getGenTable(true, CRC16);
		} else if(order == 32) {
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
	 * @param data		The byte data
	 */
	@Override // 8
	public boolean update(int data) {
		if(order == 0) {
			return false;
		}
		super.update(data);
		if(order == 8) {
			value = crc_table[((int) value ^ (byte) data) & 0xFF];
		} else if(order == 16) {
			if(crc_table == null) {
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
		} else if(order == 32) {
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
	 *	@param isReflect is Reflects the lower bits of the value provided
	 *	@param polynom the generator polynom
	 *	@return the GenTable
	 * */
	public int[] getGenTable(boolean isReflect, int polynom) {
		int[] result = new int[256];
		if(polynom==CRC32) {
			for (int n = 0; n < 256; n++) {
				int c = n;
				for (int k = 8; --k >= 0;) {
					if ((c & 1) != 0)
						c =  CRC32 ^ (c >>> 1);
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

	// / <summary>Reflects the lower bits of the value provided.</summary>
	// / <param name="data">The value to reflect.</param>
	// / <param name="numBits">The number of bits to reflect.</param>
	// / <returns>The reflected value.</returns>
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
