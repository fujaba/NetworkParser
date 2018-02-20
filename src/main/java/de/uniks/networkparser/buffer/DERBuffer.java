package de.uniks.networkparser.buffer;

import java.math.BigInteger;

public class DERBuffer extends ByteBuffer {
	public final static byte ARRAY = 0x00;
	/** Tag value indicating an ASN.1 "BOOLEAN" value. */
	public final static byte BOOLEAN = 0x01;

	/** Tag value indicating an ASN.1 "INTEGER" value. */
	public final static byte INTEGER = 0x02;

	/** Tag value indicating an ASN.1 "BIT STRING" value. */
	public final static byte BITSTRING = 0x03;

	/** Tag value indicating an ASN.1 "OCTET STRING" value. */
	public final static byte OCTETSTRING = 0x04;

	/** Tag value indicating an ASN.1 "NULL" value. */
	public final static byte NULL = 0x05;

	/** Tag value indicating an ASN.1 "OBJECT IDENTIFIER" value. */
	public final static byte OBJECTID = 0x06;

	/** Tag value including an ASN.1 "ENUMERATED" value */
	public final static byte ENUMERATED = 0x0A;

	/** Tag value indicating an ASN.1 "UTF8String" value. */
	public final static byte UTF8STRING = 0x0C;

	/** Tag value including a "printable" string */
	public final static byte PRINTABLESTRING = 0x13;

	/** Tag value including a "teletype" string */
	public final static byte T61STRING = 0x14;

	/** Tag value including an ASCII string */
	public final static byte IA5STRING = 0x16;

	/** Tag value indicating an ASN.1 "UTCTime" value. */
	public final static byte UTCTIME = 0x17;

	/** Tag value indicating an ASN.1 "GeneralizedTime" value. */
	public final static byte GENERALIZEDTIME = 0x18;

	/** Tag value indicating an ASN.1 "GenerallString" value. */
	public final static byte GENERALSTRING = 0x1B;

	/** Tag value indicating an ASN.1 "UniversalString" value. */
	public final static byte UNIVERSALSTRING = 0x1C;

	/** Tag value indicating an ASN.1 "BMPString" value. */
	public final static byte BMPSTRING = 0x1E;

	public void add(BigInteger paramBigInteger) {
		byte[] arrayOfByte = paramBigInteger.toByteArray();
		add(arrayOfByte);
		addBigIntegerLength(arrayOfByte.length);
		add(INTEGER);
	}

	public void addBitString(String string) {
		byte[] bytes = string.getBytes();
		add(bytes);
		addLength(bytes.length);
		add(BITSTRING);
	}
	public void addBigIntegerLength(int length) {
		if (length > 127) {
			int size = 1;
			int val = length;

			while ((val >>>= 8) != 0) {
				size++;
			}
			add((byte)(size | 0x80));

			for (int i = (size - 1) * 8; i >= 0; i -= 8) {
				add((byte)(length >> i));
			}
		} else {
			add((byte)length);
		}
	}
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

	public boolean addGroup(Object... values) {
		if(values == null || values.length < 1) {
			return false;
		}
		int pos;
		int z = values.length - 1;
		while(z >= 0) {
			Object item = values[z];
			if(item instanceof String) {
				addBitString((String) item);
			} else if(item instanceof Byte[]) {
				pos = length;
				add((Byte[])item);

				z--;
				if((Byte)values[z] == DERBuffer.BITSTRING) {
					add(0);
				}
				if(pos == 0) {
					addLength(length);
				} else {
					addLength(length- pos);
				}
				add((Byte)values[z]);
			} else if(item instanceof Object[]) {
				pos = length;
				addGroup((Object[])item);
				if(pos == 0) {
					addLength(length);
				} else {
					addLength(length - pos);
				}
				z--;
				add((Byte)values[z]);
			} else if(item instanceof BigInteger) {
				add((BigInteger)item);
			} else if(item instanceof Byte) {
				if(item.equals(NULL)) {
					add((byte)0);
					add(NULL);
				}else {
					add((Byte)item);
				}
			}
			z--;
		}
		return true;
	}

	@Override
	protected boolean addBytes(Object bytes, int len) {
		if (bytes != null) {
			position -= len;
			this.start = position;
			return super.addBytes(bytes, len);
		}
		return false;
	}
}
