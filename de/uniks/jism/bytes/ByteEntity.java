package de.uniks.jism.bytes;
/*
Copyright (c) 2012 Stefan Lindel

Permission is hereby granted,	 free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

The Software shall be used for Good, not Evil.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
import java.nio.ByteBuffer;
import java.util.Date;

import de.uniks.jism.EntityList;
import de.uniks.jism.interfaces.BaseEntity;
import de.uniks.jism.interfaces.ByteItem;

/**
 * The Class ByteEntity.
 */
public class ByteEntity implements BaseEntity, ByteItem{
	/** The Constant BIT OF A BYTE. */
	public final static int BITOFBYTE=8;
	
	/** The Byte Typ. */
	protected byte typ;
	
	/** The values. */
	protected byte[] values;
	
	/**
	 * Instantiates a new byte entity.
	 */
	public ByteEntity(){
		
	}
	
	/**
	 * Instantiates a new byte entity.
	 *
	 * @param typ the typ
	 * @param value the value
	 */
	public ByteEntity(byte typ, byte[] value){
		this.setValue(typ, value);
	}
	
	/*
	 * @see de.uni.kassel.peermessage.BaseEntity#getNewArray()
	 */
	@Override
	public EntityList getNewArray() {
		return new ByteList();
	}

	/*
	 * @see de.uni.kassel.peermessage.BaseEntity#getNewObject()
	 */
	@Override
	public BaseEntity getNewObject() {
		return new ByteEntity();
	}
	
	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public byte[] getValue() {
		return this.values;
	}

	/**
	 * Sets the value.
	 *
	 * @param value the new value
	 */
	public void setValue(byte typ, byte[] value) {
		this.typ=typ;
		this.values = value;
	}

	/**
	 * Byte to unsigned byte.
	 *
	 * @param n the Byte
	 * @return the Byte
	 */
	public byte byteToUnsignedByte(int n) {
		if (n < 128) return (byte) n;
		return (byte) (n - 256);
	}
	
	/*
	 * @see de.uni.kassel.peermessage.Entity#toString()
	 */
	@Override
	public String toString() {
		StringBuilder returnValue = new StringBuilder();
		ByteBuffer byteBuffer = getBytes(false);
		if(byteBuffer!=null){
			for (int i = 0; i < byteBuffer.limit(); i++) {
				byte value = byteBuffer.get(i);
				if (value <= 32 || value == 127) {
					returnValue.append(ByteIdMap.SPLITTER);
					returnValue.append((char) (value + ByteIdMap.SPLITTER + 1));
				} else {
					returnValue.append((char) value);
				}
			}
		}
		return returnValue.toString();
	}
	
	/*
	 * @see de.uni.kassel.peermessage.Entity#toString(int)
	 */
	@Override
	public String toString(int typ) {
		return toString();
	}
	
	/*
	 * @see de.uni.kassel.peermessage.Entity#toString(int, int)
	 */
	@Override
	public String toString(int indentFactor, int intent) {
		return toString();
	}
	
	/**
	 * Gets the bytes.
	 *
	 * @return the bytes
	 */
	public ByteBuffer getBytes(boolean isDynamic){
		int len=calcLength(isDynamic);
		ByteBuffer buffer = ByteUtil.getBuffer(len, getTyp());
		
		// Save the Len
		if(this.values!=null){
			buffer.put(this.values);
		}
		buffer.flip();
		return buffer;
	}
	
	public boolean setValues(Object value){
		byte typ = 0;
		ByteBuffer msgValue = null;
		if(value ==null){
			typ=ByteIdMap.DATATYPE_NULL;
		}
		if(value instanceof Short){
			typ=ByteIdMap.DATATYPE_SHORT;
			msgValue = ByteBuffer.allocate(Short.SIZE/BITOFBYTE);
			msgValue.putShort((Short) value);
		} else if (value instanceof Integer) {
			typ=ByteIdMap.DATATYPE_INTEGER;
			msgValue = ByteBuffer.allocate(Integer.SIZE/BITOFBYTE);
			msgValue.putInt((Integer) value);
		} else if (value instanceof Long) {
			typ=ByteIdMap.DATATYPE_LONG;
			msgValue = ByteBuffer.allocate(Long.SIZE/BITOFBYTE);
			msgValue.putLong((Long) value);
		} else if (value instanceof Float) {
			typ=ByteIdMap.DATATYPE_FLOAT;
			msgValue = ByteBuffer.allocate(Float.SIZE/BITOFBYTE);
			msgValue.putFloat((Float) value);
		} else if (value instanceof Double) {
			typ=ByteIdMap.DATATYPE_DOUBLE;
			msgValue = ByteBuffer.allocate(Double.SIZE/BITOFBYTE);
			msgValue.putDouble((Float) value);
		} else if (value instanceof Byte) {
			typ=ByteIdMap.DATATYPE_BYTE;
			msgValue = ByteBuffer.allocate(Byte.SIZE/BITOFBYTE);
			msgValue.put((Byte) value);
		} else if (value instanceof Character) {
			typ=ByteIdMap.DATATYPE_CHAR;
			msgValue = ByteBuffer.allocate(Character.SIZE/BITOFBYTE);
			msgValue.putChar((Character) value);
		} else if (value instanceof String) {
			typ=ByteIdMap.DATATYPE_STRING;
			String newValue = (String) value;
			msgValue = ByteBuffer.allocate(newValue.length());
			msgValue.put(newValue.getBytes());
		} else if (value instanceof Date) {
			typ=ByteIdMap.DATATYPE_DATE;
			msgValue = ByteBuffer.allocate(Integer.SIZE/BITOFBYTE);
			Date newValue = (Date) value;
			msgValue.putInt((int) newValue.getTime());
		} else if (value instanceof Byte[]||value instanceof byte[]){
			typ=ByteIdMap.DATATYPE_BYTEARRAY;
			byte[] newValue = (byte[]) value;
			msgValue = ByteBuffer.allocate(newValue.length);
			msgValue.put(newValue);
		}
		if(typ!=0){
			this.typ=typ;
			// Check for group
			if(msgValue!=null){
				msgValue.flip();
				this.values=msgValue.array();
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Gets the typ.
	 *
	 * @return the typ
	 */
	public byte getTyp() {
		return this.typ;
	}

	/**
	 * calculate the length of value
	 *
	 * @return the length
	 */
	public int calcLength(boolean isDynamic) {
		// Length calculate Sonderfaelle ermitteln
		int len=1+ByteUtil.getTypLen(getTyp());
		
		if(this.values!=null){
			len+=this.values.length;
		}
		return len;
	}
	
	/**
	 * Sets the len check.
	 * 
	 * @param isLenCheck
	 *            the is len check
	 * @return true, if successful
	 */
	public boolean setLenCheck(boolean isLenCheck) {
		if (!isLenCheck) {
			if(typ/16==(ByteIdMap.DATATYPE_CHECK/16)){
			}else if(ByteUtil.isGroup(typ)){
				this.typ = ByteUtil.getTyp(typ, ByteIdMap.DATATYPE_STRINGLAST);
			}
		} else {
			int size = this.values.length - 1;
			this.typ = ByteUtil.getTyp(getTyp(), size);
		}
		return true;
	}

}
