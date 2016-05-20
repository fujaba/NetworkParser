package de.uniks.networkparser.bytes;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.MapEntity;
import de.uniks.networkparser.buffer.Buffer;
import de.uniks.networkparser.buffer.ByteBuffer;
import de.uniks.networkparser.buffer.Tokener;
import de.uniks.networkparser.event.BasicMessage;
import de.uniks.networkparser.event.ObjectMapEntry;
import de.uniks.networkparser.event.UnknownMessage;
import de.uniks.networkparser.event.util.BasicMessageCreator;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
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
	public static final byte DATATYPE_CLAZZTYP = 0x44;

	/** The Constant DATATYPE_BYTEARRAY. */
	public static final byte DATATYPE_CLAZZTYPLONG = 0x45;

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

	public static final String CHARSET="UTF-8";

	public String getCharset() {
		return CHARSET;
	}

	private boolean addClazzTyp(ByteList msg, String clazzName, MapEntity map) {
		try {
			int id = map.getIndexOfClazz(clazzName);
			if (id > 0) {
				if (id <= Byte.MAX_VALUE) {
					msg.add(new ByteEntity().withValue(DATATYPE_CLAZZTYP,
							(byte) id));
				} else {
					msg.add(new ByteEntity().withValue(
							DATATYPE_CLAZZTYPLONG, (byte) id));
				}
				return true;
			}
			int pos = clazzName.lastIndexOf(".");
			if (pos > 0) {
				String lastClazz = map.getLastClazz();
				if (lastClazz != null && lastClazz.lastIndexOf(".") == pos)
					if (clazzName.substring(0, pos).equals(
							lastClazz.substring(0, pos))) {
						byte[] bytes = clazzName.substring(pos + 1)
								.getBytes(getCharset());
						msg.add(new ByteEntity().withValue(
								DATATYPE_CLAZZPACKAGE, bytes));
						return true;
					}
			}
			byte[] bytes = clazzName.getBytes(getCharset());
			msg.add(new ByteEntity().withValue(DATATYPE_CLAZZNAME,
					bytes));
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
			// Must be a assoc
			if (id <= Byte.MAX_VALUE) {
				return new ByteEntity().withValue(DATATYPE_ASSOC, (byte) id);
			} else {
				return new ByteEntity().withValue(DATATYPE_ASSOCLONG, id);
			}
		}
		ByteList msg = new ByteList();
		if (creator instanceof BasicMessageCreator) {
			BasicMessage basicEvent = (BasicMessage) entity;
			addClazzTyp(msg, basicEvent.getValue(), map);
			return msg;
		}

		if (creator instanceof SendableEntityCreatorTag) {
			String tag = ((SendableEntityCreatorTag) creator).getTag();
			if(tag != null) {
				byte cId = tag.getBytes(Charset.forName("UTF-8"))[0];
				msg.add(new ByteEntity().withValue(ByteTokener.DATATYPE_CLAZZID, cId));
			}
		} else {
			Object reference = creator.getSendableInstance(true);
			addClazzTyp(msg, reference.getClass().getName(), map);
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

			// Kill Empty Fields
			ByteItem[] array = msg.toArray(new ByteItem[msg.size()]);
			for (int i = array.length - 1; i > 0; i--) {
				if (!array[i].isEmpty()) {
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
			// Map, List, Assocs
			if (value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>) value;
				ByteList byteList = new ByteList()
						.withTyp(ByteTokener.DATATYPE_LIST);
				for (Object childValue : list) {
					ByteItem child = encodeValue(childValue, filter);
					if (child != null) {
						byteList.add(child);
					}
				}
				return byteList;
			}
			if (value instanceof Map<?, ?>) {
				ByteList byteList = new ByteList()
						.withTyp(ByteTokener.DATATYPE_MAP);
				Map<?, ?> map = (Map<?, ?>) value;
				ByteItem child;

				for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
					java.util.Map.Entry<?, ?> entity = (java.util.Map.Entry<?, ?>) i.next();
					ByteList item = new ByteList()
							.withTyp(ByteTokener.DATATYPE_CHECK);

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
	 * @param buffer			the in buffer
	 * @param eventCreater		the Creator as Factory
	 * @param map 				the MapEntry for decoding-runtime values
	 * @return 					the object
	 */
	public Object decodeClazz(Buffer buffer, SendableEntityCreator eventCreater, MapEntity map) {
		if (eventCreater == null) {
			UnknownMessage e = new UnknownMessage();
			if(buffer != null)
				e.set(UnknownMessage.PROPERTY_VALUE, buffer.array(-1, true));
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
							eventCreater.setValue(entity, property, item,
									IdMap.NEW);
						}
					} else {
						eventCreater.setValue(entity, property, value,
								IdMap.NEW);
					}
				}
			}
		}
		return entity;
	}

	public Object decodeValue(ByteEntity entity, MapEntity map) {
		if(entity == null){
			return null;
		}
		byte typ = entity.getTyp();
		ByteBuffer buffer = new ByteBuffer();
		Object value = entity.getValue(ByteEntity.VALUE);
		if(value!=null) {
			buffer.with((byte[])value);
		}
		return decodeValue(typ, buffer, buffer.length(), map);
	}

	/**
	 * Gets the decode object.
	 *
	 * @param current The CurrentChar (Typ of value)
	 * @param buffer the Buffer for decoding
	 * @param map decoding Runtime values
	 * @return the decode object
	 */
	public Object decodeValue(byte current, Buffer buffer, MapEntity map) {
		if (buffer == null || buffer.remaining() < 1) {
			return null;
		}
		return decodeValue(current, buffer, buffer.length(), map);
	}

	Object decodeValue(Buffer buffer, int end, MapEntity map) {
		return decodeValue(buffer.getByte(), buffer, end, map);
	}

	/**
	 * Gets the decode object.
	 * @param typ The CurrentChar (Typ of value)
	 * @param buffer the byteBuffer
	 * @param end EndIndex
	 * @param map decoding Runtimevalue
	 * @return the decode object
	 */
	public Object decodeValue(byte typ, Buffer buffer, int end, MapEntity map) {
		if (buffer == null || buffer.remaining() < 1) {
			return null;
		}
		if (typ == ByteTokener.DATATYPE_NULL) {
			return null;
		}
		if (typ == ByteTokener.DATATYPE_BYTE) {
			return Byte.valueOf(buffer.getByte());
		}
		if (typ == ByteTokener.DATATYPE_CHAR) {
			return Character.valueOf(buffer.getChar());
		}
		if (typ == ByteTokener.DATATYPE_SHORT) {
			return Short.valueOf(buffer.getShort());
		}
		if (typ == ByteTokener.DATATYPE_INTEGER) {
			return Integer.valueOf(buffer.getInt());
		}
		if (typ == ByteTokener.DATATYPE_LONG) {
			return Long.valueOf(buffer.getLong());
		}
		if (typ == ByteTokener.DATATYPE_FLOAT) {
			return Float.valueOf(buffer.getFloat());
		}
		if (typ == ByteTokener.DATATYPE_DOUBLE) {
			return Double.valueOf(buffer.getDouble());
		}
		if (typ == ByteTokener.DATATYPE_DATE) {
			return new Date(buffer.getLong());
		}
		if (typ == ByteTokener.DATATYPE_CLAZZNAME) {
			int len = buffer.getByte() - ByteTokener.SPLITTER;
			SendableEntityCreator eventCreater;
			try {
				eventCreater = getCreator(new String(buffer.array(len, false), getCharset()), true);
				return decodeClazz(buffer, eventCreater, map);
			} catch (Exception e) {
			}
			return null;
		}
		if (typ == ByteTokener.DATATYPE_CLAZZNAMELONG) {
			int len = buffer.getInt();
			SendableEntityCreator eventCreater;
			try {
				eventCreater = getCreator(new String(buffer.array(len, false), getCharset()), true);
				return decodeClazz(buffer, eventCreater, map);
			} catch (Exception e) {
			}
			return null;
		}
		if (typ == ByteTokener.DATATYPE_CLAZZTYP) {
			int pos = buffer.getByte() - ByteTokener.SPLITTER;
			SendableEntityCreator eventCreater = getCreator(map.getClazz(pos), true);
			return decodeClazz(buffer, eventCreater, map);
		}
		if (typ == ByteTokener.DATATYPE_CLAZZTYPLONG) {
			int pos = buffer.getInt();
			SendableEntityCreator eventCreater = getCreator(map.getClazz(pos), true);
			return decodeClazz(buffer, eventCreater, map);
		}
		if (typ == ByteTokener.DATATYPE_CLAZZID) {
			typ = buffer.getByte();
			String id;
			try {
				id = new String(new byte[]{typ}, "UTF-8");
			} catch (UnsupportedEncodingException e) {
				id = "";
			}
			SendableEntityCreator eventCreater = getCreator(id, true);
			if(eventCreater == null) {
				SimpleKeyValueList<String, SendableEntityCreator> creators = getMap().getCreators();
				for(int i=0;i<creators.size();i++) {
					if(creators.getKeyByIndex(i).startsWith(id)) {
						eventCreater = creators.getValueByIndex(i);
						break;
					}
				}
			}

			return decodeClazz(buffer, eventCreater, map);
		}
		if (typ == ByteTokener.DATATYPE_ASSOC) {
			int pos = buffer.getByte();
			return map.getVisitedObjects(pos);
		}
		if (typ == ByteTokener.DATATYPE_ASSOCLONG) {
			int pos = buffer.getInt();
			return map.getVisitedObjects(pos);
		}
		if (ByteUtil.isGroup(typ)) {
			byte subgroup = ByteUtil.getSubGroup(typ);
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
			byte group = ByteUtil.getGroup(typ);
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
							values.add(new ObjectMapEntry().with(list.get(0),
									list.get(1)));
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
}
