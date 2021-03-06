package de.uniks.networkparser.bytes;

/*
NetworkParser
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
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.EntityUtil;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.Tokener;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
import de.uniks.networkparser.list.ObjectMapEntry;
import de.uniks.networkparser.list.SimpleKeyValueList;

/**
 * The Class ByteIdMap.
 */

public class ByteTokener extends Tokener {
	/** The SPLITTER. */
	public static final char SPLITTER = ' ';

	/** The Constant FIXED VALUE. */
	public static final byte DATATYPE_FIXED = 0x00;

	/** The Constant NULL-VALUE. */
	public static final byte DATATYPE_NULL = 0x22;

	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_CLAZZID = 0x23;

	/** SIMPLE TYPES The Constant DATATYPE_BYTE. */
	/** The Constant DATATYPE_INTEGER. */
	public static final byte DATATYPE_SHORT = 0x30;

	/** The Constant DATATYPE_INTEGER. */
	public static final byte DATATYPE_INTEGER = 0x31;

	/** The Constant DATATYPE_INTEGER. */
	public static final byte DATATYPE_LONG = 0x32;

	/** The Constant DATATYPE_FLOAT. */
	public static final byte DATATYPE_FLOAT = 0x33;

	/** The Constant DATATYPE_DOUBLE. */
	public static final byte DATATYPE_DOUBLE = 0x34;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_DATE = 0x35;

	/** The Constant DATATYPE_BYTE. */
	public static final byte DATATYPE_BYTE = 0x36;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_UNSIGNEDBYTE = 0x37;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_BYTEARRAY = 0x3A;

	/** The Constant DATATYPE_CHAR. */
	public static final byte DATATYPE_CHAR = 0x40;

	/** The Constant CLASS-VALUE. */
	public static final byte DATATYPE_CLAZZNAME = 0x41;

	/** The Constant CLASS-VALUE. */
	public static final byte DATATYPE_CLAZZNAMELONG = 0x42;

	/** The Constant CLASS-VALUE. */
	public static final byte DATATYPE_CLAZZPACKAGE = 0x43;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_CLAZZTYPE = 0x44;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_CLAZZTYPELONG = 0x45;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_ASSOC = 0x46;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_ASSOCLONG = 0x47;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_CLAZZSTREAM = 0x50;

	/** The Constant DATATYPE_STRING. */
	public static final byte DATATYPE_STRING = 0x4A;

	/** The Constant DATATYPE_LIST. */
	public static final byte DATATYPE_LIST = 0x5A;

	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_MAP = 0x6A;

	/** The Constant CHECKTYPE. */
	public static final byte DATATYPE_CHECK = 0x7A;

	/** The Constant LEN_LITTLE. */
	public static final byte LEN_LITTLE = 0x01;

	/** The Constant LEN_SHORT. */
	public static final byte LEN_SHORT = 0x02;

	/** The Constant LEN_MID. */
	public static final byte LEN_MID = 0x03;

	/** The Constant LEN_BIG. */
	public static final byte LEN_BIG = 0x04;

	/** The Constant DATATYPE_LAST. */
	public static final byte LEN_LAST = 0x05;

	public String getCharset() {
		return BaseItem.ENCODING;
	}

	private boolean addClazzType(ByteList msg, String clazzName, MapEntity map) {
		if (map == null || clazzName == null) {
			return false;
		}
		try {
			int id = map.getIndexOfClazz(clazzName);
			if (id > 0) {
				if (id <= Byte.MAX_VALUE) {
					msg.add(new ByteEntity().withValue(DATATYPE_CLAZZTYPE, (byte) id));
				} else {
					msg.add(new ByteEntity().withValue(DATATYPE_CLAZZTYPELONG, (byte) id));
				}
				return true;
			}
			int pos = clazzName.lastIndexOf(".");
			if (pos > 0) {
				String lastClazz = map.getLastClazz();
				if (lastClazz != null && lastClazz.lastIndexOf(".") == pos)
					if (clazzName.substring(0, pos).equals(lastClazz.substring(0, pos))) {
						byte[] bytes = clazzName.substring(pos + 1).getBytes(getCharset());
						msg.add(new ByteEntity().withValue(DATATYPE_CLAZZPACKAGE, bytes));
						return true;
					}
			}
			byte[] bytes = clazzName.getBytes(getCharset());
			msg.add(new ByteEntity().withValue(DATATYPE_CLAZZNAME, bytes));
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	@Override
	public ByteItem encode(Object entity, MapEntity map) {
		SendableEntityCreator creator = getCreatorClass(entity);
		if (creator == null) {
			return null;
		}
		int id = map.getIndexVisitedObjects(entity);
		if (id >= 0) {
			/* Must be a assoc */
			if (id <= Byte.MAX_VALUE) {
				return new ByteEntity().withValue(DATATYPE_ASSOC, (byte) id);
			} else {
				return new ByteEntity().withValue(DATATYPE_ASSOCLONG, id);
			}
		}
		ByteList msg = new ByteList();

		if (creator instanceof SendableEntityCreatorTag) {
			String tag = ((SendableEntityCreatorTag) creator).getTag();
			if (tag != null) {
				byte cId = tag.getBytes(Charset.forName(BaseItem.ENCODING))[0];
				msg.add(new ByteEntity().withValue(ByteTokener.DATATYPE_CLAZZID, cId));
			}
		} else {
			Object reference = creator.getSendableInstance(true);
			addClazzType(msg, reference.getClass().getName(), map);
		}

		map.with(entity);
		String[] properties = creator.getProperties();
		if (properties != null) {
			Object referenceObj = creator.getSendableInstance(true);
			for (String property : properties) {
				Object value = creator.getValue(entity, property);
				if (creator.getValue(referenceObj, property) == value) {
					value = null;
				}
				ByteItem child = encodeValue(value, map);
				if (child != null) {
					msg.add(child);
				}
			}

			/* Kill Empty Fields */
			ByteItem[] array = msg.toArray(new ByteItem[msg.size()]);
			for (int i = array.length - 1; i > 0; i--) {
				if (array[i].isEmpty() == false) {
					break;
				}
				msg.remove(i);
			}
		}
		return msg;
	}

	public ByteItem encodeValue(Object value, MapEntity filter) {
		ByteEntity msgEntity = new ByteEntity();
		if (msgEntity.setValues(value)) {
			return msgEntity;
		} else {
			/* Map, List, Assocs */
			if (value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>) value;
				ByteList byteList = new ByteList().withType(ByteTokener.DATATYPE_LIST);
				for (Object childValue : list) {
					ByteItem child = encodeValue(childValue, filter);
					if (child != null) {
						byteList.add(child);
					}
				}
				return byteList;
			}
			if (value instanceof Map<?, ?>) {
				ByteList byteList = new ByteList().withType(ByteTokener.DATATYPE_MAP);
				Map<?, ?> map = (Map<?, ?>) value;
				ByteItem child;

				for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
					java.util.Map.Entry<?, ?> entity = (java.util.Map.Entry<?, ?>) i.next();
					ByteList item = new ByteList().withType(ByteTokener.DATATYPE_CHECK);

					child = encodeValue(entity.getKey(), filter);
					if (child != null) {
						item.add(child);
					}
					child = encodeValue(entity.getValue(), filter);
					if (child != null) {
						item.add(child);
					}
					byteList.add(item);
				}
				return byteList;
			}
			if (value != null) {
				return encode(value, filter);
			}
		}
		return null;
	}

	/**
	 * Decode.
	 *
	 * @param buffer       the in buffer
	 * @param eventCreater the Creator as Factory
	 * @param map          the MapEntry for decoding-runtime values
	 * @return the object
	 */
	public Object decodeClazz(Buffer buffer, SendableEntityCreator eventCreater, MapEntity map) {
		if (eventCreater == null) {
			ByteMessage e = new ByteMessage();
			if (buffer != null)
				e.withValue(buffer.array(-1, true));
			return e;
		}
		Object entity = eventCreater.getSendableInstance(false);
		String[] properties = eventCreater.getProperties();
		if (properties != null) {
			for (String property : properties) {
				if (buffer.remaining() < 1) {
					break;
				}
				Object value = decodeValue(buffer.getByte(), buffer, buffer.length() - buffer.position(), map);
				if (value != null) {
					if (value instanceof List<?>) {
						List<?> list = (List<?>) value;
						for (Iterator<?> i = list.iterator(); i.hasNext();) {
							Object item = i.next();
							eventCreater.setValue(entity, property, item, SendableEntityCreator.NEW);
						}
					} else {
						eventCreater.setValue(entity, property, value, SendableEntityCreator.NEW);
					}
				}
			}
		}
		return entity;
	}

	public Object decodeValue(ByteEntity entity, MapEntity map) {
		if (entity == null) {
			return null;
		}
		byte type = entity.getType();
		ByteBuffer buffer = new ByteBuffer();
		Object value = entity.getValue(ByteEntity.VALUE);
		if (value != null) {
			buffer.with((byte[]) value);
		}
		return decodeValue(type, buffer, buffer.length(), map);
	}

	/**
	 * Gets the decode object.
	 *
	 * @param current The CurrentChar (Type of value)
	 * @param buffer  the Buffer for decoding
	 * @param map     decoding Runtime values
	 * @return the decode object
	 */
	public Object decodeValue(byte current, Buffer buffer, MapEntity map) {
		if (buffer == null || buffer.remaining() < 1) {
			return null;
		}
		return decodeValue(current, buffer, buffer.length(), map);
	}

	Object decodeValue(Buffer buffer, int end, MapEntity map) {
		if (buffer == null) {
			return null;
		}
		return decodeValue(buffer.getByte(), buffer, end, map);
	}

	/**
	 * Gets the decode object.
	 * 
	 * @param type   The CurrentChar (Type of value)
	 * @param buffer the byteBuffer
	 * @param end    EndIndex
	 * @param map    decoding Runtimevalue
	 * @return the decode object
	 */
	public Object decodeValue(byte type, Buffer buffer, int end, MapEntity map) {
		if (buffer == null || buffer.remaining() < 1) {
			return null;
		}
		if (type == ByteTokener.DATATYPE_NULL) {
			return null;
		}
		if (type == ByteTokener.DATATYPE_BYTE) {
			return Byte.valueOf(buffer.getByte());
		}
		if (type == ByteTokener.DATATYPE_CHAR) {
			return Character.valueOf(buffer.getChar());
		}
		if (type == ByteTokener.DATATYPE_SHORT) {
			return Short.valueOf(buffer.getShort());
		}
		if (type == ByteTokener.DATATYPE_INTEGER) {
			return Integer.valueOf(buffer.getInt());
		}
		if (type == ByteTokener.DATATYPE_LONG) {
			return Long.valueOf(buffer.getLong());
		}
		if (type == ByteTokener.DATATYPE_FLOAT) {
			return Float.valueOf(buffer.getFloat());
		}
		if (type == ByteTokener.DATATYPE_DOUBLE) {
			return Double.valueOf(buffer.getDouble());
		}
		if (type == ByteTokener.DATATYPE_DATE) {
			return new Date(buffer.getLong());
		}
		if (type == ByteTokener.DATATYPE_CLAZZNAME) {
			int len = buffer.getByte() - ByteTokener.SPLITTER;
			SendableEntityCreator eventCreater;
			try {
				eventCreater = getCreator(new String(buffer.array(len, false), getCharset()), true, null);
				return decodeClazz(buffer, eventCreater, map);
			} catch (Exception e) {
			}
			return null;
		}
		if (type == ByteTokener.DATATYPE_CLAZZNAMELONG) {
			int len = buffer.getInt();
			SendableEntityCreator eventCreater;
			try {
				eventCreater = getCreator(new String(buffer.array(len, false), getCharset()), true, null);
				return decodeClazz(buffer, eventCreater, map);
			} catch (Exception e) {
			}
			return null;
		}
		if (type == ByteTokener.DATATYPE_CLAZZTYPE) {
			int pos = buffer.getByte() - ByteTokener.SPLITTER;
			SendableEntityCreator eventCreater = getCreator(map.getClazz(pos), true, null);
			return decodeClazz(buffer, eventCreater, map);
		}
		if (type == ByteTokener.DATATYPE_CLAZZTYPELONG) {
			int pos = buffer.getInt();
			SendableEntityCreator eventCreater = getCreator(map.getClazz(pos), true, null);
			return decodeClazz(buffer, eventCreater, map);
		}
		if (type == ByteTokener.DATATYPE_CLAZZID) {
			type = buffer.getByte();
			String id;
			try {
				id = new String(new byte[] { type }, BaseItem.ENCODING);
			} catch (UnsupportedEncodingException e) {
				id = "";
			}
			SendableEntityCreator eventCreater = getCreator(id, true, null);
			if (eventCreater == null) {
				SimpleKeyValueList<String, SendableEntityCreator> creators = getMap().getCreators();
				for (int i = 0; i < creators.size(); i++) {
					if (creators.getKeyByIndex(i).startsWith(id)) {
						eventCreater = creators.getValueByIndex(i);
						break;
					}
				}
			}

			return decodeClazz(buffer, eventCreater, map);
		}
		if (type == ByteTokener.DATATYPE_ASSOC) {
			int pos = buffer.getByte();
			return map.getVisitedObjects(pos);
		}
		if (type == ByteTokener.DATATYPE_ASSOCLONG) {
			int pos = buffer.getInt();
			return map.getVisitedObjects(pos);
		}
		if (EntityUtil.isGroup(type)) {
			byte subgroup = EntityUtil.getSubGroup(type);
			int len = 0;
			if (subgroup == ByteTokener.LEN_LITTLE) {
				len = buffer.getByte() - ByteTokener.SPLITTER;
			} else if (subgroup == ByteTokener.LEN_SHORT) {
				len = buffer.getByte();
			} else if (subgroup == ByteTokener.LEN_MID) {
				len = buffer.getShort();
			} else if (subgroup == ByteTokener.LEN_BIG) {
				len = buffer.getInt();
			} else if (subgroup == ByteTokener.LEN_LAST) {
				len = end - 1;
			}
			byte group = EntityUtil.getGroup(type);
			if (group == ByteTokener.DATATYPE_STRING) {
				try {
					return new String(buffer.array(len, false), getCharset());
				} catch (Exception e) {
					return "";
				}
			} else if (group == ByteTokener.DATATYPE_BYTEARRAY) {
				return buffer.array(len, false);
			} else if (group == ByteTokener.DATATYPE_LIST) {
				int start = buffer.position();
				ArrayList<Object> values = new ArrayList<Object>();
				while (start + len - buffer.position() > 0) {
					Object value = decodeValue(buffer, start + len - buffer.position(), map);
					if (value != null) {
						values.add(value);
					}
				}
				return values;
			} else if (group == ByteTokener.DATATYPE_MAP) {
				int start = buffer.position();
				ArrayList<Object> values = new ArrayList<Object>();
				while (start + len - buffer.position() > 0) {
					Object subValues = decodeValue(buffer, start + len - buffer.position(), map);
					if (subValues != null && subValues instanceof List<?>) {
						List<?> list = (List<?>) subValues;
						if (list.size() == 2) {
							values.add(new ObjectMapEntry().with(list.get(0), list.get(1)));
						}
					} else {
						break;
					}
				}
				return values;
			} else if (group == ByteTokener.DATATYPE_CHECK) {
				int start = buffer.position();
				if (buffer.length() < start + len) {
					return null;
				}
				ArrayList<Object> values = new ArrayList<Object>();
				while (start + len - buffer.position() > 0) {
					values.add(decodeValue(buffer, start + len - buffer.position(), map));
				}
				return values;
			}
		}
		return null;
	}

	@Override
	public ByteTokener withMap(IdMap map) {
		super.withMap(map);
		return this;
	}

	public static final byte[] intToByte(int value) {
		byte[] result = new byte[4];
		result[0] = (byte) (value >>> 24);
		result[1] = (byte) (value >>> 16);
		result[2] = (byte) (value >>> 8);
		result[3] = (byte) (value & 0xff);
		;
		return result;
	}
}
