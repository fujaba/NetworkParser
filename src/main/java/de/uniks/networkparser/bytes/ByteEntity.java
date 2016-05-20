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

import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.converter.ByteConverter;
import de.uniks.networkparser.converter.ByteConverterHTTP;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Converter;
/**
 * The Class ByteEntity.
 */

public class ByteEntity implements ByteItem {
	/** The Constant BIT OF A BYTE. */
	public final static int BITOFBYTE = 8;
	public final static int TYPBYTE = 1;

	public final static String TYP="TYP";
	public final static String VALUE="VALUE";

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
		return ByteUtil.clone(this.values);
	}

	/**
	 * Sets the value.
	 *
	 * @param typ
	 *			the new Typ
	 * @param value
	 *			the new value
	 * @return Itself
	 */
	public ByteEntity withValue(byte typ, byte[] value) {
		this.typ = typ;
		if(value != null){
			this.values = ByteUtil.clone(value);
		}
		return this;
	}

	/**
	 * Sets the value.
	 *
	 * @param typ
	 *			The Typ of Element
	 * @param value
	 *			the new value
	 * @return Itself
	 */
	public ByteEntity withValue(byte typ, byte value) {
		this.typ = typ;
		this.values = new byte[] {value };
		return this;
	}

	public ByteEntity withValue(byte typ, int value) {
		this.typ = typ;
		ByteBuffer msgValue = new ByteBuffer().withBufferLength(4);
		msgValue.put(value);
		this.values = msgValue.flip(true).array();
		return this;
	}

	/**
	 * Byte to unsigned byte.
	 *
	 * @param n
	 *			the Byte
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
	 *			Grammar
	 * @return converted bytes as String
	 */
	@Override
	public String toString(Converter converter) {
		if(converter instanceof ByteConverter) {
			return toString((ByteConverter)converter, false);
		}
		return null;
	}

	/**
	 * Convert the bytes to a String
	 *
	 * @param converter
	 *			Grammar
	 * @param dynamic
	 *			if byte is dynamic
	 * @return converted bytes as String
	 */
	@Override
	public String toString(ByteConverter converter, boolean dynamic) {
		if (converter == null) {
			converter = new ByteConverterHTTP();
		}
		return converter.toString(this.getBytes(dynamic));
	}

	/**
	 * Gets the bytes.
	 *
	 * @param buffer
	 *			The Buffer to write
	 * @param isDynamic
	 *			is short the Stream for message
	 * @param isLast
	 *			is the Element is the last of Group
	 * @param isPrimitive
	 *			is the Element is the StreamClazz
	 */
	@Override
	public void writeBytes(ByteBuffer buffer, boolean isDynamic,
			boolean isLast, boolean isPrimitive) {
		byte[] value = this.values;

		byte typ = getTyp();
		if (value == null) {
			typ = ByteUtil.getTyp(typ, 0, isLast);
			ByteUtil.writeByteHeader(buffer, typ, 0);
			return;
		}
		if (isDynamic) {
			if (typ == ByteTokener.DATATYPE_SHORT) {
				short bufferValue = new ByteBuffer().with(value).flip(true).getShort();
				if (bufferValue >= Byte.MIN_VALUE
						&& bufferValue <= Byte.MAX_VALUE) {
					typ = ByteTokener.DATATYPE_BYTE;
					value = new byte[] {(byte) bufferValue };
				}
			} else if (typ == ByteTokener.DATATYPE_INTEGER
					|| typ == ByteTokener.DATATYPE_LONG) {
				int bufferValue = new ByteBuffer().with(value).flip(true).getInt();
				if (bufferValue >= Byte.MIN_VALUE
						&& bufferValue <= Byte.MAX_VALUE) {
					typ = ByteTokener.DATATYPE_BYTE;
					value = new byte[] {(byte) bufferValue };
				} else if (bufferValue >= Short.MIN_VALUE
						&& bufferValue <= Short.MAX_VALUE) {
					typ = ByteTokener.DATATYPE_BYTE;
					ByteBuffer bbShort = ByteBuffer.allocate(Short.SIZE
							/ BITOFBYTE);
					bbShort.put((short) bufferValue);
					bbShort.flip(true);
					value = bbShort.array();
				}
			}
		}
		if (!isPrimitive || typ == ByteTokener.DATATYPE_CLAZZTYP
				|| typ == ByteTokener.DATATYPE_CLAZZTYPLONG) {
			typ = ByteUtil.getTyp(typ, value.length, isLast);
			ByteUtil.writeByteHeader(buffer, typ, value.length);
		}
		// SAVE Length
		buffer.put(value);
	}

	@Override
	public ByteBuffer getBytes(boolean isDynamic) {
		int len = calcLength(isDynamic, true);
		ByteBuffer buffer = ByteUtil.getBuffer(len);
		writeBytes(buffer, isDynamic, true, false);
		buffer.flip(true);
		return buffer;
	}

	public boolean setValues(Object value) {
		byte typ = 0;
		ByteBuffer msgValue = new ByteBuffer();
		if (value == null) {
			typ = ByteTokener.DATATYPE_NULL;
		}
		if (value instanceof Short) {
			typ = ByteTokener.DATATYPE_SHORT;
			msgValue.withBufferLength(Short.SIZE / BITOFBYTE);
			msgValue.put((Short) value);
		} else if (value instanceof Integer) {
			typ = ByteTokener.DATATYPE_INTEGER;
			msgValue.withBufferLength(Integer.SIZE / BITOFBYTE);
			msgValue.put((Integer) value);
		} else if (value instanceof Long) {
			typ = ByteTokener.DATATYPE_LONG;
			msgValue.withBufferLength(Long.SIZE / BITOFBYTE);
			msgValue.put((Long) value);
		} else if (value instanceof Float) {
			typ = ByteTokener.DATATYPE_FLOAT;
			msgValue.withBufferLength(Float.SIZE / BITOFBYTE);
			msgValue.put((Float) value);
		} else if (value instanceof Double) {
			typ = ByteTokener.DATATYPE_DOUBLE;
			msgValue.withBufferLength(Double.SIZE / BITOFBYTE);
			msgValue.put((Double) value);
		} else if (value instanceof Byte) {
			typ = ByteTokener.DATATYPE_BYTE;
			msgValue.withBufferLength(Byte.SIZE / BITOFBYTE);
			msgValue.put((Byte) value);
		} else if (value instanceof Character) {
			typ = ByteTokener.DATATYPE_CHAR;
			msgValue.withBufferLength(Character.SIZE / BITOFBYTE);
			msgValue.put((Character) value);
		} else if (value instanceof String) {
			typ = ByteTokener.DATATYPE_STRING;
			String newValue = (String) value;
			msgValue.withBufferLength(newValue.length());
			try {
				msgValue.put(newValue.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
			}
		} else if (value instanceof Date) {
			typ = ByteTokener.DATATYPE_DATE;
			msgValue.withBufferLength(Integer.SIZE / BITOFBYTE);
			Date newValue = (Date) value;
			msgValue.put((int) newValue.getTime());
		} else if (value instanceof Byte[] || value instanceof byte[]) {
			typ = ByteTokener.DATATYPE_BYTEARRAY;
			if (value != null) {
				byte[] newValue = (byte[]) value;
				msgValue.withBufferLength(newValue.length);
				msgValue.put(newValue);
			}
		}
		if (typ != 0) {
			this.typ = typ;
			// Check for group
			msgValue.flip(true);
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
			if (typ == ByteTokener.DATATYPE_SHORT) {
				Short bufferValue = new ByteBuffer().with(values).flip(true).getShort();
				if (bufferValue >= Byte.MIN_VALUE
						&& bufferValue <= Byte.MAX_VALUE) {
					return TYPBYTE + Byte.SIZE / BITOFBYTE;
				}
			} else if (typ == ByteTokener.DATATYPE_INTEGER
					|| typ == ByteTokener.DATATYPE_LONG) {
				Integer bufferValue = new ByteBuffer().with(values).flip(true).getInt();
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
		return getTyp() == ByteTokener.DATATYPE_NULL;
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
	public ByteEntity with(Object... values) {
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

	public ByteEntity withValue(byte[] values) {
		if(values==null){
			return this;
		}
		if(values.length>1) {
			byte[] value = new byte[values.length-1];
			for(int i=1;i<values.length;i++) {
				value[i-1] = (Byte) values[i];
			}
			this.typ = (Byte)values[0];
			this.values = value;
		}
		return this;
	}

	public Object getValue(Object key) {
		if(TYP.equals(key)) {
			return typ;
		}
		if(VALUE.equals(key)) {
			return values;
		}
		return null;
	}
}