package de.uniks.networkparser.bytes;

/*
 * NetworkParser The MIT License Copyright (c) 2010-2016 Stefan Lindel
 * https://www.github.com/fujaba/NetworkParser/
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
import java.io.UnsupportedEncodingException;
import java.util.Date;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.StringUtil;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Converter;

/**
 * The Class ByteEntity.
 * @author Stefan
 */
public class ByteEntity implements ByteItem {
  /** The Constant BIT OF A BYTE. */
  public static final int BITOFBYTE = 8;
  
  /** The Constant TYPEBYTE. */
  public static final int TYPEBYTE = 1;

  /** The Constant TYPE. */
  public static final String TYPE = "TYPE";
  
  /** The Constant VALUE. */
  public static final String VALUE = "VALUE";

  /** The Byte Type. */
  protected byte type;

  /** The values. */
  protected byte[] values;

  /**
   * To binary string.
   *
   * @return the string
   */
  public String toBinaryString() {
    if (values == null || values.length < 1) {
      return "";
    }
    byte[] result = new byte[values.length * 9 + 9];
    for (int z = 0; z < Byte.SIZE; z++) {
      result[7 - z] = (byte) (type >> z & 0x1);
    }
    result[8] = ' ';
    for (int i = 0; i < values.length; i++) {
      for (int z = 0; z < Byte.SIZE; z++) {
        result[i * 9 + 7 - z + 9] = (byte) (values[i] >> z & 0x1);
      }
      result[i * 9 + 8 + 9] = ' ';
    }
    return new String(result);
  }

  /**
   * Gets the value.
   *
   * @return the value
   */
  public byte[] getValue() {
    if (values == null)
      return null;
    return StringUtil.clone(this.values);
  }

  /**
   * Sets the value.
   *
   * @param type the new type
   * @param value the new value
   * @return Itself
   */
  public ByteEntity withValue(byte type, byte[] value) {
    this.type = type;
    if (value != null) {
      this.values = StringUtil.clone(value);
    }
    return this;
  }

  /**
   * Sets the value.
   *
   * @param type the type of Element
   * @param value the new value
   * @return Itself
   */
  public ByteEntity withValue(byte type, byte value) {
    this.type = type;
    this.values = new byte[] {value};
    return this;
  }

  /**
   * With type.
   *
   * @param type the type
   * @return the byte entity
   */
  public ByteEntity withType(byte type) {
    this.type = type;
    return this;
  }

  /**
   * With value.
   *
   * @param type the type
   * @param value the value
   * @return the byte entity
   */
  public ByteEntity withValue(byte type, int value) {
    this.type = type;
    ByteBuffer msgValue = new ByteBuffer().withBufferLength(4);
    msgValue.put(value);
    this.values = msgValue.flip(true).array();
    return this;
  }

  /**
   * Byte to unsigned byte.
   *
   * @param n the Byte
   * @return the Byte
   */
  public byte byteToUnsignedByte(int n) {
    if (n < 128)
      return (byte) n;
    return (byte) (n - 256);
  }

  /**
   * To string.
   *
   * @return the string
   */
  /*
   * @see de.uni.kassel.peermessage.Entity#toString()
   */
  @Override
  public String toString() {
    return toString(null);
  }

  /**
   * Convert the bytes to a String.
   *
   * @param converter Grammar
   * @return converted bytes as String
   */
  @Override
  public String toString(Converter converter) {
    if (converter instanceof ByteConverter) {
      return toString((ByteConverter) converter, false);
    }
    return toString(new ByteConverterHex(), false);
  }

  /**
   * Convert the bytes to a String.
   *
   * @param converter Grammar
   * @param dynamic if byte is dynamic
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
   * @param buffer The Buffer to write
   * @param isDynamic is short the Stream for message
   * @param isLast is the Element is the last of Group
   * @param isPrimitive is the Element is the StreamClazz
   */
  @Override
  public void writeBytes(ByteBuffer buffer, boolean isDynamic, boolean isLast, boolean isPrimitive) {
    byte[] value = this.values;

    byte type = getType();
    if (value == null) {
      type = EntityUtil.getType(type, 0, isLast);
      EntityUtil.writeByteHeader(buffer, type, 0);
      return;
    }
    if (isDynamic) {
      if (type == ByteTokener.DATATYPE_SHORT) {
        short bufferValue = new ByteBuffer().with(value).flip(true).getShort();
        if (bufferValue >= Byte.MIN_VALUE && bufferValue <= Byte.MAX_VALUE) {
          type = ByteTokener.DATATYPE_BYTE;
          value = new byte[] {(byte) bufferValue};
        }
      } else if (type == ByteTokener.DATATYPE_INTEGER || type == ByteTokener.DATATYPE_LONG) {
        int bufferValue = new ByteBuffer().with(value).flip(true).getInt();
        if (bufferValue >= Byte.MIN_VALUE && bufferValue <= Byte.MAX_VALUE) {
          type = ByteTokener.DATATYPE_BYTE;
          value = new byte[] {(byte) bufferValue};
        } else if (bufferValue >= Short.MIN_VALUE && bufferValue <= Short.MAX_VALUE) {
          type = ByteTokener.DATATYPE_BYTE;
          ByteBuffer bbShort = ByteBuffer.allocate(Short.SIZE / BITOFBYTE);
          bbShort.put((short) bufferValue);
          bbShort.flip(true);
          value = bbShort.array();
        }
      }
    }
    if (!isPrimitive || type == ByteTokener.DATATYPE_CLAZZTYPE || type == ByteTokener.DATATYPE_CLAZZTYPELONG) {
      type = EntityUtil.getType(type, value.length, isLast);
      EntityUtil.writeByteHeader(buffer, type, value.length);
    }
    /* SAVE Length */
    buffer.put(value);
  }

  /**
   * Gets the bytes.
   *
   * @param isDynamic the is dynamic
   * @return the bytes
   */
  @Override
  public ByteBuffer getBytes(boolean isDynamic) {
    int len = calcLength(isDynamic, true);
    ByteBuffer buffer = ByteBuffer.allocate(len);
    writeBytes(buffer, isDynamic, true, false);
    buffer.flip(true);
    return buffer;
  }

  /**
   * Sets the values.
   *
   * @param value the value
   * @return true, if successful
   */
  public boolean setValues(Object value) {
    byte type = 0;
    ByteBuffer msgValue = new ByteBuffer();
    if (value == null) {
      type = ByteTokener.DATATYPE_NULL;
    }
    if (value instanceof Short) {
      type = ByteTokener.DATATYPE_SHORT;
      msgValue.withBufferLength(Short.SIZE / BITOFBYTE);
      msgValue.put((Short) value);
    } else if (value instanceof Integer) {
      type = ByteTokener.DATATYPE_INTEGER;
      msgValue.withBufferLength(Integer.SIZE / BITOFBYTE);
      msgValue.put((Integer) value);
    } else if (value instanceof Long) {
      type = ByteTokener.DATATYPE_LONG;
      msgValue.withBufferLength(Long.SIZE / BITOFBYTE);
      msgValue.put((Long) value);
    } else if (value instanceof Float) {
      type = ByteTokener.DATATYPE_FLOAT;
      msgValue.withBufferLength(Float.SIZE / BITOFBYTE);
      msgValue.put((Float) value);
    } else if (value instanceof Double) {
      type = ByteTokener.DATATYPE_DOUBLE;
      msgValue.withBufferLength(Double.SIZE / BITOFBYTE);
      msgValue.put((Double) value);
    } else if (value instanceof Byte) {
      type = ByteTokener.DATATYPE_BYTE;
      msgValue.withBufferLength(Byte.SIZE / BITOFBYTE);
      msgValue.put((Byte) value);
    } else if (value instanceof Character) {
      type = ByteTokener.DATATYPE_CHAR;
      msgValue.withBufferLength(Character.SIZE / BITOFBYTE);
      msgValue.put((Character) value);
    } else if (value instanceof String) {
      type = ByteTokener.DATATYPE_STRING;
      String newValue = (String) value;
      msgValue.withBufferLength(newValue.length());
      try {
        msgValue.put(newValue.getBytes(ENCODING));
      } catch (UnsupportedEncodingException e) {
      }
    } else if (value instanceof Date) {
      type = ByteTokener.DATATYPE_DATE;
      msgValue.withBufferLength(Integer.SIZE / BITOFBYTE);
      Date newValue = (Date) value;
      msgValue.put((int) newValue.getTime());
    } else if (value instanceof Byte[] || value instanceof byte[]) {
      type = ByteTokener.DATATYPE_BYTEARRAY;
      if (value != null) {
        byte[] newValue = (byte[]) value;
        msgValue.withBufferLength(newValue.length);
        msgValue.put(newValue);
      }
    }
    if (type != 0) {
      this.type = type;
      /* Check for group */
      msgValue.flip(true);
      this.values = msgValue.array();
      return true;
    }
    return false;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  @Override
  public byte getType() {
    return this.type;
  }

  /**
   * calculate the length of value.
   *
   * @param isDynamic the is dynamic
   * @param isLast the is last
   * @return the length
   */
  @Override
  public int calcLength(boolean isDynamic, boolean isLast) {
    /* Length calculate Sonderfaelle ermitteln */
    if (this.values == null) {
      return TYPEBYTE;
    }
    if (isDynamic) {
      if (type == ByteTokener.DATATYPE_SHORT) {
        Short bufferValue = new ByteBuffer().with(values).flip(true).getShort();
        if (bufferValue >= Byte.MIN_VALUE && bufferValue <= Byte.MAX_VALUE) {
          return TYPEBYTE + Byte.SIZE / BITOFBYTE;
        }
      } else if (type == ByteTokener.DATATYPE_INTEGER || type == ByteTokener.DATATYPE_LONG) {
        Integer bufferValue = new ByteBuffer().with(values).flip(true).getInt();
        if (bufferValue >= Byte.MIN_VALUE && bufferValue <= Byte.MAX_VALUE) {
          return TYPEBYTE + Byte.SIZE / BITOFBYTE;
        } else if (bufferValue >= Short.MIN_VALUE && bufferValue <= Short.MAX_VALUE) {
          return TYPEBYTE + Short.SIZE / BITOFBYTE;
        }
      }
    }
    return TYPEBYTE + EntityUtil.getTypeLen(type, values.length, isLast) + this.values.length;
  }

  /**
   * Gets the new list.
   *
   * @param keyValue the key value
   * @return the new list
   */
  @Override
  public BaseItem getNewList(boolean keyValue) {
    if (keyValue) {
      return new ByteEntity();
    }
    return new ByteList();
  }

  /**
   * Checks if is empty.
   *
   * @return true, if is empty
   */
  @Override
  public boolean isEmpty() {
    return getType() == ByteTokener.DATATYPE_NULL;
  }

  /**
   * Size.
   *
   * @return the int
   */
  @Override
  public int size() {
    if (values == null) {
      return 0;
    }
    return values.length;
  }

  /**
   * Creates the.
   *
   * @param value the value
   * @return the byte entity
   */
  public static ByteEntity create(Object value) {
    ByteEntity item = new ByteEntity();
    item.setValues(value);
    return item;
  }

  /**
   * Creates the.
   *
   * @param type the type
   * @param value the value
   * @return the byte entity
   */
  public static ByteEntity create(int type, Object value) {
    ByteEntity item = new ByteEntity();
    item.setValues(value);
    item.withType((byte) type);
    return item;
  }

  /**
   * Adds the.
   *
   * @param values the values
   * @return true, if successful
   */
  @Override
  public boolean add(Object... values) {
    if (values == null) {
      return false;
    }
    if (values.length > 1) {
      byte[] value = new byte[values.length - 1];
      for (int i = 1; i < values.length; i++) {
        value[i - 1] = (Byte) values[i];
      }
      withValue((Byte) values[0], value);
    }
    return true;
  }

  /**
   * With value.
   *
   * @param values the values
   * @return the byte entity
   */
  public ByteEntity withValue(byte[] values) {
    if (values == null) {
      return this;
    }
    if (values.length > 1) {
      byte[] value = new byte[values.length - 1];
      for (int i = 1; i < values.length; i++) {
        value[i - 1] = (Byte) values[i];
      }
      this.type = (Byte) values[0];
      this.values = value;
    }
    return this;
  }

  /**
   * Gets the value.
   *
   * @param key the key
   * @return the value
   */
  public Object getValue(Object key) {
    if (TYPE.equals(key)) {
      return type;
    }
    if (VALUE.equals(key)) {
      return values;
    }
    return null;
  }
}
