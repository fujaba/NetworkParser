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
import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.Converter;
import de.uniks.networkparser.list.SimpleList;

public class ByteList extends SimpleList<ByteItem> implements ByteItem {
  /** The children of the ByteEntity. */
  private byte type;

  /* Can be a Type */
  public static final String PROPERTY_PROPERTY = "property";
  public static final String PROPERTY_TYPE = "type";
  public static final String PROPERTY_ORIENTATION = "orientation";

  @Override
  public BaseItem getNewList(boolean keyValue) {
    if (keyValue) {
      return new ByteEntity();
    }
    return new ByteList();
  }

  @Override
  public String toString() {
    return toString(null);
  }

  /**
   * Convert the bytes to a String
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
   * Convert the bytes to a String
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

  @Override
  public ByteBuffer getBytes(boolean isDynamic) {
    int len = calcLength(isDynamic, true);
    ByteBuffer buffer = ByteBuffer.allocate(len);
    writeBytes(buffer, isDynamic, true, isPrimitive(isDynamic));
    buffer.flip(true);
    return buffer;
  }

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

  @Override
  public byte getType() {
    return type;
  }

  public ByteList withType(Byte value) {
    if (value != null)
      this.type = value;
    return this;
  }

  public SimpleList<ByteItem> withValue(String value) {
    ByteConverterString converter = new ByteConverterString();
    this.add(((ByteEntity) getNewList(true)).withValue(ByteTokener.DATATYPE_FIXED, converter.decode(value)));
    return this;
  }

  @Override
  public boolean remove(Object value) {
    return removeByObject(value) >= 0;
  }
}
