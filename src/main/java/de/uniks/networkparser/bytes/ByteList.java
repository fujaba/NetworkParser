package de.uniks.networkparser.bytes;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleList;

/**
 * List of Byte.
 *
 * @author Stefan Lindel
 */
public class ByteList extends SimpleList<ByteItem> implements ByteItem {
  /** The children of the ByteEntity. */
  private byte type;

  /** The Constant PROPERTY_PROPERTY. */
  /* Can be a Type */
  public static final String PROPERTY_PROPERTY = "property";
  
  /** The Constant PROPERTY_TYPE. */
  public static final String PROPERTY_TYPE = "type";
  
  /** The Constant PROPERTY_ORIENTATION. */
  public static final String PROPERTY_ORIENTATION = "orientation";

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
   * To string.
   *
   * @return the string
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
    return toString(null, false);
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
   * @param isDynamic the is dynamic
   * @return the bytes
   */
  @Override
  public ByteBuffer getBytes(boolean isDynamic) {
    int len = calcLength(isDynamic, true);
    ByteBuffer buffer = ByteBuffer.allocate(len);
    writeBytes(buffer, isDynamic, true, isPrimitive(isDynamic));
    buffer.flip(true);
    return buffer;
  }

  /**
   * Write bytes.
   *
   * @param buffer the buffer
   * @param isDynamic the is dynamic
   * @param last the last
   * @param isPrimitive the is primitive
   */
  @Override
  public void writeBytes(ByteBuffer buffer, boolean isDynamic, boolean last, boolean isPrimitive) {
    /* Override for each ByteList */
    isPrimitive = isPrimitive(isDynamic);
    int size = calcChildren(isDynamic, last);
    byte type;
    if (isPrimitive) {
      type = ByteTokener.DATATYPE_CLAZZSTREAM;
    } else {
      type = EntityUtil.getType(getType(), size, last);
    }
    EntityUtil.writeByteHeader(buffer, type, size);

    for (int i = 0; i < size(); i++) {
      ((ByteItem) get(i)).writeBytes(buffer, isDynamic, i == size() - 1, isPrimitive);
    }
  }

  /**
   * Calc length.
   *
   * @param isDynamic the is dynamic
   * @param isLast the is last
   * @return the int
   */
  @Override
  public int calcLength(boolean isDynamic, boolean isLast) {
    if (size() == 0) {
      return 1;
    }
    int length = calcChildren(isDynamic, isLast);
    /* add The Headerlength */
    if (type != 0) {
      length += ByteEntity.TYPEBYTE + EntityUtil.getTypeLen(type, length, isLast);
    }
    return length;
  }

  /**
   * Calc children.
   *
   * @param isDynamic the is dynamic
   * @param isLast the is last
   * @return the int
   */
  public int calcChildren(boolean isDynamic, boolean isLast) {
    int length, size = size();
    if (size < 1) {
      return 0;
    }
    boolean isPrimitive = isDynamic;
    int nullerBytes = 0;
    if (this.get(size - 1) instanceof ByteEntity) {
      /* HEADER + VALUE */
      isPrimitive = isPrimitive && this.get(0).getType() == ByteTokener.DATATYPE_CLAZZTYPE;
      if (this.get(size - 1).getType() == ByteTokener.DATATYPE_NULL) {
        nullerBytes++;
      }
    } else {
      isPrimitive = false;
    }
    length = this.get(size - 1).calcLength(isDynamic, true);
    for (int i = size - 2; i >= 0; i--) {
      int len = this.get(i).calcLength(isDynamic, false);
      if (isPrimitive) {
        if (this.get(i).getType() == ByteTokener.DATATYPE_NULL) {
          nullerBytes++;
        }
        isPrimitive = (this.get(i).size() == len - 1);
      }
      length += len;
    }
    if (isPrimitive) {
      /*
       * Only for ByteList with value dynamic and values with cant be short add one for ClazzSTEAM Byte as
       * first Byte
       */
      length = length - size + ByteEntity.TYPEBYTE + ByteEntity.TYPEBYTE + nullerBytes;
    }
    return length;
  }

  private boolean isPrimitive(boolean isDynamic) {
    if (!isDynamic || this.size() < 1) {
      return false;
    }
    if (!(this.get(this.size() - 1) instanceof ByteEntity)) {
      return false;
    }
    if (this.get(0).getType() != ByteTokener.DATATYPE_CLAZZTYPE) {
      return false;
    }
    for (int i = 1; i < this.size(); i++) {
      int len = this.get(i).calcLength(isDynamic, false);
      if ((this.get(i).size() != len - 1)) {
        return false;
      }
    }
    return true;
  }

  /**
   * Gets the type.
   *
   * @return the type
   */
  @Override
  public byte getType() {
    return type;
  }

  /**
   * With type.
   *
   * @param value the value
   * @return the byte list
   */
  public ByteList withType(Byte value) {
    if (value != null)
      this.type = value;
    return this;
  }

  /**
   * With value.
   *
   * @param value the value
   * @return the simple list
   */
  public SimpleList<ByteItem> withValue(String value) {
    ByteConverter converter = new ByteConverter();
    this.add(((ByteEntity) getNewList(true)).withValue(ByteTokener.DATATYPE_FIXED, converter.decode(value)));
    return this;
  }

  /**
   * Removes the.
   *
   * @param value the value
   * @return true, if successful
   */
  @Override
  public boolean remove(Object value) {
    return removeByObject(value) >= 0;
  }
}
