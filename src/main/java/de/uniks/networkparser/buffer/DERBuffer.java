package de.uniks.networkparser.buffer;

/*
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
import java.math.BigInteger;

/** Byte buffer used for DER parsing. 
 * @author Stefan Lindel */
public class DERBuffer extends ByteBuffer {
	
	/** The Constant ARRAY. */
	public static final byte ARRAY = 0x00;
	/** Tag value indicating an ASN.1 "BOOLEAN" value. */
	public static final byte BOOLEAN = 0x01;

	/** Tag value indicating an ASN.1 "INTEGER" value. */
	public static final byte INTEGER = 0x02;

	/** Tag value indicating an ASN.1 "BIT STRING" value. */
	public static final byte BITSTRING = 0x03;

	/** Tag value indicating an ASN.1 "OCTET STRING" value. */
	public static final byte OCTETSTRING = 0x04;

	/** Tag value indicating an ASN.1 "NULL" value. */
	public static final byte NULL = 0x05;

	/** Tag value indicating an ASN.1 "OBJECT IDENTIFIER" value. */
	public static final byte OBJECTID = 0x06;

	/** Tag value including an ASN.1 "ENUMERATED" value */
	public static final byte ENUMERATED = 0x0A;

	/** Tag value indicating an ASN.1 "UTF8String" value. */
	public static final byte UTF8STRING = 0x0C;

	/**  Tag value including a "printable" string. */
	public static final byte PRINTABLESTRING = 0x13;

	/**  Tag value including a "teletype" string. */
	public static final byte T61STRING = 0x14;

	/**  Tag value including an ASCII string. */
	public static final byte IA5STRING = 0x16;

	/** Tag value indicating an ASN.1 "UTCTime" value. */
	public static final byte UTCTIME = 0x17;

	/** Tag value indicating an ASN.1 "GeneralizedTime" value. */
	public static final byte GENERALIZEDTIME = 0x18;

	/** Tag value indicating an ASN.1 "GenerallString" value. */
	public static final byte GENERALSTRING = 0x1B;

	/** Tag value indicating an ASN.1 "UniversalString" value. */
	public static final byte UNIVERSALSTRING = 0x1C;

	/** Tag value indicating an ASN.1 "BMPString" value. */
	public static final byte BMPSTRING = 0x1E;

	/**
	 * Adds the.
	 *
	 * @param paramBigInteger the param big integer
	 */
	public void add(BigInteger paramBigInteger) {
		if (paramBigInteger != null) {
			byte[] arrayOfByte = paramBigInteger.toByteArray();
			add(arrayOfByte);
			addBigIntegerLength(arrayOfByte.length);
			add(INTEGER);
		}
	}

	/**
	 * Adds the bit string.
	 *
	 * @param string the string
	 */
	public void addBitString(String string) {
		if (string != null) {
			byte[] bytes = string.getBytes();
			add(bytes);
			addLength(bytes.length);
			add(BITSTRING);
		}
	}

	/**
	 * Adds the big integer length.
	 *
	 * @param length the length
	 */
	public void addBigIntegerLength(int length) {
		if (length > 127) {
			int size = 1;
			int val = length;

			while ((val >>>= 8) != 0) {
				size++;
			}
			add((byte) (size | 0x80));

			for (int i = (size - 1) * 8; i >= 0; i -= 8) {
				add((byte) (length >> i));
			}
		} else {
			add((byte) length);
		}
	}

	/**
	 * Adds the length.
	 *
	 * @param value the value
	 */
	public void addLength(int value) {
		if (value < 128) {
			add((byte) value);
		} else if (value < 256) {
			add((byte) value);
			add(-127);
		} else if (value < 65536) {
			add((byte) value);
			add((byte) (value >> 8));
			add(-126);
		} else if (value < 16777216) {
			add((byte) value);
			add((byte) (value >> 8));
			add((byte) (value >> 16));
			add(-125);
		} else {
			add((byte) value);
			add((byte) (value >> 8));
			add((byte) (value >> 16));
			add((byte) (value >> 24));
			add(-124);
		}
	}

	/**
	 * Adds the group.
	 *
	 * @param values the values
	 * @return true, if successful
	 */
	public boolean addGroup(Object... values) {
		if (values == null || values.length < 1) {
			return false;
		}
		int pos;
		int z = values.length - 1;
		while (z >= 0) {
			Object item = values[z];
			if (item instanceof String) {
				addBitString((String) item);
			} else if (item instanceof Byte[]) {
				pos = length;
				insert((Byte[]) item, true);
				;

				z--;
				if ((Byte) values[z] == DERBuffer.BITSTRING) {
					add(0);
				}
				if (pos == 0) {
					addLength(length);
				} else {
					addLength(length - pos);
				}
				add((Byte) values[z]);
			} else if (item instanceof Object[]) {
				pos = length;
				addGroup((Object[]) item);
				if (pos == 0) {
					addLength(length);
				} else {
					addLength(length - pos);
				}
				z--;
				add((Byte) values[z]);
			} else if (item instanceof BigInteger) {
				add((BigInteger) item);
			} else if (item instanceof Byte) {
				if (item.equals(NULL)) {
					add((byte) 0);
					add(NULL);
				} else {
					add((Byte) item);
				}
			}
			z--;
		}
		return true;
	}

	/**
	 * Adds the bytes.
	 *
	 * @param bytes the bytes
	 * @param len the len
	 * @param bufferAtEnd the buffer at end
	 * @return true, if successful
	 */
	@Override
	public boolean addBytes(Object bytes, int len, boolean bufferAtEnd) {
		if (bytes != null) {
			position -= len;
			this.start = position;
			return super.addBytes(bytes, len, bufferAtEnd);
		}
		return false;
	}
}
