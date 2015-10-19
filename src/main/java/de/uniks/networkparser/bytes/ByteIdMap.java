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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.uniks.networkparser.AbstractMap;
import de.uniks.networkparser.Filter;
import de.uniks.networkparser.IdMap;
import de.uniks.networkparser.NetworkParserLog;
import de.uniks.networkparser.bytes.converter.ByteConverterHTTP;
import de.uniks.networkparser.event.BasicMessage;
import de.uniks.networkparser.event.ObjectMapEntry;
import de.uniks.networkparser.event.UnknownMessage;
import de.uniks.networkparser.event.util.BasicMessageCreator;
import de.uniks.networkparser.interfaces.BaseItem;
import de.uniks.networkparser.interfaces.ByteConverter;
import de.uniks.networkparser.interfaces.ByteItem;
import de.uniks.networkparser.interfaces.IdMapDecoder;
import de.uniks.networkparser.interfaces.SendableEntityCreator;
import de.uniks.networkparser.interfaces.SendableEntityCreatorTag;
/**
 * The Class ByteIdMap.
 */

public class ByteIdMap extends IdMap implements IdMapDecoder{
	/** The SPLITTER. */
	public static final char SPLITTER = ' ';

	/** The Constant FIXED VALUE. */
	public static final byte DATATYPE_FIXED = 0x00;

	/** The Constant NULL-VALUE. */
	public static final byte DATATYPE_NULL = 0x22;

	/** The Constant DATATYPE_MAP. */
	public static final byte DATATYPE_CLAZZID = 0x23;

	/**
	 * SIMPLE TYPES The Constant DATATYPE_BYTE.
	 */
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

	/** The decoder map. */
	protected HashMap<Byte, SendableEntityCreatorTag> decoderMap;

	private ByteFilter filter = new ByteFilter();

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * de.uni.kassel.peermessage.IdMap#addCreator(de.uni.kassel.peermessage.
	 * interfaces.SendableEntityCreator)
	 */
	public boolean addCreator(SendableEntityCreator createrClass) {
		if (createrClass instanceof SendableEntityCreatorTag) {
			if (this.decoderMap != null) {
				String tag = ((SendableEntityCreatorTag) createrClass).getTag();
				if(tag == null || tag.length()<1) {
					return false;
				}
				if (this.decoderMap.containsKey(tag.getBytes()[0])) {
					return false;
				}
			}
		}
		super.withCreator(createrClass);
		return true;
	}

	@Override
	public AbstractMap withCreator(String className,
			SendableEntityCreator createrClass) {
		super.withCreator(className, createrClass);

		if (createrClass instanceof SendableEntityCreatorTag) {
			SendableEntityCreatorTag byteCreator = (SendableEntityCreatorTag) createrClass;
			String tag = byteCreator.getTag();
			if(tag!=null && tag.length() > 0 ) {
				if (this.decoderMap == null) {
					this.decoderMap = new HashMap<Byte, SendableEntityCreatorTag>();
				}
				this.decoderMap.put(tag.getBytes()[0],
						byteCreator);
			}
		}
		return this;
	}

	/**
	 * Encode.
	 *
	 * @param entity
	 *            the entity
	 * @return the byte entity message
	 */
	@Override
	public ByteItem encode(Object entity) {
		return encode(entity, filter.withStandard(this.filter));
	}

	private boolean addClazzTyp(ByteList msg, String clazzName, Filter filter) {
		try {
			if (filter instanceof ByteFilter) {
				ByteFilter bf = (ByteFilter) filter;
				int id = bf.getIndexOfClazz(clazzName);
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
					String lastClazz = bf.getLastClazz();
					if (lastClazz != null && lastClazz.lastIndexOf(".") == pos)
						if (clazzName.substring(0, pos).equals(
								lastClazz.substring(0, pos))) {
							byte[] bytes = clazzName.substring(pos + 1)
									.getBytes(bf.getCharset());
							msg.add(new ByteEntity().withValue(
									DATATYPE_CLAZZPACKAGE, bytes));
							return true;
						}
				}
				byte[] bytes = clazzName.getBytes(bf.getCharset());
				if (id <= Byte.MAX_VALUE) {
					msg.add(new ByteEntity().withValue(DATATYPE_CLAZZNAME,
							bytes));
				} else {
					msg.add(new ByteEntity().withValue(DATATYPE_CLAZZNAMELONG,
							bytes));
				}
				return true;
			}

		} catch (UnsupportedEncodingException e) {
		}
		return false;
	}

	@Override
	public ByteItem encode(Object entity, Filter filter) {
		SendableEntityCreator creator = getCreatorClass(entity);
		if (creator == null) {
			return null;
		}
		int id = filter.getIndexVisitedObjects(entity);
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
			addClazzTyp(msg, basicEvent.getValue(), filter);
			return msg;
		}

		if (creator instanceof SendableEntityCreatorTag) {
			String tag = ((SendableEntityCreatorTag) creator).getTag();
			byte cId = tag.getBytes()[0];
			msg.add(new ByteEntity().withValue(ByteIdMap.DATATYPE_CLAZZID, cId));
		} else {
			Object reference = creator.getSendableInstance(true);
			addClazzTyp(msg, reference.getClass().getName(), filter);
		}

		filter.withObjects(entity);
		String[] properties = creator.getProperties();
		if (properties != null) {
			Object referenceObj = creator.getSendableInstance(true);
			for (String property : properties) {
				Object value = creator.getValue(entity, property);
				if (creator.getValue(referenceObj, property) == value) {
					value = null;
				}
				ByteItem child = encodeValue(value, filter);
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

	public ByteItem encodeValue(Object value, Filter filter) {
		ByteEntity msgEntity = new ByteEntity();
		if (msgEntity.setValues(value)) {
			return msgEntity;
		} else {
			// Map, List, Assocs
			if (value instanceof Collection<?>) {
				Collection<?> list = (Collection<?>) value;
				ByteList byteList = new ByteList()
						.withTyp(ByteIdMap.DATATYPE_LIST);
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
						.withTyp(ByteIdMap.DATATYPE_MAP);
				Map<?, ?> map = (Map<?, ?>) value;
				ByteItem child;

				for (Iterator<?> i = map.entrySet().iterator(); i.hasNext();) {
					java.util.Map.Entry<?, ?> entity = (Entry<?, ?>) i.next();
					ByteList item = new ByteList()
							.withTyp(ByteIdMap.DATATYPE_CHECK);

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
	 * Gets the creator decoder class.
	 *
	 * @param typ
	 *            the typ
	 * @return the creator decoder class
	 */
	public SendableEntityCreatorTag getCreatorDecoderClass(byte typ) {
		if(this.decoderMap == null) {
			return null;
		}
		return this.decoderMap.get(Byte.valueOf(typ));
	}

	/**
	 * Decode.
	 *
	 * @param value
	 *            the value
	 * @return the object
	 */
	@Override
	public Object decode(String value) {
		return decode(value, new ByteConverterHTTP());
	}

	/**
	 * Decode.
	 *
	 * @param value
	 *            the value
	 * @param converter
	 *            the Converter for bytes to String
	 * @return the object
	 */
	public Object decode(String value, ByteConverter converter) {
		if(converter == null) {
			return null;
		}
		byte[] decodeBytes = converter.decode(value);
		return decode(decodeBytes);
	}

	/**
	 * Decode.
	 *
	 * @param value
	 *            the value
	 * @return the object
	 */
	public Object decode(Object value) {
		if (value instanceof ByteBuffer) {
			return decode((ByteBuffer) value);
		} else if (value instanceof byte[]) {
			return decode(new ByteBuffer().with((byte[]) value));
		}
		return null;
	}

	@Override
	public Object decode(BaseItem value) {
		if (value instanceof ByteEntity) {
			return decode(((ByteEntity) value).getValue());
		}
		return null;
	}

	/**
	 * Decode.
	 *
	 * @param buffer
	 *            the in
	 * @return the object
	 */
	public Object decode(ByteBuffer buffer) {
		if(buffer == null) {
			return null;
		}
		if (buffer.remaining() < 1) {
			if (logger.error(this, "decode",
					NetworkParserLog.ERROR_TYP_PARSING, buffer)) {
				throw new RuntimeException("DecodeExpeption - Remaining:"
						+ buffer.remaining());
			}
			return null;
		}
		return decodeValue(buffer, buffer.length());
	}

	/**
	 * Decode.
	 *
	 * @param buffer
	 *            the in
	 * @param eventCreater
	 *            The Creator as Factory
	 * @return the object
	 */
	public Object decodeClazz(ByteBuffer buffer,
			SendableEntityCreator eventCreater) {
		if (eventCreater == null) {
			UnknownMessage e = new UnknownMessage();
			if(buffer != null)
				e.set(UnknownMessage.PROPERTY_VALUE, buffer.array());
			return e;
		}
		Object entity = eventCreater.getSendableInstance(false);
		String[] properties = eventCreater.getProperties();
		if (properties != null) {
			for (String property : properties) {
				if (buffer.remaining() < 1) {
					break;
				}
				Object value = decodeValue(buffer,
						buffer.length() - buffer.position());
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

	/**
	 * Gets the decode object.
	 *
	 * @param buffer
	 *            the in
	 * @param end
	 *            EndIndex
	 * @return the decode object
	 */
	public Object decodeValue(ByteBuffer buffer, int end) {
		if (buffer == null || buffer.remaining() < 1) {
			return null;
		}
		byte typ = buffer.getByte();
		if (typ == ByteIdMap.DATATYPE_NULL) {
			return null;
		}
		if (typ == ByteIdMap.DATATYPE_BYTE) {
			return Byte.valueOf(buffer.getByte());
		}
		if (typ == ByteIdMap.DATATYPE_CHAR) {
			return Character.valueOf(buffer.getChar());
		}
		if (typ == ByteIdMap.DATATYPE_SHORT) {
			return Short.valueOf(buffer.getShort());
		}
		if (typ == ByteIdMap.DATATYPE_INTEGER) {
			return Integer.valueOf(buffer.getInt());
		}
		if (typ == ByteIdMap.DATATYPE_LONG) {
			return Long.valueOf(buffer.getLong());
		}
		if (typ == ByteIdMap.DATATYPE_FLOAT) {
			return Float.valueOf(buffer.getFloat());
		}
		if (typ == ByteIdMap.DATATYPE_DOUBLE) {
			return Double.valueOf(buffer.getDouble());
		}
		if (typ == ByteIdMap.DATATYPE_DATE) {
			return new Date(Long.valueOf(buffer.getInt()).longValue());
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZNAME) {
			int len = buffer.getByte() - ByteIdMap.SPLITTER;
			SendableEntityCreator eventCreater;
			try {
				eventCreater = super.getCreator(new String(
						buffer.getValue(len), filter.getCharset()), true);
				return decodeClazz(buffer, eventCreater);
			} catch (UnsupportedEncodingException e) {
			}
			return null;
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZNAMELONG) {
			int len = buffer.getInt();
			SendableEntityCreator eventCreater;
			try {
				eventCreater = super.getCreator(new String(
						buffer.getValue(len), filter.getCharset()), true);
				return decodeClazz(buffer, eventCreater);
			} catch (UnsupportedEncodingException e) {
			}
			return null;
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZTYP) {
			int pos = buffer.getByte() - ByteIdMap.SPLITTER;
			ByteFilter bf = (ByteFilter) filter;
			SendableEntityCreator eventCreater = super.getCreator(
					bf.getClazz(pos), true);
			return decodeClazz(buffer, eventCreater);
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZTYPLONG) {
			int pos = buffer.getInt();
			ByteFilter bf = (ByteFilter) filter;
			SendableEntityCreator eventCreater = super.getCreator(
					bf.getClazz(pos), true);
			return decodeClazz(buffer, eventCreater);
		}
		if (typ == ByteIdMap.DATATYPE_CLAZZID) {
			typ = buffer.getByte();
			SendableEntityCreator eventCreater = getCreatorDecoderClass(typ);
			return decodeClazz(buffer, eventCreater);
		}
		if (typ == ByteIdMap.DATATYPE_ASSOC) {
			int pos = buffer.getByte();
			return filter.getVisitedObjects(pos);
		}
		if (typ == ByteIdMap.DATATYPE_ASSOCLONG) {
			int pos = buffer.getInt();
			return filter.getVisitedObjects(pos);
		}
		if (ByteUtil.isGroup(typ)) {
			byte subgroup = ByteUtil.getSubGroup(typ);
			int len = 0;
			if (subgroup == ByteIdMap.LEN_LITTLE) {
				len = buffer.getByte() - ByteIdMap.SPLITTER;
			} else if (subgroup == ByteIdMap.LEN_SHORT) {
				len = buffer.getByte();
			} else if (subgroup == ByteIdMap.LEN_MID) {
				len = buffer.getShort();
			} else if (subgroup == ByteIdMap.LEN_BIG) {
				len = buffer.getInt();
			} else if (subgroup == ByteIdMap.LEN_LAST) {
				len = end - 1;
			}
			byte group = ByteUtil.getGroup(typ);
			if (group == ByteIdMap.DATATYPE_STRING) {
				try {
					return new String(buffer.getValue(len), filter.getCharset());
				} catch (UnsupportedEncodingException e) {
					return "";
				}
			} else if (group == ByteIdMap.DATATYPE_BYTEARRAY) {
				return buffer.getValue(len);
			} else if (group == ByteIdMap.DATATYPE_LIST) {
				int start = buffer.position();
				ArrayList<Object> values = new ArrayList<Object>();
				while (start + len - buffer.position() > 0) {
					Object value = decodeValue(buffer,
							start + len - buffer.position());
					if (value != null) {
						values.add(value);
					}
				}
				return values;
			} else if (group == ByteIdMap.DATATYPE_MAP) {
				int start = buffer.position();
				ArrayList<Object> values = new ArrayList<Object>();
				while (start + len - buffer.position() > 0) {
					Object subValues = decodeValue(buffer,
							start + len - buffer.position());
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
			} else if (group == ByteIdMap.DATATYPE_CHECK) {
				int start = buffer.position();
				if (buffer.length() < start + len) {
					return null;
				}
				ArrayList<Object> values = new ArrayList<Object>();
				while (start + len - buffer.position() > 0) {
					values.add(decodeValue(buffer,
							start + len - buffer.position()));
				}
				return values;
			}
		}
		return null;
	}
}
