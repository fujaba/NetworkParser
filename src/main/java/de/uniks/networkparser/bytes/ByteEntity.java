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
import java.io.UnsupportedEncodingException;
import java.util.Date;
import de.uniks.networkparser.bytes.converter.ByteConverterHTTP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.BufferedBytes;
import de.uniks.networkparser.interfaces.ByteConverter;
import de.uniks.networkparser.interfaces.ByteItem;
/**
 * The Class ByteEntity.
 */

public class ByteEntity implements ByteItem, BaseItem {
	/** The Constant BIT OF A BYTE. */
	public final static int BITOFBYTE = 8;
	public final static int TYPBYTE = 1;

	/** The Byte Typ. */
	protected byte typ;

	/** The values. */
	protected byte[] values;

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public byte[] getValue() {
		if(values==null)
			return null;
		return this.values.clone();
	}

	/**
	 * Sets the value.
	 *
	 * @param typ
	 *            the new Typ
	 * @param value
	 *            the new value
	 * @return Itself
	 */
	public ByteEntity withValue(byte typ, byte[] value) {
		this.typ = typ;
		if(value != null){
			this.values = value.clone();
		}
		return this;
	}

	/**
	 * Sets the value.
	 *
	 * @param typ
	 *            The Typ of Element
	 * @param value
	 *            the new value
	 * @return Itself
	 */
	public ByteEntity withValue(byte typ, byte value) {
		this.typ = typ;
		this.values = new byte[] {value };
		return this;
	}

	public ByteEntity withValue(byte typ, int value) {
		this.typ = typ;
		BytesBuffer msgValue = new BytesBuffer().withLength(4);
		msgValue.put(value);
		this.values = msgValue.flip();
		return this;
	}

	/**
	 * Byte to unsigned byte.
	 *
	 * @param n
	 *            the Byte
	 * @return the Byte
	 */
	public byte byteToUnsignedByte(int n) {
		if (n < 128)
			return (byte) n;
		return (byte) (n - 256);
	}

	/*
	 * @see de.uni.kassel.peermessage.Entity#toString()
	 */
	@Override
	public String toString() {
		return toString(null);
	}

	/**
	 * Convert the bytes to a String
	 *
	 * @param converter
	 *            Grammar
	 * @return converted bytes as String
	 */
	@Override
	public String toString(ByteConverter converter) {
		return toString(converter, false);
	}

	/**
	 * Convert the bytes to a String
	 *
	 * @param converter
	 *            Grammar
	 * @param dynamic
	 *            if byte is dynamic
	 * @return converted bytes as String
	 */
	@Override
	public String toString(ByteConverter converter, boolean dynamic) {
		if (converter == null) {
			converter = new ByteConverterHTTP();
		}
		return converter.toString(this, dynamic);
	}

	/**
	 * Gets the bytes.
	 *
	 * @param buffer
	 *            The Buffer to write
	 * @param isDynamic
	 *            is short the Stream for message
	 * @param isLast
	 *            is the Element is the last of Group
	 * @param isPrimitive
	 *            is the Element is the StreamClazz
	 */
	@Override
	public void writeBytes(BufferedBytes buffer, boolean isDynamic,
			boolean isLast, boolean isPrimitive) {
		byte[] value = this.values;

		byte typ = getTyp();
		if (value == null) {
			typ = ByteUtil.getTyp(typ, 0, isLast);
			ByteUtil.writeByteHeader(buffer, typ, 0);
			return;
		}
		if (isDynamic) {
			if (typ == ByteIdMap.DATATYPE_SHORT) {
				short bufferValue = new BytesBuffer().with(value).getShort();
				if (bufferValue >= Byte.MIN_VALUE
						&& bufferValue <= Byte.MAX_VALUE) {
					typ = ByteIdMap.DATATYPE_BYTE;
					value = new byte[] {(byte) bufferValue };
				}
			} else if (typ == ByteIdMap.DATATYPE_INTEGER
					|| typ == ByteIdMap.DATATYPE_LONG) {
				int bufferValue = new BytesBuffer().with(value).getInt();
				if (bufferValue >= Byte.MIN_VALUE
						&& bufferValue <= Byte.MAX_VALUE) {
					typ = ByteIdMap.DATATYPE_BYTE;
					value = new byte[] {(byte) bufferValue };
				} else if (bufferValue >= Short.MIN_VALUE
						&& bufferValue <= Short.MAX_VALUE) {
					typ = ByteIdMap.DATATYPE_BYTE;
					BytesBuffer bbShort = BytesBuffer.allocate(Short.SIZE
							/ BITOFBYTE);
					bbShort.put((short) bufferValue);
					bbShort.flip();
					value = bbShort.array();
				}
			}
		}
		if (!isPrimitive || typ == ByteIdMap.DATATYPE_CLAZZTYP
				|| typ == ByteIdMap.DATATYPE_CLAZZTYPLONG) {
			typ = ByteUtil.getTyp(typ, value.length, isLast);
			ByteUtil.writeByteHeader(buffer, typ, value.length);
		}
		// SAVE Length
		buffer.put(value);
	}

	@Override
	public BufferedBytes getBytes(boolean isDynamic) {
		int len = calcLength(isDynamic, true);
		BufferedBytes buffer = ByteUtil.getBuffer(len);
		writeBytes(buffer, isDynamic, true, false);
		buffer.flip();
		return buffer;
	}

	public boolean setValues(Object value) {
		byte typ = 0;
		BytesBuffer msgValue = new BytesBuffer();
		if (value == null) {
			typ = ByteIdMap.DATATYPE_NULL;
		}
		if (value instanceof Short) {
			typ = ByteIdMap.DATATYPE_SHORT;
			msgValue.withLength(Short.SIZE / BITOFBYTE);
			msgValue.put((Short) value);
		} else if (value instanceof Integer) {
			typ = ByteIdMap.DATATYPE_INTEGER;
			msgValue.withLength(Integer.SIZE / BITOFBYTE);
			msgValue.put((Integer) value);
		} else if (value instanceof Long) {
			typ = ByteIdMap.DATATYPE_LONG;
			msgValue.withLength(Long.SIZE / BITOFBYTE);
			msgValue.put((Long) value);
		} else if (value instanceof Float) {
			typ = ByteIdMap.DATATYPE_FLOAT;
			msgValue.withLength(Float.SIZE / BITOFBYTE);
			msgValue.put((Float) value);
		} else if (value instanceof Double) {
			typ = ByteIdMap.DATATYPE_DOUBLE;
			msgValue.withLength(Double.SIZE / BITOFBYTE);
			msgValue.put((Double) value);
		} else if (value instanceof Byte) {
			typ = ByteIdMap.DATATYPE_BYTE;
			msgValue.withLength(Byte.SIZE / BITOFBYTE);
			msgValue.put((Byte) value);
		} else if (value instanceof Character) {
			typ = ByteIdMap.DATATYPE_CHAR;
			msgValue.withLength(Character.SIZE / BITOFBYTE);
			msgValue.put((Character) value);
		} else if (value instanceof String) {
			typ = ByteIdMap.DATATYPE_STRING;
			String newValue = (String) value;
			msgValue.withLength(newValue.length());
			try {
				msgValue.put(newValue.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		} else if (value instanceof Date) {
			typ = ByteIdMap.DATATYPE_DATE;
			msgValue.withLength(Integer.SIZE / BITOFBYTE);
			Date newValue = (Date) value;
			msgValue.put((int) newValue.getTime());
		} else if (value instanceof Byte[] || value instanceof byte[]) {
			typ = ByteIdMap.DATATYPE_BYTEARRAY;
			if (value != null) {
				byte[] newValue = (byte[]) value;
				msgValue.withLength(newValue.length);
				msgValue.put(newValue);
			}
		}
		if (typ != 0) {
			this.typ = typ;
			// Check for group
			msgValue.flip();
			this.values = msgValue.array();
			return true;
		}
		return false;
	}

	/**
	 * Gets the typ.
	 *
	 * @return the typ
	 */
	@Override
	public byte getTyp() {
		return this.typ;
	}

	/**
	 * calculate the length of value
	 *
	 * @return the length
	 */
	@Override
	public int calcLength(boolean isDynamic, boolean isLast) {
		// Length calculate Sonderfaelle ermitteln
		if (this.values == null) {
			return TYPBYTE;
		}
		if (isDynamic) {
			if (typ == ByteIdMap.DATATYPE_SHORT) {
				Short bufferValue = new BytesBuffer().with(values).getShort();
				if (bufferValue >= Byte.MIN_VALUE
						&& bufferValue <= Byte.MAX_VALUE) {
					return TYPBYTE + Byte.SIZE / BITOFBYTE;
				}
			} else if (typ == ByteIdMap.DATATYPE_INTEGER
					|| typ == ByteIdMap.DATATYPE_LONG) {
				Integer bufferValue = new BytesBuffer().with(values).getInt();
				if (bufferValue >= Byte.MIN_VALUE
						&& bufferValue <= Byte.MAX_VALUE) {
					return TYPBYTE + Byte.SIZE / BITOFBYTE;
				} else if (bufferValue >= Short.MIN_VALUE
						&& bufferValue <= Short.MAX_VALUE) {
					return TYPBYTE + Short.SIZE / BITOFBYTE;
				}
			}
		}
		return TYPBYTE + ByteUtil.getTypLen(typ, values.length, isLast)
				+ this.values.length;
	}

	@Override
	public BaseItem getNewList(boolean keyValue) {
		if(keyValue) {
			return new ByteEntity();
		}
		return new ByteList();
	}

	@Override
	public boolean isEmpty() {
		return getTyp() == ByteIdMap.DATATYPE_NULL;
	}

	@Override
	public int size() {
		if (values == null) {
			return 0;
		}
		return values.length;
	}
	
	public static ByteEntity create(Object value) {
		ByteEntity item = new ByteEntity();
		item.setValues(value);
		return item;
	}

	@Override
	public BaseItem withAll(Object... values) {
		if(values==null){
			return this;
		}
		if(values.length>1) {
			byte[] value = new byte[values.length-1];
			for(int i=1;i<values.length;i++) {
				value[i-1] = (Byte) values[i];
			}
			withValue((Byte)values[0], value);
		}
		return this;
	}

	@Override
	public Object getValueItem(Object key) {
		if("TYP".equals(key)) {
			return typ;
		}
		if("VALUE".equals(key)) {
			return values;
		}
		return null;
	}
}
